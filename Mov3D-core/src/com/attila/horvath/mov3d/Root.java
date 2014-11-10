package com.attila.horvath.mov3d;

import com.attila.horvath.screen.SplashScreen;
import com.badlogic.gdx.Game;

public class Root extends Game{
	
	@Override
	public void create() {
		setScreen(new SplashScreen(this));
	}

	public void render() {
		super.render();
	}
	
	public void dispose() {
		super.dispose();
	}
	
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	public void pause() {
		super.pause();
	}
	
	public void resume() {
		super.resume();
	}
}
