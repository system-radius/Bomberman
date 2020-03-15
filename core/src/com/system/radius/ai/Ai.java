package com.system.radius.ai;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.system.radius.ai.action.Action;
import com.system.radius.ai.action.BombPlayerAction;
import com.system.radius.objects.board.BoardState;
import com.system.radius.objects.board.WorldConstants;
import com.system.radius.objects.bombs.Bomb;
import com.system.radius.objects.players.Player;
import com.system.radius.utils.AStarUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains the mechanism for the artificial intelligence that operates a player character.
 */
public class Ai {

  private static final long PATH_CHANGE_INTERVAL = 100;

  private List<Action> actionList;

  private List<Node> currentPath = new ArrayList<>();

  private BoardState boardState;

  private Player player;

  private long lastPathChange = 0;

  private boolean consumedMove = true;

  private Node currentMove;

  private Action currentAction;

  public Ai(Player player) {

    this.boardState = BoardState.getInstance();
    this.player = player;

    initializeActions();

    player.setSpeed(1);
  }

  private void initializeActions() {

    actionList = new ArrayList<>();

    actionList.add(new BombPlayerAction(player));
  }

  private int[][] constructBoardRep() {

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

  private void findPathToEnemy(Player enemy) {

    currentPath = AStarUtils.findShortestPath(constructBoardRep(), player, enemy);
    consumedMove = true;

  }

  private boolean detectPlayer(int detectionRange) {

    int playerX = boardState.getExactX(player);
    int playerY = boardState.getExactY(player);

    int playerMinX = playerX - detectionRange;
    int playerMaxX = playerX + detectionRange;

    int playerMinY = playerY - detectionRange;
    int playerMaxY = playerY + detectionRange;

    Player trackedEnemy = null;

    int diffX = Integer.MAX_VALUE;
    int diffY = Integer.MAX_VALUE;

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

          trackedEnemy = enemy;
          diffX = tempX;
          diffY = tempY;

        }
      }

    }

    return trackedEnemy != null;
  }

  /**
   * What does the AI intend to do?
   */
  private void decide() {

    Action todo = null;

    // At least one action should be doable.
    for (Action action : actionList) {
      if (action.isDoable()) {
        todo = action;
        break;
      }
    }

    if (todo != null) {
      currentAction = todo;
      currentPath = currentAction.getActionPath();
    } else {
      currentPath.clear();
    }

    // This is going to be changed for a more dynamic approach.
//    int detectionRange = 5;
//
//    if (detectPlayer(detectionRange)) {
//      findPathToEnemy(trackedEnemy);
////      return;
//    }
//
//    int playerX = boardState.getExactX(player);
//    int playerY = boardState.getExactY(player);
//
//    for (int i = -detectionRange; i <= detectionRange; i++) {
//
//      int y = playerY + i;
//      if (y < 0 || y >= WorldConstants.WORLD_HEIGHT) {
//        continue;
//      }
//
//      for (int j = -detectionRange; j <= detectionRange; j++) {
//
//        int x = playerX + j;
//        if (x < 0 || x >= WorldConstants.WORLD_WIDTH) {
//          continue;
//        }
//
//      }
//
//    }

  }

  private void move() {

    if (consumedMove) {
      currentMove = currentPath.size() > 0 ? currentPath.remove(0) : null;
      consumedMove = false;
    }

    if (currentMove == null) {

      System.out.println("No movement available!");
      this.player.setVelX(0);
      this.player.setVelY(0);
      consumedMove = true;

      return;
    }

    float targetX = currentMove.getX() * WorldConstants.WORLD_SCALE;
    float targetY = currentMove.getY() * WorldConstants.WORLD_SCALE;

    float playerX = player.getX();
    float playerY = player.getY();

    System.out.println("[" + targetX + ", " + targetY + "] --> [" + playerX + ", " + playerY + "]");

    float speed = player.getSpeed();
    int speedLevel = (int) player.getSpeedLevel() / 2;

    float velX = (int) playerX > targetX + speedLevel ? -speed :
        (int) playerX < targetX - speedLevel ? speed : 0;
    float velY = (int) playerY > targetY + speedLevel ? -speed :
        (int) playerY < targetY - speedLevel ? speed : 0;

    if (velX == 0 && velY == 0) {
      consumedMove = true;
    }

    this.player.setVelX(velX);
    this.player.setVelY(velY);

  }

  public Player getPlayer() {
    return player;
  }

  public void update(float delta) {

    if (System.currentTimeMillis() - lastPathChange >= PATH_CHANGE_INTERVAL) {
      lastPathChange = System.currentTimeMillis();
      decide();
    }

    move();
    player.update(delta);
  }

  public void draw(Batch batch, float delta) {
    player.draw(batch, delta);
  }

}
