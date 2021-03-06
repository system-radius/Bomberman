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
import com.system.radius.utils.BombermanLogger;
import com.system.radius.utils.DebugUtils;

import java.util.Arrays;
import java.util.List;

public abstract class Bomb extends Block {

  public static final Texture BOMB_SPRITE_SHEET = new Texture("neko/img/neko_sprite_sheet.png");

  public static final Texture FIRE_SPRITE_SHEET = new Texture("neko/img/spiralingFire.png");

  protected static final TextureRegion[][] BOMB_REGIONS =
      TextureRegion.split(BOMB_SPRITE_SHEET, 32, 32);

  protected static final TextureRegion[][] FIRE_REGIONS =
      TextureRegion.split(FIRE_SPRITE_SHEET, 64, 64);

  private static final BombermanLogger LOGGER = new BombermanLogger(Bomb.class.getSimpleName());

  protected static final float FRAME_DURATION_BREATHING = 1f / 5f;

  protected static final float FRAME_DURATION_EXPLODING = 1f / 5f;

  protected static final float FRAME_DURATION_FIRE = 1f / 7.5f;

  private static final float WAIT_TIMER = 3;

  private static final float EXPLOSION_TIMER = 1;

  private List<Player> players;

  private List<Boolean> activeCollisions;

  private Player owner;

  private Circle rep;

  private Rectangle northRect;

  private Rectangle southRect;

  private Rectangle westRect;

  private Rectangle eastRect;

  private float creationTime;

  private float explosionTime;

  private boolean exploding;

  private boolean exploded;

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

  public Bomb(Player owner, float x, float y, float width, float height) {
    super(WorldConstants.BOARD_BOMB, x, y, width, height);

    this.owner = owner;
    this.creationTime = 0;
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

    loadAssets();

    players = BoardState.getInstance().getPlayers();

    Boolean[] tempArray = new Boolean[players.size()];
    Arrays.fill(tempArray, false);

    activeCollisions = Arrays.asList(tempArray);
    updateBounds();
  }

  protected void loadAssets() {

    fireStreamNorth = loadAnimation(FIRE_REGIONS[0], FRAME_DURATION_FIRE);
    fireStreamSouth = loadAnimation(FIRE_REGIONS[1], FRAME_DURATION_FIRE);
    fireStreamCenter = loadAnimation(FIRE_REGIONS[2], FRAME_DURATION_FIRE);
    fireStreamWest = loadAnimation(FIRE_REGIONS[3], FRAME_DURATION_FIRE);
    fireStreamEast = loadAnimation(FIRE_REGIONS[4], FRAME_DURATION_FIRE);
    fireStreamV = loadAnimation(FIRE_REGIONS[5], FRAME_DURATION_FIRE);
    fireStreamH = loadAnimation(FIRE_REGIONS[6], FRAME_DURATION_FIRE);

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
   * @param cost  - The cost to be included for this bomb.
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
   * @param board      - The board representation in integer.
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

    creationTime = WAIT_TIMER - 0.1f;
  }

