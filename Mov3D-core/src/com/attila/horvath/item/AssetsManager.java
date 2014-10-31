package com.attila.horvath.item;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g3d.Model;

public class AssetsManager {
	private AssetManager assets = new AssetManager();
	
	public AssetsManager() {
		try {
//			assets.load("obj/cube.g3db", Model.class);
			assets.load("obj/I.g3dj", Model.class);
			assets.load("obj/L.g3dj", Model.class);
			assets.load("obj/O.g3dj", Model.class);
			assets.load("obj/T.g3dj", Model.class);
			assets.load("obj/Z.g3dj", Model.class);
			assets.load("obj/Ground.g3dj", Model.class);
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
