package com.attila.horvath.item;

import com.attila.horvath.config.Config;
import com.attila.horvath.config.ReferenceMatrix;
import com.attila.horvath.gamelogic.CubeMatrix;
import com.attila.horvath.gamelogic.ObjectMatrix;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class Item extends ModelInstance implements RenderableProvider, Disposable {

	private boolean moving = false;
	private ObjectMatrix objectMatrix;
	private CubeMatrix cubeMatrix;
	private String obj;

	public Item(Model model, ObjectMatrix objectMatrix, String obj) {
		super(model);
		this.objectMatrix = objectMatrix;
		this.obj = obj;
		cubeMatrix = new CubeMatrix(Config.X, Config.Y, Config.Z, objectMatrix.getObjectMatrix(), obj);
		setBasics();
	}

	private void setBasics() {
		this.transform.trn(0, 142f, 0);
		this.transform.rotate(0, 1, 0, 45);
		this.calculateTransforms();
	}

	public boolean getMoving() {
		return moving;
	}

	public void setMoving(boolean moving) {
		this.moving = moving;
	}

	public Vector3[] getCorners() {
		this.calculateTransforms();
		BoundingBox box = new BoundingBox();
		this.calculateBoundingBox(box);
		float temp;
		
		Vector3[] corners = box.getCorners();
		for(int i = 0; i < corners.length; i++) {
			temp = corners[i].x;
			corners[i].x = corners[i].z;
			corners[i].z = temp;
		}
		
		return corners;
	}
	
	public boolean checkBound(int keyCode) {
		return objectMatrix.checkBound(keyCode);
	}
	
	public void setObjectPosition() {
		objectMatrix.setObjectPosition();
	}
	
	public ObjectMatrix getObjectMatrix() {
		return objectMatrix;
	}
	
	public void setCubeInstances() {
		cubeMatrix.moveCubeMatrix();
	}
	
	public void moveCubeInstances(int keyCode) {
		cubeMatrix.moveCubeInstances(keyCode);
	}
	
	public void rotateCubeMatrix(float y) {
		cubeMatrix.rotateCubeMatrix(objectMatrix.getObjectMatrix(), obj, y);
	}
	
	public Array<Cube> getCubeInstances() {
		return cubeMatrix.getInstances();
	}
	
	public void rotateObjectMatrix(int key) {
		objectMatrix.rotateMatrix(key);
	}
	
	public void setTransform(float delta) {
		this.transform.trn(0, -delta, 0);
	}

	public static class Constructor {
		private final static AssetsManager assets = new AssetsManager();
		private final Model model;
		private ObjectMatrix objectMatrix;
		private String obj;

		public Constructor(String obj) {
			model = assets.getAsset("obj/easy/" + obj + ".g3dj");
			model.calculateTransforms();
			model.materials.get(0).set(ColorAttribute.createDiffuse(Config.colors[Integer.parseInt(obj)-1]));
			
			switch(Integer.parseInt(obj)) {
				case 1:
					objectMatrix = new ObjectMatrix(ReferenceMatrix.firstMatrixE);
					break;
				case 2:
					objectMatrix = new ObjectMatrix(ReferenceMatrix.secondMatrixE);
					break;
				case 3:
					objectMatrix = new ObjectMatrix(ReferenceMatrix.thirdMatrixE);
					break;
				case 4:
					objectMatrix = new ObjectMatrix(ReferenceMatrix.fourthMatrixE);
					break;
				case 5:
					objectMatrix = new ObjectMatrix(ReferenceMatrix.fifthMatrixE);
					break;
			}
			
			this.obj = obj;
		}

		public Item construct() {
			return new Item(model, objectMatrix, obj);
		}
	}

	@Override
	public void dispose() {
		this.dispose();
	}
}
