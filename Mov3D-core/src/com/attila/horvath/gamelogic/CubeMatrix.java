package com.attila.horvath.gamelogic;

import java.util.ArrayList;

import com.attila.horvath.config.Config;
import com.attila.horvath.item.Cube;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.utils.Array;

public class CubeMatrix {
	private int X, Y, Z;

	private Cube[][][] cubeMatrix;

	public CubeMatrix(int X, int Y, int Z) {

		this.X = X;
		this.Y = Y;
		this.Z = Z;

		cubeMatrix = new Cube[X][Y][Z];

		for (int i = 0; i < Y; i++) {
			for (int j = 0; j < X; j++) {
				for (int k = 0; k < Z; k++) {
					cubeMatrix[j][i][k] = null;
				}
			}
		}
	}

	public CubeMatrix(int X, int Y, int Z, int[][][] referenceMatrix,
			String obj) {

		float cubeX, cubeY, cubeZ;
		int gap = (X - 1) / 2;

		this.X = X;
		this.Y = Y;
		this.Z = Z;

		cubeMatrix = new Cube[X][Y][Z];

		for (int i = 0; i < Y; i++) {
			for (int j = 0; j < X; j++) {
				for (int k = 0; k < Z; k++) {
					if (referenceMatrix[k][i][j] == 1) {

						cubeX = (j - gap) * 12;
						cubeY = (i - 2) * -12;
						cubeZ = (k - gap) * 12;

						cubeMatrix[k][i][j] = (new Cube.Constructor())
								.construct();
						cubeMatrix[k][i][j].materials.get(0).set(ColorAttribute.createDiffuse(Config.colors[Integer.parseInt(obj)-1]));
						cubeMatrix[k][i][j].transform.translate(-cubeX,
								cubeY - 12, cubeZ);
					}
				}
			}
		}
	}

	public void moveCubeMatrix() {
		for (int i = 0; i < Y; i++) {
			for (int j = 0; j < X; j++) {
				for (int k = 0; k < Z; k++) {
					if (cubeMatrix[i][j][k] != null) {
						cubeMatrix[i][j][k].transform.trn(0, -Config.STEP, 0);
					}
				}
			}
		}
	}

	public void moveCubeInstances(int keyCode) {
		for (int i = 0; i < Y; i++) {
			for (int j = 0; j < X; j++) {
				for (int k = 0; k < Z; k++) {
					if (cubeMatrix[i][j][k] != null) {
						switch (keyCode) {
						case Keys.LEFT:
							cubeMatrix[i][j][k].transform.trn(Config.MOVE / 2,
									0, -Config.MOVE / 2);
							break;
						case Keys.RIGHT:
							cubeMatrix[i][j][k].transform.trn(-Config.MOVE / 2,
									0, Config.MOVE / 2);
							break;
						case Keys.UP:
							cubeMatrix[i][j][k].transform.trn(Config.MOVE / 2, 0, Config.MOVE / 2);
							break;
						case Keys.DOWN:
							cubeMatrix[i][j][k].transform.trn(-Config.MOVE / 2,
									0, -Config.MOVE / 2);
							break;
						}
					}
				}
			}
		}

	}
	
