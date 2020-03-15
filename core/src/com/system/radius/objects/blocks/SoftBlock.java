package com.system.radius.objects.blocks;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.system.radius.objects.board.BoardState;
import com.system.radius.objects.board.WorldConstants;

public class SoftBlock extends Block {

  public SoftBlock(float x, float y, float width, float height) {
    super(WorldConstants.BOARD_SOFT_BLOCK, x, y, width, height);
    this.life = 1;
  }

  @Override
  public void burn() {

    if (burning) {
      return;
    }

    life--;
    if (life == 0) {
      burning = true;
      System.out.println("Burned!");
      burnStart = System.currentTimeMillis();
    }
  }

  @Override
  public void update(float delta) {

    if (!burning) {
      return;
    }

    if (System.currentTimeMillis() - burnStart >= DESTROY_TIMER) {
      BoardState.getInstance().removeFromBoard(this);
    }

  }

  @Override
  public void drawDebug(ShapeRenderer shapeRenderer, float delta) {

    shapeRenderer.setColor(Color.MAGENTA);

    if (burning) {
      shapeRenderer.setColor(Color.RED);
    }

    shapeRenderer.rect(getX(), getY(), getWidth(), getHeight());

//    drawCollisionBounds(shapeRenderer);

  }

}
