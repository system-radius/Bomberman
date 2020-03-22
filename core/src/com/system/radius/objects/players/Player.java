package com.system.radius.objects.players;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.system.radius.enums.BombType;
import com.system.radius.enums.Direction;
import com.system.radius.enums.PlayerState;
import com.system.radius.objects.AbstractBomberObject;
import com.system.radius.objects.blocks.Block;
import com.system.radius.objects.bombs.Bomb;
import com.system.radius.objects.bombs.NekoBomb;
import com.system.radius.objects.board.BoardState;
import com.system.radius.objects.board.WorldConstants;
import com.system.radius.utils.DebugUtils;

import java.util.List;

public abstract class Player extends AbstractBomberObject implements Disposable {

  public static float FRAME_DURATION = 1f / 15f;

  public static final float SPEED_COUNTER = 6f;

  /**
   * The base speed that will be multiplied with the current speed level to gat the actual speed.
   * This value is separate from the velocity values because the velocities are reset every
   * update. When moving the character, the speed is set as either the velocity X or velocity Y.
   */
  protected float baseSpeed = 100;

  protected Array<Bomb> bombs;

  /**
   * The current bomb type that this player can place.
   */
  protected BombType bombType;

  /**
   * The rectangle representing the collision bounds of the player.
   */
  protected Rectangle collisionRect;

  /**
   * A rectangle representation of the area that will allow the player to burn if in contact with
   * the fire collision from a bomb.
   */
  protected Rectangle collisionBurn;

  /**
   * The player's current speed level. To avoid having the player jump over walls dues to too
   * much computation using the velocity values, the maximum speed level is up to 5.
   */
  protected float speedLevel = 5f;

  /**
   * The current scale value, provided on the creation of this object, relevant for the creation
   * of the collision bounds.
   */
  protected float scale;

  /**
   * The number of bombs this player can carry.
   */
  protected int bombStock = 10;

  protected int firePower = 3;

  protected Direction direction = Direction.DOWN;

  private Rectangle northRect;

  private Rectangle southRect;

  private Rectangle westRect;

  private Rectangle eastRect;

  /**
   * The animation frames when going south or pressing "S" key.
   */
  private Animation<TextureRegion> sAnim;

  /**
   * The animation when going north or pressing "W" key.
   */
  private Animation<TextureRegion> wAnim;

  /**
   * The animation when going west or pressing "A" key.
   */
  private Animation<TextureRegion> aAnim;

  /**
   * The animation when going east or pressing "D" key.
   */
  private Animation<TextureRegion> dAnim;

  /**
   * The loaded sprite sheet for this player.
   */
  private Texture spriteSheet;

  private PlayerState playerState = PlayerState.IDLE;

  private float thinWidth;

  private float thinHeight;

  private float thinScale;

  Player(String spriteSheetPath, float x, float y, float scale) {
    super('P', x, y);

    this.scale = scale;

    baseSpeed = 2 * scale;
    collisionRect = new Rectangle(x, y, scale, scale);

    bombs = new Array<>();

    fixBounds();
    loadAsset(spriteSheetPath);

    thinScale = scale / 4f;
    collisionBurn = new Rectangle(x + thinScale, y + thinScale, thinScale * 2, thinScale * 2);

  }

  private void fixBounds() {

    float width = scale;
    float height = scale;

    float divider = SPEED_COUNTER - speedLevel;
    thinWidth = (width / (divider * 2));
    thinHeight = (height / (divider * 2));

    northRect = new Rectangle(x + thinWidth, (y + height) - thinHeight,
        width - (thinWidth * 2), thinHeight);
    southRect = new Rectangle(x + thinWidth, y, width - (thinWidth * 2), thinHeight);
    westRect = new Rectangle(x, y + thinHeight, thinWidth, height - (thinHeight * 2));
    eastRect = new Rectangle(x + width - thinWidth, y + thinHeight, thinWidth,
        height - (thinHeight * 2));

  }

  public void setSpeed(float speedLevel) {

    this.speedLevel = speedLevel;
    fixBounds();

  }

  private void loadAsset(String spriteSheetPath) {

    spriteSheet = new Texture(spriteSheetPath);
    TextureRegion[][] temp = TextureRegion.split(spriteSheet, 32, 32);

    TextureRegion[] sFrames = loadFrames(temp, 0);
    TextureRegion[] wFrames = loadFrames(temp, 1);
    TextureRegion[] aFrames = loadFrames(temp, 2);
    TextureRegion[] dFrames = loadFrames(temp, 3);

    sAnim = new Animation<>(FRAME_DURATION, sFrames);
    sAnim.setPlayMode(Animation.PlayMode.LOOP);
    wAnim = new Animation<>(FRAME_DURATION, wFrames);
    wAnim.setPlayMode(Animation.PlayMode.LOOP);
    aAnim = new Animation<>(FRAME_DURATION, aFrames);
    aAnim.setPlayMode(Animation.PlayMode.LOOP);
    dAnim = new Animation<>(FRAME_DURATION, dFrames);
    dAnim.setPlayMode(Animation.PlayMode.LOOP);

  }

  private TextureRegion[] loadFrames(TextureRegion[][] allFrames, int direction) {

    TextureRegion[] temp = allFrames[direction];
    TextureRegion[] container = new TextureRegion[temp.length];

    System.arraycopy(temp, 0, container, 0, temp.length);

    return container;
  }

  public abstract void useAbility();

