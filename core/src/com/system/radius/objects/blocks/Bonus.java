package com.system.radius.objects.blocks;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Intersector;
import com.system.radius.objects.board.WorldConstants;
import com.system.radius.objects.players.Player;

import java.util.Random;

public abstract class Bonus extends Block {

  private static final Random generator = new Random(System.currentTimeMillis());

  protected static final int BOMB_LIMIT =
      (int) ((WorldConstants.WORLD_HEIGHT * WorldConstants.WORLD_WIDTH) / 2);

  protected static final int FIRE_LIMIT = (int) Math.max(WorldConstants.WORLD_WIDTH,
      WorldConstants.WORLD_HEIGHT);

  protected static final float SPEED_LIMIT = 4;

  private int bonusIndex;

  public Bonus(int index, float x, float y, float width, float height) {
    super(WorldConstants.BOARD_BONUS, x, y, width, height);
    this.life = 1;
    this.bonusIndex = index;
    this.activeCollision = false;

    // Call the initialize method again as the first call from the Block object will not properly
    // set the icon for this bonus object.
    initialize();
  }

  @Override
  protected final void initialize() {
    TextureRegion[] frames = new TextureRegion[1];
    frames[0] = REGIONS[7][bonusIndex];

    animation = new Animation<>(0, frames);
  }

  @Override
  public void burn() {

    burning = true;
    burnTimer = 1f;

  }

  public void collide(Player player) {

    if (!Intersector.overlaps(this.bounds, player.getBurnCollision())) {
      return;
    }

    applyBonus(player);
  }

  /**
   * Applies the bonus to the specified player.
   *
   * @param player - The player that got in touch with the bonus block.
   */
  public abstract void applyBonus(Player player);

  public static boolean hasBonus() {

    return generator.nextInt(100) >= 75;
  }

  public static Bonus generateBonus(float x, float y) {

    float scale = WorldConstants.WORLD_SCALE;
    int bonusChance = generator.nextInt(10);
    Bonus bonus;

    if (bonusChance >= 0 && bonusChance < 3) {
      bonus = new BombBonus(x, y, scale, scale);
    } else if (bonusChance >= 3 && bonusChance < 6) {
      bonus = new FireBonus(x, y, scale, scale);
    } else if (bonusChance >= 6 && bonusChance < 9) {
      bonus = new SpeedBonus(x, y, scale, scale);
    } else {
      bonus = new FlashFireBonus(x, y, scale, scale);
    }

    return bonus;
  }

}
