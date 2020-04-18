package com.system.radius.objects.blocks;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.system.radius.objects.board.BoardState;
import com.system.radius.objects.board.WorldConstants;

public class SoftBlock extends Block {

  public SoftBlock(float x, float y, float width, float height) {
    super(WorldConstants.BOARD_SOFT_BLOCK, x, y, width, height);
    this.life = 1;
  }

  @Override
  protected void initialize() {
    TextureRegion[] frames = new TextureRegion[3];
    System.arraycopy(REGIONS[0], 0, frames, 0, 3);

    animation = new Animation<>(1f / 3f, frames);
  }

  @Override
  public void burn() {

    if (burning) {
      return;
    }

    life--;
    if (life == 0) {
      burning = true;
      burnStart = System.currentTimeMillis();
    }
  }

  @Override
  public void update(float delta) {

    if (!burning) {
      return;
    }

    animationElapsedTime += delta;
    if (System.currentTimeMillis() - burnStart >= DESTROY_TIMER) {
      BoardState.getInstance().removeFromBoard(this);
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

    shapeRenderer.setColor(Color.MAGENTA);

    if (burning) {
      shapeRenderer.setColor(Color.RED);
    }

    shapeRenderer.rect(getX(), getY(), getWidth(), getHeight());

//    drawCollisionBounds(shapeRenderer);

  }

}
