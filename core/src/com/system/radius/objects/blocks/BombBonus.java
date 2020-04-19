package com.system.radius.objects.blocks;

import com.system.radius.objects.players.Player;

public class BombBonus extends Bonus {

  public BombBonus(float x, float y, float width, float height) {
    super(0, x, y, width, height);
  }

  @Override
  public void applyBonus(Player player) {

    int nextBombStock = player.getBombStock() + 1;

    if (nextBombStock > BOMB_LIMIT) {
      return;
    }

    player.setBombStock(nextBombStock);
    burn();

  }
}
