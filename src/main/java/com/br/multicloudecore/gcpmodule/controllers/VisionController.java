package com.br.multicloudecore.gcpmodule.controllers;

import com.br.multicloudecore.gcpmodule.config.Constants;
import com.br.multicloudecore.gcpmodule.exceptions.PlacesSearchException;
import com.br.multicloudecore.gcpmodule.models.vision.landmarks.LandmarkDetectionMessage;
import com.br.multicloudecore.gcpmodule.service.ai.VisionService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

/**
 * The VisionController class handles requests related to image processing and detection.
 */
@RestController
public class VisionController {

  private final VisionService visionService;

  /**
   * Constructs a new VisionController with the specified VisionService.
   *
   * @param visionService The VisionService to be used by the controller.
   */
  public VisionController(VisionService visionService) {
    this.visionService = visionService;
  }

  /**
    * Handles the file upload for vision face detection.
     * Stores the uploaded file in a Google Cloud Storage bucket and
    * returns a ModelAndView object for rendering the result page.
    *
     * @param file The uploaded file
     * @param redirectAttributes The redirect attributes for flash messages
     * @return The ModelAndView object for rendering the result page
     */
  @PostMapping("/uploadFileToVisionFace")
  public ModelAndView handleFileUploadVisionFace(@RequestParam("file") MultipartFile file,
                                                   RedirectAttributes redirectAttributes) {
    String message;
    ModelAndView modelAndView = new ModelAndView("ai/vision/vision-face-result");
    try {
      visionService.store(file);
      byte[] imageBytes = file.getBytes();

      String base64EncodedImage = visionService.writeWithFaces(imageBytes, visionService.detectFaces(imageBytes));

      message = "Upload do arquivo realizado com sucesso! " + file.getOriginalFilename() + "!";
      modelAndView.addObject("base64EncodedImage", base64EncodedImage);
      redirectAttributes.addFlashAttribute("message", message);


    } catch (Exception e) {
      message = "Falha ao fazer upload do arquivo " + file.getOriginalFilename() + "!";
      redirectAttributes.addFlashAttribute(Constants.ERROR_MESSAGE_ATTRIBUTE, message);
    }
    return modelAndView;
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


