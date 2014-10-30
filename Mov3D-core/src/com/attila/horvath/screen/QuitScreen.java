package com.attila.horvath.screen;

import com.attila.horvath.assets.QuitScreenAssets;
import com.attila.horvath.config.Config;
import com.attila.horvath.mov3d.Root;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class QuitScreen implements Screen {

	private Root root;
	private SpriteBatch spriteBatch;
	private QuitScreenAssets assets;
	private PauseScreen previousPScreen;

	public QuitScreen(Root root) {
		this.root = root;

		assets = new QuitScreenAssets(this.root);

		initComponent();
	}
	
	public QuitScreen(Root root, PauseScreen previousPScreen) {
		this.root = root;
		this.previousPScreen = previousPScreen;

		assets = new QuitScreenAssets(this.root, this.previousPScreen);

		initComponent();
	}

	private void initComponent() {
		spriteBatch = new SpriteBatch();

		Gdx.input.setInputProcessor(assets.getStage());
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glViewport(0, 0, (int) Config.WIDTH, (int) Config.HEIGHT);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		assets.getStage().act(delta);

		spriteBatch.begin();
		assets.getStage().draw();
		spriteBatch.end();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		spriteBatch.dispose();
		
		assets.dispose();
	}

}
