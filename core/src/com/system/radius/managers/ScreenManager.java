package com.system.radius.managers;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.system.radius.enums.ScreenType;
import com.system.radius.screens.AbstractScreen;

import java.util.HashMap;
import java.util.Map;

public class ScreenManager {

  private static final String TAG = ScreenManager.class.toString();

  private static Map<ScreenType, AbstractScreen> screens = new HashMap<>();

  private ScreenManager() {
  }

  public static AbstractScreen getScreen(ScreenType screenType, Game game) {

    if (!screens.containsKey(screenType)) {

      try {

        AbstractScreen screen =
            (AbstractScreen) Class.forName(screenType.getScreenClass().getName()).getConstructor(Game.class).newInstance(game);

        screens.put(screenType, screen);

        System.out.println("Created screen: " + screenType);

      } catch (Exception e) {
        Gdx.app.debug(TAG, e.getMessage());
        e.printStackTrace();
      }
    }

    return screens.get(screenType);
  }

}