  public void plantBomb() {

    if (bombs.size >= bombStock) {
      return;
    }

    float exactX = getExactX();
    float exactY = getExactY();

    BoardState boardState = BoardState.getInstance();
    if (boardState.getChar((int) (exactX / scale), (int) (exactY / scale)) != WorldConstants.BOARD_EMPTY) {
      return;
    }

    Bomb bomb = new NekoBomb(this, exactX, exactY, scale, scale);
    bombs.add(bomb);

    BoardState.getInstance().addToBoard(bomb);
  }

  public void removeBomb(Bomb bomb) {

    bombs.removeValue(bomb, false);
    BoardState.getInstance().removeFromBoard(bomb);

  }

  public Rectangle getBounds() {
    return collisionRect;
  }

  public float getSpeed() {
    return baseSpeed * speedLevel;
  }

  public int getFirePower() {
    return firePower;
  }

  protected void updateBounds() {
    collisionRect.setPosition(x, y);

    northRect.setPosition(x + thinWidth, (y + scale) - thinHeight);
    southRect.setPosition(x + thinWidth, y);
    eastRect.setPosition(x + scale - thinWidth, y + thinHeight);
    westRect.setPosition(x, y + thinHeight);

    collisionBurn.setPosition(x + thinScale, y + thinScale);

  }

  @Override
  public void setX(float x) {
    super.setX(x);
    updateBounds();
  }

  @Override
  public void setY(float y) {
    super.setY(y);
    updateBounds();
  }

  public float getExactX() {

    float excessX = x % scale >= (scale / 2) ? 1 : 0;
    float fullX = (int) x / (int) scale;

    return (fullX + excessX) * scale;
  }

  public float getExactY() {

    float excessY = y % scale >= (scale / 2) ? 1 : 0;
    float fullY = (int) y / (int) scale;

    return (fullY + excessY) * scale;
  }

  public float getSpeedLevel() {
    return speedLevel;
  }

  public void setDirection(Direction direction) {
    this.direction = direction;
  }

  public void setDirection() {
    // weird, try to make this cleaner
    if (velY < 0 && velX > 0) {
      direction = Direction.DOWN_RIGHT;
    } else if (velY < 0 && velX < 0) {
      direction = Direction.DOWN_LEFT;
    } else if (velY < 0 && velX == 0) {
      direction = Direction.DOWN;
    } else if (velY > 0 && velX > 0) {
      direction = Direction.UP_RIGHT;
    } else if (velY > 0 && velX < 0) {
      direction = Direction.UP_LEFT;
    } else if (velY > 0 && velX == 0) {
      direction = Direction.UP;
    } else if (velY == 0 && velX > 0) {
      direction = Direction.RIGHT;
    } else if (velY == 0 && velX < 0) {
      direction = Direction.LEFT;
    }
  }

  public Direction getDirection() {
    return direction;
  }

  public Animation<TextureRegion> getActiveAnimation() {

    switch (direction) {
      case UP:
      case UP_RIGHT:
        return wAnim;
      case LEFT:
      case UP_LEFT:
        return aAnim;
      case RIGHT:
      case DOWN_RIGHT:
        return dAnim;
      case DOWN_LEFT:
      case DOWN:
      default:
        return sAnim;
    }
  }

  public Array<Bomb> getBombs() {

    return bombs;
  }

  public void collide(List<Block> blocks) {

    for (Block block : blocks) {

      if (!block.isActiveCollision(this)) {
        continue;
      }

      Rectangle blockBound = block.getBounds();
      float blockY = block.getY();
      float blockX = block.getX();
      float blockH = block.getHeight();
      float blockW = block.getWidth();

      if (Intersector.overlaps(blockBound, northRect)) {
        this.setY(blockY - blockH);
      } else if (Intersector.overlaps(blockBound, southRect)) {
        this.setY(blockY + blockH);
      }

      if (Intersector.overlaps(blockBound, eastRect)) {
        this.setX(blockX - blockW);
      } else if (Intersector.overlaps(blockBound, westRect)) {
        this.setX(blockX + blockW);
      }

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

  public Rectangle getBurnCollision() {
    return collisionBurn;
  }

  @Override
  public void update(float delta) {

    setDirection();
    if (getVelX() != 0 || getVelY() != 0) {
      playerState = PlayerState.WALKING;
    } else {
      playerState = PlayerState.IDLE;
    }

    for (Bomb bomb : bombs) {
      bomb.update(delta);
    }

  }

  @Override
  public void draw(Batch batch, float delta) {

    for (Bomb bomb : bombs) {
      bomb.draw(batch, delta);
    }

    animationElapsedTime += delta;
    if (playerState == PlayerState.WALKING) {
      batch.draw(getActiveAnimation().getKeyFrame(animationElapsedTime), getX(), getY(), scale,
          scale);
    } else {
      batch.draw(getActiveAnimation().getKeyFrames()[0], getX(), getY(), scale, scale);
    }

  }

  @Override
  public void drawDebug(ShapeRenderer shapeRenderer, float delta) {

    for (Bomb bomb : bombs) {
      bomb.drawDebug(shapeRenderer, delta);
    }

    shapeRenderer.setColor(Color.TEAL);
    DebugUtils.drawRect(shapeRenderer, getBounds());

    shapeRenderer.setColor(Color.GREEN);

    DebugUtils.drawRect(shapeRenderer, getNorthRect());
    DebugUtils.drawRect(shapeRenderer, getSouthRect());
    DebugUtils.drawRect(shapeRenderer, getEastRect());
    DebugUtils.drawRect(shapeRenderer, getWestRect());

    shapeRenderer.setColor(Color.RED);
    DebugUtils.drawRect(shapeRenderer, collisionBurn);

  }

  @Override
  public void dispose() {
    spriteSheet.dispose();
  }

}
