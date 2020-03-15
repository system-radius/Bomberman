package com.system.radius.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.system.radius.enums.ScreenType;
import com.system.radius.managers.ScreenManager;
import com.system.radius.utils.ConfigUtils;
import com.system.radius.utils.FontUtils;

/**
 * The screen to be presented when the game loads.
 */
public class StartScreen extends AbstractScreen {

  /**
   * A stage instance for the rendering of the user interface.
   */
  private Stage stage;

  /**
   * The game instance for disposal and screen changing.
   */
  private Game game;

  /**
   * The skin to be used for displaying.
   */
  private Skin skin;

  public StartScreen(Game game) {
    super(game, ConfigUtils.getWorldWidth(), ConfigUtils.getWorldHeight(),
        ConfigUtils.getWorldScale());

    this.game = game;

  }

  @Override
  public void show() {

    // Load the skin file.
    skin = new Skin(Gdx.files.internal("ui/skin/uiskin.json"));

    // Create a table instance used to layout the screen contents.
    Table buttonTable = new Table(skin);

    // Create a stage instance with viewport corresponding to the scaled world width and world
    // height. Also set the stage as the input processor for the application.
    stage = new Stage(new FitViewport(scaledWorldWidth, scaledWorldHeight));
    Gdx.input.setInputProcessor(stage);

    // Create a label style for the game title and load the font from the FontUtils class with
    // the specified parameters.
    Label.LabelStyle titleLabelStyle = new Label.LabelStyle();
    titleLabelStyle.font = FontUtils.getFont(48, Color.WHITE, 2, Color.BLACK);

    // Create the title label.
    String text = "Boom!";
    Label label = new Label(text, titleLabelStyle);

    // Add a new row for the title label in the table.
    buttonTable.row().colspan(3).expandX().fillX();
    buttonTable.add(label).fillX();

    // Create the buttons to be used for the application.
    initializeButtons(buttonTable);

    // Align the center of the table with the screen center.
    buttonTable.setPosition((scaledWorldWidth) / 2, (scaledWorldHeight) / 2);

    // Add the table as an actor to the stage, this will let the table to be rendered.
    stage.addActor(buttonTable);

  }

  /**
   * Creates the button instances to be used for this screen.
   *
   * @param buttonTable - The table that will contain the buttons.
   */
  private void initializeButtons(Table buttonTable) {

    // The start button is highlighted to represent a positive action.
    final TextButton startButton = new TextButton("Start!", skin, "colored");

    // Create a new row for the start button, spanning three columns.
    buttonTable.row().colspan(3).expandX().fillX();
    buttonTable.add(startButton).fillX();

    // The quit button is a normal button.
    TextButton quitButton = new TextButton("Quit", skin, "default");

    // Create a new row for the quit button, spanning three columns.
    buttonTable.row().colspan(3).expandX().fillX();
    buttonTable.add(quitButton).fillX();

    startButton.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {

        if (startButton.isDisabled()) {
          return;
        }

        // Create a game screen and start the actual game.
        // Or simply create a character selection screen here.
        startButton.setDisabled(true);

        startGame();
      }
    });

    quitButton.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        // Quit the game and properly dispose the loaded assets.
        quit();
      }
    });

  }

  @Override
  public void resize(int width, int height) {
    stage.getViewport().update(width, height, true);
  }

  @Override
  public void update(float delta) {

    if (Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
      startGame();
    }

    stage.act(delta);
  }

  @Override
  public void draw(float delta) {

    stage.draw();

  }

  @Override
  public void dispose() {
    super.dispose();

    skin.dispose();
    stage.dispose();
  }

  public void quit() {
    game.dispose();
    Gdx.app.exit();
  }

  private void startGame() {

    GameScreen gameScreen = (GameScreen) ScreenManager.getScreen(ScreenType.GAME, game);
    gameScreen.reset();

    game.setScreen(gameScreen);
  }

}
