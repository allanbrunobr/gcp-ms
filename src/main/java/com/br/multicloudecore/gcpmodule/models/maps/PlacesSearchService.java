package com.br.multicloudecore.gcpmodule.models.maps;


import com.br.multicloudecore.gcpmodule.exceptions.PlacesSearchException;
import com.br.multicloudecore.gcpmodule.security.service.VaultKeyValueService;
import com.google.maps.GeoApiContext;
import com.google.maps.PlacesApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResult;
import com.google.maps.model.RankBy;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;


/**
 * The PlacesSearchService class is responsible for searching places
 * near the specified latitude and longitude coordinates.
 */
@Service
public class PlacesSearchService {

  @Getter
  private final GeoApiContext context;


    public PlacesSearchService(VaultKeyValueService vaultKeyValueService) {
      String apiKey = vaultKeyValueService.getSecretValue("google_api_key_places");
      this.context = new GeoApiContext.Builder().apiKey(apiKey).build();

    }

  /**
   * Searches for places based on the given latitude and longitude coordinates.
   *
   * @param latitude  the latitude of the location
   * @param longitude the longitude of the location
   * @return a CompletableFuture that will eventually complete with a PlaceSearchResult object.
   */
  public CompletableFuture<PlaceSearchResult> searchPlaces(double latitude, double longitude) {
    LatLng location = new LatLng(latitude, longitude);
    return CompletableFuture.supplyAsync(() -> {
      PlaceSearchResult result = new PlaceSearchResult();
      try {
        result.setRestaurants(getTopPlaces(PlaceType.RESTAURANT, 5, location, "restaurant"));
        result.setAirports(getTopPlaces(PlaceType.AIRPORT, 2, location, "airport"));
        result.setTouristAttractions(getTopPlaces(
                PlaceType.TOURIST_ATTRACTION, 7, location, "tourist"));
        result.setShoppingMalls(getTopPlaces(PlaceType.SHOPPING_MALL, 5, location, "shopping"));
        result.setHotels(getTopPlaces(PlaceType.LODGING, 5, location, "hotel"));
      } catch (Exception e) {
        throw new PlacesSearchException("Error seaching for places near "
                + latitude + ", " + longitude + ": " + e.getMessage(), e);
      }
      return result;
    });
  }

  /**
   * Retrieves the top places of a given place type within a specified
   * limit around a given location.
   *
   * @param placeType the type of place to retrieve
   * @param limit     the maximum number of places to retrieve
   * @param location  the location around which to retrieve the places
   * @return a list of PlaceInfo objects representing the top places
   * @throws Exception if there is an error retrieving the places
   */
  private List<PlaceInfo> getTopPlaces(PlaceType placeType,
                                         int limit,
                                         LatLng location,
                                         String keyword) throws PlacesSearchException {
    try {
      PlacesSearchResult[] places = PlacesApi
              .nearbySearchQuery(context, location)
              .radius(50000)
              .type(placeType)
              .rankby(RankBy.PROMINENCE)
              .keyword(keyword)
              .await()
              .results;

      Arrays.sort(places, Comparator.comparingInt(place ->
            Optional.of(place.userRatingsTotal).orElse(0)));
      Collections.reverse(Arrays.asList(places));
      return createPlaceInfos(Arrays.copyOfRange(places, 0, limit));
    } catch (ApiException | InterruptedException | IOException | IllegalArgumentException e) {
      Thread.currentThread().interrupt();
      throw new PlacesSearchException("Error occurred while searching for top places.", e);
    }
  }

  /**
   * Creates PlaceInfo objects based on the given PlacesSearchResult array.
   *
   * @param placesSearchResults an array of PlacesSearchResult objects
   *                           representing the search results.
   * @return a List of PlaceInfo objects containing information about the places.
   */
  private List<PlaceInfo> createPlaceInfos(PlacesSearchResult[] placesSearchResults) {
    List<PlaceInfo> placeInfos = new ArrayList<>();
    for (PlacesSearchResult searchResult : placesSearchResults) {
      PlaceInfo placeInfo = new PlaceInfo();
      placeInfo.setName(searchResult.name);
      placeInfo.setVicinity(searchResult.vicinity);

      // Crie URLs personalizadas para o site e o Google Maps para cada lugar.
      String placeId = searchResult.placeId;
      String websiteUrl = "https://www.example.com/place/" + placeId; // URL personalizado para o site
      String googleMapsUrl = "https://maps.google.com/?q=" + searchResult.geometry.location.lat + "," + searchResult.geometry.location.lng; // URL personalizado para o Google Maps
      placeInfo.setWebsiteUrl(websiteUrl);
      placeInfo.setGoogleMapsUrl(googleMapsUrl);
      placeInfos.add(placeInfo);
    }

    return placeInfos;
  }
}

