package com.system.radius.enums;

import com.badlogic.gdx.Input;

public enum Direction {

  UP (Input.Keys.W, Input.Keys.S),

  DOWN (Input.Keys.S, Input.Keys.W),

  LEFT (Input.Keys.A, Input.Keys.D),

  RIGHT (Input.Keys.D, Input.Keys.A),

  UP_LEFT (0, 0),

  UP_RIGHT (0, 0),

  DOWN_LEFT (0, 0),

  DOWN_RIGHT (0, 0);

  private int key;

  private int oppositeKey;

  Direction (int keyCode, int oppositeKey) {
    this.key = keyCode;
    this.oppositeKey = oppositeKey;
  }

  public int getKeyCode() {
    return key;
  }

  public int getOppositeKey () {
    return oppositeKey;
  }

}
