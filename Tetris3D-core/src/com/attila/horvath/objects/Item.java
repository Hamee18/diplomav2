package com.attila.horvath.objects;

import com.attila.horvath.config.Config;
import com.attila.horvath.config.ReferenceMatrix;
import com.attila.horvath.game.logic.CubeMatrix;
import com.attila.horvath.game.logic.ObjectMatrix;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class Item {

	private boolean moving = false;
	private ObjectMatrix objectMatrix;
	private CubeMatrix cubeMatrix;
	private int obj;

	public Item(int obj, int difficulty) {
		
		switch(obj) {
		case 1:
			this.objectMatrix = new ObjectMatrix(ReferenceMatrix.firstMatrixE);
			break;
		case 2:
			this.objectMatrix = new ObjectMatrix(ReferenceMatrix.secondMatrixE);
			break;
		case 3:
			this.objectMatrix = new ObjectMatrix(ReferenceMatrix.thirdMatrixE);
			break;
		case 4:
			this.objectMatrix = new ObjectMatrix(ReferenceMatrix.fourthMatrixE);
			break;
		case 5:
			this.objectMatrix = new ObjectMatrix(ReferenceMatrix.fifthMatrixE);
			break;
		case 6:
			this.objectMatrix = new ObjectMatrix(ReferenceMatrix.firstMatrixH);
			break;
		case 7:
			this.objectMatrix = new ObjectMatrix(ReferenceMatrix.secondMatrixH);
			break;
		case 8:
			this.objectMatrix = new ObjectMatrix(ReferenceMatrix.thirdMatrixH);
			break;
		case 9:
			this.objectMatrix = new ObjectMatrix(ReferenceMatrix.fourthMatrixH);
			break;
		case 10:
			this.objectMatrix = new ObjectMatrix(ReferenceMatrix.fifthMatrixH);
			break;
	}
	
		this.obj = obj;
		cubeMatrix = new CubeMatrix(Config.X, Config.Y, Config.Z, objectMatrix.getObjectMatrix(), String.valueOf(this.obj));
	}


	public boolean getMoving() {
		return moving;
	}

	public void setMoving(boolean moving) {
		this.moving = moving;
	}

	public boolean canRotate(int keyCode, Cube[][][] cubeMatrix, Vector3 middlePoint, int X, int Y, int Z) {
		return objectMatrix.canRotate(keyCode, cubeMatrix, middlePoint, X, Y, Z);
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
	
	public void rotateCubeMatrix(int keyCode) {
		cubeMatrix.rotateCubeMatrix(keyCode);
	}
	
	public Array<Cube> getCubeInstances() {
		return cubeMatrix.getInstances();
	}
	
	public void rotateObjectMatrix(int key) {
		objectMatrix.rotateMatrix(key);
	}
}
