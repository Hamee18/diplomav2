package com.attila.horvath.game.logic;

import java.util.ArrayList;
import java.util.Random;

import com.attila.horvath.config.Config;
import com.attila.horvath.objects.Cube;
import com.attila.horvath.objects.Item;
import com.attila.horvath.screen.HighscoreScreen;
import com.attila.horvath.tetris3d.Root;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class WindowsGameScreenLogic {

	private Root root;

	private Item currentItem; // Aktuális objektum.
	private Array<Cube> instances; // Aktuális objektumot leképzõ kockák.
	private Vector3 middlePoint; // Aktuális objektum pozíciója a játéktérbe.
	private CubeMatrix cubeMatrix; // Játéktért leképzõ 3D mátrix.
	private int X, Y, Z; // Játéktér mérete 3 dimenzió szerint.
	private int scorePoint; // Elért eredmény.
	private int bound; // Objektum a játéktér szélén van-e? Ha igen, akkor
						// megadja, hogy hol.
	private boolean canMove; // Mozoghat-e az objektum?
	private int actObj = 0, nextObj = 3; // Aktuális és a következõ objektum
											// kódja.
	private Random random = new Random();

	private boolean moveLeft, moveRight, moveUp, moveDown;
	private float zeroY, zeroZ;

	private int difficulty;

	public WindowsGameScreenLogic(Root root, int difficulty) {
		this.difficulty = difficulty;
		this.root = root;

		load();
	}

	private void load() {
		setDimension();
		instances = new Array<Cube>();

		cubeMatrix = new CubeMatrix(X, Y, Z);
		bound = -1;

		zeroY = Gdx.input.getAccelerometerY();
		zeroZ = Gdx.input.getAccelerometerZ();

	}

	private void setDimension() {
		switch (difficulty) {
		case 0:
			X = Config.WXE;
			Y = Config.WYE;
			Z = Config.WZE;
			break;
		case 1:
			X = Config.WXM;
			Y = Config.WYM;
			Z = Config.WZM;
			break;
		case 2:
			X = Config.WXH;
			Y = Config.WYH;
			Z = Config.WZH;
			break;
		default:
			X = Config.WXE;
			Y = Config.WYE;
			Z = Config.WZE;
			break;
		}
	}

	public void loadObject(int nextObj) {
		actObj = nextObj;

		currentItem = new Item(actObj, difficulty);
		currentItem.setMoving(true);

		middlePoint = new Vector3(0, 0, 0);
		bound = -1;

		switch (difficulty) {
		case 0:
			this.nextObj = random.nextInt(5) + 1;
			break;
		case 1:
			this.nextObj = random.nextInt(10) + 1;
			break;
		case 2:
			this.nextObj = random.nextInt(5) + 6;
			break;
		}

	}

	public int checkCollision() {
		if (isCollision()) {
			currentItem.setMoving(false);

			return nextObj;
		}

		return 0;
	}

	private boolean isCollision() {
		int gap = (X - 1) / 2; // Játéktér és a széle közötti hossz.
		int worldX = (int) middlePoint.x + gap - 1; // A lehulló objektum X
													// koordinátája a
													// játéktérben.
		int worldY = Math.abs((int) middlePoint.y); // A lehulló objektum Y
													// koordinátája a
													// játéktérben.
		int worldZ = (int) middlePoint.z + gap - 1; // A lehulló objektum Z
													// koordinátája a
													// játéktérben.

		int[][][] objectMatrix = currentItem.getObjectMatrix()
				.getObjectMatrix();
		Cube[][][] cubeMatrix = this.cubeMatrix.getCubeMatrix();

		// Ha elértük a játéktér alját.
		if (worldY == 17) {
			for (int i = 0; i < Config.X; i++) {
				for (int k = 0; k < Config.Z; k++) {
					// Játéktér keretein belül vagyunk-e?
					if (((worldX + i) >= 0 && (worldX + i) < X)
							&& ((worldZ + k) >= 0 && (worldZ + k) < Z)) {
						// Következõ lemozgatáskor lenne-e ütközés?
						if ((cubeMatrix[worldX + i][worldY][worldZ + k] != null  && objectMatrix[i][Config.Y - 1][k] == 1)
								|| (cubeMatrix[worldX + i][worldY][worldZ + k] != null && objectMatrix[i][Config.Y - 2][k] == 1)) {

							// Lesz ütközés.
							if(this.cubeMatrix.setCubeMatrix(worldX, worldY, worldZ,
									objectMatrix, actObj)) {
								root.setScreen(new HighscoreScreen(root,
										scorePoint));
							} else {
								instances.clear();
								instances = this.cubeMatrix.getInstances();

								// Sorok vizsgálata, hogy betelt-e valamelyik?
								checkRows();
							}

							return true;
						}
					}
				}
			}

			// Aktuális objektumot a megfelelõ pozícióba mozgatjuk.
			currentItem.setObjectPosition();
			// Lesz ütközés.
			if(this.cubeMatrix.setCubeMatrix(worldX, worldY, worldZ,
					objectMatrix, actObj)) {
				root.setScreen(new HighscoreScreen(root,
						scorePoint));
			} else {
				instances.clear();
				instances = this.cubeMatrix.getInstances();

				// Sorok vizsgálata, hogy betelt-e valamelyik?
				checkRows();
			}

			return true;
		}

		// Még nem vagyuk a játéktér szélén.
		for (int i = 0; i < Config.X; i++) {
			for (int j = 0; j < Config.Y; j++) {
				for (int k = 0; k < Config.Z; k++) {
					// Játéktér keretein belül vagyunk-e?
					if ((worldY - j > 0)
							&& ((worldX + i) >= 0 && (worldX + i) < X)
							&& ((worldZ + k) >= 0 && (worldZ + k) < Z)) {
						// Következõ lemozgatáskor lenne-e ütközés?
						if (cubeMatrix[worldX + i][worldY - j + 1][worldZ + k] != null
								&& objectMatrix[i][Config.Y - j - 1][k] == 1) {

							// Lesz ütközés.
							if(this.cubeMatrix.setCubeMatrix(worldX, worldY, worldZ,
									objectMatrix, actObj)) {
								root.setScreen(new HighscoreScreen(root,
										scorePoint));
							} else {
								instances.clear();
								instances = this.cubeMatrix.getInstances();

								// Sorok vizsgálata, hogy betelt-e valamelyik?
								checkRows();
							}

							return true;
						}
					}

				}
			}
		}

		// Nincs ütközés.
		return false;
	}

	private void checkRows() {
		int i, j, k;
		Cube[][][] cubeMatrix;
		ArrayList<Integer> fullRowArray = new ArrayList<Integer>();
		boolean isFull = true;

		cubeMatrix = this.cubeMatrix.getCubeMatrix();
		fullRowArray.clear();

		i = Y - 1;
		while (i >= 0) {
			isFull = true;
			j = 0;
			while (j < Z && isFull) {
				k = 0;
				while (k < X && isFull) {
					if (cubeMatrix[j][i][k] == null) {
						isFull = false;
					}
					k++;
				}
				j++;
			}

			if (isFull) {
				fullRowArray.add(i);
			}

			i--;
		}

		if (!fullRowArray.isEmpty()) {
			int score = 0;

			score = this.cubeMatrix.translateCubeMatrix(fullRowArray);
			instances.clear();
			instances = this.cubeMatrix.getInstances();

			scorePoint += 100 * Math.pow(score, 2);
		}
	}

	public void moveObjects() {
		currentItem.setCubeInstances();
		setBoundingBox(-1);
	}

	public void moveObjectAccelerometer(float X, float Y, float Z,
			boolean rotate) {
		if (Y < (zeroY - 5)) {
			moveLeft = true;
		}

		if (Y > (zeroY + 5)) {
			moveRight = true;
		}

		if (Z < (zeroZ - 5)) {
			moveDown = true;
		}

		if (Z > (zeroZ + 5)) {
			moveUp = true;
		}

		if (moveLeft && !moveUp && (Y > (zeroY - 1)) && (Y < (zeroY + 1))) {
			if (rotate) {
				keyUp(Keys.A);
				moveLeft = false;
			} else {
				keyUp(Keys.LEFT);
				moveLeft = false;
			}

		}

		if (moveRight && (Y > (zeroY - 1)) && (Y < (zeroY + 1))) {
			if (rotate) {
				keyUp(Keys.D);
				moveRight = false;
			} else {
				keyUp(Keys.RIGHT);
				moveRight = false;
			}

		}

		if (moveDown && (Z > (zeroZ - 1)) && (Z < (zeroZ + 1))) {
			if (rotate) {
				keyUp(Keys.S);
				moveDown = false;
			} else {
				keyUp(Keys.DOWN);
				moveDown = false;
			}

		}

		if (moveUp && !moveLeft && (Z > (zeroZ - 1)) && (Z < (zeroZ + 1))) {
			if (rotate) {
				keyUp(Keys.W);
				moveUp = false;
			} else {
				keyUp(Keys.UP);
				moveUp = false;
			}

		}

		if (moveLeft && moveUp && (Z > (zeroZ - 1)) && (Z < (zeroZ + 1))
				&& (Y > (zeroY - 1)) && (Y < (zeroY + 1))) {

			if (rotate) {
				keyUp(Keys.Q);
			}

			moveLeft = false;
			moveUp = false;
		}
		
		if (moveRight && moveUp && (Z > (zeroZ - 1)) && (Z < (zeroZ + 1))
				&& (Y > (zeroY - 1)) && (Y < (zeroY + 1))) {

			if (rotate) {
				keyUp(Keys.E);
			}

			moveRight = false;
			moveUp = false;
		}
	}

	public boolean keyUp(int keycode) {

		switch (keycode) {
		case Keys.CONTROL_LEFT:
			this.cubeMatrix.rotateCubeMatrix(Keys.Q);

			instances.clear();
			instances = this.cubeMatrix.getInstances();

			break;
		case Keys.LEFT:
			setBoundingBox(Keys.LEFT);

			if (!moveOut()) {
				currentItem.moveCubeInstances(Keys.LEFT);
			}

			break;
		case Keys.RIGHT:
			setBoundingBox(Keys.RIGHT);

			if (!moveOut()) {
				currentItem.moveCubeInstances(Keys.RIGHT);
			}
			break;
		case Keys.UP:
			setBoundingBox(Keys.UP);

			if (!moveOut()) {
				currentItem.moveCubeInstances(Keys.UP);
			}
			break;
		case Keys.DOWN:
			setBoundingBox(Keys.DOWN);

			if (!moveOut()) {
				currentItem.moveCubeInstances(Keys.DOWN);
			}
			break;
		case Keys.A:
			if (currentItem.canRotate(Keys.A, cubeMatrix.getCubeMatrix(),
					middlePoint, X, Y, Z)) {
				if (bound == -1) {
					currentItem.rotateCubeMatrix(Keys.A);
				} else if (enableMove(bound)) {
					currentItem.rotateCubeMatrix(Keys.A);
					currentItem.moveCubeInstances(bound);
					setBoundingBox(bound);
				}
			}
			break;
		case Keys.D:
			if (currentItem.canRotate(Keys.D, cubeMatrix.getCubeMatrix(),
					middlePoint, X, Y, Z)) {
				if (bound == -1) {
					currentItem.rotateCubeMatrix(Keys.D);
				} else if (enableMove(bound)) {
					currentItem.rotateCubeMatrix(Keys.D);
					currentItem.moveCubeInstances(bound);
					setBoundingBox(bound);
				}
			}
			break;
		case Keys.W:
			if (currentItem.canRotate(Keys.W, cubeMatrix.getCubeMatrix(),
					middlePoint, X, Y, Z)) {
				if (bound == -1) {
					currentItem.rotateCubeMatrix(Keys.W);
				} else if (enableMove(bound)) {
					currentItem.rotateCubeMatrix(Keys.W);
					currentItem.moveCubeInstances(bound);
					setBoundingBox(bound);
				}
			}
			break;
		case Keys.S:
			if (currentItem.canRotate(Keys.S, cubeMatrix.getCubeMatrix(),
					middlePoint, X, Y, Z)) {
				if (bound == -1) {
					currentItem.rotateCubeMatrix(Keys.S);
				} else if (enableMove(bound)) {
					currentItem.rotateCubeMatrix(Keys.S);
					currentItem.moveCubeInstances(bound);
					setBoundingBox(bound);
				}
			}
			break;
		case Keys.Q:
			if (currentItem.canRotate(Keys.Q, cubeMatrix.getCubeMatrix(),
					middlePoint, X, Y, Z)) {
				if (bound == -1) {
					currentItem.rotateCubeMatrix(Keys.Q);
				} else if (enableMove(bound)) {
					currentItem.rotateCubeMatrix(Keys.Q);
					currentItem.moveCubeInstances(bound);
					setBoundingBox(bound);
				}
			}
			break;
		case Keys.E:
			if (currentItem.canRotate(Keys.E, cubeMatrix.getCubeMatrix(),
					middlePoint, X, Y, Z)) {
				if (bound == -1) {
					currentItem.rotateCubeMatrix(Keys.E);
				} else if (enableMove(bound)) {
					currentItem.rotateCubeMatrix(Keys.E);
					currentItem.moveCubeInstances(bound);
					setBoundingBox(bound);
				}
			}
			break;
		}

		return true;
	}

	private void setBoundingBox(int keycode) {
		int limit = ((X - 1) / 2) - 1;

		switch (keycode) {
		case Keys.LEFT: {
			if (middlePoint.z > -limit && enableMove(Keys.LEFT)) {
				middlePoint.z -= 1;
				bound = -1;
				canMove = true;
			} else if (middlePoint.z == -limit
					&& currentItem.checkBound(Keys.LEFT)
					&& enableMove(Keys.LEFT)) {
				middlePoint.z -= 1;
				bound = Keys.RIGHT;
				canMove = true;
			} else {
				canMove = false;
			}

			break;
		}
		case Keys.RIGHT: {
			if (middlePoint.z < limit && enableMove(Keys.RIGHT)) {
				middlePoint.z += 1;
				bound = -1;
				canMove = true;
			} else if (middlePoint.z == limit
					&& currentItem.checkBound(Keys.RIGHT)
					&& enableMove(Keys.RIGHT)) {
				middlePoint.z += 1;
				bound = Keys.LEFT;
				canMove = true;
			} else {
				canMove = false;
			}

			break;
		}
		case Keys.UP: {
			if (middlePoint.x < limit && enableMove(Keys.UP)) {
				middlePoint.x += 1;
				bound = -1;
				canMove = true;
			} else if (middlePoint.x == limit
					&& currentItem.checkBound(Keys.UP) && enableMove(Keys.UP)) {
				middlePoint.x += 1;
				bound = Keys.DOWN;
				canMove = true;
			} else {
				canMove = false;
			}

			break;
		}
		case Keys.DOWN: {
			if (middlePoint.x > -limit && enableMove(Keys.DOWN)) {
				middlePoint.x -= 1;
				bound = -1;
				canMove = true;
			} else if (middlePoint.x == -limit
					&& currentItem.checkBound(Keys.DOWN)
					&& enableMove(Keys.DOWN)) {
				middlePoint.x -= 1;
				bound = Keys.UP;
				canMove = true;
			} else {
				canMove = false;
			}

			break;
		}
		case -1:
			middlePoint.y -= 1;

			break;
		}
	}

	private boolean enableMove(int keyCode) {
		int gap = (X - 1) / 2;
		int x, y, z;
		int[][][] objectMatrix = currentItem.getObjectMatrix()
				.getObjectMatrix();
		Cube[][][] cubeMatrix = this.cubeMatrix.getCubeMatrix();

		x = (int) middlePoint.x + gap;
		y = Math.abs((int) middlePoint.y);
		z = (int) middlePoint.z + gap;

		x--;
		z--;

		switch (keyCode) {
		case Keys.LEFT:
			if (x >= 0 && y >= 0 && z >= 0) {
				for (int i = 0; i < Config.X; i++) {
					for (int j = 0; j < Config.Y; j++) {
						if (((y - (Config.Y - 1) + j) < Y && (y
								- (Config.Y - 1) + j) >= 0)
								&& ((x + i) >= 0 && ((x + i) < X))
								&& ((z - 1) >= 0)) {
							if (currentItem.checkBound(Keys.LEFT)) {
								if (cubeMatrix[x + i][y - (Config.Y - 1) + j][z] != null
										&& objectMatrix[i][j][1] == 1) {
									return false;
								}
							} else {
								if (cubeMatrix[x + i][y - (Config.Y - 1) + j][z - 1] != null
										&& objectMatrix[i][j][0] == 1) {
									return false;
								}
							}
						}
					}
				}
			}
			break;
		case Keys.RIGHT:
			if (x >= 0 && y >= 0 && z >= 0) {
				for (int i = 0; i < Config.X; i++) {
					for (int j = 0; j < Config.Y; j++) {
						if (((y - (Config.Y - 1) + j) < Y && (y
								- (Config.Y - 1) + j) >= 0)
								&& ((x + i) >= 0 && ((x + i) < X))
								&& ((z + Config.Z) < Z)) {
							if (currentItem.checkBound(Keys.RIGHT)) {
								if (cubeMatrix[x + i][y - (Config.Y - 1) + j][z
										+ Config.Z - 1] != null
										&& objectMatrix[i][j][1] == 1) {
									return false;
								}
							} else {
								if (cubeMatrix[x + i][y - (Config.Y - 1) + j][z
										+ Config.Z] != null
										&& objectMatrix[i][j][Config.Z - 1] == 1) {
									return false;
								}
							}
						}
					}
				}
			}
			break;
		case Keys.UP:
			if (x >= 0 && y >= 0 && z >= 0) {
				for (int j = 0; j < Config.Y; j++) {
					for (int k = 0; k < Config.Z; k++) {
						if (((y - (Config.Y - 1) + j) < Y && (y
								- (Config.Y - 1) + j) >= 0)
								&& ((z + k) >= 0 && ((z + k) < Z))) {
							if (currentItem.checkBound(Keys.UP)) {
								if (cubeMatrix[x + Config.X - 1][y
										- (Config.Y - 1) + j][z + k] != null
										&& objectMatrix[1][j][k] == 1) {
									return false;
								}
							} else {
								if (cubeMatrix[x + Config.X][y
										- (Config.Y - 1) + j][z + k] != null
										&& objectMatrix[Config.X - 1][j][k] == 1) {
									return false;
								}
							}
						}
					}
				}
			}
			break;
		case Keys.DOWN:
			if (x >= 0 && y >= 0 && z >= 0) {
				for (int j = 0; j < Config.Y; j++) {
					for (int k = 0; k < Config.Z; k++) {
						if (((y - (Config.Y - 1) + j) < Y && (y
								- (Config.Y - 1) + j) >= 0)
								&& ((z + k) >= 0 && ((z + k) < Z))) {
							if (currentItem.checkBound(Keys.DOWN)) {
								if (cubeMatrix[x][y - (Config.Y - 1) + j][z
										+ k] != null
										&& objectMatrix[1][j][k] == 1) {
									return false;
								}
							} else {
								if (cubeMatrix[x - 1][y - (Config.Y - 1) + j][z
										+ k] != null
										&& objectMatrix[0][j][k] == 1) {
									return false;
								}
							}
						}
					}
				}
			}
			break;
		}

		return true;
	}

	private boolean moveOut() {
		if (!canMove) {
			return true;
		}

		return false;
	}

	public int getNextObj() {
		return nextObj;
	}

	public int getScorePoint() {
		return scorePoint;
	}

	public Item getCurrentItem() {
		return currentItem;
	}

	public Array<Cube> getInstances() {
		return instances;
	}

	public void dispose() {
		for (Cube actCube : instances) {
			actCube.dispose();
		}
	}
}
