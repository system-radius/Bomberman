package com.system.radius.enums;

import com.system.radius.screens.AbstractScreen;
import com.system.radius.screens.GameScreen;
import com.system.radius.screens.StartScreen;

/**
 * Screen types for caching the screens.
 */
public enum ScreenType {

  /**
   * Start screen
   */
  START(StartScreen.class),

  /**
   * Game screen
   */
  GAME(GameScreen.class);

  private Class<? extends AbstractScreen> screen;

  ScreenType(Class<? extends AbstractScreen> screen) {
    this.screen = screen;
  }

  public Class<? extends AbstractScreen> getScreenClass() {

    return screen;
  }

}
