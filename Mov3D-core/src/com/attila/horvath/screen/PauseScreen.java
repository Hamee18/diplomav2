package com.attila.horvath.screen;

import com.attila.horvath.assets.PauseScreenAssets;
import com.attila.horvath.config.Config;
import com.attila.horvath.game.screen.WindowsGameScreen;
import com.attila.horvath.mov3d.Root;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class PauseScreen implements Screen {

	private SpriteBatch batch;
	private Root root;
	private WindowsGameScreen gameScreen;
	private PauseScreenAssets assets;

	public PauseScreen(Root root, WindowsGameScreen gameScreen) {
		this.root = root;
		this.gameScreen = gameScreen;
		
		batch = new SpriteBatch();
		assets = new PauseScreenAssets(this.root, this.gameScreen, this);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glViewport(0, 0, (int) Config.WIDTH, (int) Config.HEIGHT);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		assets.getStage().act(delta);
		
		batch.begin();
		assets.getStage().draw();
		batch.end();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
		assets = new PauseScreenAssets(this.root, this.gameScreen, this);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		batch.dispose();
		assets.dispose();
	}

	@Override
	public void hide() {
	}
}
