package com.attila.horvath.screen;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquations;
import aurelienribon.tweenengine.TweenManager;

import com.attila.horvath.mov3d.Root;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SplashScreen implements Screen{

	private Root root;
	private Texture splashTex;
	private Sprite splashSprite;
	private SpriteBatch spriteBatch;
	private TweenManager manager;
	
	
	public SplashScreen(Root root) {
		this.root = root;
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        
        manager.update(delta);
        
        spriteBatch.begin();
        splashSprite.draw(spriteBatch);
        spriteBatch.end();
	}

	@Override
	public void resize(int width, int height) {
		
	}

	@Override
	public void show() {
		splashTex = new Texture("ui/splashImage.png");
		splashTex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		splashSprite = new Sprite(splashTex);
		splashSprite.setColor(1, 1, 1, 0);
		splashSprite.setPosition(0,0);
		spriteBatch = new SpriteBatch();
		
		Tween.registerAccessor(Sprite.class, new SpriteTween());
		manager = new TweenManager();
		
		TweenCallback tweenCallback = new TweenCallback() {
			
			@Override
			public void onEvent(int type, BaseTween<?> source) {
				tweenComplete();
			}
		};
		
		Tween.to(splashSprite, SpriteTween.ALPHA, 3f).target(1).ease(TweenEquations.easeInQuad).repeatYoyo(1, 0.5f).
		setCallback(tweenCallback).setCallbackTriggers(TweenCallback.COMPLETE).start(manager);
	}
	
	public void tweenComplete() {
		root.setScreen(new MainScreen(root));
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
		splashTex.dispose();
		spriteBatch.dispose();
	}

}
