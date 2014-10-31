package com.attila.horvath.mov3d.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import com.attila.horvath.mov3d.Root;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		
		cfg.title = "Tetris3D";
//		cfg.useGL30 = true;
		cfg.resizable = false;
				
//		cfg.fullscreen = true;
		cfg.foregroundFPS = 60;
		cfg.vSyncEnabled = true;

	    new LwjglApplication(new Root(), cfg);
	}
}
