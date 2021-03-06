package com.system.radius.utils;

import com.system.radius.ai.Node;
import com.system.radius.objects.AbstractBomberObject;
import com.system.radius.objects.board.BoardState;
import com.system.radius.objects.board.WorldConstants;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * An A* implementation.
 *
 * @author Radius Sairo
 */
public class AStarUtils {

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

  public AStarUtils() {
    // Empty constructor.
  }

  public List<Node> findShortestPath(int[][] board, AbstractBomberObject source,
                                     AbstractBomberObject target) {

    return findPathInternal(board, source, target);
  }

  public List<Node> findShortestPath(int[][] board, Node source, Node target) {

    return findPathInternal(board, source.getX(), source.getY(), target.getX(),
        target.getY());
  }

  /**
   * Blindly search for available spaces in the vicinity of the source.
   *
   * @param board  - The board that serves as the maze to be traversed.
   * @param source - The source node.
   * @param depth  - The depth of searching level.
   * @return The list of available spaces around the source.
   */
  public List<Node> searchSpaces(int[][] board, Node source, int depth) {

    return findSpacesInternal(board, source.getX(), source.getY(), depth);
  }

  /**
   * Search for empty spaces around the given source coordinates.
   *
   * @param board   - The board that serves as the maze to be traversed.
   * @param sourceX - The X-coordinate of the source.
   * @param sourceY - The Y-coordinate of the source.
   * @param depth   - The depth of searching level.
   * @return The list of available spaces in around the source.
   */
  private List<Node> findSpacesInternal(int[][] board, int sourceX, int sourceY, int depth) {

    openList.clear();
    closedList.clear();

    maze = board;

    Node now = new Node(null, sourceX, sourceY, 0, 0);
    openList.add(now);
    for (int i = 0; openList.size() > 0; i++) {
      now = this.openList.remove(0);

      int x = now.getX();
      int y = now.getY();

      if (i >= depth) {
        // If the depth level is reached, break from the search.
        break;
      }

      closedList.add(now);

//      if (board[y][x] >= 0) {
        addChildrenToOpenList(now);
//      }
    }

    return new ArrayList<>(closedList);
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

      int x = now.getX();
      int y = now.getY();

      if (x == targetX && y == targetY) {
        return fixPath(now);
      }

      closedList.add(now);

//      if (board[y][x] >= 0) {
        // Do not expand the node if it is not passable.
        addChildrenToOpenList(now);
//      }

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

    BoardState boardState = BoardState.getInstance();

    for (int y = -1; y <= 1; y++) {

      int childY = parentY + y;
      if (childY < 0 || childY >= maze.length) {
        continue;
      }

      for (int x = -1; x <= 1; x++) {

        int childX = parentX + x;
        if ((x != 0 && y != 0) || childX < 0 || childX >= maze[0].length ||
            (maze[childY][childX] < 0)) {
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

  /**
   * Prints mazes side-by-side. Mazes to be printed are supposed to be of same sizes.
   *
   * @param mazes - The mazes to be printed.
   */
  public static void printMaze(int[][]... mazes) {

    for (int y = mazes[0].length - 1; y >= 0; y--) {
      // Loop for printing details per row.
      for (int z = 0; z < mazes.length; z++) {
        // Loop for printing mazes.

        for (int x = 0; x < mazes[z][y].length; x++) {
          // Loop for printing mazes per column.
          System.out.print(getCharRep(mazes[z][y][x]));
        }

        if (z + 1 < mazes.length) {
          System.out.print(" |  ");
        }

      }
      System.out.println();
    }

  }

  private static String getCharRep(int value) {

    String string;
    switch (value) {
      case -2:
        string = "o ";
        break;
      case -1:
        string = "x ";
        break;
      case 0:
        string = "! ";
        break;
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
        string = "  ";
        break;
      default:
        string = "# ";
    }

    return string;
  }

}
