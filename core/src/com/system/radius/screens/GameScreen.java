package com.system.radius.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.system.radius.ai.Ai;
import com.system.radius.controllers.Player1Controller;
import com.system.radius.enums.ScreenType;
import com.system.radius.managers.ScreenManager;
import com.system.radius.objects.blocks.Block;
import com.system.radius.objects.blocks.Bonus;
import com.system.radius.objects.blocks.HardBlock;
import com.system.radius.objects.blocks.SoftBlock;
import com.system.radius.objects.board.BoardState;
import com.system.radius.objects.board.WorldConstants;
import com.system.radius.objects.bombs.Bomb;
import com.system.radius.objects.players.Player;
import com.system.radius.objects.players.Player1;
import com.system.radius.utils.BombermanLogger;
import com.system.radius.utils.FieldConfig;

import java.util.ArrayList;
import java.util.List;

public class GameScreen extends AbstractScreen {

  private static final BombermanLogger LOGGER =
      new BombermanLogger(GameScreen.class.getSimpleName());

  private ShapeRenderer shapeRenderer;

  private OrthographicCamera camera;

  private Viewport viewport;

  private BoardState boardState;

  private List<Player> players;

  private List<Ai> ais;

  private int playerCount = 4;

  private boolean allowPlayer = false;

  private boolean randomize = true;

  private boolean allowDebug = false;

  public GameScreen(Game game) {
    super(game, WorldConstants.WORLD_WIDTH, WorldConstants.WORLD_HEIGHT,
        WorldConstants.WORLD_SCALE);
  }

  public void reset() {

    LOGGER.info(" = = = = = = = = = = = = = = = = = = = = = = = = ");
    Gdx.app.setApplicationLogger(new BombermanLogger());
    Gdx.app.setLogLevel(Logger.INFO);

    FieldConfig.reset();
    color = allowDebug ? Color.BLACK : FieldConfig.getColorScheme();

    boardState = BoardState.getInstance();
    boardState.reset();

    float spacing = 2f;

    for (int x = 0; x < worldWidth; x++) {
      for (int y = 0; y < worldHeight; y++) {

        if (x == 0 || y == 0 || x + 1 == worldWidth || y + 1 == worldHeight) {
          boardState.addToBoard(new Block(x * scale, y * scale, scale, scale));
        } else if (x % spacing == 0 && y % spacing == 0) {
          boardState.addToBoard(new HardBlock(x * scale, y * scale, scale, scale));
        }
      }
    }

    if (randomize) {
      randomizeField();
    }
    // Board construction should be done first before creating the players.
    createPlayers();
  }

  private void createPlayers() {
    ais = new ArrayList<>();

    switch (playerCount) {
      case 4:
        //Bottom-right
        ais.add(new Ai(new Player1((worldWidth - 2) * scale, 1f * scale, scale)));
      case 3:
        // Top-left
        ais.add(new Ai(new Player1(1f * scale, (worldHeight - 2) * scale, scale)));
      case 2:
        // Top-right
        ais.add(new Ai(new Player1((worldWidth - 2) * scale, (worldHeight - 2) * scale, scale)));
      case 1:
      default:
        if (allowPlayer) {
          // Create a player instance if allowed.
          Player player = new Player1(1f * scale, 1f * scale, scale);
          boardState.addPlayer(player);

          player.setSpeed(2);

          break;
        }

        // Otherwise, create an AI.
        // Bottom-left
        ais.add(new Ai(new Player1(1f * scale, 1f * scale, scale)));
    }

    players = new ArrayList<>(boardState.getPlayers());

    for (Ai ai : ais) {
      boardState.addPlayer(ai.getPlayer());
    }
  }

  private void randomizeField() {

    int totalArea = (int) (worldWidth * worldHeight);
    int boundingBlocks = (int) (worldWidth * 2) + (int) (worldHeight * 2);

    int placeableBlocks = totalArea - boundingBlocks;

    for (int i = 0; i < placeableBlocks; i++) {
      int tempX = (int) (Math.random() * worldWidth);
      int tempY = (int) (Math.random() * worldHeight);

      if (boardState.getChar(tempX, tempY) != WorldConstants.BOARD_EMPTY || isBoardCorner(tempX,
          tempY)) {
        continue;
      }

      boardState.addToBoard(new SoftBlock(tempX * scale, tempY * scale, scale, scale, Bonus.hasBonus()));
    }

  }

