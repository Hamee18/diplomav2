package com.attila.horvath.game.screen;

import java.util.Random;

import com.attila.horvath.game.GameCamera;
import com.attila.horvath.game.GameEnvironment;
import com.attila.horvath.item.Item;
import com.attila.horvath.mov3d.Root;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
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
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

public class WindowsGameScreen implements Screen, InputProcessor {

	private final static short GROUND_FLAG = 1 << 8;
//	private final static short OBJECT_FLAG = 1 << 9;
	private final static short ALL_FLAG = -1;
	private final static float WIDTH = Gdx.graphics.getWidth();
	private final static float HEIGHT = Gdx.graphics.getHeight();
	private final static String[] objektumok = { "I", "L", "O", "T", "Z" };

	private Root root;
	private ModelBatch modelBatch;
	private GameCamera worldCamera;
	private GameEnvironment worldEnv;
	private Item currentItem;
	private float spawnTimer;
	private Array<Item> instances;
	private boolean collision = false;
	private FPSLogger fps = new FPSLogger();
	private btCollisionWorld collisionWorld;
	private btCollisionConfiguration collisionConfig;
	private btDispatcher dispatcher;
	private MyContactListener contactListener;
	private btBroadphaseInterface broadphase;
	private Random random;
	private int rotate = 0;
	

	private OrthographicCamera camera;
	private Sprite sprite;
	private SpriteBatch batch;
	private Texture texture;
	
	private Stage stage;
	private Skin arrowSkin;
	private Skin turnSkin;
	private TextureAtlas arrowAtlas;
	private TextureAtlas turnAtlas;
	private SpriteBatch spriteBatch;
	private TextButton left;
	private TextButton right;
	private TextButton up;
	private TextButton down;
	private TextButton turnLeft;
	private TextButton turnRight;
	private TextButton turnUp;
	private TextButton turnDown;
	private TextButton turnLeftZ;
	private TextButton turnRightZ;
	

	class MyContactListener extends ContactListener {
		@Override
		public boolean onContactAdded(int userValue0, int partId0, int index0,
				int userValue1, int partId1, int index1) {
			instances.get(userValue0).setMoving(false);
			instances.get(userValue1).setMoving(false);
			
			return true;
		}
	}

	public WindowsGameScreen(Root root) {
		this.root = root;
		
		//Bullet inicializálása
		Bullet.init();	
		collisionConfig = new btDefaultCollisionConfiguration();
		dispatcher = new btCollisionDispatcher(collisionConfig);
		broadphase = new btDbvtBroadphase();
		collisionWorld = new btCollisionWorld(dispatcher, broadphase,
				collisionConfig);
		contactListener = new MyContactListener();

		//Elsõ lehulló objektum és a játéktér beállitása
		random = new Random();
		instances = new Array<Item>();
		try {
			modelBatch = new ModelBatch();
			worldCamera = new GameCamera();
			worldEnv = new GameEnvironment();
			currentItem = (new Item.Constructor(
					objektumok[random.nextInt(objektumok.length)])).construct();
			currentItem.setMoving(true);
			currentItem.setUserValue(instances.size);
			instances.add(currentItem);
			collisionWorld.addCollisionObject(currentItem.getBody(),
					GROUND_FLAG, ALL_FLAG);
			spawnTimer = 0.3f;
		} catch (Exception e) {
			Gdx.app.log(null, e.toString());
		}

		//Játéktér hátterének beállitása
		camera = new OrthographicCamera(1, HEIGHT / WIDTH);

		batch = new SpriteBatch();
		texture = new Texture(Gdx.files.internal("ui/background.jpg"));
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		TextureRegion region = new TextureRegion(texture, 0, 0, WIDTH, HEIGHT);
		sprite = new Sprite(region);
		sprite.setSize(1f, sprite.getHeight() / sprite.getWidth());
		sprite.setOrigin(sprite.getWidth() / 2, sprite.getHeight() / 2);
		sprite.setPosition(-sprite.getWidth() / 2, -sprite.getHeight() / 2);
	}

