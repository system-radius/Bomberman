package com.system.radius.utils;

import com.badlogic.gdx.graphics.Color;

import java.util.Random;

public class FieldConfig {

  private static int fieldIndex;

  private static Color[] colorSchemes = {
      new Color(0xc5ac5aff),
      new Color(0x689e30ff),
      new Color(0xbaa47cff),
      new Color(0xa9a9a9ff),
      new Color(0xa1a295ff),
      new Color(0xc0dcf7ff),
      new Color(0x6dbdeeff)
  };

  private FieldConfig() {

  }

  public static int reset() {

    fieldIndex = new Random(System.currentTimeMillis()).nextInt(colorSchemes.length);

    return fieldIndex;
  }

  public static int getFieldIndex() {
    return fieldIndex;
  }

  public static Color getColorScheme() {
    return colorSchemes[fieldIndex];
  }

}