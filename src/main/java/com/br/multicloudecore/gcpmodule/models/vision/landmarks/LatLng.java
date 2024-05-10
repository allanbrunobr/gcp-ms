package com.br.multicloudecore.gcpmodule.models.vision.landmarks;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents a geographic location on Earth using latitude and longitude coordinates.
 */
@Getter
@Setter
public class LatLng {

  private double latitude;
  private double longitude;

}