  private void explode() {

    if (exploding) {
      return;
    }

    // Explode this bomb.
    exploding = true;

    updateBounds();

    BoardState boardState = BoardState.getInstance();
    int exactX = boardState.getExactX(this);
    int exactY = boardState.getExactY(this);

    float scale = WorldConstants.WORLD_SCALE;
    fireStreamNorthBound = new Rectangle(exactX * scale, (exactY + 1) * scale, scale,
        (rangeNorth - 1) * scale);
    fireStreamSouthBound = new Rectangle(exactX * scale, (exactY - (rangeSouth - 1)) * scale,
        scale, (rangeSouth - 1) * scale);
    fireStreamWestBound = new Rectangle((exactX - (rangeWest - 1)) * scale, exactY * scale,
        (rangeWest - 1) * scale, scale);
    fireStreamEastBound = new Rectangle((exactX + 1) * scale, exactY * scale,
        (rangeEast - 1) * scale, scale);

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

  /**
   * Attempts to burn the player. The player is actually burned if the burn collision rectangle
   * touches any of the fire collision rectangle of this bomb.
   *
   * @param player - A player to be burned.
   */
  protected void attemptPlayerBurn(Player player) {

    Rectangle rect = player.getBurnCollision();
    if (Intersector.overlaps(fireStreamEastBound, rect) ||
        Intersector.overlaps(fireStreamWestBound, rect) ||
        Intersector.overlaps(fireStreamNorthBound, rect) ||
        Intersector.overlaps(fireStreamSouthBound, rect) ||
        Intersector.overlaps(rect, bounds)) {

      // The player will only burn if they touch any of the fire bounds.
      player.burn();
    }

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

    if (boardState.getChar(x, y) == WorldConstants.BOARD_PERMA_BLOCK ||
        boardState.getChar(x, y) == WorldConstants.BOARD_HARD_BLOCK ||
        boardState.getChar(x, y) == WorldConstants.BOARD_TO_DESTROY) {
      return 2;
    }

    if (boardState.getChar(x, y) == WorldConstants.BOARD_SOFT_BLOCK) {
      boardState.setChar(x, y, WorldConstants.BOARD_TO_DESTROY);
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

    if (counter + 1 < rangeNorth) {
      batch.draw(fireStreamV.getKeyFrame(animationElapsedTime), exactX * scale,
          (exactY + counter) * scale, scale, scale);
    } else if (counter < rangeNorth) {
      batch.draw(fireStreamNorth.getKeyFrame(animationElapsedTime), exactX * scale,
          (exactY + counter) * scale, scale, scale);
    }

    if (counter + 1 < rangeSouth) {
      batch.draw(fireStreamV.getKeyFrame(animationElapsedTime), exactX * scale,
          (exactY - counter) * scale, scale, scale);
    } else if (counter < rangeSouth) {
      batch.draw(fireStreamSouth.getKeyFrame(animationElapsedTime), exactX * scale,
          (exactY - counter) * scale, scale, scale);
    }

    if (counter + 1 < rangeWest) {
      batch.draw(fireStreamH.getKeyFrame(animationElapsedTime), (exactX - counter) * scale,
          exactY * scale, scale, scale);
    } else if (counter < rangeWest) {
      batch.draw(fireStreamWest.getKeyFrame(animationElapsedTime), (exactX - counter) * scale,
          exactY * scale, scale, scale);
    }

    if (counter + 1 < rangeEast) {
      batch.draw(fireStreamH.getKeyFrame(animationElapsedTime), (exactX + counter) * scale,
          exactY * scale, scale, scale);
    } else if (counter < rangeEast) {
      batch.draw(fireStreamEast.getKeyFrame(animationElapsedTime), (exactX + counter) * scale,
          exactY * scale, scale, scale);
    }

  }

  protected void drawFire(Batch batch) {

    BoardState boardState = BoardState.getInstance();

    int exactX = boardState.getExactX(this);
    int exactY = boardState.getExactY(this);

    for (int i = 0; i <= totalRange; i++) {
      drawFireLength(batch, i, exactX, exactY);
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

    animationElapsedTime += delta;

    if (!exploding) {

      creationTime += delta;
      updateBounds();
      if (creationTime >= WAIT_TIMER) {
        explode();
      }
    } else {

      explosionTime += delta;
      if (explosionTime >= EXPLOSION_TIMER) {
        owner.removeBomb(this);
        exploded = true;
      }
    }

    if (exploding) {

      // While this bomb is exploding, always attempt to burn players.
      BoardState boardState = BoardState.getInstance();
      for (Player player : boardState.getPlayers()) {
        attemptPlayerBurn(player);
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
  public void draw(Batch batch) {

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
  public void drawDebug(ShapeRenderer shapeRenderer) {

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
