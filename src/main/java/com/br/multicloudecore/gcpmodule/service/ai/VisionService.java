package com.br.multicloudecore.gcpmodule.service.ai;

import com.br.multicloudecore.gcpmodule.events.EventBus;
import com.br.multicloudecore.gcpmodule.exceptions.PlacesSearchException;
import com.br.multicloudecore.gcpmodule.exceptions.UploadFileToStorageException;
import com.br.multicloudecore.gcpmodule.interfaces.EventListener;
import com.br.multicloudecore.gcpmodule.models.vision.facerecognition.FaceDetectionMessage;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.CompletableFuture;


/**
 * The VisionService class provides functionality for storing images in a
 * Google Cloud Storage bucket and detecting landmarks in an image using
 * the Google Cloud Vision API.
 */
@Service
public class VisionService implements EventListener<FaceDetectionMessage> {

    private static final Logger logger = LoggerFactory.getLogger(VisionService.class);

    private EventBus eventBus;
    private Storage storage;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Value("${project.id}")
    private String projectId;

    @Value("${bucket.name}")
    private String bucketName;

    private static final String SECURE_DIR_CREATION_ERROR = "Failed to set directory permissions: ";


    /**
     * Constructs a new VisionService with the specified EventBus and SimpMessagingTemplate.
     *
     * @param eventBus              The EventBus to be used by the service.
     * @param simpMessagingTemplate The SimpMessagingTemplate to be used by the service.
     */
    public VisionService(EventBus eventBus, SimpMessagingTemplate simpMessagingTemplate) {
        this.eventBus = eventBus;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.eventBus.subscribe(FaceDetectionMessage.class, this);
        this.storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
    }

    /**
     * Stores the given file in the bucket.
     *
     * @param file the file to store
     */

    public void store(MultipartFile file) {
        try {
            String fileName = UUID.randomUUID() + file.getOriginalFilename();
            BlobId blobId = BlobId.of(bucketName, fileName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
            storage.create(blobInfo, file.getBytes());
        } catch (IOException e) {
            throw new UploadFileToStorageException("Error storing file in bucket.", e);
        }
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
        configureDirectoryPermissions(tempDir);
        return tempDir;
    }
    private void configureDirectoryPermissions(Path tempDir) throws IOException {
        File tempDirFile = tempDir.toFile();

        if (!SystemUtils.IS_OS_UNIX) {
            Set<AclEntryPermission> perms = EnumSet.of(AclEntryPermission.READ_ACL, AclEntryPermission.WRITE_ACL);
            AclEntry.Builder builder = AclEntry.newBuilder();
            builder.setType(AclEntryType.DENY);
            builder.setPrincipal(tempDir.getFileSystem().getUserPrincipalLookupService().lookupPrincipalByName("Everyone"));
            builder.setPermissions(perms);
            AclEntry aclEntry = builder.build();

            Files.getFileAttributeView(tempDir, AclFileAttributeView.class)
                    .setAcl(Collections.singletonList(aclEntry));
        }
        if (!tempDirFile.setReadable(true, true)) {
            throw new IOException(SECURE_DIR_CREATION_ERROR + "Failed to set directory as non-readable for others: " + tempDirFile.getAbsolutePath());
        }
        if (!tempDirFile.setWritable(true, true)) {
            throw new IOException(SECURE_DIR_CREATION_ERROR + "Failed to set directory as non-writable for others: " + tempDirFile.getAbsolutePath());
        }
        if (!tempDirFile.setExecutable(true, true)) {
            throw new IOException(SECURE_DIR_CREATION_ERROR + "Failed to set directory as non-executable for others: " + tempDirFile.getAbsolutePath());
        }
    }

    private File createSecureTempFile(Path tempDir) throws IOException {
        Set<PosixFilePermission> filePerms = PosixFilePermissions.fromString("rw-------");
        return Files.createTempFile(tempDir, "temp", ".jpg", PosixFilePermissions.asFileAttribute(filePerms)).toFile();
    }

    /**
     * Handles the FaceDetectionMessage event.
     *
     * @param event the FaceDetectionMessage event
     */
    @Override
    public void onEvent(FaceDetectionMessage event) {
        logger.debug("Mensagem recebida pelo UploadServiceVision: {} ", event.getFacesData());
        simpMessagingTemplate.convertAndSend("/topic/analysisResult", event.getFacesData());
    }

    /**
     * Detects landmarks in an image.
     *
     * @param filePath The file path of the image
     * @return A list of AnnotateImageResponse objects
     */
    public List<AnnotateImageResponse> detectLandmarks(String filePath) {
        List<AnnotateImageRequest> requests = new ArrayList<>();
        try {
            FileInputStream fileInputStream = new FileInputStream(filePath);
            ByteString imgBytes = ByteString.readFrom(fileInputStream);
            Image img = Image.newBuilder().setContent(imgBytes).build();
            Feature feat = Feature.newBuilder().setType(Feature.Type.LANDMARK_DETECTION).build();
            AnnotateImageRequest request =
                    AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
            fileInputStream.close();
            requests.add(request);
            try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
                BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
                return response.getResponsesList();
            }
        } catch (FileNotFoundException e) {
            throw new PlacesSearchException("File not found: " + filePath, e);
        } catch (IOException e) {
            throw new PlacesSearchException("Error detecting landmarks in image.", e);
        }
    }

    public List<FaceAnnotation> detectFaces(byte[] imageBytes) throws IOException {
        List<AnnotateImageRequest> requests = new ArrayList<>();

        ByteString byteString = ByteString.copyFrom(imageBytes);

        Image img = Image.newBuilder().setContent(byteString).build();
        Feature feat = Feature.newBuilder().setType(Feature.Type.FACE_DETECTION).build();
        AnnotateImageRequest request =
                AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
        requests.add(request);

        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            List<FaceAnnotation> allAnnotations = new ArrayList<>();
            for (AnnotateImageResponse res : responses) {
                allAnnotations.addAll(res.getFaceAnnotationsList());
            }
            if (allAnnotations.isEmpty()) {
                return Collections.emptyList();
            }
        }
        return Collections.emptyList();
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
}
