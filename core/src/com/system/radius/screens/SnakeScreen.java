package com.system.radius.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.system.radius.enums.Direction;
import com.system.radius.enums.GameState;

import static com.system.radius.enums.Direction.RIGHT;
import static com.system.radius.enums.Direction.UP;
import static com.system.radius.enums.GameState.PLAYING;

public class SnakeScreen extends AbstractScreen {

  private static final String GAME_OVER_TEXT = "Game Over! Tap space to restart!";

  private static final float MOVE_TIME = 0.1f;

  private static final int POINTS_PER_APPLE = 20;

  private Color color = Color.GRAY;

  private Viewport viewport;

  private Camera camera;

  private BitmapFont bitmapFont;

  private GlyphLayout glyphLayout = new GlyphLayout();

  private ShapeRenderer shapeRenderer;

  private Texture snakeHead;

  private Texture snakeBody;

  private Texture apple;

  private Direction direction = RIGHT;

  private GameState state = PLAYING;

  private Array<BodyPart> bodyParts = new Array<>();

  private boolean appleAvailable = false;

  private float timer = MOVE_TIME;

  private int score = 0;

  private int appleX = 0;

  private int appleY = 0;

  private int snakeX = 0;

  private int snakeY = 0;

  public SnakeScreen(Game game) {
    super(game, 20, 10, 64);
  }

  @Override
  public void show() {

    shapeRenderer = new ShapeRenderer();
    bitmapFont = new BitmapFont();

    camera = new OrthographicCamera(screenWidth, screenHeight);
    camera.position.set((scaledWorldWidth / 2), (scaledWorldHeight / 2), 0);
    camera.update();

//    viewport = new FitViewport(scaledWorldWidth, scaledWorldHeight, camera);
    viewport = new StretchViewport(scaledWorldWidth, scaledWorldHeight, camera);

    System.out.println(scaledWorldWidth + " x " + scaledWorldHeight);
    System.out.println("(" + camera.position.x + ", " + scaledWorldHeight + ")");

    snakeHead = new Texture(Gdx.files.internal("head.png"));
    snakeBody = new Texture(Gdx.files.internal("body.png"));
    apple = new Texture(Gdx.files.internal("apple.png"));

  }

  public void update(float delta) {

    switch (state) {
      case PLAYING:
        queryInput();

        timer -= delta;
        if (timer <= 0) {
          // moveSnake the snake
          moveSnake();
          checkBodyCollision();
        }
        checkAppleCollision();
        checkAndPlaceApple();

        break;
      case GAME_OVER:
        checkForRestart();
        break;
    }
  }

  public void draw() {

    Gdx.gl.glClearColor(color.r, color.g, color.b, color.a);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

//    drawGrid();

    spriteBatch.setProjectionMatrix(camera.projection);
    spriteBatch.setTransformMatrix(camera.view);

    spriteBatch.begin();
    spriteBatch.draw(snakeHead, snakeX, snakeY, scale, scale);

    for (BodyPart bodyPart : bodyParts) {
      bodyPart.draw(spriteBatch);
    }

    if (appleAvailable) {
      spriteBatch.draw(apple, appleX, appleY, scale, scale);
    }

    if (state == GameState.GAME_OVER) {
      glyphLayout.setText(bitmapFont, GAME_OVER_TEXT);

      bitmapFont.draw(spriteBatch, GAME_OVER_TEXT, (camera.position.x - (glyphLayout.width / 2)),
          (scaledWorldHeight + scale / 2));
    } else {
      drawScore(spriteBatch);
    }
    spriteBatch.end();
  }

  private void drawScore(Batch batch) {
    if (PLAYING != state) {
      return;
    }

    String scoreAsString = Integer.toString(score);
    glyphLayout = new GlyphLayout(bitmapFont, scoreAsString);

    bitmapFont.draw(batch, scoreAsString, ((camera.position.x) - (glyphLayout.width / 2)),
        (scaledWorldHeight + scale / 2));
  }

