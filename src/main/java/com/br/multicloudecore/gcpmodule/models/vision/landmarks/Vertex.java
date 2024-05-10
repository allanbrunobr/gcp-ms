package com.br.multicloudecore.gcpmodule.models.vision.landmarks;

import lombok.Getter;
import lombok.Setter;

/**

 The `Vertex` class represents a vertex in a two-dimensional space.

 - The class has two private instance variables: `x` and `y`,
 representing the x and y coordinates of the vertex.
 - The class provides getter and setter methods for accessing and modifying the coordinates.

 Example Usage:
 ```
 Vertex vertex = new Vertex();
 vertex.setX(10);
 vertex.setY(20);
 int x = vertex.getX(); // returns 10
 int y = vertex.getY(); // returns 20
 ```

 */
@Getter
@Setter
public class Vertex {

  private int vertexX;
  private int vertexY;

}
