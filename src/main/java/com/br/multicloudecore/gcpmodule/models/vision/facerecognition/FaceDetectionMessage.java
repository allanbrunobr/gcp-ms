package com.br.multicloudecore.gcpmodule.models.vision.facerecognition;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;



/**
 * The FaceDetectionMessage class represents a message containing face detection data.
 * It includes the URL of the image and a list of FaceData objects
 * that represent the detected faces.
 */
@Getter
@Setter
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FaceDetectionMessage {
  private String imageUrl;
  private List<FaceData> facesData;
}

