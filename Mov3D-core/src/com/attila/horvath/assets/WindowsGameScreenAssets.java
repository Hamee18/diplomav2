package com.attila.horvath.assets;

import java.util.ArrayList;
import java.util.Random;

import com.attila.horvath.config.Config;
import com.attila.horvath.game.GameCamera;
import com.attila.horvath.game.GameEnvironment;
import com.attila.horvath.game.screen.WindowsGameScreen;
import com.attila.horvath.gamelogic.CubeMatrix;
import com.attila.horvath.gamelogic.WorldMatrix;
import com.attila.horvath.item.Cube;
import com.attila.horvath.item.Ground;
import com.attila.horvath.item.Item;
import com.attila.horvath.mov3d.Root;
import com.attila.horvath.screen.PauseScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.I18NBundle;

public class WindowsGameScreenAssets implements InputProcessor {

	private Root root;
	private WindowsGameScreen gameScreen;
	private GameCamera worldCamera;
	private GameEnvironment worldEnv;
	private Ground ground;
	private Item currentItem;
	private Array<Cube> instances;
	private final static String[] objects = { "2", "2", "2", "2", "2" };
	private static Random random = new Random();
	private String actObj = "", nextObj = "3";
	private Vector3 middlePoint;
	private WorldMatrix worldMatrix;
	private CubeMatrix cubeMatrix;
	private int X, Y, Z, scorePoint, bound;
	private boolean canMove;

	// Buttons and UI
	private Stage stage;
	private Skin arrowSkin, imageButtonSkin;
	private TextureAtlas arrowAtlas, imageButtonAtlas;
	private TextButton left, right, up, down;
	private TextButton turnLeft, turnRight, turnUp, turnDown, turnLeftZ,
			turnRightZ;
	private TextButton pauseButton;
	private Label score, nextItem, point;
	private Texture nextObject;
	private TextureRegion nextObjectRegion;

	// Preferences
	private FileHandle baseFileHandle;
	private I18NBundle myBundle;
	private Preferences preferences;
	private int difficulty;

	// Background
	private OrthographicCamera camera;
	private Sprite sprite;
	private Texture texture;

	public WindowsGameScreenAssets(Root root, WindowsGameScreen gameScreen) {
		this.root = root;
		this.gameScreen = gameScreen;

		load();
	}

	private void load() {
		scorePoint = 0;
		instances = new Array<Cube>();
		loadPreferences();

		arrowAtlas = new TextureAtlas("ui/pack/gamebutton.pack");
		imageButtonAtlas = new TextureAtlas("ui/pack/imagegamebutton.pack");
		arrowSkin = new Skin(Gdx.files.internal("ui/json/arrowGameSkin.json"),
				arrowAtlas);
		imageButtonSkin = new Skin(
				Gdx.files.internal("ui/json/turnGameSkin.json"),
				imageButtonAtlas);

		nextObjectRegion = new TextureRegion();

		try {
			worldCamera = new GameCamera();
			worldEnv = new GameEnvironment();
		} catch (Exception e) {
			Gdx.app.log(null, e.toString());
		}

		if (stage == null) {
			stage = new Stage();
		}
		stage.clear();

		loadBackground();
		loadGround();
		setDimension();
		loadLabel();
		loadObject();
		if (preferences.getBoolean("keyboard")) {
			loadComponents();
		}

		worldMatrix = new WorldMatrix(X, Y, Z);
		cubeMatrix = new CubeMatrix(X, Y, Z);
		bound = -1;
		setInputProcessors();
	}

	private void loadPreferences() {
		preferences = Gdx.app.getPreferences("tetris3d.settings");

		String displayLang = preferences.getString("display", "");

		if (displayLang == "") {
			baseFileHandle = Gdx.files.internal("ui/localization/magyar");
		} else {
			baseFileHandle = Gdx.files.internal("ui/localization/"
					+ displayLang);
		}

		myBundle = I18NBundle.createBundle(baseFileHandle, "UTF8");
	}

	private void loadGround() {
		difficulty = preferences.getInteger("difficulty");

		ground = (new Ground.Constructor(difficulty)).construct();
	}

