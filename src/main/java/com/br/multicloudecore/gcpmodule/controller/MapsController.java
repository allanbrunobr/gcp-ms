package com.br.multicloudecore.gcpmodule.controller;

import com.br.multicloudecore.gcpmodule.exceptions.PlacesSearchException;
import com.br.multicloudecore.gcpmodule.models.maps.PlaceSearchResult;
import com.br.multicloudecore.gcpmodule.models.maps.PlacesSearchService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.concurrent.ExecutionException;

/**
 * The MapsController class is responsible for handling requests related to maps.
 * It provides methods for searching places near specified coordinates.
 */
@RestController
public class MapsController {

  private final PlacesSearchService placesSearchService;

  /**
   * Constructor for MapsController.
   *
   * @param placesSearchService The service for searching places.
   */
  public MapsController(PlacesSearchService placesSearchService) {
    this.placesSearchService = placesSearchService;
  }

  /**
   * Searches for places based on the given latitude and longitude coordinates.
   *
   * @param latitude  the latitude of the location
   * @param longitude the longitude of the location
   * @param model     the Model object for rendering the view
   * @return a ModelAndView object representing the view for displaying the search results
   */
  @PostMapping("/maps")
  public ModelAndView handleCoordinates(@RequestParam double latitude,
                                        @RequestParam double longitude,
                                        Model model) {
    ModelAndView modelAndView = new ModelAndView("maps/maps-result");
    try {
      PlaceSearchResult placeSearchResult = placesSearchService
                                        .searchPlaces(latitude, longitude).get();
      model.addAttribute("placeSearchResult", placeSearchResult);
    } catch (InterruptedException | ExecutionException e) {
      Thread.currentThread().interrupt();
      throw new PlacesSearchException("Error occurred during place search.", e);
    }
    return modelAndView;
  }
}
