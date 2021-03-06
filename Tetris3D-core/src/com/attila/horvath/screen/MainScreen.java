package com.attila.horvath.screen;

import com.attila.horvath.assets.MainScreenAssets;
import com.attila.horvath.config.Config;
import com.attila.horvath.tetris3d.Root;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MainScreen implements Screen {

	private Root root;
	private SpriteBatch spriteBatch;
	private SpriteBatch batch;
	private MainScreenAssets assets;

	private float stateTime;

	public MainScreen(Root root) {
		this.root = root;

		assets = new MainScreenAssets(this.root);
		assets.getMenuMusic().play();

		initComponent();

		stateTime = 0F;
	}

	private void initComponent() {
		spriteBatch = new SpriteBatch();
		batch = new SpriteBatch();

		Gdx.input.setInputProcessor(assets.getStage());
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glViewport(0, 0, (int) Config.WIDTH, (int) Config.HEIGHT);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		stateTime += Gdx.graphics.getDeltaTime();
		assets.setCurrentFrameAnim(stateTime);

		batch.setProjectionMatrix(assets.getCamera().combined);
		batch.begin();
		batch.draw(assets.getCurrentFrameAnimL(), 48, 0);
		batch.draw(assets.getCurrentFrameAnimO(), Config.WIDTH - 96, 0);
		batch.draw(assets.getCurrentFrameAnimZ(), Config.WIDTH / 2,
				Config.HEIGHT / 2);
		batch.draw(assets.getCurrentFrameAnimI(), 144, 0);
		batch.draw(assets.getCurrentFrameAnimL(), Config.WIDTH - 144, 144);
		batch.end();

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
		assets.getMenuMusic().stop();
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
		batch.dispose();
	}
}