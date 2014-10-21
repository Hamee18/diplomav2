package com.attila.horvath.game.screen;

import com.attila.horvath.assets.WindowsGameScreenAssets;
import com.attila.horvath.config.Config;
import com.attila.horvath.mov3d.Root;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;

public class WindowsGameScreen implements Screen{

	private Root root;
	private float spawnTimer;
	
	private ModelBatch modelBatch;
	private SpriteBatch batch;
	private SpriteBatch spriteBatch;
	
	private WindowsGameScreenAssets assets;
	
	public WindowsGameScreen(Root root) {
		this.root = root;
		
		assets = new WindowsGameScreenAssets(this.root, this);
		spawnTimer = 0.3f;
	}

	@Override
	public void render(float delta) {
		final float d = Math.min(1f / 30f, Gdx.graphics.getDeltaTime());

		assets.checkCollision();
		
		if ((spawnTimer -= d) < 0) {
			assets.moveObjects();
			spawnTimer = 0.3f;
		}

		Gdx.gl.glViewport(0, 0, (int)Config.WIDTH, (int)Config.HEIGHT);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		assets.getWorldCamera().update();

		batch.setProjectionMatrix(assets.getCamera().combined);
		batch.begin();
		assets.getSprite().draw(batch);
		batch.end();
		
		assets.getStage().act(delta);
		spriteBatch.begin();
		assets.getStage().draw();
		spriteBatch.end();

		modelBatch.begin(assets.getWorldCamera().getCamera());
		modelBatch.render(assets.getInstances(), assets.getWorldEnv().getEnvironment());
		modelBatch.end();

	}

	@Override
	public void resize(int width, int height) {
		
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void show() {
		batch = new SpriteBatch();
		spriteBatch = new SpriteBatch();
		modelBatch = new ModelBatch();
		assets.setInputProcessors();
	}

	@Override
	public void hide() {

	}

	@Override
	public void dispose() {
		assets.dispose();
		
		modelBatch.dispose();
		root.dispose();
	}
}
