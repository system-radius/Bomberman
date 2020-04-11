package com.system.radius.objects.board;

import com.system.radius.objects.AbstractBomberObject;
import com.system.radius.objects.blocks.Block;
import com.system.radius.objects.bombs.Bomb;
import com.system.radius.objects.players.Player;
import com.system.radius.utils.AStarUtils;
import com.system.radius.utils.Range;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents the current state of the board being played on. Also contains utility methods for
 * board representation construction.
 */
public class BoardState {

  private static BoardState instance;

  private AbstractBomberObject[][] board;

  private List<Player> players;

  private char[][] boardRep;

  private int boardWidth;

  private int boardHeight;

  private BoardState(int boardWidth, int boardHeight) {
    this.boardWidth = boardWidth;
    this.boardHeight = boardHeight;
  }

  /**
   * Gets a board state instance.
   *
   * @return The BoardState instance
   */
  public static BoardState getInstance() {

    if (instance == null) {
      instance = new BoardState((int) WorldConstants.WORLD_WIDTH,
          (int) WorldConstants.WORLD_HEIGHT);
    }

    return instance;
  }

  public char[][] getBoardRep() {
    return boardRep;
  }

  public void reset() {
    board = new AbstractBomberObject[boardHeight][boardWidth];
    boardRep = new char[boardHeight][boardWidth];

    players = new ArrayList<>();

    for (int i = 0; i < boardHeight; i++) {
      for (int j = 0; j < boardWidth; j++) {
        board[i][j] = null;
        boardRep[i][j] = WorldConstants.BOARD_EMPTY;
      }
    }
  }

  public List<Player> getPlayers() {

    return Collections.unmodifiableList(players);
  }

  public void addPlayer(Player player) {
    players.add(player);
  }

  public int getExactX(AbstractBomberObject object) {

    float x = object.getX();
    float scale = WorldConstants.WORLD_SCALE;
    float excessX = x % scale >= (scale / 2) ? 1 : 0;
    float fullX = (int) x / (int) scale;

    return (int) ((fullX + excessX));
  }

  public int getExactY(AbstractBomberObject object) {

    float y = object.getY();
    float scale = WorldConstants.WORLD_SCALE;
    float excessY = y % scale >= (scale / 2) ? 1 : 0;
    float fullY = (int) y / (int) scale;

    return (int) ((fullY + excessY));
  }

  public void addToBoard(AbstractBomberObject object) {

    int x = getExactX(object);
    int y = getExactY(object);

    board[y][x] = object;
    boardRep[y][x] = object.getCharacterRepresentation();

  }

  public void removeFromBoard(AbstractBomberObject object) {

    int x = getExactX(object);
    int y = getExactY(object);

    board[y][x] = null;
    boardRep[y][x] = WorldConstants.BOARD_EMPTY;

  }

  public void removeFromBoard(int x, int y) {

    board[y][x] = null;
    boardRep[y][x] = ' ';

  }

  public void burnObject(int x, int y) {

    AbstractBomberObject object = board[y][x];

    if (object == null) {
      return;
    }

    object.burn();

  }

  public AbstractBomberObject getObject(int x, int y) {

    return board[y][x];
  }

  public char getChar(int x, int y) {

    if (x < 0 || x >= WorldConstants.WORLD_WIDTH || y < 0 || y >= WorldConstants.WORLD_HEIGHT) {
      return WorldConstants.BOARD_PERMA_BLOCK;
    }

    return boardRep[y][x];
  }

  /**
   * Sets the character representation for a coordinate in the board. This method should be used
   * sparingly as frequent modification of the board may create unwanted effects.
   *
   * @param x - The X-coordinate to be set.
   * @param y - The Y-coordinate to be set.
   * @param c - The character to be set.
   */
  public void setChar(int x, int y, char c) {
    boardRep[y][x] = c;
  }

  /**
   * Checks for the existence of blocks around the specified coordinate, with regards to the fire
   * power of the player.
   *
   * @param x     - The X-coordinate for the checking.
   * @param y     - The Y-coordinate for the checking.
   * @param range - The fire power / range of the player's bomb.
   * @return The number of blocks that can be destroyed.
   */
  public int checkBlocks(int x, int y, int range) {

    Range rangeObject = new Range(x, y, range);
    Map<Character, Boolean> burnTargets = rangeObject.getBurnTargets();

    int count = 0;
    Set<Character> keys = burnTargets.keySet();
    for (char key : keys) {
      count += burnTargets.get(key) ? 1 : 0;
    }

    return count;
  }

