package com.br.multicloudecore.gcpmodule.service.ai;

import com.br.multicloudecore.gcpmodule.events.EventBus;
import com.br.multicloudecore.gcpmodule.exceptions.UploadFileToStorageException;
import com.br.multicloudecore.gcpmodule.models.vision.facerecognition.FaceDetectionMessage;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.List;
import java.util.*;
import java.util.concurrent.CompletableFuture;


/**
 * The VisionService class provides functionality for storing images in a
 * Google Cloud Storage bucket and detecting landmarks in an image using
 * the Google Cloud Vision API.
 */
@Service
public class VisionService {

    private static final Logger logger = LoggerFactory.getLogger(VisionService.class);

    private final EventBus eventBus;
    private final Storage storage;

    @Value("${project.id}")
    private String projectId;

    @Value("${bucket.name}")
    private String bucketName;


    public VisionService(EventBus eventBus,
                         Storage storage) {
        this.eventBus = eventBus;
        this.storage = storage;
    }

    public CompletableFuture<FaceDetectionMessage> processImageAndWaitForResult(MultipartFile file) {
        CompletableFuture<FaceDetectionMessage> resultFuture = new CompletableFuture<>();

        try {
            String imageUrl = uploadToCloudStorage(file);

            EventBus.Subscription<FaceDetectionMessage> subscription = eventBus.subscribe(FaceDetectionMessage.class, message -> {
                if (message.getImageUrl().equals(imageUrl)) {
                    resultFuture.complete(message);
                    return false;
                }
                return true;
            });

            CompletableFuture.delayedExecutor(30, java.util.concurrent.TimeUnit.SECONDS).execute(() -> {
                if (!resultFuture.isDone()) {
                    resultFuture.completeExceptionally(new java.util.concurrent.TimeoutException("Processing timed out"));
                    subscription.unsubscribe();
                }
            });

            logger.info("Waiting for face detection results for image: {}", imageUrl);

        } catch (Exception e) {
            resultFuture.completeExceptionally(e);
        }

        return resultFuture;
    }

    public List<AnnotateImageResponse> detectLandmarks(String filePath) throws IOException {
        List<AnnotateImageResponse> responses;
        try (ImageAnnotatorClient vision = ImageAnnotatorClient.create()) {
            ByteString imgBytes = ByteString.readFrom(new FileInputStream(filePath));
            Image img = Image.newBuilder().setContent(imgBytes).build();
            Feature feature = Feature.newBuilder().setType(Feature.Type.LANDMARK_DETECTION).build();
            AnnotateImageRequest request = AnnotateImageRequest.newBuilder().addFeatures(feature).setImage(img).build();
            BatchAnnotateImagesResponse response = vision.batchAnnotateImages(Collections.singletonList(request));
            responses = response.getResponsesList();
        }
        return responses;
    }

    private String uploadToCloudStorage(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(file.getContentType())
                .build();

        storage.create(blobInfo, file.getBytes());

        return String.format("https://storage.googleapis.com/%s/%s", bucketName, fileName);
    }

  public String writeWithFaces(byte[] imageBytes, List<FaceAnnotation> faces) throws IOException {
    BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
    annotateWithFaces(image, faces);
    return encodeImageToBase64(image);
  }

  private void annotateWithFaces(BufferedImage image, List<FaceAnnotation> faces) {
    for (FaceAnnotation face : faces) {
      annotateWithFace(image, face);
    }
  }

  private void annotateWithFace(BufferedImage image, FaceAnnotation face) {
    Graphics2D graphics = image.createGraphics();
    graphics.setStroke(new BasicStroke(5));
    graphics.setColor(new Color(0x00ff00));
    graphics.draw(createPolygonFromVertices(face.getFdBoundingPoly().getVerticesList()));
    graphics.dispose();
  }

  private Polygon createPolygonFromVertices(List<Vertex> vertices) {
    Polygon polygon = new Polygon();
    for (Vertex vertex : vertices) {
      polygon.addPoint(vertex.getX(), vertex.getY());
    }
    return polygon;
  }

  private String encodeImageToBase64(BufferedImage image) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ImageIO.write(image, "jpg", baos);
    return Base64.getEncoder().encodeToString(baos.toByteArray());
  }


    /**
     * Detects landmarks in an image.
     *
     * @param file the image file
     * @return a CompletableFuture with a List of AnnotateImageResponse objects
     * @throws IOException if there is an error reading the file
     */
    public CompletableFuture<List<AnnotateImageResponse>> detectLandmarkImage(
            MultipartFile file) throws IOException {

        Path tempDir = createSecureTempDirectory();
        File tempFile = createSecureTempFile(tempDir);

        file.transferTo(tempFile);
        String filePath = tempFile.getAbsolutePath();
        List<AnnotateImageResponse> responses = detectLandmarks(filePath);

        try {
            Files.delete(tempFile.toPath());
        } catch (IOException e) {
            throw new UploadFileToStorageException("Fail to delete temp file: " + tempFile.toPath(), e);
        }

        return CompletableFuture.completedFuture(responses);
    }

    private Path createSecureTempDirectory() throws IOException {
        Set<PosixFilePermission> dirPerms = PosixFilePermissions.fromString("rwx------");
        Path tempDir = Files.createTempDirectory("secure-temp-dir", PosixFilePermissions.asFileAttribute(dirPerms));
        if (SystemUtils.IS_OS_WINDOWS) {
            setWindowsDirectoryPermissions(tempDir);
        }
        return tempDir;
    }
    private void setWindowsDirectoryPermissions(Path tempDir) throws IOException {
        Set<PosixFilePermission> perms = EnumSet.noneOf(PosixFilePermission.class);
        Files.setPosixFilePermissions(tempDir, perms);
    }

    private File createSecureTempFile(Path tempDir) throws IOException {
        File tempFile = File.createTempFile("secure-temp-file", ".tmp", tempDir.toFile());
        if (SystemUtils.IS_OS_WINDOWS) {
            setWindowsFilePermissions(tempFile.toPath());
        }
        return tempFile;
    }

    private void setWindowsFilePermissions(Path tempFile) throws IOException {
        DosFileAttributeView view = Files.getFileAttributeView(tempFile, DosFileAttributeView.class);
        if (view != null) {
            // Define the desired permissions
            view.setReadOnly(false); // Set to true if you want the file to be read-only
        } else {
            throw new IOException("Failed to set permissions on temp file: " + tempFile);
        }
    }

}
