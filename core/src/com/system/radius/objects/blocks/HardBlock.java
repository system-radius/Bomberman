package com.system.radius.objects.blocks;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.system.radius.objects.board.WorldConstants;

public class HardBlock extends Block {

  public HardBlock(float x, float y, float width, float height) {
    super(WorldConstants.BOARD_HARD_BLOCK, x, y, width, height);
  }

  @Override
  protected void initialize() {
    TextureRegion[] frames = new TextureRegion[1];
    frames[0] = REGIONS[0][4];

    animation = new Animation<>(0, frames);

  }

  @Override
  public void drawDebug(ShapeRenderer shapeRenderer) {

    shapeRenderer.setColor(Color.BLUE);
    shapeRenderer.rect(getX(), getY(), getWidth(), getHeight());

//    drawCollisionBounds(shapeRenderer);

  }

}