  public void updateObjects(float delta) {
    for (int i = 0; i < boardRep.length; i++) {
      for (int j = 0; j < boardRep[i].length; j++) {
        if (boardRep[i][j] == WorldConstants.BOARD_BOMB || boardRep[i][j] == WorldConstants.BOARD_EMPTY) {
          continue;
        }

        board[i][j].update(delta);

      }
    }
  }

  public List<Block> getSurroundingBlocks(Player player) {

    int x = getExactX(player);
    int y = getExactY(player);

    List<Block> blocks = new ArrayList<>();

    for (int i = y - 1; i <= y + 1; i++) {

      if (i < 0 || i >= WorldConstants.WORLD_HEIGHT) {
        continue;
      }

      for (int j = x - 1; j <= x + 1; j++) {

        if (j < 0 || j >= WorldConstants.WORLD_WIDTH) {
          continue;
        }

        if (board[i][j] instanceof Block) {
          blocks.add((Block) board[i][j]);
        }
      }
    }

    return blocks;
  }

  /**
   * Constructs a representation of the game / board state in integer. This helps in tracking
   * which way to go, or which way is safer based on the updated costs.
   *
   * @param player - The current player as basis of the construction.
   * @return The integer representation of the board.
   */
  public final int[][] constructBoardRep(Player player) {

    int worldHeight = (int) WorldConstants.WORLD_HEIGHT;
    int worldWidth = (int) WorldConstants.WORLD_WIDTH;

    char[][] charBoard = this.getBoardRep();

    return updateBoardCost(player, createBoardRep(player, charBoard, worldWidth, worldHeight),
        charBoard, worldWidth, worldHeight);
  }

  /**
   * Creates the first board representation with default costs.
   *
   * @param player      - The current player as basis of the construction.
   * @param charBoard   - The character-based board which will be the basis for the non-passable
   *                    terrain.
   * @param worldWidth  - The current world's width.
   * @param worldHeight - The current world's height.
   * @return The default integer representation.
   */
  private int[][] createBoardRep(Player player, char[][] charBoard, int worldWidth,
                                 int worldHeight) {


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
   * @param player      - The current player as basis of the construction.
   * @param intBoard    - The default integer board representation.
   * @param charBoard   - The character-based board representation.
   * @param worldWidth  - The current world's width.
   * @param worldHeight - The current world's height.
   * @return The new board with updated costs.
   */
  private int[][] updateBoardCost(Player player, int[][] intBoard, char[][] charBoard,
                                  int worldWidth, int worldHeight) {

    int speedLevel = (int) player.getSpeedLevel();
    int multiSpeedLevel = speedLevel * 10;
    int playerX = this.getExactX(player);
    int playerY = this.getExactY(player);

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
        Bomb bomb = (Bomb) getObject(x, y);
        updateBombCost(player, intBoard, bomb);

      }
    }

    return intBoard;
  }

  /**
   * Updates the board cost based on the bomb positions.
   *
   * @param player - The current player as basis of construction.
   * @param board  - The constructed board to be updated.
   * @param bomb   - The bomb as basis of path update.
   */
  private void updateBombCost(Player player, int[][] board, Bomb bomb) {

    int speedLevel = (int) player.getSpeedLevel();
    bomb.updateBoardCost(board, speedLevel);

  }

  /**
   * This method adapts the values from the parent board to the hypothetical board for the
   * prediction of new path to be traversed after the parent action is done.
   * All boards to be adapted is supposed to be rectangular in shape.
   *
   * @param hypotheticalBoard - The base board to be updated.
   * @param parentBoard       - The parent board whose values will be copied.
   */
  public void adaptBoard(int[][] hypotheticalBoard, int[][] parentBoard) {

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

  /**
   * Creates a copy of the hypothetical board for the application of the theoretical bombs.
   *
   * @param hypotheticalBoard - The board to be copied.
   * @return A copy of the hypothetical board.
   */
  public int[][] copyBoard(int[][] hypotheticalBoard) {

    int h = hypotheticalBoard.length;
    int w = hypotheticalBoard[0].length;

    int[][] tempBoard = new int[h][w];

    for (int i = 0; i < h; i++) {
      for (int j = 0; j < w; j++) {
        tempBoard[i][j] = hypotheticalBoard[i][j];
      }
    }

    return tempBoard;
  }
}
