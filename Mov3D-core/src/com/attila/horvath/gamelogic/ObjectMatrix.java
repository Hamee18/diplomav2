package com.attila.horvath.gamelogic;

import com.badlogic.gdx.Input.Keys;

public class ObjectMatrix {
	
	private static final int X = 3;
	private static final int Y = 3;
	private static final int Z = 3;
	
	private int[][][] objectMatrix;

	public ObjectMatrix(int[][][] objectMatrix) {
		this.objectMatrix = objectMatrix;
	}
	
	public void rotateMatrix(int direction) {
		int temp;
		
		switch(direction) {
			case Keys.A: 
				
			case Keys.D: 
				
				break;
			case Keys.W:
				
				break;
			case Keys.S:
				break;
			case Keys.Q:
				for(int j = 0; j < Y; j++) {
					for(int i = 0; i < X; i++) {
						for (int k = 0; k < Z; k++) {
							temp = objectMatrix[k][j][2-i];
							objectMatrix[k][j][2-i] = objectMatrix[i][j][k];
							objectMatrix[i][j][k] = temp;
						}
					}
				}
				break;
			case Keys.E: 
				for(int j = 0; j < Y; j++) {
					for(int i = 0; i < X; i++) {
						for (int k = 0; k < Z; k++) {
							temp = objectMatrix[2-k][j][i];
							objectMatrix[2-k][j][i] = objectMatrix[i][j][k];
							objectMatrix[i][j][k] = temp;
						}
					}
				}
				break;
		}
	}
	
	public int[][][] getObjectMatrix() {
		return objectMatrix;
	}
	
}
