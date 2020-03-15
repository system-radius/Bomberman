package com.system.radius.ui;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Disposable;
import com.system.radius.utils.DebugUtils;

public class BorderedTextButton extends TextButton implements Disposable {

  private ShapeRenderer renderer;

  private Rectangle rectangle;

  private Stage stage;

  public BorderedTextButton(Stage stage, String text, TextButtonStyle style) {
    super(text, style);

    this.stage = stage;
    renderer = new ShapeRenderer();
    rectangle = new Rectangle(0, 0, getWidth(), getHeight());

  }

  @Override
  public void setPosition(float x, float y) {
    super.setPosition(x, y);

    rectangle.setPosition(x, y);
  }

  @Override
  public void draw(Batch batch, float delta) {
    super.draw(batch, delta);

    Camera camera = stage.getCamera();
    renderer.setProjectionMatrix(camera.projection);
    renderer.setTransformMatrix(camera.view);

    renderer.begin(ShapeRenderer.ShapeType.Line);
    DebugUtils.drawRect(renderer, rectangle);
    renderer.end();

  }

  @Override
  public void dispose() {
    renderer.dispose();
  }

}
