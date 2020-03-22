package com.system.radius.objects.bombs;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.system.radius.objects.blocks.Block;
import com.system.radius.objects.board.BoardState;
import com.system.radius.objects.board.WorldConstants;
import com.system.radius.objects.players.Player;
import com.system.radius.utils.DebugUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Bomb extends Block {

  protected static final float FRAME_DURATION_BREATHING = 1f / 5f;

  protected static final float FRAME_DURATION_EXPLODING = 1f / 5f;

  protected static final float FRAME_DURATION_FIRE = 1f / 7.5f;

  private static final long WAIT_TIMER = 3000;

  private static final long EXPLOSION_TIMER = 1000;

  private List<Player> players;

  private List<Boolean> activeCollisions;

  private Player owner;

  private Circle rep;

  private Rectangle northRect;

  private Rectangle southRect;

  private Rectangle westRect;

  private Rectangle eastRect;

  private long creationTime;

  private long explosionTime;

  private boolean exploding;

  private boolean exploded;

  private Texture fireSpriteSheet;

  private float totalRange;

  private float rangeNorth;

  private float rangeSouth;

  private float rangeWest;

  private float rangeEast;

  protected Animation<TextureRegion> breathingAnimation;

  protected Animation<TextureRegion> headExAnimation;

  protected Animation<TextureRegion> fireStreamNorth;

  protected Animation<TextureRegion> fireStreamSouth;

  protected Animation<TextureRegion> fireStreamWest;

  protected Animation<TextureRegion> fireStreamEast;

  protected Animation<TextureRegion> fireStreamV;

  protected Animation<TextureRegion> fireStreamH;

  protected Animation<TextureRegion> fireStreamCenter;

  protected Rectangle fireStreamNorthBound;

  protected Rectangle fireStreamSouthBound;

  protected Rectangle fireStreamWestBound;

  protected Rectangle fireStreamEastBound;

  public Bomb(Player owner, String path, float x, float y, float width, float height) {
    super(WorldConstants.BOARD_BOMB, x, y, width, height);

    this.owner = owner;
    this.creationTime = System.currentTimeMillis();
    this.totalRange = owner.getFirePower();

    rep = new Circle(x + (width / 2), y + (height / 2), width / 2);

    float divider = 2;
    float thinWidth = (width / (divider * 2));
    float thinHeight = (height / (divider * 2));
    northRect = new Rectangle(x + thinWidth, (y + height) - thinHeight,
        width - (thinWidth * 2), thinHeight);
    southRect = new Rectangle(x + thinWidth, y, width - (thinWidth * 2), thinHeight);
    westRect = new Rectangle(x, y + thinHeight, thinWidth, height - (thinHeight * 2));
    eastRect = new Rectangle(x + width - thinWidth, y + thinHeight, thinWidth,
        height - (thinHeight * 2));

    spriteSheet = new Texture(path);
    loadAssets();

    players = BoardState.getInstance().getPlayers();

    Boolean[] tempArray = new Boolean[players.size()];
    Arrays.fill(tempArray, false);

    activeCollisions = Arrays.asList(tempArray);
    updateBounds();
  }

  protected void loadAssets() {

    fireSpriteSheet = new Texture("neko/img/fire.png");
    TextureRegion[][] allFrames = TextureRegion.split(fireSpriteSheet, 16, 16);

    fireStreamNorth = loadAnimation(allFrames[0], FRAME_DURATION_FIRE);
    fireStreamSouth = loadAnimation(allFrames[1], FRAME_DURATION_FIRE);
    fireStreamCenter = loadAnimation(allFrames[2], FRAME_DURATION_FIRE);
    fireStreamWest = loadAnimation(allFrames[3], FRAME_DURATION_FIRE);
    fireStreamEast = loadAnimation(allFrames[4], FRAME_DURATION_FIRE);
    fireStreamV = loadAnimation(allFrames[5], FRAME_DURATION_FIRE);
    fireStreamH = loadAnimation(allFrames[6], FRAME_DURATION_FIRE);

  }

  protected Animation<TextureRegion> loadAnimation(TextureRegion[] frames, float frameDuration) {

    int length = 8;
    TextureRegion[] container = new TextureRegion[length];
    System.arraycopy(frames, 0, container, 0, length);

    return new Animation<>(frameDuration, container);
  }

  /**
   * Update the board cost with a specified cost.
   *
   * @param board - The board representation in integer.
   * @param cost - The cost to be included for this bomb.
   */
  public void updateBoardCostSetCost(int[][] board, int cost) {

    updateBounds();
    BoardState boardState = BoardState.getInstance();

    int exactX = boardState.getExactX(this);
    int exactY = boardState.getExactY(this);

    for (int i = 0; i <= totalRange; i++) {

      updateCellCost(board, exactX, exactY + i, cost, (int) rangeNorth, i);
      updateCellCost(board, exactX, exactY - i, cost, (int) rangeSouth, i);
      updateCellCost(board, exactX + i, exactY, cost, (int) rangeEast, i);
      updateCellCost(board, exactX - i, exactY, cost, (int) rangeWest, i);

    }

  }

  /**
   * Update the board's movement costs based on the specified speed level.
   *
   * @param board - The board representation in integer.
   * @param speedLevel - The speed level to be the basis of the cost.
   */
  public void updateBoardCost(int[][] board, int speedLevel) {

    int cost = WorldConstants.FIRE_PATH_COST - (speedLevel * 10);

    if (this.exploding) {
      cost = -1;
    }

    updateBoardCostSetCost(board, cost);

  }

  private void updateCellCost(int[][] board, int x, int y, int cost, int range, int checker) {

    // Do not update the board cost if the current cell is blocked.
    if (checker < range && board[y][x] >= 0) {
      board[y][x] = cost;
    }

  }

  public Rectangle getNorthRect() {
    return northRect;
  }

  public Rectangle getSouthRect() {
    return southRect;
  }

  public Rectangle getWestRect() {
    return westRect;
  }

  public Rectangle getEastRect() {
    return eastRect;
  }

  protected void drawCollisionBounds(ShapeRenderer shapeRenderer) {

    shapeRenderer.setColor(Color.GREEN);

    DebugUtils.drawRect(shapeRenderer, getNorthRect());
    DebugUtils.drawRect(shapeRenderer, getSouthRect());
    DebugUtils.drawRect(shapeRenderer, getEastRect());
    DebugUtils.drawRect(shapeRenderer, getWestRect());

    shapeRenderer.setColor(Color.CYAN);

    DebugUtils.drawRect(shapeRenderer, fireStreamNorthBound);
    DebugUtils.drawRect(shapeRenderer, fireStreamSouthBound);
    DebugUtils.drawRect(shapeRenderer, fireStreamEastBound);
    DebugUtils.drawRect(shapeRenderer, fireStreamWestBound);
  }

  @Override
  public void burn() {

    creationTime = System.currentTimeMillis() - WAIT_TIMER + 100;
  }

  private void explode() {

    if (exploding) {
      return;
    }

    // Explode this bomb.
    explosionTime = System.currentTimeMillis();
    exploding = true;

    updateBounds();

    BoardState boardState = BoardState.getInstance();
    int exactX = boardState.getExactX(this);
    int exactY = boardState.getExactY(this);

    float scale = WorldConstants.WORLD_SCALE;
    fireStreamNorthBound = new Rectangle(exactX * scale, (exactY + 1) * scale, scale,
        (rangeNorth - 1) * scale);
    fireStreamSouthBound = new Rectangle(exactX * scale, exactY * scale, scale,
        -(rangeSouth - 1) * scale);
    fireStreamWestBound = new Rectangle(exactX * scale, exactY * scale, -(rangeWest - 1) * scale,
        scale);
    fireStreamEastBound = new Rectangle((exactX + 1) * scale, exactY * scale,
        (rangeEast - 1) * scale,
        scale);

    for (int i = 1; i <= totalRange; i++) {

      burnObject(boardState, i, (int) rangeNorth, exactX, exactY + i);
      burnObject(boardState, i, (int) rangeSouth, exactX, exactY - i);
      burnObject(boardState, i, (int) rangeWest, exactX - i, exactY);
      burnObject(boardState, i, (int) rangeEast, exactX + i, exactY);
    }

    animationElapsedTime = 0;
  }

  protected void burnObject(BoardState boardState, int counter, int range, int burnX, int burnY) {

    if (counter >= range) {
      return;
    }

    boardState.burnObject(burnX, burnY);

  }

  protected void attemptPlayerBurn(Player player) {


  }

  private void updateBounds() {

    BoardState boardState = BoardState.getInstance();
    int exactX = boardState.getExactX(this);
    int exactY = boardState.getExactY(this);

    rangeNorth = checkObstacle(boardState, exactX, exactY + 1, 'w', 1);
    rangeSouth = checkObstacle(boardState, exactX, exactY - 1, 's', 1);
    rangeWest = checkObstacle(boardState, exactX - 1, exactY, 'a', 1);
    rangeEast = checkObstacle(boardState, exactX + 1, exactY, 'd', 1);

    float scale = WorldConstants.WORLD_SCALE;

    fireStreamNorthBound = new Rectangle(exactX * scale, (exactY + 1) * scale, scale,
        (rangeNorth - 1) * scale);
    fireStreamSouthBound = new Rectangle(exactX * scale, exactY * scale, scale,
        -(rangeSouth - 1) * scale);
    fireStreamWestBound = new Rectangle(exactX * scale, exactY * scale, -(rangeWest - 1) * scale,
        scale);
    fireStreamEastBound = new Rectangle((exactX + 1) * scale, exactY * scale,
        (rangeEast - 1) * scale,
        scale);

  }

  private int checkObstacle(BoardState boardState, int x, int y, char dir, int counter) {

    if (counter > totalRange) {
      return 1;
    }

    if (boardState.getChar(x, y) == WorldConstants.BOARD_PERMA_BLOCK) {
      return 2;
    }

    if (boardState.getChar(x, y) == WorldConstants.BOARD_HARD_BLOCK || boardState.getChar(x, y) == WorldConstants.BOARD_SOFT_BLOCK) {
      return 2;
    }

    switch (dir) {
      case 'w':
        return 1 + checkObstacle(boardState, x, y + 1, dir, counter + 1);
      case 's':
        return 1 + checkObstacle(boardState, x, y - 1, dir, counter + 1);
      case 'a':
        return 1 + checkObstacle(boardState, x - 1, y, dir, counter + 1);
      case 'd':
        return 1 + checkObstacle(boardState, x + 1, y, dir, counter + 1);
    }

    return 1;
  }

  protected void drawFireLength(Batch batch, int counter, int exactX, int exactY) {

    float scale = WorldConstants.WORLD_SCALE;

    if (counter < rangeNorth) {
      batch.draw(fireStreamV.getKeyFrame(animationElapsedTime), exactX * scale,
          (exactY + counter) * scale, scale, scale);
    }

    if (counter < rangeSouth) {
      batch.draw(fireStreamV.getKeyFrame(animationElapsedTime), exactX * scale,
          (exactY - counter) * scale, scale, scale);
    }

    if (counter < rangeWest) {
      batch.draw(fireStreamH.getKeyFrame(animationElapsedTime), (exactX - counter) * scale,
          exactY * scale, scale, scale);
    }

    if (counter < rangeEast) {
      batch.draw(fireStreamH.getKeyFrame(animationElapsedTime), (exactX + counter) * scale,
          exactY * scale, scale, scale);
    }

  }

  protected void drawFireBounds(Batch batch, int counter, int exactX, int exactY) {

    float scale = WorldConstants.WORLD_SCALE;

    if (counter < rangeNorth) {
      batch.draw(fireStreamNorth.getKeyFrame(animationElapsedTime), exactX * scale,
          (exactY + counter) * scale, scale, scale);
    }

    if (counter < rangeSouth) {
      batch.draw(fireStreamSouth.getKeyFrame(animationElapsedTime), exactX * scale,
          (exactY - counter) * scale, scale, scale);
    }

    if (counter < rangeWest) {
      batch.draw(fireStreamWest.getKeyFrame(animationElapsedTime), (exactX - counter) * scale,
          exactY * scale, scale, scale);
    }

    if (counter < rangeEast) {
      batch.draw(fireStreamEast.getKeyFrame(animationElapsedTime), (exactX + counter) * scale,
          exactY * scale, scale, scale);
    }

  }

  protected void drawFire(Batch batch) {

    BoardState boardState = BoardState.getInstance();

    int exactX = boardState.getExactX(this);
    int exactY = boardState.getExactY(this);

    for (int i = 0; i <= totalRange; i++) {
      if (i == totalRange) {
        drawFireBounds(batch, i, exactX, exactY);
      } else {
        drawFireLength(batch, i, exactX, exactY);
      }
    }

    batch.draw(fireStreamCenter.getKeyFrame(animationElapsedTime), getX(), getY(), getWidth(),
        getHeight());

  }

  @Override
  public boolean isActiveCollision(Player player) {

    int index = -1;
    for (int i = 0; i < players.size(); i++) {
      if (players.get(i).equals(player)) {
        index = i;
        break;
      }
    }

    return index != -1 && activeCollisions.get(index);
  }

  @Override
  public void update(float delta) {

    long lapsedTime = System.currentTimeMillis();

    if (!exploding) {

      updateBounds();
      if (lapsedTime - creationTime >= WAIT_TIMER) {
        explode();
      }
    } else {

      if (EXPLOSION_TIMER <= lapsedTime - explosionTime) {
        owner.removeBomb(this);
        exploded = true;
      }
    }

    for (int i = 0; i < players.size(); i++) {

      // If the collision is already active, do not reset.
      if (activeCollisions.get(i)) {
        continue;
      }

      Player player = players.get(i);
      boolean collision = !Intersector.overlaps(player.getBounds(), bounds);

      // Reset the collision status for the player.
      activeCollisions.set(i, collision);

    }

  }

  @Override
  public void draw(Batch batch, float delta) {

    animationElapsedTime += delta;
    if (!exploding) {
      batch.draw(breathingAnimation.getKeyFrame(animationElapsedTime), getX(), getY(), getWidth(),
          getHeight());
    } else {
      drawFire(batch);
      batch.draw(headExAnimation.getKeyFrame(animationElapsedTime), getX(), getY(), getWidth(),
          getHeight());
    }

  }

  @Override
  public void drawDebug(ShapeRenderer shapeRenderer, float delta) {

//    shapeRenderer.setColor(Color.RED);
//
//    if (exploding) {
//      DebugUtils.drawRect(shapeRenderer, bounds);
//    } else {
//      DebugUtils.drawCircle(shapeRenderer, rep);
//    }

//    if (exploding) {
    drawCollisionBounds(shapeRenderer);
//    }
  }

  @Override
  public void dispose() {
    super.dispose();

    fireSpriteSheet.dispose();
  }

  public boolean isExploding() {
    return exploding;
  }

  public boolean isExploded() {
    return exploded;
  }

  public Rectangle getBounds() {
    return bounds;
  }

  public Circle getRep() {
    return rep;
  }
}
