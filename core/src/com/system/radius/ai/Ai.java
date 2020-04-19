package com.system.radius.ai;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.system.radius.ai.action.Action;
import com.system.radius.ai.action.BombBlocksAction;
import com.system.radius.ai.action.BombPlayerAction;
import com.system.radius.ai.action.DefenseAction;
import com.system.radius.enums.PlayerState;
import com.system.radius.objects.board.BoardState;
import com.system.radius.objects.board.WorldConstants;
import com.system.radius.objects.players.Player;
import com.system.radius.utils.BombermanLogger;
import com.system.radius.utils.DebugUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains the mechanism for the artificial intelligence that operates a player character.
 */
public class Ai {

  private static final BombermanLogger LOGGER = new BombermanLogger(Ai.class.getSimpleName());

  private static final float PATH_CHANGE_INTERVAL = 0.1f;

  private BoardState boardState = BoardState.getInstance();

  private List<Action> actionList;

  private List<Node> currentPath = new ArrayList<>();

  private Player player;

  private Action currentAction;

  private float lastPathChange = 0;

  private boolean reset = false;

  private int[][] board;

  public Ai(Player player) {

    this.player = player;

    initializeActions();

  }

  private void initializeActions() {

    actionList = new ArrayList<>();

    Action defenseAction = new DefenseAction(this);
    Action bombPlayerAction = new BombPlayerAction(this, defenseAction);
    Action bombBlocksAction = new BombBlocksAction(this, defenseAction);

    defenseAction.addChainedAction(bombPlayerAction, bombBlocksAction);

    actionList.add(bombPlayerAction);
    actionList.add(bombBlocksAction);
    actionList.add(defenseAction);
  }

  private void chooseAction(boolean chained) {

    Action todo = null;

    // At least one action should be doable.
    for (Action action : actionList) {

      boolean doable = chained ? action.isDoable(currentAction.getHypotheticalBoard()) :
          action.isDoable();

      LOGGER.info("Checking action: " + action.getClass().getSimpleName()
          + ", doable: " + doable);

      if (doable) {
        LOGGER.info("Action selected!");
        todo = action;
        break;
      }
    }

    if (todo != null) {
      currentAction = todo;
      currentPath = currentAction.getActionPath();
    } else if (currentPath != null) {
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
        LOGGER.info("Choosing action as chained action.");
        chooseAction(true);
      }

      return;
    }

    LOGGER.info("Choosing action as is.");
    chooseAction(false);

  }

  private void move(float delta) {

    // Simply retrieve the first move from the path.
    Node currentMove = currentPath != null && currentPath.size() > 0 ? currentPath.get(0) : null;

    if (currentMove == null) {

      // Null currentMove means that the action path is consumed, signifying that the AI can act
      // based on the specified action.
      this.player.setVelX(0);
      this.player.setVelY(0);

      if (currentAction != null) {
        currentAction.act();

        if (currentAction.isComplete()) {
          // If the action is complete, the AI may proceed with the chained action.
          Action lastAction = currentAction;
          currentAction = currentAction.getChainedAction();

          lastAction.resetComplete();
          if (currentAction != null) {
            currentPath = currentAction.getActionPath();
//            LOGGER.info("Getting chained action: " + currentPath.size());
          }
        }
      }

      return;
    }

    float targetX = currentMove.getX() * WorldConstants.WORLD_SCALE;
    float targetY = currentMove.getY() * WorldConstants.WORLD_SCALE;

    float playerX = player.getX();
    float playerY = player.getY();

    float speed = player.getSpeed();
    float dSpeed = speed * delta;

    float velX = 0;
    float diffX = targetX - playerX;
    if (playerX > targetX) {
      // If the player x modified by the current speed will pass the target X,
      // assign the difference between the last x and the target X.
      velX = playerX - dSpeed > targetX ? -speed : diffX;
    } else if (playerX < targetX) {
      velX = playerX + dSpeed < targetX ? speed : diffX;
    }

    float velY = 0;
    float diffY = targetY - playerY;
    if (playerY > targetY) {
      velY = playerY - dSpeed > targetY ? -speed : diffY;
    } else if (playerY < targetY) {
      velY = playerY + dSpeed < targetY ? speed : diffY;
    }

    float speedLevel = player.getSpeedLevel();
    if (Math.abs(velX) < speedLevel && Math.abs(velY) < speedLevel) {
      // Remove the movement node from the action path if there is no movement.
      currentPath.remove(0);
      velX = velY = 0;
    }

    this.player.setVelX(velX);
    this.player.setVelY(velY);

  }

  private void reset() {

    reset = false;
    currentAction = null;
    currentPath = null;

    lastPathChange = 0;

  }

  public Player getPlayer() {
    return player;
  }

  public int[][] getBoard() {
    return board;
  }

  public void update(float delta) {

    PlayerState playerState = player.getPlayerState();

    if (!PlayerState.DYING.equals(playerState) && !PlayerState.DEAD.equals(playerState)) {

      if (reset) {
        reset();
      }

      // The following can only be done while the player is alive.
      lastPathChange += delta;
      if (lastPathChange >= PATH_CHANGE_INTERVAL) {
        String actionClass = currentAction != null ?
            currentAction.getClass().getSimpleName() : null;
        LOGGER.info(" = = = = = [" + actionClass + "]");
        lastPathChange %= PATH_CHANGE_INTERVAL;

        // Create a representation of the board for the actions to be evaluated.
        // The board should be updated every time something has happened.
        board = boardState.constructBoardRep(player);

//      AStarUtils.printMaze(board);
        decide();
      }

      move(delta);
    } else {
      reset = true;
    }

    // Update the player.
    player.update(delta);

  }

  public void draw(Batch batch) {
    player.draw(batch);
  }

  public void drawDebug(ShapeRenderer shapeRenderer) {

    player.drawDebug(shapeRenderer);

    if (currentPath == null) {
      return;
    }

    float scale = WorldConstants.WORLD_SCALE;

    for (Node node : currentPath) {
      DebugUtils.drawRect(shapeRenderer, new Rectangle(node.getX() * scale, node.getY() * scale,
          scale, scale));
    }

  }

}
