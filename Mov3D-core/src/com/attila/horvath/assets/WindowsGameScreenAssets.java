package com.attila.horvath.assets;

import java.util.ArrayList;
import java.util.Random;

import com.attila.horvath.config.Config;
import com.attila.horvath.game.GameCamera;
import com.attila.horvath.game.GameEnvironment;
import com.attila.horvath.game.screen.WindowsGameScreen;
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
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.ContactListener;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseInterface;
import com.badlogic.gdx.physics.bullet.collision.btCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btCollisionWorld;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btDispatcher;
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

	private final static short GROUND_FLAG = 1 << 8;
//	private final static short OBJECT_FLAG = 1 << 9;
	private final static short ALL_FLAG = -1;

	private Root root;
	private WindowsGameScreen gameScreen;
	private GameCamera worldCamera;
	private GameEnvironment worldEnv;
	private Ground ground;
	private Item currentItem;
	private Array<Item> instances;
	private boolean collision = false;
	private ArrayList<Integer> collID;
	private Vector3[] groundCorners, currentItemCorners;
	private Vector3 cameraRotation;
	private final static String[] objects = { "Z", "Z", "Z", "Z", "Z" };
	private static Random random = new Random();
	private String actObj = "";

	// Collision detection
	private btCollisionWorld collisionWorld;
	private btCollisionConfiguration collisionConfig;
	private btDispatcher dispatcher;
	private MyContactListener contactListener;
	private btBroadphaseInterface broadphase;
	private int rotate = 0;

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

	// Background
	private OrthographicCamera camera;
	private Sprite sprite;
	private Texture texture;

	class MyContactListener extends ContactListener {
		@Override
		public boolean onContactAdded(int userValue0, int partId0, int index0,
				int userValue1, int partId1, int index1) {
			instances.get(userValue1 - 1).setMoving(false);

			if (!collID.contains(userValue1)) {
				collID.add(userValue1);
				collision = true;
			}

			return true;
		}
	}

	public WindowsGameScreenAssets(Root root, WindowsGameScreen gameScreen) {
		this.root = root;
		this.gameScreen = gameScreen;

		load();
	}

	private void load() {
		instances = new Array<Item>();
		loadPreferences();

		arrowAtlas = new TextureAtlas("ui/pack/gamebutton.pack");
		imageButtonAtlas = new TextureAtlas("ui/pack/imagegamebutton.pack");
		arrowSkin = new Skin(Gdx.files.internal("ui/json/arrowGameSkin.json"),
				arrowAtlas);
		imageButtonSkin = new Skin(Gdx.files.internal("ui/json/turnGameSkin.json"),
				imageButtonAtlas);
		
		cameraRotation = new Vector3();
		nextObjectRegion = new TextureRegion();
		
		try {
			worldCamera = new GameCamera();
			worldCamera.getCamera().projection.getTranslation(cameraRotation);
			worldEnv = new GameEnvironment();
		} catch (Exception e) {
			Gdx.app.log(null, e.toString());
		}

		if (stage == null) {
			stage = new Stage();
		}
		stage.clear();

		loadBackground();
		loadBullet();
		loadGround();
		loadLabel();
		loadObject();
		if (preferences.getBoolean("keyboard")) {
			loadComponents();
		}
		
		collID = new ArrayList<Integer>();
		setInputProcessors();
	}

	private void loadPreferences() {
		preferences = Gdx.app.getPreferences("tetris3d.settings");
		
		String displayLang = preferences.getString("display", "");
		
		if (displayLang == "") {
			baseFileHandle = Gdx.files.internal("ui/localization/magyar");
		} else {
			baseFileHandle = Gdx.files.internal("ui/localization/" + displayLang);
		}
		
		myBundle = I18NBundle.createBundle(baseFileHandle, "UTF8");
	}
	
	private void loadBullet() {
		// Bullet inicializálása
		Bullet.init();
		collisionConfig = new btDefaultCollisionConfiguration();
		dispatcher = new btCollisionDispatcher(collisionConfig);
		broadphase = new btDbvtBroadphase();
		collisionWorld = new btCollisionWorld(dispatcher, broadphase,
				collisionConfig);
		contactListener = new MyContactListener();
	}

	private void loadGround() {
		ground = (new Ground.Constructor()).construct();
		ground.setUserValue(instances.size);
		collisionWorld.addCollisionObject(ground.getBody(), GROUND_FLAG,
				ALL_FLAG);
		groundCorners = ground.getCorners();
	}

	private void loadObject() {
		if (actObj == "") {
			currentItem = (new Item.Constructor(objects[random.nextInt(objects.length)])).construct();
		} else {
			currentItem = (new Item.Constructor(actObj)).construct();
		}
		
		currentItem.setMoving(true);
		currentItem.setUserValue(instances.size + 1);
		instances.add(currentItem);
		collisionWorld.addCollisionObject(currentItem.getBody(), GROUND_FLAG,
				ALL_FLAG);
		currentItemCorners = currentItem.getCorners();
		
		actObj = objects[random.nextInt(objects.length)];
		nextObject = new Texture(Gdx.files.internal("ui/objects/" + actObj + ".png"));
		nextObjectRegion = new TextureRegion(nextObject, 0, 0, 100, 100);
		nextObjectRegion.flip(false, false);
	}

	private void loadBackground() {
		// Játéktér hátterének beállitása
		camera = new OrthographicCamera(1, Config.HEIGHT / Config.WIDTH);

		texture = new Texture(Gdx.files.internal("ui/background.png"));
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		TextureRegion region = new TextureRegion(texture, 0, 0, (int)Config.WIDTH,
				(int)Config.HEIGHT);
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
		pauseButton.setPosition(Config.WIDTH - pauseButton.getWidth() - 45, Config.HEIGHT - pauseButton.getHeight() - 45);
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

	public void setInputProcessors() {
		InputProcessor keyboard = this;
		InputProcessor buttons = stage;
		InputMultiplexer inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(keyboard);
		inputMultiplexer.addProcessor(buttons);

		Gdx.input.setInputProcessor(inputMultiplexer);
	}

	public void checkCollision() {
		if (collision) {
			loadObject();
			collision = false;
		} else {
			collisionWorld.performDiscreteCollisionDetection();
		}
	}

	public void moveObjects() {
		for (Item i : instances) {
			if (i.getMoving()) {
				i.setTransform(12f);
			}
		}
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

	public Array<Item> getInstances() {
		return instances;
	}

	public GameEnvironment getWorldEnv() {
		return worldEnv;
	}

	public void dispose() {
		for (Item item : instances) {
			item.dispose();
		}
		instances.clear();

		collisionWorld.dispose();
		broadphase.dispose();
		dispatcher.dispose();
		collisionConfig.dispose();

		contactListener.dispose();

	}

	@Override
	public boolean keyUp(int keycode) {
		switch (keycode) {
		case Keys.CONTROL_LEFT:
			worldCamera.getCamera().rotate(-90, 0, 1, 0);

			switch (rotate) {
			case 0:
				worldCamera.getCamera().position.set(0,
						Gdx.graphics.getHeight() / 5,
						-1 * (Gdx.graphics.getHeight() / 1.8f));
				break;
			case 1:
				worldCamera.getCamera().position.set(
						Gdx.graphics.getHeight() / 1.8f,
						Gdx.graphics.getHeight() / 5, 0);
				break;
			case 2:
				worldCamera.getCamera().position.set(0,
						Gdx.graphics.getHeight() / 5,
						Gdx.graphics.getHeight() / 1.8f);
				break;
			case 3:
				worldCamera.getCamera().position.set(
						-1 * (Gdx.graphics.getHeight() / 1.8f),
						Gdx.graphics.getHeight() / 5, 0);
				break;
			}
			worldCamera.getCamera().update();
			worldCamera.getCamera().projection.getTranslation(cameraRotation);
			if (rotate < 3) {
				rotate++;
			} else {
				rotate = 0;
			}
			break;
		case Keys.LEFT:
			setBoundingBox(Keys.LEFT);

			if (!moveOut()) {
				switch (rotate) {
				case 0:
					currentItem.transform.trn(Config.MOVE/2, 0, -Config.MOVE/2);
					break;
				case 1:
					currentItem.transform.trn(Config.MOVE, 0, 0);
					break;
				case 2:
					currentItem.transform.trn(0, 0, Config.MOVE);
					break;
				case 3:
					currentItem.transform.trn(-Config.MOVE, 0, 0);
					break;
				}
				currentItem.setWordTransform();
			} else {
				setBoundingBox(Keys.RIGHT);
			}
			break;
		case Keys.RIGHT:
			setBoundingBox(Keys.RIGHT);

			if (!moveOut()) {
				switch (rotate) {
				case 0:
					currentItem.transform.trn(-Config.MOVE/2, 0, Config.MOVE/2);
					break;
				case 1:
					currentItem.transform.trn(-Config.MOVE, 0, 0);
					break;
				case 2:
					currentItem.transform.trn(0, 0, -Config.MOVE);
					break;
				case 3:
					currentItem.transform.trn(Config.MOVE, 0, 0);
					break;
				}
				currentItem.setWordTransform();
			} else {
				setBoundingBox(Keys.LEFT);
			}
			break;
		case Keys.UP:
			setBoundingBox(Keys.UP);

			if (!moveOut()) {
				switch (rotate) {
				case 0:
					currentItem.transform.trn(Config.MOVE/2, 0, Config.MOVE/2);
					break;
				case 1:
					currentItem.transform.trn(0, 0, Config.MOVE);
					break;
				case 2:
					currentItem.transform.trn(-Config.MOVE, 0, 0);
					break;
				case 3:
					currentItem.transform.trn(0, 0, -Config.MOVE);
					break;
				}
				currentItem.setWordTransform();
			} else {
				setBoundingBox(Keys.DOWN);
			}
			break;
		case Keys.DOWN:
			setBoundingBox(Keys.DOWN);

			if (!moveOut()) {
				switch (rotate) {
				case 0:
					currentItem.transform.trn(-Config.MOVE/2, 0, -Config.MOVE/2);
					break;
				case 1:
					currentItem.transform.trn(0, 0, -Config.MOVE);
					break;
				case 2:
					currentItem.transform.trn(Config.MOVE, 0, 0);
					break;
				case 3:
					currentItem.transform.trn(0, 0, Config.MOVE);
					break;
				}
				currentItem.setWordTransform();
			} else {
				setBoundingBox(Keys.UP);
			}
			break;
		case Keys.A:
			switch (rotate) {
			case 0: {
				currentItem.transform.rotate(1, 0, 0, 90);
				break;
			}
			case 1: {
				currentItem.transform.rotate(0, 0, 1, 90);
				break;
			}
			case 2: {
				currentItem.transform.rotate(1, 0, 0, -90);
				break;
			}
			case 3: {
				currentItem.transform.rotate(0, 0, 1, -90);
				break;
			}
			}
			currentItem.setWordTransform();
			break;
		case Keys.D:
			switch (rotate) {
			case 0: {
				currentItem.transform.rotate(1, 0, 0, -90);
				break;
			}
			case 1: {
				currentItem.transform.rotate(0, 0, 1, -90);
				break;
			}
			case 2: {
				currentItem.transform.rotate(1, 0, 0, 90);
				break;
			}
			case 3: {
				currentItem.transform.rotate(0, 0, 1, 90);
				break;
			}
			}
			currentItem.setWordTransform();
			break;
		case Keys.W:
			switch (rotate) {
			case 0: {
				currentItem.transform.rotate(0, 0, 1, 90);
				break;
			}
			case 1: {
				currentItem.transform.rotate(1, 0, 0, -90);
				break;
			}
			case 2: {
				currentItem.transform.rotate(0, 0, 1, -90);
				break;
			}
			case 3: {
				currentItem.transform.rotate(1, 0, 0, 90);
				break;
			}
			}
			currentItem.setWordTransform();
			break;
		case Keys.S:
			switch (rotate) {
			case 0: {
				currentItem.transform.rotate(0, 0, 1, -90);
				break;
			}
			case 1: {
				currentItem.transform.rotate(1, 0, 0, 90);
				break;
			}
			case 2: {
				currentItem.transform.rotate(0, 0, 1, 90);
				break;
			}
			case 3: {
				currentItem.transform.rotate(1, 0, 0, -90);
				break;
			}
			}
			currentItem.setWordTransform();
			break;
		case Keys.Q:
			currentItem.transform.rotate(0, 1, 0, 90);
			currentItem.setWordTransform();
			break;
		case Keys.E:
			currentItem.transform.rotate(0, 1, 0, -90);
			currentItem.setWordTransform();
			break;
		case Keys.ESCAPE:
			root.setScreen(new PauseScreen(root, gameScreen));
			break;
		}

		return true;
	}

	private void setBoundingBox(int keycode) {
		int i = 0;

		switch (keycode) {
		case Keys.LEFT: {
			while (i < currentItemCorners.length) {
				currentItemCorners[i].z -= Config.STEP;
				currentItemCorners[i].x += Config.STEP;
				i++;
			}

			break;
		}
		case Keys.RIGHT: {
			while (i < currentItemCorners.length) {
				currentItemCorners[i].z += Config.STEP;
				currentItemCorners[i].x -= Config.STEP;
				i++;
			}

			break;
		}
		case Keys.UP: {
			while (i < currentItemCorners.length) {
				currentItemCorners[i].z += Config.STEP;
				currentItemCorners[i].x += Config.STEP;
				i++;
			}

			break;
		}
		case Keys.DOWN: {
			while (i < currentItemCorners.length) {
				currentItemCorners[i].z -= Config.STEP;
				currentItemCorners[i].x -= Config.STEP;
				i++;
			}

			break;
		}
		}
	}

	private boolean moveOut() {

		int i = 0, j = 0;

		while (i < groundCorners.length) {
			j = 0;
			while (j < currentItemCorners.length) {
				if ((groundCorners[i].x > 0 && currentItemCorners[j].x > 0) && groundCorners[i].x < currentItemCorners[j].x) {
					return true;
				}
				
				if ((groundCorners[i].x < 0 && currentItemCorners[j].x < 0) && groundCorners[i].x >= currentItemCorners[j].x) {
					return true;
				}
				
				if ((groundCorners[i].z > 0 && currentItemCorners[j].z > 0) && groundCorners[i].z < currentItemCorners[j].z) {
					return true;
				}
				
				if ((groundCorners[i].z < 0 && currentItemCorners[j].z < 0) && groundCorners[i].z >= currentItemCorners[j].z) {
					return true;
				}
				j++;
			}
			i++;
		}

		return false;
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