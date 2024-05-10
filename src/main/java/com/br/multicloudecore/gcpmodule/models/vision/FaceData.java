package com.br.multicloudecore.gcpmodule.models.vision;

import lombok.Getter;
import lombok.Setter;

/**
 * Classe que representa os dados de uma face.
 */
@Getter
@Setter
public class FaceData {
  private int id;
  private String joyLikelihood;
  private String angerLikelihood;
  private String sorrowLikelihood;
  private String surpriseLikelihood;

}
