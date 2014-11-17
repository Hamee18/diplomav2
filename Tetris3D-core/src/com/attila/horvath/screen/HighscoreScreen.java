package com.attila.horvath.screen;

import com.attila.horvath.assets.HighscoreScreenAssets;
import com.attila.horvath.config.Config;
import com.attila.horvath.tetris3d.Root;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class HighscoreScreen implements Screen{

	private Root root;
	
	private SpriteBatch spriteBatch;
	private HighscoreScreenAssets assets;
	private int newScore;
	
	public HighscoreScreen(Root root, int newScore) {
		this.root = root;
		this.newScore = newScore;
		
		assets = new HighscoreScreenAssets(this.root, this.newScore);
		initComponent();
	}
	
	public HighscoreScreen(Root root) {
		this(root, 0);
	}
	
	private void initComponent() {
		Gdx.input.setInputProcessor(assets.getStage());

		spriteBatch = new SpriteBatch();
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
		dispose();
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		assets.dispose();
		spriteBatch.dispose();
	}

}
