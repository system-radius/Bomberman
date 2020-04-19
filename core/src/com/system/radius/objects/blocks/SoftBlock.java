package com.system.radius.objects.blocks;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.system.radius.objects.board.WorldConstants;
import com.system.radius.utils.FieldConfig;

public class SoftBlock extends Block {

  public SoftBlock(float x, float y, float width, float height, boolean hasBonus) {
    super(WorldConstants.BOARD_SOFT_BLOCK, x, y, width, height, hasBonus);
    this.life = 1;
  }

  @Override
  protected void initialize() {
    TextureRegion[] frames = new TextureRegion[3];
    System.arraycopy(REGIONS[FieldConfig.getFieldIndex()], 0, frames, 0, 3);

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
    }
  }

  @Override
  public void drawDebug(ShapeRenderer shapeRenderer) {

    shapeRenderer.setColor(hasBonus ? Color.GOLD : Color.MAGENTA);

    if (burning) {
      shapeRenderer.setColor(Color.RED);
    }

    shapeRenderer.rect(getX(), getY(), getWidth(), getHeight());

//    drawCollisionBounds(shapeRenderer);

  }

}
