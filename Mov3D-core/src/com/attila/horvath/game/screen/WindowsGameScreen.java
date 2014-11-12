package com.attila.horvath.game.screen;

import com.attila.horvath.assets.WindowsGameScreenAssets;
import com.attila.horvath.config.Config;
import com.attila.horvath.mov3d.Root;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.math.Vector3;

@SuppressWarnings("deprecation")
public class WindowsGameScreen implements Screen{

	private Root root;
	private float spawnTimer;
	
	private ModelBatch modelBatch;
	private SpriteBatch batch, batchNext;
	private SpriteBatch spriteBatch;
	private ModelBatch shadowBatch;
	private DirectionalShadowLight shadowLight;
	private FPSLogger fps;
	
	private WindowsGameScreenAssets assets;
	
	public WindowsGameScreen(Root root) {
		this.root = root;
		
		assets = new WindowsGameScreenAssets(this.root, this);
		shadowBatch = assets.getWorldEnv().getShadowBatch();
		shadowLight = assets.getWorldEnv().getShadowLight();
		spawnTimer = 0.3f;
		
		fps = new FPSLogger();
	}

	@Override
	public void render(float delta) {
		final float d = Math.min(1f / 30f, Gdx.graphics.getDeltaTime());
		
		if ((spawnTimer -= d) < 0) {
			assets.checkCollision();
			assets.moveObjects();
			spawnTimer = 0.3f;
		}
		
		Gdx.gl.glViewport(0, 0, (int)Config.WIDTH, (int)Config.HEIGHT);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		assets.getWorldCamera().update();

		shadowLight.begin(Vector3.Zero, assets.getWorldCamera().getCamera().direction);
		shadowBatch.begin(shadowLight.getCamera());
		shadowBatch.render(assets.getCurrentItem());
		shadowBatch.end();
		shadowLight.end();
		
		batch.setProjectionMatrix(assets.getCamera().combined);
		batch.begin();
		assets.getSprite().draw(batch);
		batch.end();
				
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

//		fps.log();
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
