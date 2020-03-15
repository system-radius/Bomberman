package com.system.radius.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.system.radius.Bomberman;
import com.system.radius.OrthographicCameraExample;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1280;
		config.height = 720;
//    config.width = 240;
//    config.height = 320;
		new LwjglApplication(new Bomberman(), config);
//		new LwjglApplication(new OrthographicCameraExample(), config);
	}
}
