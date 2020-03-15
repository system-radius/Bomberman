package com.system.radius.objects.bombs;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.system.radius.objects.players.Player;

/**
 * Represents the default bomb.
 */
public class NekoBomb extends Bomb {

  public NekoBomb(Player owner, float x, float y, float width, float height) {
    super(owner, "neko/img/neko_sprite_sheet.png", x, y, width, height);
  }

  @Override
  protected void loadAssets() {
    super.loadAssets();

    TextureRegion[][] allFrames = TextureRegion.split(spriteSheet, 32, 32);
    TextureRegion[] nekoFrames = new TextureRegion[allFrames[0].length];

    System.arraycopy(allFrames[2], 0, nekoFrames, 0, nekoFrames.length);
    breathingAnimation = new Animation<>(FRAME_DURATION_BREATHING, nekoFrames);
    breathingAnimation.setPlayMode(Animation.PlayMode.LOOP);

    TextureRegion[] headExFrames = new TextureRegion[2];
    System.arraycopy(allFrames[3], 0, headExFrames, 0, 2);
    headExAnimation = new Animation<>(FRAME_DURATION_EXPLODING, headExFrames);
    headExAnimation.setPlayMode(Animation.PlayMode.LOOP);
  }

}
