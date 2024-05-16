package com.br.multicloudecore.gcpmodule.models.vision.facerecognition;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Classe que representa os dados de uma face.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class FaceData {
  private int id;
  private String joyLikelihood;
  private String angerLikelihood;
  private String sorrowLikelihood;
  private String surpriseLikelihood;
}
