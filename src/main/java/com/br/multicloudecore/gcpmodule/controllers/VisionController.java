package com.br.multicloudecore.gcpmodule.controllers;

import com.br.multicloudecore.gcpmodule.exceptions.PlacesSearchException;
import com.br.multicloudecore.gcpmodule.models.vision.facerecognition.FaceDetectionMessage;
import com.br.multicloudecore.gcpmodule.models.vision.landmarks.LandmarkDetectionMessage;
import com.br.multicloudecore.gcpmodule.service.ai.VisionService;
import com.br.multicloudecore.gcpmodule.utils.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * The VisionController class handles requests related to image processing and detection.
 */
@RestController
@CrossOrigin(origins = "http://localhost:3001")
public class VisionController {

  private final VisionService visionService;

  @Value("${bucket.name}")
  private String bucketName;

  public VisionController(VisionService visionService) {
    this.visionService = visionService;
  }


  @PostMapping("/uploadFileToVisionFace")
  public ResponseEntity<?> handleFileUploadVisionFace(@RequestParam("file") MultipartFile file) {
    try {
      CompletableFuture<FaceDetectionMessage> resultFuture = visionService.processImageAndWaitForResult(file);

      FaceDetectionMessage result = resultFuture.get(35, TimeUnit.SECONDS);
      return ResponseEntity.ok(result);

    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing image: " + e.getMessage());
    }
  }

  /**
     * Handles the file upload for vision landmark detection.
     * Renders the result page with the detected landmarks.
     *
     * @param file The uploaded image file
     * @param model The model object for passing data to the view
     * @return The ModelAndView object for rendering the result page
     */
  @PostMapping("/uploadFileToVisionLandmarks")
  public ModelAndView handleFileUploadVisionLandmarks(@RequestParam("file") MultipartFile file,
                                                        Model model) {
    ModelAndView modelAndView = new ModelAndView("ai/vision/vision-landmark-result");
    try {
      visionService.detectLandmarkImage(file).thenAccept(landmarkDataList ->
          model.addAttribute("landmarkDataList",
                            new LandmarkDetectionMessage("", landmarkDataList))
      ).exceptionally(ex -> {
        throw new PlacesSearchException("An error occurred while processing the request.", ex);
      });
    } catch (PlacesSearchException e) {
      modelAndView.addObject(Constants.ERROR_MESSAGE_ATTRIBUTE, "Error in find landmarks.");
      modelAndView.setViewName(Constants.ERROR_VIEW_NAME);
    } catch (IOException e) {
      modelAndView.addObject(Constants.ERROR_MESSAGE_ATTRIBUTE,
              "Error in processing the image file.");
      modelAndView.setViewName(Constants.ERROR_VIEW_NAME);
    }
    return modelAndView;
  }
}

