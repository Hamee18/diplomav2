package com.attila.horvath.item;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.utils.Disposable;

public class Cube extends ModelInstance implements RenderableProvider,
		Disposable {

	public Cube(Model model) {
		super(model);
		
		setBasics();
	}

	private void setBasics() {
		this.transform.trn(0, 142f, 0);
		this.transform.rotate(0, 1, 0, 45);
	}
	
	public void setTransform(float delta) {
		this.transform.trn(0, -delta, 0);
	}
		
	@Override
	public void dispose() {
		this.dispose();
	}

	public static class Constructor {
		private final static AssetsManager assets = new AssetsManager();
		private final Model model;

		public Constructor() {
			model = assets.getAsset("obj/cube.g3dj");
		}

		public Cube construct() {
			return new Cube(model);
		}
	}
	
}
