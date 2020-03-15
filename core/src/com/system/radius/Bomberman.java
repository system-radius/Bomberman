package com.system.radius;

import com.badlogic.gdx.Game;
import com.system.radius.enums.ScreenType;
import com.system.radius.flappeBee.FlapScreen;
import com.system.radius.managers.ScreenManager;
import com.system.radius.screens.StartScreen;
import com.system.radius.utils.ConfigUtils;

public class Bomberman extends Game {

//	public static final float HEIGHT = 480;
//	public static final float WIDTH = (Gdx.graphics.getWidth() / Gdx.graphics.getHeight()) * HEIGHT;
//
//	private OrthographicCamera camera;
//
//	Stage stage;
//
//	ImageButton play;
//	TextButton quit;
//
//	TextButton.TextButtonStyle buttonStyle;
//	BitmapFont font;
//	Skin skin;
//
//	Texture atlas;
//
//	SpriteBatch batch;
//	Texture img;
	
	@Override
	public void create () {

//		stage = new Stage();
//		Gdx.input.setInputProcessor(stage);
//
//		font = new BitmapFont();
//		skin = new Skin();
//
//		atlas = new Texture(Gdx.files.internal("items.png"));
//		// 89 -> 209, 227 -> 260
//		TextureRegion region = new TextureRegion(atlas, 89, 227, 120, 33);
//		Drawable drawable = new TextureRegionDrawable(region);
//
//		play = new ImageButton(drawable);
////		play.setPosition(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//
//		stage.addActor(play);
//
//		batch = new SpriteBatch();

		ConfigUtils.loadConfig();

		setScreen(ScreenManager.getScreen(ScreenType.START, this));
	}
}
