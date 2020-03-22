package com.system.radius.utils;

import com.system.radius.ai.Node;
import com.system.radius.objects.board.WorldConstants;
import com.system.radius.objects.players.Player;

public class BombUtils {

  private BombUtils() {
  }

  public static void updateBoardCost(int[][] board, Node position, Player player) {

    int cost = WorldConstants.FIRE_PATH_COST - (int) (player.getSpeedLevel() * 10);
    updateBoardCost(board, position.getX(), position.getY(), player.getFirePower(), cost);

  }

  /**
   * Updates the path cost for the board. The update is such that a bomb is placed on the
   * indicated coordinates.
   *
   * @param board     - The board with an integer representation of the movement costs.
   * @param x         - The X coordinate for the theoretical bomb to be placed.
   * @param y         - The Y coordinate for the theoretical bomb to be placed.
   * @param fireRange - The range of the theoretical bomb.
   * @param cost      - The cost of moving through the fire path.
   */
  public static void updateBoardCost(int[][] board, int x, int y, int fireRange, int cost) {

    Range range = new Range(x, y, fireRange);
    range.updateBoard(board, cost);

  }

}
