package com.system.radius.objects.players;

import com.system.radius.objects.board.BoardState;

public class Player1 extends Player {

  public Player1(float x, float y, float scale) {
    super("neko/img/tokoy_sprite_sheet.png", x, y, scale);
  }

  @Override
  public void useAbility() {

  }

  @Override
  public void burn() {

  }

  @Override
  public void update(float delta) {
    super.update(delta);

    x += velX * delta;
    y += velY * delta;

    // Consume the velocity
//    velX = velY = 0;

    updateBounds();

    collide(BoardState.getInstance().getSurroundingBlocks(this));
  }
}
