package com.system.radius.objects.players;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.system.radius.enums.Direction;
import com.system.radius.enums.PlayerState;

import java.util.UUID;

public class Entity {

  private static final String TAG = Entity.class.getSimpleName();

  private Vector2 velocity;

  private String entityId;

  private Direction currentDirection = Direction.LEFT;

  private Direction prevDirection = Direction.UP;

  private Animation walkLeft;

  private Animation walkRight;

  private Animation walkUp;

  private Animation walkDown;

  private Array<TextureRegion> walkLeftFrames;

  private Array<TextureRegion> walkRightFrames;

  private Array<TextureRegion> walkUpFrames;

  private Array<TextureRegion> walkDownFrames;

  protected Vector2 nextPlayerPosition;

  protected Vector2 currentPlayerPosition;

  protected PlayerState state = PlayerState.IDLE;

  protected float frameTime = 0f;

  protected Sprite frameSprite = null;

  protected TextureRegion currentFrame = null;

  public final int frame_width = 64;

  public final int frame_height = 64;

  public Rectangle boundingBox;

  public Entity() {
    initEntity();
    init(0, 0);
  }

  public void initEntity() {
    this.entityId = UUID.randomUUID().toString();
    this.nextPlayerPosition = new Vector2();
    this.currentPlayerPosition = new Vector2();
    this.boundingBox = new Rectangle();
    this.velocity = new Vector2(2f, 2f);

    loadDefaultSprite();
  }

  public void update(float delta) {
    frameTime = (frameTime + delta) % 5;

    setBoundingBoxSize(0f, 0.5f);
  }

  public void init(float startX, float startY) {
    this.currentPlayerPosition.x = this.nextPlayerPosition.x = startX;
    this.currentPlayerPosition.y = this.nextPlayerPosition.y = startY;
  }

  public void setBoundingBoxSize(float widthReduction, float heightReduction) {
    float widthReductAmount = 1f - widthReduction;
    float heightReductAmount = 1f - heightReduction;

    float width;
    float height;

    if (widthReductAmount > 0 && widthReductAmount < 1f) {
      width = frame_width * widthReductAmount;
    } else {
      width = frame_width;
    }

    if (heightReductAmount > 0 && heightReductAmount < 1f) {
      height = frame_height * heightReductAmount;
    } else {
      height = frame_height;
    }

    if (width == 0 || height == 0) {
      Gdx.app.debug(TAG, "Width or height is 0! " + width + " : " + height);
    }

    boundingBox.set(nextPlayerPosition.x, nextPlayerPosition.y, width, height);
  }

  private void loadDefaultSprite() {
    Texture texture = new Texture(Gdx.files.internal("player.png"));

    TextureRegion[][] regions = TextureRegion.split(texture, frame_width, frame_height);
    frameSprite = new Sprite(regions[0][0].getTexture(), 0, 0, frame_width, frame_height);

    currentFrame = regions[0][0];

    loadAnimations(regions);
  }

  private void loadAnimations(TextureRegion[][] textureFrames) {

    int capacity = 6;

    walkUpFrames = new Array<>(capacity);
    walkDownFrames = new Array<>(capacity);
    walkLeftFrames = new Array<>(capacity);
    walkRightFrames = new Array<>(capacity);

    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < capacity; i++) {
        TextureRegion region = textureFrames[i][j];

        switch(i) {
          case 0:
            walkDownFrames.insert(j, region);
            break;
          case 1:
            walkUpFrames.insert(j, region);
            break;
          case 2:
            walkLeftFrames.insert(j, region);
            break;
          case 3:
            walkRightFrames.insert(j, region);
            break;
        }
      }
    }

    float frameDuration = 0.17f;
    walkDown = new Animation(frameDuration, walkDownFrames, Animation.PlayMode.LOOP);
    walkUp = new Animation(frameDuration, walkUpFrames, Animation.PlayMode.LOOP);
    walkLeft = new Animation(frameDuration, walkLeftFrames, Animation.PlayMode.LOOP);
    walkRight = new Animation(frameDuration, walkRightFrames, Animation.PlayMode.LOOP);

  }

}
