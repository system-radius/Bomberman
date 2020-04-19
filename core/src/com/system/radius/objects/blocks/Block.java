package com.system.radius.objects.blocks;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;
import com.system.radius.objects.AbstractBomberObject;
import com.system.radius.objects.board.BoardState;
import com.system.radius.objects.board.WorldConstants;
import com.system.radius.objects.players.Player;
import com.system.radius.utils.FieldConfig;

import java.util.List;

public class Block extends AbstractBomberObject implements Disposable {

  public static final Texture BLOCKS_SPRITE_SHEET = new Texture("neko/img/blocks.png");

  protected static final TextureRegion[][] REGIONS =
      TextureRegion.split(BLOCKS_SPRITE_SHEET, 32, 32);

  protected final static float DESTROY_TIMER = 1f;

  protected Animation<TextureRegion> animation;

  protected List<Player> players;

  protected Rectangle bounds;

  protected Texture spriteSheet;

  protected boolean activeCollision;

  protected boolean burning;

  protected boolean hasBonus;

  protected float burnTimer;

  private float width;

  private float height;

  public Block(char charRep, float x, float y, float width, float height, boolean hasBonus) {
    super(charRep, x, y);

    this.hasBonus = hasBonus;

    this.width = width;
    this.height = height;

    bounds = new Rectangle(x, y, width, height);
    activeCollision = true;

    initialize();
  }

  public Block(char charRep, float x, float y, float width, float height) {
    this(charRep, x, y, width, height, false);
  }

  public Block(float x, float y, float width, float height) {
    this(WorldConstants.BOARD_PERMA_BLOCK, x, y, width, height);
  }

  protected void initialize() {

    TextureRegion[] frames = new TextureRegion[1];
    frames[0] = REGIONS[FieldConfig.getFieldIndex()][6];

    animation = new Animation<>(0, frames);

  }

  public float getWidth() {
    return width;
  }

  public float getHeight() {
    return height;
  }

  public Rectangle getBounds() {
    return bounds;
  }

  public boolean isActiveCollision(Player player) {
    return activeCollision;
  }

  @Override
  public void burn() {

  }

  @Override
  public void update(float delta) {

    if (!burning) {
      return;
    }

    burnTimer += delta;
    animationElapsedTime += delta;
    if (burnTimer >= DESTROY_TIMER) {
      BoardState boardState = BoardState.getInstance();
      boardState.removeFromBoard(this);
      if (hasBonus) {
        boardState.addToBoard(Bonus.generateBonus(getX(), getY()));
      }
    }

  }

  @Override
  public void draw(Batch batch) {

    if (!burning) {
      batch.draw(animation.getKeyFrames()[0], getX(), getY(), getWidth(), getHeight());
      return;
    }

    batch.draw(animation.getKeyFrame(animationElapsedTime), getX(), getY(), getWidth(), getHeight());

  }

  @Override
  public void drawDebug(ShapeRenderer shapeRenderer) {

    shapeRenderer.setColor(Color.WHITE);
    shapeRenderer.rect(getX(), getY(), getWidth(), getHeight());

//    drawCollisionBounds(shapeRenderer);

  }

  @Override
  public void dispose() {
    spriteSheet.dispose();
  }
}
