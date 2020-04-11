package com.system.radius.utils;

import com.system.radius.ai.Node;
import com.system.radius.objects.board.BoardState;
import com.system.radius.objects.board.WorldConstants;

import java.util.HashMap;
import java.util.Map;

public class Range {

  private BoardState boardState = BoardState.getInstance();

  private Map<Character, Boolean> burnTargets = new HashMap<>();

  private int x;

  private int y;

  private int range;

  private int north;

  private int south;

  private int west;

  private int east;

  public Range(Node position, int range) {

    this(position.getX(), position.getY(), range);
  }

  public Range(int x, int y, int range) {

    this.x = x;
    this.y = y;
    this.range = range;

    updateRange();
  }

  public void updateRange() {

    north = checkObstacle(boardState, x, y + 1, 'w', 1);
    south = checkObstacle(boardState, x, y - 1, 's', 1);
    east = checkObstacle(boardState, x + 1, y, 'd', 1);
    west = checkObstacle(boardState, x - 1, y, 'a', 1);

  }

  public void updateBoard(int[][] board, int cost) {

    for (int i = 0; i <= range; i++) {

      updateCellCost(board, x, y + i, cost, north, i);
      updateCellCost(board, x, y - i, cost, south, i);
      updateCellCost(board, x + i, y, cost, east, i);
      updateCellCost(board, x - i, y, cost, west, i);

    }

  }

  private void updateCellCost(int[][] board, int updatedX, int updatedY, int cost,
                              int directionalRange, int checker) {

    if (checker < directionalRange && board[updatedY][updatedX] >= 0) {
      board[updatedY][updatedX] = cost;
    }

  }

  private int checkObstacle(BoardState boardState, int x, int y, char dir, int counter) {

    if (counter > range) {
      burnTargets.put(dir, false);
      return 1;
    }

    if (boardState.getChar(x, y) == WorldConstants.BOARD_PERMA_BLOCK ||
        boardState.getChar(x, y) == WorldConstants.BOARD_HARD_BLOCK ||
        boardState.getChar(x, y) == WorldConstants.BOARD_TO_DESTROY) {
      burnTargets.put(dir, false);
      return 2;
    }

    if (boardState.getChar(x, y) == WorldConstants.BOARD_SOFT_BLOCK) {
      burnTargets.put(dir, true);
      return 2;
    }

    switch (dir) {
      case 'w':
        return 1 + checkObstacle(boardState, x, y + 1, dir, counter + 1);
      case 's':
        return 1 + checkObstacle(boardState, x, y - 1, dir, counter + 1);
      case 'a':
        return 1 + checkObstacle(boardState, x - 1, y, dir, counter + 1);
      case 'd':
        return 1 + checkObstacle(boardState, x + 1, y, dir, counter + 1);
    }

    return 1;
  }

  public Map<Character, Boolean> getBurnTargets() {
    return burnTargets;
  }

  public int getNorth() {
    return north;
  }

  public int getSouth() {
    return south;
  }

  public int getWest() {
    return west;
  }

  public int getEast() {
    return east;
  }
}
