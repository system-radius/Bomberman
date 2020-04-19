package com.system.radius.objects.blocks;

import com.system.radius.objects.players.Player;

public class FireBonus extends Bonus {

  public FireBonus(float x, float y, float width, float height) {
    super(1, x, y, width, height);
  }

  @Override
  public void applyBonus(Player player) {

    int nextFirePower = player.getFirePower() + 1;
    burn();

    if (nextFirePower > FIRE_LIMIT) {
      return;
    }

    player.setFirePower(nextFirePower);

  }

}
