package com.system.radius.utils;

import com.system.radius.ai.Node;
import com.system.radius.objects.AbstractBomberObject;
import com.system.radius.objects.board.BoardState;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * An A* implementation.
 *
 * @author Radius Sairo
 */
public class AStarUtils {

  private static AStarUtils instance;

  /**
   * The open list, contains all of the nodes to be explored.
   */
  private final List<Node> openList = new ArrayList<>();

  /**
   * The closed list, contains all of the nodes that are already explored.
   */
  private final List<Node> closedList = new ArrayList<>();

  /**
   * The representation of the board through which the search will travel.
   */
  private int[][] maze;

  /**
   * The target X coordinate.
   */
  private int targetX;

  /**
   * The target Y coordinate.
   */
  private int targetY;

  private AStarUtils() {

  }

  public static List<Node> findShortestPath(int[][] board, AbstractBomberObject source,
                                            AbstractBomberObject target) {

    if (instance == null) {
      instance = new AStarUtils();
    }

    return instance.findPathInternal(board, source, target);
  }

  public static List<Node> findShortestPath(int[][] board, Node source, Node target) {

    if (instance == null) {
      instance = new AStarUtils();
    }

    return instance.findPathInternal(board, source.getX(), source.getY(), target.getX(),
        target.getY());
  }

  private List<Node> findPathInternal(int[][] board, AbstractBomberObject source,
                                      AbstractBomberObject target) {

    BoardState boardState = BoardState.getInstance();
    return findPathInternal(board, boardState.getExactX(source), boardState.getExactY(source),
        boardState.getExactX(target), boardState.getExactY(target));
  }

  private List<Node> findPathInternal(int[][] board, int sourceX, int sourceY, int targetX,
                                      int targetY) {

    // Reset the status for the lists.
    openList.clear();
    closedList.clear();

    maze = board;

    Node now = new Node(null, sourceX, sourceY, 0, 0);
    // this.sourceX = sourceX;
    // this.sourceY = sourceY;
    this.targetX = targetX;
    this.targetY = targetY;

    for (openList.add(now); openList.size() > 0; ) {

      now = this.openList.remove(0);

      if (now.getX() == targetX && now.getY() == targetY) {
        return fixPath(now);
      }

      closedList.add(now);
      addChildrenToOpenList(now);

    }

    // The moves are exhausted, and there is no way to reach the target.
    return null;
  }

  private List<Node> fixPath(Node node) {

    List<Node> path = new ArrayList<>();
    path.add(node);
    for (Node parent = node.getParent(); parent != null; parent = parent.getParent()) {

      if (parent.getParent() == null) {
        break;
      }
      path.add(0, parent);
    }

    // The target is reachable.
    return path;
  }

  private void addChildrenToOpenList(Node parent) {

    int parentX = parent.getX();
    int parentY = parent.getY();

    float parentG = parent.getG();

    for (int y = -1; y <= 1; y++) {

      int childY = parentY + y;
      if (childY < 0 || childY >= maze.length) {
        continue;
      }

      for (int x = -1; x <= 1; x++) {

        int childX = parentX + x;
        if ((x != 0 && y != 0) || childX < 0 || childX >= maze[0].length || maze[childY][childX] == -1) {
          continue;
        }

        Node child = new Node(parent, childX, childY, parentG + 1 + maze[childY][childX],
            computeHeuristic(childX, childY));

        if (!(findInList(openList, child) || findInList(closedList, child))) {
          openList.add(child);
        }
      }
    }

    Collections.sort(openList);

  }

  private float computeHeuristic(int x, int y) {

    return (float) Math.abs(x - targetX) + Math.abs(y - targetY);
  }

  private boolean findInList(List<Node> nodes, Node node) {

    int x = node.getX();
    int y = node.getY();

    for (Node inList : nodes) {
      if (x == inList.getX() && y == inList.getY()) {
        return true;
      }
    }

    return false;
  }

  private void printMaze() {

    for (int i = 0; i < maze.length; i++) {
      for (int j = 0; j < maze[i].length; j++) {

        switch (maze[i][j]) {
          case -1:
            System.out.print("x ");
            break;
          case 1:
          case 2:
          case 3:
          case 4:
          case 5:
            System.out.print("  ");
            break;
          default:
            System.out.print("? ");
        }

      }

      System.out.println();
    }

  }

}
