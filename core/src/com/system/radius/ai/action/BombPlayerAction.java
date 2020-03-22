package com.system.radius.ai.action;

import com.badlogic.gdx.utils.Logger;
import com.system.radius.ai.Node;
import com.system.radius.objects.board.WorldConstants;
import com.system.radius.objects.players.Player;
import com.system.radius.utils.AStarUtils;
import com.system.radius.utils.BombUtils;

import java.util.ArrayList;
import java.util.List;

public class BombPlayerAction extends Action {

  private static final Logger LOGGER = new Logger(BombPlayerAction.class.getSimpleName());

  public BombPlayerAction(Player player) {
    super(player);
    chainedAction = new DefenseAction(player);
  }

  private void processPath() {

    int size = actionPath.size();
    if (size == 0) {
      return;
    }

    int range = player.getFirePower();

    // Find a path to the player that is straight.
    List<Node> straightPath = new ArrayList<>();

    // Remove the last two paths to determine the direction.
    Node finalNode = actionPath.get(size - 1);

    straightPath.add(finalNode);

    if (range == 1 || size == 1) {
      // Special behavior if the AI only has 1 range.
      actionPath.removeAll(straightPath);
      return;
    }

    int exactX = boardState.getExactX(player);
    int exactY = boardState.getExactY(player);
    actionPath.add(0, new Node(null, exactX, exactY, 0, 0));

    size = actionPath.size();

    Node lastNodeChecked = actionPath.get(size - 2);
    straightPath.add(0, lastNodeChecked);

    int finalNodeX = finalNode.getX();
    int finalNodeY = finalNode.getY();

    int lastNodeX = lastNodeChecked.getX();
    int lastNodeY = lastNodeChecked.getY();

    boolean horizontal = finalNodeX != lastNodeX;
    boolean vertical = finalNodeY != lastNodeY;

    // Start from the third node to the last, since the first two were already checked.
    for (int i = size - 3; i >= 0; i--) {

      Node node = actionPath.get(i);

      if (horizontal) {
        int nodeX = node.getX();

        if (nodeX == lastNodeX) {
//          straightPath.remove(straightPath.size() - 1);
          straightPath.remove(0);
          break;
        }

        lastNodeX = nodeX;

      } else if (vertical) {
        int nodeY = node.getY();

        if (nodeY == lastNodeY) {
//          straightPath.remove(straightPath.size() - 1);
          straightPath.remove(0);
          break;
        }

        lastNodeY = nodeY;

      }
      straightPath.add(0, node);

    }

    if (straightPath.size() > range) {
      int spSize = straightPath.size();
      straightPath = straightPath.subList(spSize - range, spSize);
    }

    actionPath.remove(0);
    actionPath.removeAll(straightPath);
  }

  @Override
  public boolean isDoable(int[][] parentBoard, Node source) {

    // Create the board based on the current events.
    hypotheticalBoard = constructBoardRep();

    // Adapt the values from the parent board for the projection of future behavior.
    adaptBoard(parentBoard);

    if (!isTargetAcquired()) {
      return false;
    }

    if (source == null) {
      int playerX = boardState.getExactX(player);
      int playerY = boardState.getExactY(player);
      source = new Node(null, playerX, playerY, 0, 0);
    }

    actionPath = AStarUtils.findShortestPath(hypotheticalBoard, source, target);

    // This means that there is a definite path from the AI to the enemy.
    boolean canAct = actionPath != null && actionPath.size() != 0;
    if (canAct) {
      complete = false;
    }

    if (actionPath == null) {
      // There is no way to reach the target. This action cannot be done.
      return false;
    }
    // Path processing removes some part of the path acquired, which is why the size is saved.
    processPath();
    if (actionPath.size() > 0) {
      Node lastNode = actionPath.get(actionPath.size() - 1);
      BombUtils.updateBoardCost(hypotheticalBoard, lastNode, player);
      chainedAction.isDoable(hypotheticalBoard, lastNode);
    }

    return canAct;
  }

  @Override
  public boolean isTargetAcquired() {

//    int detectionRange = 2 * (int) player.getSpeedLevel();
    int detectionRange = 50 * (int) player.getSpeedLevel();
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

          target = new Node(null, enemyX, enemyY, 0, 0);
          diffX = tempX;
          diffY = tempY;

        }
      }
    }

    return target != null;
  }

  @Override
  public void act() {

    if (!isDoable()) {
      return;
    }

    LOGGER.info("Placing bomb!");
    System.out.println("Placing bomb!");
    player.plantBomb();

    complete = true;

  }

}