	@Override
	public void render(float delta) {
		final float d = Math.min(1f / 30f, Gdx.graphics.getDeltaTime());

		collisionWorld.performDiscreteCollisionDetection();

		//Objektum mozgatás
		if ((spawnTimer -= d) < 0) {
			for (Item i : instances) {
				if (i.transform.getTranslation(Vector3.Y).y > -100f
						&& i.getMoving() && !collision) {
					i.setTransform(5f);
				} else if (collision
						|| currentItem.transform.getTranslation(Vector3.Y).y <= -100f) {
					currentItem = (new Item.Constructor(
							objektumok[random.nextInt(objektumok.length)]))
							.construct();
					currentItem.setMoving(true);
					currentItem.setUserValue(instances.size);
					instances.add(currentItem);
					collisionWorld.addCollisionObject(currentItem.getBody(),
							GROUND_FLAG, ALL_FLAG);

					collision = false;
				}
			}
			spawnTimer = 0.3f;
		}

		Gdx.gl.glViewport(0, 0, (int)WIDTH, (int)HEIGHT);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		worldCamera.update();

		fps.log();

		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		sprite.draw(batch);
		batch.end();
		
		stage.act(delta);
		spriteBatch.begin();
		stage.draw();
		spriteBatch.end();

		modelBatch.begin(worldCamera.getCamera());
		modelBatch.render(instances, worldEnv.getEnvironment());
		modelBatch.end();

	}