  public void drawGrid() {

    shapeRenderer.setProjectionMatrix(camera.projection);
    shapeRenderer.setTransformMatrix(camera.view);
    shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

    for (int x = 0; x < scaledWorldWidth; x += scale) {
      for (int y = 0; y < scaledWorldHeight; y += scale) {
        shapeRenderer.rect(x, y, scale, scale);
      }
    }

    shapeRenderer.end();
  }

  /**
   * To be called if the timer needs reset.
   */
  public void moveSnake() {
    // reset the timer
    timer = MOVE_TIME;

    // move the snake based on the past position of the front pieces
    for (int i = bodyParts.size - 1; i >= 0; i--) {
      BodyPart part = bodyParts.get(i);
      if (i != 0) {
        BodyPart past = bodyParts.get(i - 1);
        part.updateBodyPosition(past.x, past.y);
      } else {
        part.updateBodyPosition(snakeX, snakeY);
      }
    }

    // move the snake according to the direction.
    switch (direction) {
      case RIGHT:
        snakeX = (int) (snakeX >= scaledWorldWidth - scale ? 0 : snakeX + scale);
        break;
      case LEFT:
        snakeX = (int) (snakeX <= 0 ? (int) scaledWorldWidth - scale : snakeX - scale);
        break;
      case DOWN:
        snakeY = (int) (snakeY <= 0 ? (int) scaledWorldHeight - scale : snakeY - scale);
        break;
      case UP:
        snakeY = (int) (snakeY >= scaledWorldHeight - scale ? 0 : snakeY + scale);
    }

//    camera.position.set(snakeX, snakeY, 0);
//    camera.update();

  }

  private void checkForRestart() {
    if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
      doRestart();
    }
  }

  private void doRestart() {
    state = PLAYING;
    bodyParts.clear();
    direction = RIGHT;
    timer = MOVE_TIME;

    snakeX = snakeY = 0;
    appleAvailable = false;

    score = 0;

  }

  private void checkBodyCollision() {

    if (checkPosition(snakeX, snakeY)) {
      state = GameState.GAME_OVER;
    }
  }

  private void checkAppleCollision() {

    if (!appleAvailable) {
      return;
    }

    if (snakeX == appleX && snakeY == appleY) {
      appleAvailable = false;
      bodyParts.add(new BodyPart(snakeBody));
      addToScore();
    }

  }

  private void addToScore() {
    score += POINTS_PER_APPLE;
  }

  private void checkAndPlaceApple() {
    if (appleAvailable) {
      return;
    }

    do {
      appleX = (int) (MathUtils.random((int) worldWidth - 1) * scale);
      appleY = (int) (MathUtils.random((int) worldHeight - 1) * scale);
      appleAvailable = true;
    } while (checkPosition(appleX, appleY) || (appleX == snakeX && appleY == snakeY));
  }

  private boolean checkPosition(int x, int y) {
    for (BodyPart bodyPart : bodyParts) {
      if (bodyPart.x == x && bodyPart.y == y) {
        return true;
      }
    }

    return false;
  }

  private void queryInput() {

    boolean wPressed = Gdx.input.isKeyPressed(UP.getKeyCode());
    boolean sPressed = Gdx.input.isKeyPressed(Direction.DOWN.getKeyCode());
    boolean aPressed = Gdx.input.isKeyPressed(Direction.LEFT.getKeyCode());
    boolean dPressed = Gdx.input.isKeyPressed(Direction.RIGHT.getKeyCode());

    if (wPressed && direction != Direction.DOWN) {
      direction = UP;
    } else if (sPressed && direction != UP) {
      direction = Direction.DOWN;
    } else if (aPressed && direction != Direction.RIGHT) {
      direction = Direction.LEFT;
    } else if (dPressed && direction != Direction.LEFT) {
      direction = Direction.RIGHT;
    }

  }

  private class BodyPart {

    private Texture texture;

    private int x = (int) -scale;
    private int y = (int) -scale;

    public BodyPart(Texture texture) {
      this.texture = texture;
    }

    public void updateBodyPosition(int x, int y) {
      this.x = x;
      this.y = y;
    }

    public void draw(Batch batch) {
      if (!(x == snakeX && y == snakeY)) {
        batch.draw(texture, x, y, scale, scale);
      }
    }
  }

}
