package com.attila.horvath.game;

import com.attila.horvath.config.Config;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;

@SuppressWarnings("deprecation")
public class GameEnvironment {
	private Environment environment;
	private DirectionalShadowLight shadowLight;
	private ModelBatch shadowBatch;
	private PerspectiveCamera camera;

	public GameEnvironment() {
		environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.Ambient, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, 0f, -90f, 0f));
        shadowLight = new DirectionalShadowLight(1024, 1024, 100f, 100f, 1f, 20f);
        shadowLight.set(0.8f, 0.8f, 0.8f, 0f, -90f, 0f); 
        environment.add(shadowLight);
        environment.shadowMap = shadowLight;
        
        shadowBatch = new ModelBatch(new DepthShaderProvider());
        
        camera = new PerspectiveCamera(67, Config.WIDTH, Config.HEIGHT);
        camera.position.set(0f, 100f, 0f);
        camera.lookAt(0f, -90f, 0f);
        camera.near = 1f;
        camera.far = 250f;
        camera.update();
	}
	
	public Environment getEnvironment() {
		return environment;
	}

	public DirectionalShadowLight getShadowLight() {
		return shadowLight;
	}

	public ModelBatch getShadowBatch() {
		return shadowBatch;
	}
	
	public PerspectiveCamera getCamera() {
		return camera;
	}
}
