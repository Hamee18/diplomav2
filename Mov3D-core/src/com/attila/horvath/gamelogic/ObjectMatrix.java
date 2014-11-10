package com.attila.horvath.gamelogic;

import com.badlogic.gdx.Gdx;
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
		int[][][] tempMatrix = new int[X][Y][Z];

		for (int i = 0; i < X; i++) {
			for (int j = 0; j < Y; j++) {
				for (int k = 0; k < Z; k++) {
					tempMatrix[i][j][k] = 0;
				}
			}
		}

		switch (direction) {
		case Keys.A:
			for (int i = 0; i < X; i++) {
				for (int j = 0; j < Y; j++) {
					for (int k = 0; k < Z; k++) {
						tempMatrix[i][2-k][j] = objectMatrix[i][j][k];
					}
				}
			}
			
			break;
		case Keys.D:
			for (int i = 0; i < X; i++) {
				for (int j = 0; j < Y; j++) {
					for (int k = 0; k < Z; k++) {
						tempMatrix[i][k][2-j] = objectMatrix[i][j][k];
					}
				}
			}
			
			break;
		case Keys.W:
			for (int i = 0; i < X; i++) {
				for (int j = 0; j < Y; j++) {
					for (int k = 0; k < Z; k++) {
						tempMatrix[2-k][j][i] = objectMatrix[j][k][i];
					}
				}
			}
			
			break;
		case Keys.S:
			for (int i = 0; i < X; i++) {
				for (int j = 0; j < Y; j++) {
					for (int k = 0; k < Z; k++) {
						tempMatrix[k][2-j][i] = objectMatrix[j][k][i];
					}
				}
			}
			
			break;
		case Keys.Q:
			for (int i = 0; i < X; i++) {
				for (int j = 0; j < Y; j++) {
					for (int k = 0; k < Z; k++) {
						tempMatrix[j][i][2-k] = objectMatrix[k][i][j];
					}
				}
			}
			
			break;
		case Keys.E:
			for (int i = 0; i < X; i++) {
				for (int j = 0; j < Y; j++) {
					for (int k = 0; k < Z; k++) {
						tempMatrix[2-j][i][k] = objectMatrix[k][i][j];
					}
				}
			}
			
			break;
		}

		objectMatrix = tempMatrix;
		Gdx.app.log("Rotate", toString());
	}
	
	public boolean checkBound(int keyCode) {
		int x, y, z;
		boolean empty = true;
		
		switch (keyCode) {
		case Keys.LEFT: 
			y = 0;
			while(y < Y && empty) {
				x = 0;
				while(x < X && empty) {
					if (objectMatrix[x][y][0] == 1) {
						empty = false;
					}
					x++;
				}
				y++;
			}
			break;
		case Keys.RIGHT: 
			y = 0;
			while(y < Y && empty) {
				x = 0;
				while(x < X && empty) {
					if (objectMatrix[x][y][Z-1] == 1) {
						empty = false;
					}
					x++;
				}
				y++;
			}
			break;
		case Keys.UP: 
			y = 0;
			while(y < Y && empty) {
				z = 0;
				while(z < Z && empty) {
					if (objectMatrix[X-1][y][z] == 1) {
						empty = false;
					}
					z++;
				}
				y++;
			}
			break;
		case Keys.DOWN: 
			y = 0;
			while(y < Y && empty) {
				z = 0;
				while(z < Z && empty) {
					if (objectMatrix[0][y][z] == 1) {
						empty = false;
					}
					z++;
				}
				y++;
			}
			break;
		}
		return empty;
	}
	
	public void setObjectPosition() {
		int x, z;
		boolean empty = true;
		
		x = 0;
		while(x < X && empty) {
			z = 0;
			while(z < Z && empty) {
				if (objectMatrix[x][Y-1][z] == 1) {
					empty = false;
				}
				z++;
			}
			x++;
		}
		
		if (empty) {
			for(int i = 0; i < X; i++) {
				for(int j = (Y-1); j > 0; j--) {
					for(int k = 0; k < Z; k++) {
						objectMatrix[i][j][k] = objectMatrix[i][j-1][k];
						objectMatrix[i][j-1][k] = 0;
					}
				}
			}
		}
	}

	public int[][][] getObjectMatrix() {
		return objectMatrix;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("\n");

		for (int i = 0; i < X; i++) {
			for (int j = 0; j < Y; j++) {
				for (int k = 0; k < Z; k++) {
					sb.append(objectMatrix[i][j][k] + " ");
				}
				sb.append("\n");
			}
			sb.append("\n\n");
		}

		return sb.toString();
	}

}
