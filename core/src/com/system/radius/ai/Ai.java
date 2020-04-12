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

  private static final long PATH_CHANGE_INTERVAL = 100;

  private BoardState boardState = BoardState.getInstance();

  private List<Action> actionList;

  private List<Node> currentPath = new ArrayList<>();

  private Player player;

  private Node currentMove;

  private Action currentAction;

  private long lastPathChange = 0;

  private boolean consumedMove = true;

  private boolean reset = false;

  private int[][] board;

  public Ai(Player player) {

    this.player = player;

    initializeActions();

    player.setSpeed(3);
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
        LOGGER.info("Choosing action as chained action.");
        chooseAction(true);
      }

      return;
    }

    LOGGER.info("Choosing action as is.");
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
      this.player.setVelX(0);
      this.player.setVelY(0);
      consumedMove = true;

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

  private void reset() {

    reset = false;
    currentAction = null;
    currentPath = null;
    currentMove = null;

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

      }

      // The following can only be done while the player is alive.
      if (System.currentTimeMillis() - lastPathChange >= PATH_CHANGE_INTERVAL) {
        String actionClass = currentAction != null ?
            currentAction.getClass().getSimpleName() : null;
        LOGGER.info(" = = = = = [" + actionClass + "]");
        lastPathChange = System.currentTimeMillis();

        // Create a representation of the board for the actions to be evaluated.
        // The board should be updated every time something has happened.
        board = boardState.constructBoardRep(player);

//      AStarUtils.printMaze(board);
        decide();
      }

      move();
    } else {
      reset = true;
    }

    player.update(delta);
  }

  public void draw(Batch batch, float delta) {
    player.draw(batch, delta);
  }

  public void drawDebug(ShapeRenderer shapeRenderer, float delta) {

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