	@Override
	public void resize(int width, int height) {
		
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void show() {
		spriteBatch = new SpriteBatch();
		arrowAtlas = new TextureAtlas("ui/pack/gamebutton.pack");
		turnAtlas = new TextureAtlas("ui/pack/turngamebutton.pack");
		arrowSkin = new Skin(Gdx.files.internal("ui/json/arrowGameSkin.json"), arrowAtlas);
		turnSkin = new Skin(Gdx.files.internal("ui/json/turnGameSkin.json"), turnAtlas);
		
		if (stage == null) {
			stage = new Stage();
		}
		stage.clear();
		
		InputProcessor keyboard = this;
		InputProcessor buttons = stage;
		InputMultiplexer inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(keyboard);
		inputMultiplexer.addProcessor(buttons);
		
		Gdx.input.setInputProcessor(inputMultiplexer);
			
		left = new TextButton("<", arrowSkin);
		left.setPosition(WIDTH-left.getWidth()-95, 5);
		left.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				WindowsGameScreen.this.keyUp(Keys.LEFT);
			}
		});
		
		down = new TextButton("v", arrowSkin);
		down.setPosition(WIDTH-down.getWidth()-50, 5);
		down.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				WindowsGameScreen.this.keyUp(Keys.DOWN);
			}
		});
		
		up = new TextButton("^", arrowSkin);
		up.setPosition(WIDTH-down.getWidth()-50, 50);
		up.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				WindowsGameScreen.this.keyUp(Keys.UP);
			}
		});
		
		right = new TextButton(">", arrowSkin);
		right.setPosition(WIDTH-down.getWidth()-5, 5);
		right.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				WindowsGameScreen.this.keyUp(Keys.RIGHT);
			}
		});
		
		TextButtonStyle style = new TextButtonStyle();
		style.font = turnSkin.getFont("black");		
		style.up = turnSkin.getDrawable("turnLeft");
		style.down = turnSkin.getDrawable("turnLeftClick");
		turnLeft = new TextButton("", style);
		turnLeft.setPosition(5, 5);
		turnLeft.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				WindowsGameScreen.this.keyUp(Keys.A);
			}
		});
		
		TextButtonStyle style1 = new TextButtonStyle();
		style1.font = turnSkin.getFont("black");		
		style1.up = turnSkin.getDrawable("turnLeftZ");
		style1.down = turnSkin.getDrawable("turnLeftZClick");
		turnLeftZ = new TextButton("", style1);
		turnLeftZ.setPosition(5, 50);
		turnLeftZ.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				WindowsGameScreen.this.keyUp(Keys.Q);
			}
		});
		
		TextButtonStyle style2 = new TextButtonStyle();
		style2.font = turnSkin.getFont("black");		
		style2.up = turnSkin.getDrawable("turnDown");
		style2.down = turnSkin.getDrawable("turnDownClick");
		turnDown = new TextButton("", style2);
		turnDown.setPosition(50, 5);
		turnDown.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				WindowsGameScreen.this.keyUp(Keys.S);
			}
		});
		
		TextButtonStyle style3 = new TextButtonStyle();
		style3.font = turnSkin.getFont("black");		
		style3.up = turnSkin.getDrawable("turnUp");
		style3.down = turnSkin.getDrawable("turnUpClick");
		turnUp = new TextButton("", style3);
		turnUp.setPosition(50, 50);
		turnUp.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				WindowsGameScreen.this.keyUp(Keys.W);
			}
		});
		
		TextButtonStyle style4 = new TextButtonStyle();
		style4.font = turnSkin.getFont("black");		
		style4.up = turnSkin.getDrawable("turnRight");
		style4.down = turnSkin.getDrawable("turnRightClick");
		turnRight = new TextButton("", style4);
		turnRight.setPosition(100, 5);
		turnRight.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				WindowsGameScreen.this.keyUp(Keys.D);
			}
		});
		
		TextButtonStyle style5 = new TextButtonStyle();
		style5.font = turnSkin.getFont("black");		
		style5.up = turnSkin.getDrawable("turnRightZ");
		style5.down = turnSkin.getDrawable("turnRightZClick");
		turnRightZ = new TextButton("", style5);
		turnRightZ.setPosition(100, 50);
		turnRightZ.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				WindowsGameScreen.this.keyUp(Keys.E);
			}
		});
		
		stage.addActor(left);
		stage.addActor(down);
		stage.addActor(up);
		stage.addActor(right);
		stage.addActor(turnLeft);
		stage.addActor(turnLeftZ);
		stage.addActor(turnDown);
		stage.addActor(turnUp);
		stage.addActor(turnRight);
		stage.addActor(turnRightZ);
	}

	@Override
	public void hide() {

	}

	@Override
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

		modelBatch.dispose();
		root.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		switch (keycode) {
		case Keys.SHIFT_LEFT:
			worldCamera.getCamera().rotate(-90, 0, 1, 0);
			
			switch(rotate) {
			case 0:
				worldCamera.getCamera().position.set(0, Gdx.graphics.getHeight()/40,-1 * (Gdx.graphics.getHeight()/3));
				break;
			case 1:
				worldCamera.getCamera().position.set(Gdx.graphics.getHeight()/3, Gdx.graphics.getHeight()/40, 0);
				break;
			case 2:
				worldCamera.getCamera().position.set(0, Gdx.graphics.getHeight()/40, Gdx.graphics.getHeight()/3);
				break;
			case 3:
				worldCamera.getCamera().position.set(-1 * (Gdx.graphics.getHeight()/3), Gdx.graphics.getHeight()/40, 0);
				break;
			}
			if (rotate < 3) {
				rotate++;
			} else {
				rotate = 0;
			}
			break;
		case Keys.LEFT:
			switch(rotate){
			case 0:
				currentItem.transform.trn(0, 0, -5);
				break;
			case 1:
				currentItem.transform.trn(5, 0, 0);
				break;
			case 2: 
				currentItem.transform.trn(0, 0, 5);
				break;
			case 3:
				currentItem.transform.trn(-5, 0, 0);
				break;
			}
			currentItem.setWordTransform();
			break;
		case Keys.RIGHT:
			switch(rotate){
			case 0:
				currentItem.transform.trn(0, 0, 5);
				break;
			case 1:
				currentItem.transform.trn(-5, 0, 0);
				break;
			case 2: 
				currentItem.transform.trn(0, 0, -5);
				break;
			case 3:
				currentItem.transform.trn(5, 0, 0);
				break;
			}
			currentItem.setWordTransform();
			break;
		case Keys.UP:
			switch(rotate){
			case 0:
				currentItem.transform.trn(5, 0, 0);
				break;
			case 1:
				currentItem.transform.trn(0, 0, 5);
				break;
			case 2: 
				currentItem.transform.trn(-5, 0, 0);
				break;
			case 3:
				currentItem.transform.trn(0, 0, -5);
				break;
			}
			currentItem.setWordTransform();
			break;
		case Keys.DOWN:
			switch(rotate){
			case 0:
				currentItem.transform.trn(-5, 0, 0);
				break;
			case 1:
				currentItem.transform.trn(0, 0, -5);
				break;
			case 2: 
				currentItem.transform.trn(5, 0, 0);
				break;
			case 3:
				currentItem.transform.trn(0, 0, 5);
				break;
			}
			currentItem.setWordTransform();
			break;
		case Keys.A:
			currentItem.transform.rotate(1, 0, 0, 90);
			currentItem.setWordTransform();
			break;
		case Keys.D:
			currentItem.transform.rotate(1, 0, 0, -90);
			currentItem.setWordTransform();
			break;
		case Keys.W:
			currentItem.transform.rotate(0, 0, 1, 90);
			currentItem.setWordTransform();
			break;
		case Keys.S:
			currentItem.transform.rotate(0, 0, 1, -90);
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
			Gdx.app.exit();
			break;
		}
		
		return true;
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
}
