package com.system.radius.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

import java.util.HashMap;
import java.util.Map;

/**
 * A utility class containing methods for creation of fonts. The fonts are cached in a hash map
 * allowing for easier retrieval when needed.
 */
public class FontUtils {

  private static FontUtils instance;

  private FreeTypeFontGenerator fontGenerator;

  private Map<String, BitmapFont> fonts;

  private FontUtils() {

    fonts = new HashMap<>();
    fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/calibri.ttf"));

  }

  /**
   * The actual font creation takes place here.
   *
   * @param size        - The font size.
   * @param fontColor   - The font color.
   * @param borderWidth - The border width.
   * @param borderColor - The border color.
   * @return A bitmap font created based on the given parameters.
   */
  private BitmapFont createFont(int size, Color fontColor, int borderWidth, Color borderColor) {

    FreeTypeFontGenerator.FreeTypeFontParameter parameter =
        new FreeTypeFontGenerator.FreeTypeFontParameter();

    parameter.size = size;
    parameter.color = fontColor;
    parameter.borderWidth = borderWidth;
    parameter.borderColor = borderColor;
    parameter.borderStraight = true;
    parameter.minFilter = Texture.TextureFilter.Linear;
    parameter.magFilter = Texture.TextureFilter.Linear;

    return fontGenerator.generateFont(parameter);
  }

  /**
   * Creates a font based on the given size and font color. The border width defaults to 0.
   *
   * @param size  - The font size.
   * @param color - The font color.
   * @return A bitmap font created based on the given parameters.
   */
  public static BitmapFont getNoBorderFont(int size, Color color) {

    return getFont(size, color, 0, Color.BLACK);
  }

  /**
   * Creates a font based on the given size and border width. The font color defaults to
   * {@link Color#WHITE} and the border color defaults to {@link Color#BLACK}.
   *
   * @param size        - The font size.
   * @param borderWidth - The border width.
   * @return A bitmap font created based on the given parameters.
   */
  public static BitmapFont getFont(int size, int borderWidth) {

    return getFont(size, Color.WHITE, borderWidth, Color.BLACK);
  }

  /**
   * Creates a font based on the given parameters.
   *
   * @param size        - The font size.
   * @param fontColor   - The font color.
   * @param borderWidth - The border width.
   * @param borderColor - The border color.
   * @return A bitmap font created based on the given parameters.
   */
  public static BitmapFont getFont(int size, Color fontColor, int borderWidth,
                                   Color borderColor) {

    if (instance == null) {
      instance = new FontUtils();
    }

    String key = stringify(size, fontColor, borderWidth, borderColor);

    if (instance.fonts.get(key) == null) {
      instance.fonts.put(key, instance.createFont(size, fontColor, borderWidth, borderColor));
    }

    return instance.fonts.get(key);
  }

  /**
   * Memory clean-up.
   */
  public static void dispose() {
    instance.fontGenerator.dispose();
  }

  /**
   * Takes the given parameters and returns a string.
   *
   * @param size        - to be turned to string.
   * @param fontColor   - to be turned to string.
   * @param borderWidth - to be turned to string.
   * @param borderColor - to be turned to string.
   * @return A string based on the given parameters.
   */
  private static String stringify(int size, Color fontColor, int borderWidth,
                                  Color borderColor) {

    StringBuilder builder = new StringBuilder();
    builder.append(size);
    builder.append(fontColor.toString());
    builder.append(borderWidth);
    builder.append(borderColor);

    return builder.toString();
  }

}
