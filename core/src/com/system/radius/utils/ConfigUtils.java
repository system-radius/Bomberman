package com.system.radius.utils;

import com.badlogic.gdx.Gdx;

import java.io.InputStream;
import java.util.Properties;

public class ConfigUtils {

  private static final String TAG = ConfigUtils.class.getSimpleName();

  public static final String WORLD_HEIGHT = "world_height";

  public static final String WORLD_WIDTH = "world_width";

  public static final String WORLD_SCALE = "world_scale";

  private static final Properties properties = new Properties();

  private ConfigUtils() {

  }

  public static void loadConfig() {

    try (InputStream is = Gdx.files.internal("config.properties").read()) {

      properties.load(is);
      Gdx.app.debug(TAG, "Loaded configuration!");

    } catch (Exception e) {
      Gdx.app.debug(TAG, e.getMessage());
    }

  }

  public static float getFloat(String key) {

    return Float.parseFloat(properties.getProperty(key));
  }

  public static float getWorldWidth() {

    return getFloat(WORLD_WIDTH);
  }

  public static float getWorldHeight() {

    return getFloat(WORLD_HEIGHT);
  }

  public static float getWorldScale() {

    return getFloat(WORLD_SCALE);
  }

}
