package com.attila.horvath.game.logic;

import com.attila.horvath.objects.Cube;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector3;

public class ObjectMatrix {

	private final int X = 3;
	private final int Y = 3;
	private final int Z = 3;

	private int[][][] objectMatrix;

	public ObjectMatrix(int[][][] objectMatrix) {
		this.objectMatrix = objectMatrix;
	}

	public boolean canRotate(int keyCode, Cube[][][] cubeMatrix,
			Vector3 middlePoint, int X, int Y, int Z) {
		int gap = (X - 1) / 2;
		int x, y, z;

		x = (int) middlePoint.x + gap - 1;
		y = Math.abs((int) middlePoint.y) - (this.Y - 1);
		z = (int) middlePoint.z + gap - 1;

		Gdx.app.log("X", String.valueOf(x));
		Gdx.app.log("Y", String.valueOf(y));
		Gdx.app.log("Z", String.valueOf(z));
		
		rotateMatrix(keyCode);
		
		for (int i = 0; i < this.X; i++) {
			for (int j = 0; j < this.Y; j++) {
				for (int k = 0; k < this.Z; k++) {
					if (((x+i) >= 0 && (x+i) < X) && ((y+j) >= 0 && (y+j) < Y) && ((z+k) >= 0 && (z+k) < Z)) {
						if (cubeMatrix[x + i][y + j][z + k] != null
								&& objectMatrix[i][j][k] == 1) {
							switch (keyCode) {
							case Keys.A:
								rotateMatrix(Keys.D);
								return false;
							case Keys.D:
								rotateMatrix(Keys.A);
								return false;
							case Keys.W:
								rotateMatrix(Keys.S);
								return false;
							case Keys.S:
								rotateMatrix(Keys.W);
								return false;
							case Keys.Q:
								rotateMatrix(Keys.E);
								return false;
							case Keys.E:
								rotateMatrix(Keys.Q);
								return false;
							}
							Gdx.app.log("Ütközne?", "Igen");
						}
					}
				}
			}
		}
		
		return true;
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
						tempMatrix[i][2 - k][j] = objectMatrix[i][j][k];
					}
				}
			}

			break;
		case Keys.D:
			for (int i = 0; i < X; i++) {
				for (int j = 0; j < Y; j++) {
					for (int k = 0; k < Z; k++) {
						tempMatrix[i][k][2 - j] = objectMatrix[i][j][k];
					}
				}
			}

			break;
		case Keys.W:
			for (int i = 0; i < X; i++) {
				for (int j = 0; j < Y; j++) {
					for (int k = 0; k < Z; k++) {
						tempMatrix[2 - k][j][i] = objectMatrix[j][k][i];
					}
				}
			}

			break;
		case Keys.S:
			for (int i = 0; i < X; i++) {
				for (int j = 0; j < Y; j++) {
					for (int k = 0; k < Z; k++) {
						tempMatrix[k][2 - j][i] = objectMatrix[j][k][i];
					}
				}
			}

			break;
		case Keys.Q:
			for (int i = 0; i < X; i++) {
				for (int j = 0; j < Y; j++) {
					for (int k = 0; k < Z; k++) {
						tempMatrix[j][i][2 - k] = objectMatrix[k][i][j];
					}
				}
			}

			break;
		case Keys.E:
			for (int i = 0; i < X; i++) {
				for (int j = 0; j < Y; j++) {
					for (int k = 0; k < Z; k++) {
						tempMatrix[2 - j][i][k] = objectMatrix[k][i][j];
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
			while (y < Y && empty) {
				x = 0;
				while (x < X && empty) {
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
			while (y < Y && empty) {
				x = 0;
				while (x < X && empty) {
					if (objectMatrix[x][y][Z - 1] == 1) {
						empty = false;
					}
					x++;
				}
				y++;
			}
			break;
		case Keys.UP:
			y = 0;
			while (y < Y && empty) {
				z = 0;
				while (z < Z && empty) {
					if (objectMatrix[X - 1][y][z] == 1) {
						empty = false;
					}
					z++;
				}
				y++;
			}
			break;
		case Keys.DOWN:
			y = 0;
			while (y < Y && empty) {
				z = 0;
				while (z < Z && empty) {
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
		while (x < X && empty) {
			z = 0;
			while (z < Z && empty) {
				if (objectMatrix[x][Y - 1][z] == 1) {
					empty = false;
				}
				z++;
			}
			x++;
		}

		if (empty) {
			for (int i = 0; i < X; i++) {
				for (int j = (Y - 1); j > 0; j--) {
					for (int k = 0; k < Z; k++) {
						objectMatrix[i][j][k] = objectMatrix[i][j - 1][k];
						objectMatrix[i][j - 1][k] = 0;
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
