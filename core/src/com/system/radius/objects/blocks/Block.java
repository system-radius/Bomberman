package com.system.radius.objects.blocks;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;
import com.system.radius.objects.AbstractBomberObject;
import com.system.radius.objects.board.WorldConstants;
import com.system.radius.objects.players.Player;

import java.util.List;

public class Block extends AbstractBomberObject implements Disposable {

  protected final static long DESTROY_TIMER = 1000;

  protected List<Player> players;

  protected Texture spriteSheet;

  protected Rectangle bounds;

  protected boolean activeCollision;

  protected boolean burning;

  protected long burnStart;

  private float width;

  private float height;

  public Block(char charRep, float x, float y, float width, float height) {
    super(charRep, x, y);

    this.width = width;
    this.height = height;

    bounds = new Rectangle(x, y, width, height);
    activeCollision = true;

  }

  public Block(float x, float y, float width, float height) {
    this(WorldConstants.BOARD_PERMA_BLOCK, x, y, width, height);
  }

//  public void collide(Player player) {
//
//    Rectangle playerBounds = player.getBounds();
//
//    float h2 = height / 2;
//    float w2 = width / 2;
//
//    if (Intersector.overlaps(playerBounds, northRect)) {
//      player.setY(this.y + height);
//    } else if (Intersector.overlaps(playerBounds, southRect)) {
//      player.setY(this.y - height);
//    }
//
//    if (Intersector.overlaps(playerBounds, eastRect)) {
//      player.setX(this.x + width);
//    } else if (Intersector.overlaps(playerBounds, westRect)) {
//      player.setX(this.x - width);
//    }
//
//  }

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

  }

  @Override
  public void draw(Batch batch, float delta) {

  }

  @Override
  public void drawDebug(ShapeRenderer shapeRenderer, float delta) {

    shapeRenderer.setColor(Color.WHITE);
    shapeRenderer.rect(getX(), getY(), getWidth(), getHeight());

//    drawCollisionBounds(shapeRenderer);

  }

  @Override
  public void dispose() {
    spriteSheet.dispose();
  }
}
