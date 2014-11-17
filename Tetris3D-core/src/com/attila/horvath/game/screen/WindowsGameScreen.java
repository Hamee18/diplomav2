package com.attila.horvath.game.screen;

import com.attila.horvath.assets.WindowsGameScreenAssets;
import com.attila.horvath.config.Config;
import com.attila.horvath.tetris3d.Root;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;

public class WindowsGameScreen implements Screen{

	private Root root;
	private float spawnTimer;
	
	private ModelBatch modelBatch;
	private SpriteBatch batchNext;
	private SpriteBatch spriteBatch;
	private FPSLogger fps;
	
	private WindowsGameScreenAssets assets;
	
	public WindowsGameScreen(Root root) {
		this.root = root;
		
		assets = new WindowsGameScreenAssets(this.root, this);
		spawnTimer = 1f;
		
		fps = new FPSLogger();
	}

	@Override
	public void render(float delta) {
		final float d = Math.min(1f / 30f, Gdx.graphics.getDeltaTime());
		
		if ((spawnTimer -= d) < 0) {
			if(!assets.checkCollision()) {
				assets.moveObjects();
			}
			spawnTimer = 1f;
		}
		
		Gdx.gl.glViewport(0, 0, (int)Config.WIDTH, (int)Config.HEIGHT);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		assets.getWorldCamera().update();
				
		batchNext.begin();
		batchNext.draw(assets.getNextObject(), 50, Config.HEIGHT - 190);
		batchNext.end();
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		
		assets.getStage().act(delta);
		spriteBatch.begin();
		assets.getStage().draw();
		spriteBatch.end();
		

		modelBatch.begin(assets.getWorldCamera().getCamera());
		modelBatch.render(assets.getCurrentItem().getCubeInstances(), assets.getWorldEnv().getEnvironment());
		modelBatch.render(assets.getGround(), assets.getWorldEnv().getEnvironment());
		modelBatch.render(assets.getInstances(), assets.getWorldEnv().getEnvironment());
		modelBatch.end();

		fps.log();
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
		batchNext = new SpriteBatch();
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
