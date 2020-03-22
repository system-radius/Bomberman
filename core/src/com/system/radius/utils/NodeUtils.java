package com.system.radius.utils;

import com.system.radius.ai.Node;
import com.system.radius.objects.AbstractBomberObject;
import com.system.radius.objects.board.BoardState;

public class NodeUtils {

  private static BoardState boardState = BoardState.getInstance();

  private NodeUtils() {
  }

  /**
   * Creates a node based on an object.
   *
   * @param object - The object whose coordinates will be the basis for the node.
   * @return A node.
   */
  public static Node createNode(AbstractBomberObject object) {

    int exactX = boardState.getExactX(object);
    int exactY = boardState.getExactY(object);

    return createNode(exactX, exactY);
  }

  /**
   * Creates a node based on the coordinates.
   *
   * @param x - The X coordinate.
   * @param y - The Y coordinate.
   * @return A node.
   */
  public static Node createNode(int x, int y) {

    return new Node(null, x, y, 0, 0);
  }

}
