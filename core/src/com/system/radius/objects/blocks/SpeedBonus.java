package com.system.radius.objects.blocks;

import com.system.radius.objects.players.Player;

public class SpeedBonus extends Bonus {

  public SpeedBonus(float x, float y, float width, float height) {
    super(3, x, y, width, height);
  }

  @Override
  public void applyBonus(Player player) {

    float nextSpeedLevel = player.getSpeedLevel() + 0.5f;

    if (nextSpeedLevel > SPEED_LIMIT) {
      return;
    }

    System.out.println("Current speed: " + nextSpeedLevel);

    player.setSpeed(nextSpeedLevel);
    burn();
  }
}
