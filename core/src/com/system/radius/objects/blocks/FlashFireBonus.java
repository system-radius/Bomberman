package com.system.radius.objects.blocks;

import com.system.radius.objects.players.Player;

public class FlashFireBonus extends Bonus {

  public FlashFireBonus(float x, float y, float width, float height) {
    super(2, x, y, width, height);
  }

  @Override
  public void applyBonus(Player player) {

    int nextFirePower = player.getFirePower() + 3;

    if (nextFirePower > FIRE_LIMIT) {
      return;
    }

    player.setFirePower(nextFirePower);
    burn();

  }

}
