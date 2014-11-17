package com.attila.horvath.assets;

import com.attila.horvath.config.Config;
import com.attila.horvath.game.GameCamera;
import com.attila.horvath.game.GameEnvironment;
import com.attila.horvath.game.logic.WindowsGameScreenLogic;
import com.attila.horvath.game.screen.WindowsGameScreen;
import com.attila.horvath.objects.AssetsManager;
import com.attila.horvath.objects.Cube;
import com.attila.horvath.objects.Ground;
import com.attila.horvath.objects.Item;
import com.attila.horvath.screen.PauseScreen;
import com.attila.horvath.tetris3d.Root;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Model;
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
	private WindowsGameScreenLogic gameLogic;

	private int nextObj = 3;
	private int scorePoint;

	// Buttons and UI
	private Stage stage;
	private Skin arrowSkin, imageButtonSkin;
	private TextureAtlas arrowAtlas, imageButtonAtlas;
	private TextButton left, right, up, down, rotate;
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
	
	// Sounds
	private Sound moveSound, rotateSound, scoreSound;
	private boolean isSound;

	public WindowsGameScreenAssets(Root root, WindowsGameScreen gameScreen) {
		this.root = root;
		this.gameScreen = gameScreen;

		load();
	}

	private void load() {
		scorePoint = 0;
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

		loadGround();
		loadLabel();
		gameLogic = new WindowsGameScreenLogic(root, difficulty);
		loadObject();
		if (preferences.getBoolean("keyboard")) {
			loadKeyboardComponents();
		}
		loadComponent();
		
		setInputProcessors();
		loadSounds();
		
		stage.addActor(score);
		stage.addActor(nextItem);
		stage.addActor(point);
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
		AssetsManager assets = new AssetsManager();
		Model model;
		
		difficulty = preferences.getInteger("difficulty");

		switch (difficulty) {
		case 0:
			model = assets.getAsset("obj/groundEasy.g3dj");
			break;
		case 1:
			model = assets.getAsset("obj/groundMedium.g3dj");
			break;
		case 2:
			model = assets.getAsset("obj/groundHard.g3dj");
			break;
		default:
			model = assets.getAsset("obj/groundEasy.g3dj");
			break;
		}
		
		ground = new Ground(model);
	}

	private void loadObject() {
		gameLogic.loadObject(nextObj);
		nextObj = gameLogic.getNextObj();
		nextObject = new Texture(Gdx.files.internal("ui/objects/" + nextObj
				+ ".png"));
		nextObjectRegion = new TextureRegion(nextObject, 0, 0, 100, 100);
		nextObjectRegion.flip(false, false);
	}

	private void loadLabel() {
		score = new Label(myBundle.get("score"), arrowSkin);
		score.setPosition(Config.WIDTH - 155, Config.HEIGHT - 125);
		nextItem = new Label(myBundle.get("nextItem"), arrowSkin);
		nextItem.setPosition(40, Config.HEIGHT - 85);
		point = new Label("0", arrowSkin);
		point.setPosition(Config.WIDTH - 155, Config.HEIGHT - 155);
	}

	private void loadKeyboardComponents() {
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
		
		TextButtonStyle style6 = new TextButtonStyle();
		style6.font = imageButtonSkin.getFont("black");
		style6.up = imageButtonSkin.getDrawable("left");
		style6.down = imageButtonSkin.getDrawable("leftClick");
		left = new TextButton("", style6);
		left.setPosition(Config.WIDTH - left.getWidth() - 130, 55);
		left.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				WindowsGameScreenAssets.this.keyUp(Keys.LEFT);
			}
		});
		
		TextButtonStyle style7 = new TextButtonStyle();
		style7.font = imageButtonSkin.getFont("black");
		style7.up = imageButtonSkin.getDrawable("right");
		style7.down = imageButtonSkin.getDrawable("rightClick");
		right = new TextButton("", style7);
		right.setPosition(Config.WIDTH - right.getWidth() - 40, 55);
		right.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				WindowsGameScreenAssets.this.keyUp(Keys.RIGHT);
			}
		});
		
		TextButtonStyle style8 = new TextButtonStyle();
		style8.font = imageButtonSkin.getFont("black");
		style8.up = imageButtonSkin.getDrawable("down");
		style8.down = imageButtonSkin.getDrawable("downClick");
		down = new TextButton("", style8);
		down.setPosition(Config.WIDTH - down.getWidth() - 85, 55);
		down.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				WindowsGameScreenAssets.this.keyUp(Keys.DOWN);
			}
		});
		
		TextButtonStyle style9 = new TextButtonStyle();
		style9.font = imageButtonSkin.getFont("black");
		style9.up = imageButtonSkin.getDrawable("up");
		style9.down = imageButtonSkin.getDrawable("upClick");
		up = new TextButton("", style9);
		up.setPosition(Config.WIDTH - up.getWidth() - 85, 100);
		up.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				WindowsGameScreenAssets.this.keyUp(Keys.UP);
			}
		});

		stage.addActor(turnLeft);
		stage.addActor(turnLeftZ);
		stage.addActor(turnDown);
		stage.addActor(turnUp);
		stage.addActor(turnRight);
		stage.addActor(turnRightZ);
		stage.addActor(left);
		stage.addActor(down);
		stage.addActor(up);
		stage.addActor(right);
	}
	
	private void loadComponent() {
		TextButtonStyle style = new TextButtonStyle();
		style.font = imageButtonSkin.getFont("black");
		style.up = imageButtonSkin.getDrawable("rotateCamera");
		style.down = imageButtonSkin.getDrawable("rotateCameraClick");
		rotate = new TextButton("", style);
		rotate.setPosition(Config.WIDTH - rotate.getWidth() - 40, 100);
		rotate.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				WindowsGameScreenAssets.this.keyUp(Keys.CONTROL_LEFT);
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
		
		stage.addActor(rotate);
		stage.addActor(pauseButton);
	}

	public void setInputProcessors() {
		InputProcessor keyboard = this;
		InputProcessor buttons = stage;
		InputMultiplexer inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(keyboard);
		inputMultiplexer.addProcessor(buttons);

		Gdx.input.setInputProcessor(inputMultiplexer);
	}
	
	private void loadSounds() {
		isSound = preferences.getBoolean("music", true);
		
		moveSound = Gdx.audio.newSound(Gdx.files.internal("music/move.wav"));
		rotateSound = Gdx.audio.newSound(Gdx.files.internal("music/rotate.wav"));
		scoreSound = Gdx.audio.newSound(Gdx.files.internal("music/score.wav"));
	}

	public boolean checkCollision() {
		nextObj = gameLogic.checkCollision();
		if (nextObj != 0) {
			loadObject();
			scorePoint = gameLogic.getScorePoint();
			point.setText(String.valueOf(scorePoint));
			if(isSound) {
				scoreSound.play();
			}

			return true;
		}
		
		return false;
	}

	public void moveObjects() {
		gameLogic.moveObjects();
	}

	@Override
	public boolean keyUp(int keycode) {

		switch (keycode) {
		case Keys.CONTROL_LEFT:
			if(isSound) {
				rotateSound.play();
			}
			gameLogic.keyUp(keycode);
			break;
		case Keys.LEFT:
		case Keys.RIGHT:
		case Keys.UP:
		case Keys.DOWN:
			if(isSound) {
				moveSound.play();
			}
			gameLogic.keyUp(keycode);
			break;
		case Keys.A:
		case Keys.D:
		case Keys.W:
		case Keys.S:
		case Keys.Q:
		case Keys.E:
			if(isSound) {
				rotateSound.play();
			}
			gameLogic.keyUp(keycode);
			break;
		case Keys.ESCAPE:
			root.setScreen(new PauseScreen(root, gameScreen));
			break;
		}

		return true;
	}

	public GameCamera getWorldCamera() {
		return worldCamera;
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
		return gameLogic.getCurrentItem();
	}

	public Array<Cube> getInstances() {
		return gameLogic.getInstances();
	}

	public GameEnvironment getWorldEnv() {
		return worldEnv;
	}

	public void dispose() {
		gameLogic.dispose();
		ground.dispose();
		arrowAtlas.dispose();
		imageButtonAtlas.dispose();
		arrowSkin.dispose();
		imageButtonSkin.dispose();
		nextObject.dispose();
		stage.dispose();
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