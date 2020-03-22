package com.system.radius.ai.action;

import com.system.radius.ai.Node;
import com.system.radius.objects.board.BoardState;
import com.system.radius.objects.board.WorldConstants;
import com.system.radius.objects.bombs.Bomb;
import com.system.radius.objects.players.Player;
import com.system.radius.utils.NodeUtils;

import java.util.List;

public abstract class Action {

  /**
   * The path that will be traversed.
   */
  protected List<Node> actionPath;

  /**
   * A board state instance.
   */
  protected BoardState boardState = BoardState.getInstance();

  /**
   * The player instance which will be the point of reference for acting.
   */
  protected Player player;

  /**
   * The target of the action, the action path will be constructed with regards to this target.
   */
  protected Node target;

  /**
   * Another action that is to be performed after this action is completed.
   */
  protected Action chainedAction;

  protected boolean complete;

  /**
   * Holds the most recent representation of the board from the parent action.
   */
  protected int[][] hypotheticalBoard;

  public Action(Player player) {
    this.player = player;
  }

  /**
   * Checks if the current action is doable. This will also find the actionPath.
   *
   * @return {@code true} if the action can be done; {@code false} otherwise.
   */
  public boolean isDoable() {

    return isDoable(null, NodeUtils.createNode(player));
  }

  /**
   * Checks if the current action is doable with consideration to the parent action.
   *
   * @param parentBoard - The state of the board after the parent action is completed.
   * @return {@code true} if the action can be done; {@code false} otherwise.
   */
  public boolean isDoable(int[][] parentBoard) {

    return isDoable(parentBoard, NodeUtils.createNode(player));
  }

  /**
   * Checks if the current action is doable with consideration to the parent action and the new
   * position.
   *
   * @param parentBoard - The state of the board after the parent action is completed.
   * @param sourceNode  - The node as the source of the current action.
   * @return {@code true} if the action can be done; {@code false} otherwise.
   */
  public abstract boolean isDoable(int[][] parentBoard, Node sourceNode);

  /**
   * Attempts to retrieve the target for this action.
   *
   * @return {@code true} if the target is acquired; {@code false} otherwise.
   */
  public abstract boolean isTargetAcquired();

  /**
   * Do the action after reaching the target.
   */
  public abstract void act();

  /**
   * Constructs a representation of the game / board state in integer. This helps in tracking
   * which way to go, or which way is safer based on the updated costs.
   *
   * @return The integer representation of the board.
   */
  protected final int[][] constructBoardRep() {

    int worldHeight = (int) WorldConstants.WORLD_HEIGHT;
    int worldWidth = (int) WorldConstants.WORLD_WIDTH;

    char[][] charBoard = boardState.getBoardRep();

    return updateBoardCost(createBoardRep(charBoard, worldWidth, worldHeight), charBoard,
        worldWidth, worldHeight);
  }

  /**
   * Creates the first board representation with default costs.
   *
   * @param charBoard   - The character-based board which will be the basis for the non-passable
   *                    terrain.
   * @param worldWidth  - The current world's width.
   * @param worldHeight - The current world's height.
   * @return The default integer representation.
   */
  protected int[][] createBoardRep(char[][] charBoard, int worldWidth, int worldHeight) {


    int[][] intBoard = new int[worldHeight][worldWidth];

    int speedLevel = (int) player.getSpeedLevel();
    int cost = (int) Player.SPEED_COUNTER - speedLevel;

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

    return intBoard;
  }

  /**
   * Updates the board representation with the proper costs based on the location of the bombs,
   * and other things that should be taken into account.
   * <p>
   * This method may be overridden to provide more cost interpretations.
   *
   * @param intBoard    - The default integer board representation.
   * @param charBoard   - The character-based board representation.
   * @param worldWidth  - The current world's width.
   * @param worldHeight - The current world's height.
   * @return The new board with updated costs.
   */
  protected int[][] updateBoardCost(int[][] intBoard, char[][] charBoard, int worldWidth,
                                    int worldHeight) {

    int speedLevel = (int) player.getSpeedLevel();
    int multiSpeedLevel = speedLevel * 10;
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
        if (x < 0 || x >= worldWidth || charBoard[y][x] != WorldConstants.BOARD_BOMB) {
          continue;
        }

        // The object marked by (x, y) is sure to be a bomb instance.
        Bomb bomb = (Bomb) boardState.getObject(x, y);
        updateBombCost(intBoard, bomb);

      }
    }

    return intBoard;
  }

  protected void updateBombCost(int[][] board, Bomb bomb) {

    int speedLevel = (int) player.getSpeedLevel();
    bomb.updateBoardCost(board, speedLevel);

  }

  /**
   * This method adapts the values from the parent board to the hypothetical board for the
   * prediction of new path to be traversed after the parent action is done.
   * All boards to be adapted is supposed to be rectangular in shape.
   *
   * @param parentBoard - The parent board whose values will be copied.
   */
  void adaptBoard(int[][] parentBoard) {

    if (parentBoard == null) {
      return;
    }

    int boardHeight = parentBoard.length;
    int boardWidth = parentBoard[0].length;

    for (int i = 0; i < boardHeight; i++) {
      for (int j = 0; j < boardWidth; j++) {

        int parentValue = parentBoard[i][j];
        if (parentValue == hypotheticalBoard[i][j]) {
          // If there is no difference to be adapted, skip.
          continue;
        }

        hypotheticalBoard[i][j] = parentValue;

      }
    }
  }

  public List<Node> getActionPath() {
    return actionPath;
  }

  public Action getChainedAction() {
    return chainedAction;
  }

  public int[][] getHypotheticalBoard() {
    return hypotheticalBoard;
  }

  public boolean isComplete() {
    return complete;
  }

}
