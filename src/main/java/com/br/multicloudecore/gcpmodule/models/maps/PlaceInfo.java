package com.br.multicloudecore.gcpmodule.models.maps;

import lombok.Getter;
import lombok.Setter;

/**
 * This class represents information about a place.
 * It stores the name, vicinity, website URL, and Google Maps URL of the place.
 */
@Getter
@Setter
public class PlaceInfo {

  private String name;
  private String vicinity;
  private String websiteUrl;
  private String googleMapsUrl;

}