	private void loadObject() {
		currentItem = (new Item.Constructor(nextObj)).construct();
		currentItem.setMoving(true);
		actObj = nextObj;
		middlePoint = new Vector3(0, 0, 0);

		nextObj = objects[random.nextInt(objects.length)];
		nextObject = new Texture(Gdx.files.internal("ui/objects/" + nextObj
				+ ".png"));
		nextObjectRegion = new TextureRegion(nextObject, 0, 0, 100, 100);
		nextObjectRegion.flip(false, false);
	}

	private void loadBackground() {
		// Játéktér hátterének beállitása
		camera = new OrthographicCamera(1, Config.HEIGHT / Config.WIDTH);

		texture = new Texture(Gdx.files.internal("ui/background.png"));
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		TextureRegion region = new TextureRegion(texture, 0, 0,
				(int) Config.WIDTH, (int) Config.HEIGHT);
		sprite = new Sprite(region);
		sprite.setSize(1f, 1f * sprite.getHeight() / sprite.getWidth());
		sprite.setOrigin(sprite.getWidth() / 2, sprite.getHeight() / 2);
		sprite.setPosition(-sprite.getWidth() / 2, -sprite.getHeight() / 2);
	}

	private void loadLabel() {
		score = new Label(myBundle.get("score"), arrowSkin);
		score.setPosition(Config.WIDTH - 155, Config.HEIGHT - 125);
		nextItem = new Label(myBundle.get("nextItem"), arrowSkin);
		nextItem.setPosition(40, Config.HEIGHT - 85);
		point = new Label("0", arrowSkin);
		point.setPosition(Config.WIDTH - 155, Config.HEIGHT - 155);
	}

