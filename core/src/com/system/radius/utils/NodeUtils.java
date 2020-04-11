package com.system.radius.utils;

import com.system.radius.ai.Node;
import com.system.radius.objects.AbstractBomberObject;
import com.system.radius.objects.board.BoardState;

import java.util.ArrayList;
import java.util.List;

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

  /**
   * Remove the duplicates from the base list. The indicated duplicates are from another list.
   *
   * @param baseList    - The base list from which the duplicates are to be removed.
   * @param toBeRemoved - The list containing the items to be removed.
   * @return The new list with the duplicates removed.
   */
  public static List<Node> removeDuplicates(List<Node> baseList, List<Node> toBeRemoved) {

    List<Node> nodeList = new ArrayList<>();

    for (Node node : baseList) {

      if (findInList(toBeRemoved, node)) {
        // Skip adding the node that was found in the list that is to be removed.
        continue;
      }

      // Add the node as it does not have a duplicate in the to be removed list.
      nodeList.add(node);
    }

    return nodeList;
  }

  public static boolean findInList(List<Node> list, Node node) {

    String nodeString = node.toString();
    for (Node inList : list) {

      String temp = inList.toString();
      if (temp.equals(nodeString)) {
        // If the node has an equal string, then it exists in the list.
        return true;
      }

    }

    return false;
  }

}
