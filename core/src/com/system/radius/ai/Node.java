package com.system.radius.ai;

import java.util.Comparator;

/**
 * Represents a movement for the AI.
 */
public class Node implements Comparable<Node> {

  /**
   * The X coordinate for this node.
   */
  private int x;

  /**
   * The Y coordinate for this node.
   */
  private int y;

  /**
   * The heuristic distance from this node to the goal node.
   */
  private float h;

  /**
   * The cost of movement for this node.
   */
  private float g;

  /**
   * To be used for tracking the path that lead to this node.
   */
  private Node parent;

  public Node(Node parent, int x, int y, float g, float h) {

    this.parent = parent;
    this.x = x;
    this.y = y;

    this.g = g;
    this.h = h;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public float getH() {
    return h;
  }

  public void setH(float h) {
    this.h = h;
  }

  public float getG() {
    return g;
  }

  public void setG(float g) {
    this.g = g;
  }

  public Node getParent() {
    return parent;
  }

  public void setParent(Node parent) {
    this.parent = parent;
  }

  @Override
  public String toString() {

    return "[" + x + ", " + y + "]";
  }

  @Override
  public int compareTo(Node that) {
    return Float.compare(this.h + this.g, that.h + that.g);
  }
}