	private void loadComponents() {
		left = new TextButton("<", arrowSkin);
		left.setPosition(Config.WIDTH - left.getWidth() - 130, 55);
		left.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				WindowsGameScreenAssets.this.keyUp(Keys.LEFT);
			}
		});

		down = new TextButton("v", arrowSkin);
		down.setPosition(Config.WIDTH - down.getWidth() - 85, 55);
		down.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				WindowsGameScreenAssets.this.keyUp(Keys.DOWN);
			}
		});

		up = new TextButton("^", arrowSkin);
		up.setPosition(Config.WIDTH - down.getWidth() - 85, 100);
		up.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				WindowsGameScreenAssets.this.keyUp(Keys.UP);
			}
		});

		right = new TextButton(">", arrowSkin);
		right.setPosition(Config.WIDTH - down.getWidth() - 40, 55);
		right.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				WindowsGameScreenAssets.this.keyUp(Keys.RIGHT);
			}
		});

		TextButtonStyle styleP = new TextButtonStyle();
		styleP.font = imageButtonSkin.getFont("black");
		styleP.up = imageButtonSkin.getDrawable("pause");
		styleP.down = imageButtonSkin.getDrawable("pauseClick");
		pauseButton = new TextButton("", styleP);
		pauseButton.setPosition(Config.WIDTH - pauseButton.getWidth() - 45,
				Config.HEIGHT - pauseButton.getHeight() - 45);
		pauseButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				root.setScreen(new PauseScreen(root, gameScreen));
			}
		});

		TextButtonStyle style = new TextButtonStyle();
		style.font = imageButtonSkin.getFont("black");
		style.up = imageButtonSkin.getDrawable("turnLeft");
		style.down = imageButtonSkin.getDrawable("turnLeftClick");
		turnLeft = new TextButton("", style);
		turnLeft.setPosition(40, 55);
		turnLeft.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				WindowsGameScreenAssets.this.keyUp(Keys.A);
			}
		});

		TextButtonStyle style1 = new TextButtonStyle();
		style1.font = imageButtonSkin.getFont("black");
		style1.up = imageButtonSkin.getDrawable("turnLeftZ");
		style1.down = imageButtonSkin.getDrawable("turnLeftZClick");
		turnLeftZ = new TextButton("", style1);
		turnLeftZ.setPosition(40, 100);
		turnLeftZ.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				WindowsGameScreenAssets.this.keyUp(Keys.Q);
			}
		});

		TextButtonStyle style2 = new TextButtonStyle();
		style2.font = imageButtonSkin.getFont("black");
		style2.up = imageButtonSkin.getDrawable("turnDown");
		style2.down = imageButtonSkin.getDrawable("turnDownClick");
		turnDown = new TextButton("", style2);
		turnDown.setPosition(85, 55);
		turnDown.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				WindowsGameScreenAssets.this.keyUp(Keys.S);
			}
		});

		TextButtonStyle style3 = new TextButtonStyle();
		style3.font = imageButtonSkin.getFont("black");
		style3.up = imageButtonSkin.getDrawable("turnUp");
		style3.down = imageButtonSkin.getDrawable("turnUpClick");
		turnUp = new TextButton("", style3);
		turnUp.setPosition(85, 100);
		turnUp.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				WindowsGameScreenAssets.this.keyUp(Keys.W);
			}
		});

		TextButtonStyle style4 = new TextButtonStyle();
		style4.font = imageButtonSkin.getFont("black");
		style4.up = imageButtonSkin.getDrawable("turnRight");
		style4.down = imageButtonSkin.getDrawable("turnRightClick");
		turnRight = new TextButton("", style4);
		turnRight.setPosition(130, 55);
		turnRight.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				WindowsGameScreenAssets.this.keyUp(Keys.D);
			}
		});

		TextButtonStyle style5 = new TextButtonStyle();
		style5.font = imageButtonSkin.getFont("black");
		style5.up = imageButtonSkin.getDrawable("turnRightZ");
		style5.down = imageButtonSkin.getDrawable("turnRightZClick");
		turnRightZ = new TextButton("", style5);
		turnRightZ.setPosition(130, 100);
		turnRightZ.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				WindowsGameScreenAssets.this.keyUp(Keys.E);
			}
		});

		stage.addActor(score);
		stage.addActor(nextItem);
		stage.addActor(point);
		stage.addActor(left);
		stage.addActor(down);
		stage.addActor(up);
		stage.addActor(right);
		stage.addActor(pauseButton);
		stage.addActor(turnLeft);
		stage.addActor(turnLeftZ);
		stage.addActor(turnDown);
		stage.addActor(turnUp);
		stage.addActor(turnRight);
		stage.addActor(turnRightZ);
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

	public void setInputProcessors() {
		InputProcessor keyboard = this;
		InputProcessor buttons = stage;
		InputMultiplexer inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(keyboard);
		inputMultiplexer.addProcessor(buttons);

		Gdx.input.setInputProcessor(inputMultiplexer);
	}

	public void checkCollision() {
		if (isCollision()) {
			currentItem.setMoving(false);

			loadObject();
		}
	}

	private boolean isCollision() {
		int x, y, z, gap = (X - 1) / 2;
		int[][][] objectMatrix = currentItem.getObjectMatrix()
				.getObjectMatrix();
		int[][][] worldMatrix = this.worldMatrix.getWorldMatrix();

		if ((int) middlePoint.x < 0) {
			x = (int) middlePoint.x + gap;
		} else {
			x = (int) middlePoint.x + gap;
		}

		y = Math.abs((int) middlePoint.y);

		if ((int) middlePoint.z < 0) {
			z = (int) middlePoint.z + gap;
		} else {
			z = (int) middlePoint.z + gap;
		}

		int wX = x - 1, wY = y, wZ = z - 1;

		if (wY == 17) {
			for (int i = 0; i < Config.X; i++) {
				for (int k = 0; k < Config.Z; k++) {
					if (((wX + i) >= 0 && (wX + i) < X)
							&& ((wZ + k) >= 0 && (wZ + k) < Z)) {
						if ((worldMatrix[wX + i][wY][wZ + k] == 1 && objectMatrix[i][Config.Y - 1][k] == 1)
								|| (worldMatrix[wX + i][wY][wZ + k] == 1 && objectMatrix[i][Config.Y - 2][k] == 1)) {
							setWorldMatrix(wX, wY, wZ, objectMatrix,
									worldMatrix);

							return true;
						}
					}
				}
			}

			currentItem.setObjectPosition();
			setWorldMatrix(wX, wY, wZ, objectMatrix, worldMatrix);

			return true;
		}

		if (x >= 0 && y >= 0 && z >= 0) {
			for (int i = 0; i < Config.X; i++) {
				for (int j = 0; j < Config.Y; j++) {
					for (int k = 0; k < Config.Z; k++) {
						if ((y - j > 0) && ((wX + i) >= 0 && (wX + i) < X)
								&& ((wZ + k) >= 0 && (wZ + k) < Z)) {
							if (worldMatrix[wX + i][wY - j + 1][wZ + k] == 1
									&& objectMatrix[i][Config.Y - j - 1][k] == 1) {
								setWorldMatrix(wX, wY, wZ, objectMatrix,
										worldMatrix);

								return true;
							}
						}

					}
				}
			}
		}

		return false;
	}

	private void setWorldMatrix(int x, int y, int z, int[][][] objectMatrix,
			int[][][] worldMatrix) {
		float cubeX, cubeY, cubeZ;
		int gap = (X - 1) / 2;
		Cube cubeMatrix[][][] = this.cubeMatrix.getCubeMatrix();

		for (int i = 0; i < Config.X; i++) {
			for (int j = 0; j < Config.Y; j++) {
				for (int k = 0; k < Config.Z; k++) {

					if ((y - j > 0) && ((x + i) >= 0 && (x + i) < X)
							&& ((z + k) >= 0 && (z + k) < Z)) {
						if (objectMatrix[i][j][k] == 1) {
							worldMatrix[x + i][y - Config.Y + j + 1][z + k] = objectMatrix[i][j][k];
							Cube tempCube = (new Cube.Constructor())
									.construct();

							cubeZ = (x - gap) * 12 + (i * 12);
							cubeX = (z - gap) * 12 + (k * 12);
							cubeY = (y - Config.Y + j + 1) * -12;

							tempCube.transform.translate(-cubeX, cubeY - 12,
									cubeZ);
							tempCube.materials
									.get(0)
									.set(ColorAttribute
											.createDiffuse(Config.colors[Integer
													.parseInt(actObj) - 1]));
							cubeMatrix[x + i][y - Config.Y + j + 1][z + k] = tempCube;
						}
					}
				}
			}
		}

		this.worldMatrix.setWorldMatrix(worldMatrix);
		this.cubeMatrix.setCubeMatrix(cubeMatrix);
		instances.clear();
		instances = this.cubeMatrix.getInstances();

//		Gdx.app.log("WorldMatrix", this.worldMatrix.toString());
//		Gdx.app.log("CubeMatrix", this.cubeMatrix.toString());

		checkRows();
	}

	private void checkRows() {
		int i, j, k;
		int[][][] worldMatrix;
		ArrayList<Integer> fullRowArray = new ArrayList<Integer>();
		boolean isFull = true;

		worldMatrix = this.worldMatrix.getWorldMatrix();
		fullRowArray.clear();

		i = Y - 1;
		while (i >= 0) {
			isFull = true;
			j = 0;
			while (j < Z && isFull) {
				k = 0;
				while (k < X && isFull) {
					if (worldMatrix[j][i][k] != 1) {
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
			this.worldMatrix.translateWorldMatrix(fullRowArray);
			cubeMatrix.translateCubeMatrix(fullRowArray);
			instances.clear();
			instances = this.cubeMatrix.getInstances();

			point.setText(String.valueOf(scorePoint += 100));
		}
	}

	public void moveObjects() {
		currentItem.setTransform(Config.STEP);
		currentItem.setCubeInstances();
		setBoundingBox(-1);
	}

	@Override
	public boolean keyUp(int keycode) {

		switch (keycode) {
		case Keys.CONTROL_LEFT:
			this.worldMatrix.rotateWorldMatrix();
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
			if (currentItem.canRotate(Keys.A, worldMatrix.getWorldMatrix(),
					middlePoint, X, Y, Z)) {
				if(bound == -1) {
					currentItem.rotateCubeMatrix(Keys.A);
				} else if (enableMove(bound)){
					currentItem.rotateCubeMatrix(Keys.A);
					currentItem.moveCubeInstances(bound);
					setBoundingBox(bound);
				}		
			}
			break;
		case Keys.D:
			if (currentItem.canRotate(Keys.D, worldMatrix.getWorldMatrix(),
					middlePoint, X, Y, Z)) {
				if(bound == -1) {
					currentItem.rotateCubeMatrix(Keys.D);
				} else if (enableMove(bound)){
					currentItem.rotateCubeMatrix(Keys.D);
					currentItem.moveCubeInstances(bound);
					setBoundingBox(bound);
				}
			}
			break;
		case Keys.W:
			if (currentItem.canRotate(Keys.W, worldMatrix.getWorldMatrix(),
					middlePoint, X, Y, Z)) {
				if(bound == -1) {
					currentItem.rotateCubeMatrix(Keys.W);
				} else if (enableMove(bound)){
					currentItem.rotateCubeMatrix(Keys.W);
					currentItem.moveCubeInstances(bound);
					setBoundingBox(bound);
				}
			}
			break;
		case Keys.S:
			if (currentItem.canRotate(Keys.S, worldMatrix.getWorldMatrix(),
					middlePoint, X, Y, Z)) {
				if(bound == -1) {
					currentItem.rotateCubeMatrix(Keys.S);
				} else if (enableMove(bound)){
					currentItem.rotateCubeMatrix(Keys.S);
					currentItem.moveCubeInstances(bound);
					setBoundingBox(bound);
				}
			}
			break;
		case Keys.Q:
			if (currentItem.canRotate(Keys.Q, worldMatrix.getWorldMatrix(),
					middlePoint, X, Y, Z)) {
				if(bound == -1) {
					currentItem.rotateCubeMatrix(Keys.Q);
				} else if (enableMove(bound)){
					currentItem.rotateCubeMatrix(Keys.Q);
					currentItem.moveCubeInstances(bound);
					setBoundingBox(bound);
				}
			}
			break;
		case Keys.E:
			if (currentItem.canRotate(Keys.E, worldMatrix.getWorldMatrix(),
					middlePoint, X, Y, Z)) {
				if(bound == -1) {
					currentItem.rotateCubeMatrix(Keys.E);
				} else if (enableMove(bound)){
					currentItem.rotateCubeMatrix(Keys.E);
					currentItem.moveCubeInstances(bound);
					setBoundingBox(bound);
				}
			}
			break;
		case Keys.ESCAPE:
			root.setScreen(new PauseScreen(root, gameScreen));
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
		int[][][] worldMatrix = this.worldMatrix.getWorldMatrix();

		if ((int) middlePoint.x < 0) {
			x = (int) middlePoint.x + gap;
		} else {
			x = (int) middlePoint.x + gap;
		}

		y = Math.abs((int) middlePoint.y);

		if ((int) middlePoint.z < 0) {
			z = (int) middlePoint.z + gap;
		} else {
			z = (int) middlePoint.z + gap;
		}

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
								if (worldMatrix[x + i][y - (Config.Y - 1) + j][z] == 1
										&& objectMatrix[i][j][1] == 1) {
									return false;
								}
							} else {
								if (worldMatrix[x + i][y - (Config.Y - 1) + j][z - 1] == 1
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
//							Gdx.app.log("X", String.valueOf(x + i));
//							Gdx.app.log("Z", String.valueOf(z + Config.Z - 1));
							if (currentItem.checkBound(Keys.RIGHT)) {
								if (worldMatrix[x + i][y - (Config.Y - 1) + j][z
										+ Config.Z - 1] == 1
										&& objectMatrix[i][j][1] == 1) {
									return false;
								}
							} else {
								if (worldMatrix[x + i][y - (Config.Y - 1) + j][z
										+ Config.Z] == 1
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
								if (worldMatrix[x + Config.X - 1][y
										- (Config.Y - 1) + j][z + k] == 1
										&& objectMatrix[1][j][k] == 1) {
									return false;
								}
							} else {
								if (worldMatrix[x + Config.X][y
										- (Config.Y - 1) + j][z + k] == 1
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
								if (worldMatrix[x][y - (Config.Y - 1) + j][z
										+ k] == 1
										&& objectMatrix[1][j][k] == 1) {
									return false;
								}
							} else {
								if (worldMatrix[x - 1][y - (Config.Y - 1) + j][z
										+ k] == 1
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

	public GameCamera getWorldCamera() {
		return worldCamera;
	}

	public OrthographicCamera getCamera() {
		return camera;
	}

	public Sprite getSprite() {
		return sprite;
	}

	public Texture getNextObject() {
		return nextObject;
	}

	public Stage getStage() {
		return stage;
	}

	public Ground getGround() {
		return ground;
	}

	public Item getCurrentItem() {
		return currentItem;
	}

	public Array<Cube> getInstances() {
		return instances;
	}

	public GameEnvironment getWorldEnv() {
		return worldEnv;
	}

	public void dispose() {
		for (Cube cube : instances) {
			cube.dispose();
		}
		instances.clear();
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}
}