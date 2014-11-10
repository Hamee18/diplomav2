package com.attila.horvath.item;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Disposable;

public class Ground extends ModelInstance implements RenderableProvider,
		Disposable {

	private Quaternion rotationGround;
	
	public Ground(Model model) {
		super(model);

		rotationGround = new Quaternion();
		setBasics();
	}

	private void setBasics() {
		rotationGround = this.transform.getRotation(rotationGround, true);
		this.transform.trn(0, -80, 0);
		this.transform.rotate(0, 1, 0, 45);
	}

	public Vector3[] getCorners() {
		BoundingBox box = new BoundingBox();
		this.calculateBoundingBox(box);

		return box.getCorners();
	}

	public Vector3 getCenter() {
		BoundingBox box = new BoundingBox();
		this.calculateBoundingBox(box);

		return box.getCenter();
	}

	public Quaternion getRotationGround() {
		return rotationGround;
	}

	@Override
	public void dispose() {
		this.dispose();
	}

	public void setTransform(float delta) {
		this.transform.trn(0, -delta, 0);
	}

	public static class Constructor {
		private final static AssetsManager assets = new AssetsManager();
		private final Model model;

		public Constructor(int difficulty) {
			switch (difficulty) {
			case 0:
				model = assets.getAsset("obj/groundEasy.g3dj");
				break;
			case 1:
				model = assets.getAsset("obj/groundMedium.g3dj");
				break;
			case 2:
				model = assets.getAsset("obj/groundHard.g3dj");
				break;
			default:
				model = assets.getAsset("obj/groundEasy.g3dj");
				break;
			}

		}

		public Ground construct() {
			return new Ground(model);
		}
	}
}
