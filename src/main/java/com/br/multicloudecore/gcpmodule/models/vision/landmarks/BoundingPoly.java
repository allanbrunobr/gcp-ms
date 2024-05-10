package com.br.multicloudecore.gcpmodule.models.vision.landmarks;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * BoundingPoly represents a polygon with the bounding coordinates.
 */
@Getter
@Setter
public class BoundingPoly {
  private List<Vertex> vertices;
  private List<NormalizedVertex> normalizedVertices;
}
