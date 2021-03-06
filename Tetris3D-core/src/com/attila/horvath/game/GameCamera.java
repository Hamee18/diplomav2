package com.attila.horvath.game;

import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;

public class GameCamera{
	
	private PerspectiveCamera camera;
	
	public GameCamera() {
		camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(-1f * (Gdx.graphics.getHeight() / 2f), Gdx.graphics.getHeight()/5, 0);
        camera.lookAt(0, 40, 0);
        camera.near = 1f;
        camera.far = Gdx.graphics.getHeight() + 248f;
        camera.update();

	}
	
	public PerspectiveCamera getCamera() {
		return camera;
	}
	
	public void setCameraPosition(float x, float y, float z) {
		camera.position.set(x, y, z);
	}
		
	public void update() {
		camera.update();
	}
}
