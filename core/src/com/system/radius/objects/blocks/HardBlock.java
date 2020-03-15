package com.system.radius.objects.blocks;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.system.radius.objects.board.WorldConstants;

public class HardBlock extends Block {

  public HardBlock(float x, float y, float width, float height) {
    super(WorldConstants.BOARD_HARD_BLOCK, x, y, width, height);
  }

  @Override
  public void draw(Batch batch, float delta) {

  }

  @Override
  public void drawDebug(ShapeRenderer shapeRenderer, float delta) {

    shapeRenderer.setColor(Color.BLUE);
    shapeRenderer.rect(getX(), getY(), getWidth(), getHeight());

//    drawCollisionBounds(shapeRenderer);

  }

}
