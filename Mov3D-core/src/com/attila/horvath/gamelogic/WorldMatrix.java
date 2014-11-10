package com.attila.horvath.gamelogic;

import java.util.ArrayList;
import com.badlogic.gdx.Gdx;

public class WorldMatrix {
	private int X, Y, Z;

	private int[][][] worldMatrix;

	public WorldMatrix(int X, int Y, int Z) {
		
		this.X = X;
		this.Y = Y;
		this.Z = Z;
		
		worldMatrix = new int[X][Y][Z];

		for (int i = 0; i < X; i++) {
			for (int j = 0; j < Y; j++) {
				for (int k = 0; k < Z; k++) {
					worldMatrix[i][j][k] = 0;
				}
			}
		}
	}

	public void rotateWorldMatrix() {
		int[][][] tempMatrix = new int[X][Y][Z];

		for (int i = 0; i < Y; i++) {
			for (int j = 0; j < X; j++) {
				for (int k = 0; k < Z; k++) {
					tempMatrix[j][i][(X-1) - k] = worldMatrix[k][i][j];
				}
			}
		}

		worldMatrix = tempMatrix;
		 Gdx.app.log("World rotate", toString());
	}

	public void translateWorldMatrix(ArrayList<Integer> fullRowArray) {
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

			
			if(moveStep > 0) {
				for (int j = 0; j < X; j++) {
					for (int k = 0; k < Z; k++) {
						if ((i - moveStep) > 0) {
							worldMatrix[j][i][k] = worldMatrix[j][i - moveStep][k];
							worldMatrix[j][i - moveStep][k] = 0;
						} else {
							worldMatrix[j][i][k] = 0;
						}
					}
				}
			}
		}

		Gdx.app.log("Translate rotate", toString());
	}

	public int[][][] getWorldMatrix() {
		return worldMatrix;
	}

	public void setWorldMatrix(int[][][] worldMatrix) {
		this.worldMatrix = worldMatrix;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("\n");

		for (int i = 0; i < X; i++) {
			for (int j = 0; j < Y; j++) {
				for (int k = 0; k < Z; k++) {
					sb.append(worldMatrix[i][j][k] + " ");
				}
				sb.append("\n");
			}
			sb.append("\n\n");
		}

		return sb.toString();
	}

}
