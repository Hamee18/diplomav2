package com.attila.horvath.gamelogic;

public class WorldMatrix {
	private static final int X = 5;
	private static final int Y = 18;
	private static final int Z = 5;
	
	private int[][][] worldMatrix;
	
	public WorldMatrix() {
		worldMatrix = new int[X][Y][Z];
		
		for(int i = 0; i < X; i++) {
			for(int j = 0; j < Y; j++) {
				for (int k = 0; k < Z; k++) {
					worldMatrix[i][j][k] = 0;
				}
			}
		}
	}
	
	public void rotateWorldMatrix(int direction) {
		int temp;
		
		if (direction == 0) {
			for(int j = 0; j < Y; j++) {
				for(int i = 0; i < X; i++) {
					for (int k = 0; k < Z; k++) {
						temp = worldMatrix[k][j][2-i];
						worldMatrix[k][j][2-i] = worldMatrix[i][j][k];
						worldMatrix[i][j][k] = temp;
					}
				}
			}
		} else {
			for(int j = 0; j < Y; j++) {
				for(int i = 0; i < X; i++) {
					for (int k = 0; k < Z; k++) {
						temp = worldMatrix[2-k][j][i];
						worldMatrix[2-k][j][i] = worldMatrix[i][j][k];
						worldMatrix[i][j][k] = temp;
					}
				}
			}
		}
	}
	
	public int[][][] getWorldMatrix() {
		return worldMatrix;
	}
}
