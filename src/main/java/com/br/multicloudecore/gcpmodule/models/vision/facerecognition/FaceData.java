package com.br.multicloudecore.gcpmodule.models.vision.facerecognition;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

/**
 * Classe que representa os dados de uma face.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class FaceData {
  private String joyLikelihood;
  private String angerLikelihood;
  private String sorrowLikelihood;
  private String surpriseLikelihood;
  private float detectionConfidence;
  private BoundingPoly boundingPoly;


  @Getter
  @Setter
  public static class BoundingPoly {
    private Vertex[] vertices;

    @Getter
    @Setter
    public static class Vertex {
      private float x;
      private float y;
    }
  }
}
