package com.attila.horvath.mov3d;

import com.attila.horvath.screen.SplashScreen;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

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
