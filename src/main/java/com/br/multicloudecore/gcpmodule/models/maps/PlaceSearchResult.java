package com.br.multicloudecore.gcpmodule.models.maps;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * This class represents the result of a place search, containing lists
 * of different types of places.
 */
@Getter
@Setter
@NoArgsConstructor
public class PlaceSearchResult {

  private List<PlaceInfo> restaurants;
  private List<PlaceInfo> airports;
  private List<PlaceInfo> touristAttractions;
  private List<PlaceInfo> shoppingMalls;
  private List<PlaceInfo> hotels;
}
