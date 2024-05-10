package com.br.multicloudecore.gcpmodule.models.vision.landmarks;


import lombok.Getter;
import lombok.Setter;

/**
 * Landmark data.
 */
@Getter
@Setter
public class LandmarkData {
  private String mid;
  private String description;
  private double score;
  private BoundingPoly boundingPoly;
  private Locations locations;
}

