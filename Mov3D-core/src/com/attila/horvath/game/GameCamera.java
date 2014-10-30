package com.attila.horvath.game;

import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;

public class GameCamera{
	
	private PerspectiveCamera camera;
	//private OrthographicCamera camera;
	
	public GameCamera() {
		camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(-1f * (Gdx.graphics.getHeight() / 1.8f), Gdx.graphics.getHeight()/5, 0);
        camera.lookAt(0, 40, 0);
        camera.near = 1f;
        camera.far = Gdx.graphics.getHeight() + 148f;
//        camera.rotate(-45, 0, 1, 0);
        camera.update();

		/*camera = new OrthographicCamera(Gdx.graphics.getWidth()/4, Gdx.graphics.getHeight()/4);
		camera.position.set(-150, 150, -150);
		camera.lookAt(0, 0, 0);
		camera.near = 1f;
		camera.far = 400f;
		
		camera.update();*/
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
