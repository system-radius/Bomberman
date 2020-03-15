package com.system.radius.controllers;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.system.radius.enums.Keys;
import com.system.radius.objects.players.Player;

import java.util.HashMap;
import java.util.Map;

public class Player2Controller implements InputProcessor {

  private static final String TAG = Player1Controller.class.getSimpleName();

  private static Map<Keys, Boolean> keys = new HashMap<>();

  private Player player;

  //initialize the hashmap for inputs
  static {
    keys.put(Keys.LEFT, false);
    keys.put(Keys.RIGHT, false);
    keys.put(Keys.UP, false);
    keys.put(Keys.DOWN, false);
    keys.put(Keys.QUIT, false);
    keys.put(Keys.BOMB, false);
  }

  public Player2Controller(Player player) {
    this.player = player;
  }

  @Override
  public boolean keyDown(int keycode) {

    if (keycode == Input.Keys.LEFT) {

      if (keys.get(Keys.RIGHT)) {
        return false;
      }
      pressLeft(true);
      return true;
    } else if (keycode == Input.Keys.RIGHT) {

      if (keys.get(Keys.RIGHT)) {
        return false;
      }
      pressRight(true);
      return true;
    } else if (keycode == Input.Keys.DOWN) {

      if (keys.get(Keys.UP)) {
        return false;
      }
      pressDown(true);
      return true;
    } else if (keycode == Input.Keys.UP) {

      if (keys.get(Keys.DOWN)) {
        return false;
      }
      pressUp(true);
      return true;
    } else if (keycode == Input.Keys.ENTER) {

      pressBomb(true);
      return true;
    } else if (keycode == Input.Keys.ESCAPE) {

      pressQuit(true);
      return true;
    }

    return false;
  }

  @Override
  public boolean keyUp(int keycode) {

    if (keycode == Input.Keys.LEFT) {

      if (keys.get(Keys.RIGHT)) {
        return false;
      }
      pressLeft(false);
      return true;
    } else if (keycode == Input.Keys.RIGHT) {

      if (keys.get(Keys.LEFT)) {
        return false;
      }
      pressRight(false);
      return true;
    } else if (keycode == Input.Keys.DOWN) {

      if (keys.get(Keys.UP)) {
        return false;
      }
      pressDown(false);
      return true;
    } else if (keycode == Input.Keys.UP) {

      if (keys.get(Keys.DOWN)) {
        return false;
      }
      pressUp(false);
      return true;
    } else if (keycode == Input.Keys.ENTER) {

      pressBomb(false);
      return true;
    } else if (keycode == Input.Keys.ESCAPE) {

      pressQuit(false);
      return true;
    }

    return false;
  }

  @Override
  public boolean keyTyped(char character) {
    return false;
  }

  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {

    if (button == Input.Buttons.LEFT || button == Input.Buttons.RIGHT) {
//      this.setClickedMouseCoordinates(screenX, screenY);
    }

    if (button == Input.Buttons.LEFT) {
      // selection
    }

    if (button == Input.Buttons.RIGHT) {
      // action
    }

    return true;
  }

  @Override
  public boolean touchUp(int screenX, int screenY, int pointer, int button) {

    if (button == Input.Buttons.LEFT) {
      // selection
    }

    if (button == Input.Buttons.RIGHT) {
      // action
    }

    return true;
  }

  @Override
  public boolean touchDragged(int screenX, int screenY, int pointer) {
    return false;
  }

  @Override
  public boolean mouseMoved(int screenX, int screenY) {
    return false;
  }

  @Override
  public boolean scrolled(int amount) {
    return false;
  }

  public void pressLeft(boolean press) {
    keys.put(Keys.LEFT, press);

    player.setVelX(press ? -player.getSpeed() : 0);

  }

  public void pressRight(boolean press) {
    keys.put(Keys.RIGHT, press);

    player.setVelX(press ? player.getSpeed() : 0);

  }

  public void pressDown(boolean press) {
    keys.put(Keys.DOWN, press);

    player.setVelY(press ? -player.getSpeed() : 0);

  }

  public void pressUp(boolean press) {
    keys.put(Keys.UP, press);

    player.setVelY(press ? player.getSpeed() : 0);

  }

  public void pressBomb(boolean press) {
    keys.put(Keys.BOMB, press);

    if (press) {
      player.plantBomb();
    }
  }

  public void pressQuit(boolean press) {
    keys.put(Keys.QUIT, press);
  }

}