  private boolean isBoardCorner(int x, int y) {
    return ((x >= worldWidth - 3 || x <= 2) && (y >= worldHeight - 3 || y <= 2));
  }

  @Override
  public void show() {

    if (players.size() > 0) {
      InputMultiplexer multiplexer = new InputMultiplexer();
      multiplexer.addProcessor(new Player1Controller(players.get(0)));
      //    multiplexer.addProcessor(new Player2Controller());

      Gdx.input.setInputProcessor(multiplexer);
    }

    shapeRenderer = new ShapeRenderer();

    float divider = 2f;
    camera = new OrthographicCamera(scaledWorldWidth / divider, scaledWorldHeight / divider);
    camera.position.set(0, 0, 0);
    updateCamera();

    viewport = new FitViewport(651, 357, camera);

  }

  @Override
  public void resize(int width, int height) {
    viewport.update(width, height);
  }

  @Override
  public void update(float delta) {

    if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
      game.setScreen(ScreenManager.getScreen(ScreenType.START, game));
    }

    boardState.updateObjects(delta);
//    processInput();
    for (Player player : players) {
      player.update(delta);
    }

    for (Ai ai : ais) {
      ai.update(delta);
    }

    updateCamera();

  }

  @Override
  public void draw() {

    spriteBatch.setProjectionMatrix(camera.projection);
    spriteBatch.setTransformMatrix(camera.view);

    spriteBatch.begin();

    drawPlayer();
    for (Ai ai : ais) {
      ai.draw(spriteBatch);
    }

    drawGrid();
    spriteBatch.end();

    if (allowDebug) {
      drawDebug();
    }
  }

  private void drawDebug() {
    shapeRenderer.setProjectionMatrix(camera.projection);
    shapeRenderer.setTransformMatrix(camera.view);
    shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

    drawGridDebug();

    for (Player player : players) {
      player.drawDebug(shapeRenderer);
    }

    for (Ai ai : ais) {
      ai.drawDebug(shapeRenderer);
    }

    shapeRenderer.end();
  }

  private void drawPlayer() {

    for (Player player : players) {
      player.draw(spriteBatch);
    }

  }

  private void drawGrid() {

    BoardState boardState = BoardState.getInstance();
    for (int x = 0; x < worldWidth; x++) {
      for (int y = 0; y < worldHeight; y++) {

        if (boardState.getChar(x, y) == WorldConstants.BOARD_EMPTY ||
            boardState.getChar(x, y) == WorldConstants.BOARD_BOMB) {
          continue;
        }
        boardState.getObject(x, y).draw(spriteBatch);

      }
    }

  }

  private void drawGridDebug() {

    BoardState boardState = BoardState.getInstance();
    for (int x = 0; x < worldWidth; x++) {
      for (int y = 0; y < worldHeight; y++) {

        if (boardState.getChar(x, y) != WorldConstants.BOARD_EMPTY) {
          boardState.getObject(x, y).drawDebug(shapeRenderer);
        }

      }
    }

  }

  @Override
  public void dispose() {
    shapeRenderer.dispose();
    Block.BLOCKS_SPRITE_SHEET.dispose();
    Bomb.BOMB_SPRITE_SHEET.dispose();
    Bomb.FIRE_SPRITE_SHEET.dispose();
  }

  private void updateCamera() {

    float effectiveViewportWidth = camera.viewportWidth / 2f;
    float effectiveViewportHeight = camera.viewportHeight / 2f;

    if (players.size() > 0) {
      camera.position.x = players.get(0).getX();
      camera.position.y = players.get(0).getY();
    }
    camera.position.x = MathUtils.clamp(camera.position.x, effectiveViewportWidth,
        scaledWorldWidth - effectiveViewportWidth);
    camera.position.y = MathUtils.clamp(camera.position.y, effectiveViewportHeight,
        scaledWorldHeight - effectiveViewportHeight);

    camera.update();

  }
}
