package com.system.radius.ai.action;

import com.system.radius.objects.board.WorldConstants;
import com.system.radius.objects.players.Player;
import com.system.radius.utils.AStarUtils;

import java.util.List;

public class BombPlayerAction extends Action {

  public BombPlayerAction(Player player) {
    super(player);
  }

  @Override
  public boolean isDoable() {

    if (!isTargetAcquired()) {
      return false;
    }

    actionPath = AStarUtils.findShortestPath(constructBoardRep(), player, target);

    return actionPath.size() != 0;
  }

  @Override
  public boolean isTargetAcquired() {

    int detectionRange = WorldConstants.DETECTION_RANGE * (int) player.getSpeedLevel();
    int playerX = boardState.getExactX(player);
    int playerY = boardState.getExactY(player);

    int playerMinX = playerX - detectionRange;
    int playerMaxX = playerX + detectionRange;

    int playerMinY = playerY - detectionRange;
    int playerMaxY = playerY + detectionRange;

    int diffX = Integer.MAX_VALUE;
    int diffY = Integer.MAX_VALUE;

    target = null;
    List<Player> players = boardState.getPlayers();
    for (Player enemy : players) {

      if (player.equals(enemy)) {
        continue;
      }

      int enemyX = boardState.getExactX(enemy);
      int enemyY = boardState.getExactY(enemy);

      if (enemyX >= playerMinX && enemyX <= playerMaxX && enemyY >= playerMinY && enemyY <= playerMaxY) {

        int tempX = Math.abs(enemyX - playerX);
        int tempY = Math.abs(enemyY - playerY);

        if (tempX < diffX && tempY < diffY) {

          target = enemy;
          diffX = tempX;
          diffY = tempY;

        }
      }
    }

    return target != null;
  }
}
