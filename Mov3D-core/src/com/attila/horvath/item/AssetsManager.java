package com.attila.horvath.item;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g3d.Model;

public class AssetsManager {
	private AssetManager assets = new AssetManager();
	
	public AssetsManager() {
		try {
			assets.load("obj/cube.g3dj", Model.class);
			assets.load("obj/1.g3dj", Model.class);
			assets.load("obj/2.g3dj", Model.class);
			assets.load("obj/3.g3dj", Model.class);
			assets.load("obj/4.g3dj", Model.class);
			assets.load("obj/5.g3dj", Model.class);
			assets.load("obj/easy/1.g3dj", Model.class);
			assets.load("obj/easy/2.g3dj", Model.class);
			assets.load("obj/easy/3.g3dj", Model.class);
			assets.load("obj/easy/4.g3dj", Model.class);
			assets.load("obj/easy/5.g3dj", Model.class);
			assets.load("obj/groundEasy.g3dj", Model.class);
			assets.load("obj/groundMedium.g3dj", Model.class);
			assets.load("obj/groundHard.g3dj", Model.class);
			assets.finishLoading();
		} catch (Exception e) {
			Gdx.app.log("asset:", e.toString());
		}
		
	}
	
	public Model getAsset(String name) {
		return assets.get(name, Model.class);
	}
	
	public AssetManager getAssetManager() {
		return assets;
	}
}
