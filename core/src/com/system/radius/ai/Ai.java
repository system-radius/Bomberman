package com.system.radius.ai;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Logger;
import com.system.radius.ai.action.Action;
import com.system.radius.ai.action.BombPlayerAction;
import com.system.radius.ai.action.DefenseAction;
import com.system.radius.objects.board.BoardState;
import com.system.radius.objects.board.WorldConstants;
import com.system.radius.objects.players.Player;
import com.system.radius.utils.NodeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains the mechanism for the artificial intelligence that operates a player character.
 */
public class Ai {

  private static final Logger LOGGER = new Logger(Ai.class.getSimpleName());

  private static final long PATH_CHANGE_INTERVAL = 250;

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

    player.setSpeed(3);
  }

  private void initializeActions() {

    actionList = new ArrayList<>();

    actionList.add(new BombPlayerAction(player));
    actionList.add(new DefenseAction(player));
  }

  private void chooseAction(boolean chained) {

    Action todo = null;

    // At least one action should be doable.
    for (Action action : actionList) {

      boolean doable = chained ? action.isDoable(currentAction.getHypotheticalBoard()) :
          action.isDoable();

      if (doable) {
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

  }

  /**
   * What does the AI intend to do?
   */
  private void decide() {

    if (currentAction != null && currentAction.isDoable()) {
      currentPath = currentAction.getActionPath();

      if (currentAction instanceof DefenseAction && currentAction.isComplete()) {
        chooseAction(true);
      }

      return;
    }

    chooseAction(false);

  }

  private void move() {

    if (consumedMove) {
      currentMove = currentPath != null && currentPath.size() > 0 ? currentPath.remove(0) : null;
      consumedMove = false;
    }

    if (currentMove == null) {

      // Null currentMove means that the action path is consumed, signifying that the AI can act
      // based on the specified action.
      LOGGER.info("No movement available!");
//      System.out.println("No movement available!");
      this.player.setVelX(0);
      this.player.setVelY(0);
      consumedMove = true;

      if (currentAction != null) {
        currentAction.act();

        if (currentAction.isComplete()) {
          // If the action is complete, the AI may proceed with the chained action.
          currentAction = currentAction.getChainedAction();

          if (currentAction != null) {
            currentPath = currentAction.getActionPath();
            System.out.println("Getting chained action: " + currentPath.size());
          }
        }
      }

      return;
    }

    float targetX = currentMove.getX() * WorldConstants.WORLD_SCALE;
    float targetY = currentMove.getY() * WorldConstants.WORLD_SCALE;

    float playerX = player.getX();
    float playerY = player.getY();

//    System.out.println("[" + targetX + ", " + targetY + "] --> [" + playerX + ", " + playerY +
//        "]");

//    Node lastNode = currentPath.size() > 0 ? currentPath.get(currentPath.size() - 1) : currentMove;
//    System.out.println("Goal: " + lastNode + ", current: " + currentMove + " --> " + NodeUtils.createNode(player));

//    Node finalNode = currentPath.get(currentPath.size() - 1);
//
//    System.out.println("[" + playerX + ", " + playerY + "] --> [" + finalNode.getX() + ", " +
//    finalNode.getY() + "]");


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
