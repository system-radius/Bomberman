package com.system.radius.ai.action;

import com.system.radius.ai.Node;
import com.system.radius.objects.AbstractBomberObject;
import com.system.radius.objects.board.BoardState;
import com.system.radius.objects.board.WorldConstants;
import com.system.radius.objects.bombs.Bomb;
import com.system.radius.objects.players.Player;

import java.util.List;

public abstract class Action {

  protected List<Node> actionPath;

  protected BoardState boardState = BoardState.getInstance();

  protected Player player;

  protected AbstractBomberObject target;

  public Action(Player player) {
    this.player = player;
  }

  /**
   * Checks if the current action is doable. This will also find the actionPath.
   *
   * @return {@code true} if the action can be done; {@code false} otherwise.
   */
  public abstract boolean isDoable();

  /**
   * Attempts to retrieve the target for this action.
   *
   * @return {@code true} if the target is acquired; {@code false} otherwise.
   */
  public abstract boolean isTargetAcquired();

  protected int[][] constructBoardRep() {

    int worldHeight = (int) WorldConstants.WORLD_HEIGHT;
    int worldWidth = (int) WorldConstants.WORLD_WIDTH;

    int[][] intBoard = new int[worldHeight][worldWidth];
    char[][] charBoard = boardState.getBoardRep();

    int speedLevel = (int) player.getSpeedLevel();
    int cost = (int) Player.SPEED_COUNTER - speedLevel;

    int multiSpeedLevel = speedLevel * 10;

    // Retrieve the base board representation.
    for (int i = 0; i < worldHeight; i++) {
      for (int j = 0; j < worldWidth; j++) {

        if (charBoard[i][j] != WorldConstants.BOARD_EMPTY) {
          // Mark everything else as unpassable.
          intBoard[i][j] = -1;
        } else {
          // Empty spaces cost movement depending on the character's speed level.
          intBoard[i][j] = cost;
        }

      }
    }

    int playerX = boardState.getExactX(player);
    int playerY = boardState.getExactY(player);

    // Apply the bomb's fire ranges' cost.
    for (int i = -multiSpeedLevel; i <= multiSpeedLevel; i++) {
      int y = playerY + i;

      // Out-of-bounds checking
      if (y < 0 || y >= worldHeight) {
        continue;
      }

      for (int j = -multiSpeedLevel; j <= multiSpeedLevel; j++) {

        int x = playerX + j;

        // Out-of-bounds + object of interest checking.
        if (x < 0 || x >= worldWidth || charBoard[playerY + i][playerX + j] != WorldConstants.BOARD_BOMB) {
          continue;
        }

        // The object marked by (x, y) is sure to be a bomb instance.
        Bomb bomb = (Bomb) boardState.getObject(x, y);
        bomb.updateBoardCost(intBoard, speedLevel);

      }
    }

    return intBoard;
  }

  public List<Node> getActionPath() {
    return actionPath;
  }

}
