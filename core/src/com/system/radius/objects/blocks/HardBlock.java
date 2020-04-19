package com.system.radius.objects.blocks;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.system.radius.objects.board.BoardState;
import com.system.radius.objects.board.WorldConstants;
import com.system.radius.utils.FieldConfig;

public class HardBlock extends Block {

  public HardBlock(float x, float y, float width, float height) {
    super(WorldConstants.BOARD_HARD_BLOCK, x, y, width, height, true);
    this.life = 10;
  }

  @Override
  protected void initialize() {
    TextureRegion[] frames = new TextureRegion[3];
    System.arraycopy(REGIONS[FieldConfig.getFieldIndex()], 3, frames, 0, 3);

    animation = new Animation<>(1f / 3f, frames);

  }

  @Override
  public void burn() {

    if (burning) {
      return;
    }

    life--;

    if (life == 1) {
      // Change the marking of this block as soft block.
      BoardState boardState = BoardState.getInstance();
      boardState.setChar(boardState.getExactX(this), boardState.getExactY(this),
          WorldConstants.BOARD_SOFT_BLOCK);
    }

    if (life == 0) {
      burning = true;
    }

  }

  @Override
  public void drawDebug(ShapeRenderer shapeRenderer) {

    shapeRenderer.setColor(Color.BLUE);
    shapeRenderer.rect(getX(), getY(), getWidth(), getHeight());

//    drawCollisionBounds(shapeRenderer);

  }

}
