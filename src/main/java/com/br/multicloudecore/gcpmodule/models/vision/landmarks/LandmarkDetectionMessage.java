package com.br.multicloudecore.gcpmodule.models.vision.landmarks;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.LocationInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


/**
 * Represents a message for landmark detection.
 * This class provides methods to filter and map annotation responses to landmark data objects.
 * The landmark data contains information about the detected landmarks, such as the landmark ID,
 * description, score, bounding polygon, and location coordinates.
 * This class can be used in applications that require the detection and analysis of landmarks
 * in images.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class LandmarkDetectionMessage {

  private String imageUrl;
  private List<LandmarkData> landmarkData;

  /**
   * Represents a message containing landmark detection results for an image.
   * This message encapsulates the image URL and landmark data extracted from
   * the detection responses.
   *
   * @param imageUrl  The URL of the image for which landmark detection was performed.
   * @param responses The list of responses containing landmark detection annotations.
   */
  public LandmarkDetectionMessage(String imageUrl, List<AnnotateImageResponse> responses) {
    this.imageUrl = imageUrl;
    this.landmarkData = mapResponsesToLandmarkData(filterHighestScoreLandmarks(responses));
  }


  /**
   * Filters the list of AnnotateImageResponse objects and returns a new list containing
   * only the response objects with the highest score landmarks.
   *
   * @param responses The list of AnnotateImageResponse objects to filter.
   * @return The filtered list of AnnotateImageResponse objects.
   */
  public List<AnnotateImageResponse> filterHighestScoreLandmarks(
                                        List<AnnotateImageResponse> responses) {
    List<AnnotateImageResponse> filteredResponses = new ArrayList<>();
    for (AnnotateImageResponse response : responses) {
      if (!response.hasError()) {
        double highestScore = Double.MIN_VALUE;
        EntityAnnotation highestScoreLandmark = null;
        for (EntityAnnotation annotation : response.getLandmarkAnnotationsList()) {
          double score = annotation.getScore();
          if (score > highestScore) {
            highestScore = score;
            highestScoreLandmark = annotation;
          }
        }
        if (highestScoreLandmark != null) {
          AnnotateImageResponse filteredResponse = AnnotateImageResponse.newBuilder()
                            .addLandmarkAnnotations(highestScoreLandmark)
                            .build();
          filteredResponses.add(filteredResponse);
        }
      }
    }
    return filteredResponses;
  }

  private List<LandmarkData> mapResponsesToLandmarkData(List<AnnotateImageResponse> responses) {
    List<LandmarkData> landmarkDataList = new ArrayList<>();
    for (AnnotateImageResponse response : responses) {
      if (!response.hasError()) {
        for (EntityAnnotation annotation : response.getLandmarkAnnotationsList()) {
          LocationInfo locationInfo = annotation.getLocationsList().listIterator().next();
          LandmarkData data = new LandmarkData();
          data.setMid(annotation.getMid());
          data.setDescription(annotation.getDescription());
          data.setScore(annotation.getScore());
          data.setBoundingPoly(mapBoundingPoly(annotation.getBoundingPoly()));
          data.setLocations(mapLocations(locationInfo));
          landmarkDataList.add(data);
        }
      }
    }
    return landmarkDataList;
  }
  /**
   * Maps a BoundingPoly object to a BoundingPoly object.
   *
   * @param boundingPoly The BoundingPoly object to map.
   * @return The mapped BoundingPoly object.
   */

  private BoundingPoly mapBoundingPoly(com.google.cloud.vision.v1.BoundingPoly boundingPoly) {
    BoundingPoly poly = new BoundingPoly();
    poly.setVertices(mapVertices(boundingPoly.getVerticesList()));
    poly.setNormalizedVertices(mapNormalizedVertices(boundingPoly.getNormalizedVerticesList()));
    return poly;
  }

  private List<Vertex> mapVertices(List<com.google.cloud.vision.v1.Vertex> verticesList) {
    List<Vertex> vertices = new ArrayList<>();
    for (com.google.cloud.vision.v1.Vertex vertex : verticesList) {
      Vertex v = new Vertex();
      v.setVertexX(vertex.getX());
      v.setVertexY(vertex.getY());
      vertices.add(v);
    }
    return vertices;
  }

  private List<NormalizedVertex> mapNormalizedVertices(
                                    List<com.google.cloud.vision.v1.NormalizedVertex>
                                                                    normalizedVerticesList) {
    List<NormalizedVertex> normalizedVertices = new ArrayList<>();
    for (com.google.cloud.vision.v1.NormalizedVertex vertex : normalizedVerticesList) {
      NormalizedVertex v = new NormalizedVertex();
      v.setNormalizedVertexX(vertex.getX());
      v.setNormalizedVertexY(vertex.getY());
      normalizedVertices.add(v);
    }
    return normalizedVertices;
  }

  private Locations mapLocations(LocationInfo locationInfo) {
    Locations locations = new Locations();
    locations.setLatLng(mapLatLng(locationInfo.getLatLng()));
    return locations;
  }

  private LatLng mapLatLng(com.google.type.LatLng latLng) {
    LatLng coordinates = new LatLng();
    coordinates.setLatitude(latLng.getLatitude());
    coordinates.setLongitude(latLng.getLongitude());
    return coordinates;
  }
}