	public void rotateCubeMatrix(int keyCode) {
		Cube[][][] tempMatrix = new Cube[X][Y][Z];
		float cubeX, cubeY, cubeZ;

		for (int i = 0; i < X; i++) {
			for (int j = 0; j < Y; j++) {
				for (int k = 0; k < Z; k++) {
					tempMatrix[i][j][k] = null;
				}
			}
		}

		switch (keyCode) {
		case Keys.A:
			for (int i = 0; i < X; i++) {
				for (int j = 0; j < Y; j++) {
					for (int k = 0; k < Z; k++) {
						tempMatrix[i][(Y-1) - k][j] = cubeMatrix[i][j][k];
						if (tempMatrix[i][(Y-1) - k][j] != null) {
							cubeY = (((Y-1) - k)-j) * -12;
							cubeX = (j-k) * -12;
							tempMatrix[i][(Y-1) - k][j].transform.translate(cubeX, cubeY, 0);
						}
						cubeMatrix[i][j][k] = null;
					}
				}
			}

			break;
		case Keys.D:
			for (int i = 0; i < X; i++) {
				for (int j = 0; j < Y; j++) {
					for (int k = 0; k < Z; k++) {
						tempMatrix[i][k][(Z-1) - j] = cubeMatrix[i][j][k];
						if (tempMatrix[i][k][(Z-1) - j] != null) {
							cubeY = (k-j) * -12;
							cubeX = (((Z-1) - j)-k) * -12;
							tempMatrix[i][k][(Y-1) - j].transform.translate(cubeX, cubeY, 0);
						}
						cubeMatrix[i][j][k] = null;
					}
				}
			}
			break;
		case Keys.W:
			for (int i = 0; i < X; i++) {
				for (int j = 0; j < Y; j++) {
					for (int k = 0; k < Z; k++) {
						tempMatrix[(X-1) - k][j][i] = cubeMatrix[j][k][i];
						if (tempMatrix[(X-1) - k][j][i] != null) {
							cubeY = (j-k) * -12;
							cubeZ = (((X-1) - k)-j) * 12;
							tempMatrix[(X-1) - k][j][i].transform.translate(0, cubeY, cubeZ);
						}
						cubeMatrix[j][k][i] = null;
					}
				}
			}
			break;
		case Keys.S:
			for (int i = 0; i < X; i++) {
				for (int j = 0; j < Y; j++) {
					for (int k = 0; k < Z; k++) {
						tempMatrix[k][(Y-1) - j][i] = cubeMatrix[j][k][i];
						if (tempMatrix[k][(Y-1) - j][i] != null) {
							cubeY = (((Y-1) - j)-k) * -12;
							cubeZ = (k-j) * 12;
							tempMatrix[k][(Y-1) - j][i] .transform.translate(0, cubeY, cubeZ);
						}
						cubeMatrix[j][k][i] = null;
					}
				}
			}

			break;
		case Keys.Q:
			for (int i = 0; i < Y; i++) {
				for (int j = 0; j < X; j++) {
					for (int k = 0; k < Z; k++) {
						tempMatrix[j][i][(Z-1) - k] = cubeMatrix[k][i][j];
						if (tempMatrix[j][i][(Z-1) - k] != null) {
							cubeX = (((Z-1) - k)-j) * -12;
							cubeZ = (j-k) * 12;
							tempMatrix[j][i][(Z-1) - k].transform.translate(cubeX, 0, cubeZ);
						}
						cubeMatrix[k][i][j] = null;
					}
				}
			}

			break;
		case Keys.E:
			for (int i = 0; i < Y; i++) {
				for (int j = 0; j < X; j++) {
					for (int k = 0; k < Z; k++) {
						tempMatrix[(X-1) - j][i][k] = cubeMatrix[k][i][j];
						if (tempMatrix[(X-1) - j][i][k] != null) {
							cubeX = (k-j) * -12;
							cubeZ = (((X-1) - j)-k) * 12;
							tempMatrix[(X-1) - j][i][k].transform.translate(cubeX, 0, cubeZ);
						}
						cubeMatrix[k][i][j] = null;
					}
				}
			}

			break;
		}

		cubeMatrix = tempMatrix;
	}

	public void translateCubeMatrix(ArrayList<Integer> fullRowArray) {
		int index = 0;
		int moveStep = 1;
		
		for (int i = (Y - 1); i >= 0; i--) {
			if ((index < fullRowArray.size()) && (i == fullRowArray.get(index))) {
				while (((index + 1) < fullRowArray.size())
						&& (fullRowArray.get(index) == (1 + fullRowArray
								.get(index + 1)))) {
					index++;
					moveStep++;
				}
				
			}

			if (moveStep > 0) {
				for (int j = 0; j < X; j++) {
					for (int k = 0; k < Z; k++) {
						if ((i - moveStep) > 0) {
							cubeMatrix[j][i][k] = null;
							cubeMatrix[j][i][k] = cubeMatrix[j][i - moveStep][k];
							cubeMatrix[j][i - moveStep][k] = null;
							if (cubeMatrix[j][i][k] != null) {
								cubeMatrix[j][i][k].transform.translate(0,
										-Config.STEP * moveStep, 0);
							}
						}
					}
				}
			}
		}
	}

	public Cube[][][] getCubeMatrix() {
		return cubeMatrix;
	}

	public void setCubeMatrix(Cube[][][] cubeMatrix) {
		this.cubeMatrix = cubeMatrix;
	}

	public Array<Cube> getInstances() {
		Array<Cube> instances = new Array<Cube>();

		for (int i = 0; i < X; i++) {
			for (int j = 0; j < Y; j++) {
				for (int k = 0; k < Z; k++) {
					if (cubeMatrix[i][j][k] != null)
						instances.add(cubeMatrix[i][j][k]);
				}
			}
		}

		return instances;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder("\n");

		for (int i = 0; i < X; i++) {
			for (int j = 0; j < Y; j++) {
				for (int k = 0; k < Z; k++) {
					if (cubeMatrix[i][j][k] != null)
						sb.append(1 + " ");
					else
						sb.append(0 + " ");
				}
				sb.append("\n");
			}
			sb.append("\n\n");
		}

		return sb.toString();
	}
}
