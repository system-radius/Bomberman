package com.system.radius.objects.board;

import com.badlogic.gdx.physics.box2d.World;
import com.system.radius.objects.AbstractBomberObject;
import com.system.radius.objects.blocks.Block;
import com.system.radius.objects.players.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    System.out.println("burning: " + x + ", " + y);

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

    return boardRep[y][x];
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
}
