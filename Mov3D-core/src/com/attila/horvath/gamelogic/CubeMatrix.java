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
	
	public void rotateCubeMatrix(int[][][] worldMatrix, String actObj, float yPos) {
		Cube[][][] tempMatrix = new Cube[X][Y][Z];
		float cubeX, cubeY, cubeZ;
		int gap = (X - 1) / 2;
		yPos = yPos+2;
		
		for (int i = 0; i < Y; i++) {
			for (int j = 0; j < X; j++) {
				for (int k = 0; k < Z; k++) {
					if (worldMatrix[k][i][j] == 1) {

						cubeX = (j - gap) * 12;
						cubeY = (i - yPos) * -12;
						cubeZ = (k - gap) * 12;

						tempMatrix[k][i][j] = (new Cube.Constructor())
								.construct();
						tempMatrix[k][i][j].materials.get(0).set(
								ColorAttribute
										.createDiffuse(Config.colors[Integer
												.parseInt(actObj) - 1]));
						tempMatrix[k][i][j].transform.translate(-cubeX,
								cubeY - 12, cubeZ);
					}
					cubeMatrix[k][i][j] = null;
				}
			}
		}

		cubeMatrix = tempMatrix;

		Gdx.app.log("Cube matrix", toString());
	}

	public void rotateCubeMatrix(int[][][] worldMatrix, String actObj) {
		this.rotateCubeMatrix(worldMatrix, actObj, -2f);
	}

	public void translateCubeMatrix(ArrayList<Integer> fullRowArray) {
		int index = 0;
		int moveStep = 1;
		
		for (int i = (Y - 1); i >= 0; i--) {
			if ((index < fullRowArray.size()) && (i == fullRowArray.get(index))) {
				while (((index + 1) < fullRowArray.size())
						&& (fullRowArray.get(index) == (1 + fullRowArray
								.get(index + 1)))) {
					Gdx.app.log("Full", String.valueOf(fullRowArray.get(index)));
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

		Gdx.app.log("Cube matrix", toString());
	}

	public Cube[][][] getCubeMatrix() {
		return cubeMatrix;
	}

	public void setCubeMatrix(Cube[][][] cubeMatrix) {
		this.cubeMatrix = cubeMatrix;
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
}
