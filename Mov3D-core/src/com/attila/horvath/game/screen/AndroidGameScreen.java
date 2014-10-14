package com.attila.horvath.game.screen;

import java.util.Random;

import com.attila.horvath.game.GameCamera;
import com.attila.horvath.game.GameEnvironment;
import com.attila.horvath.item.Item;
import com.attila.horvath.mov3d.Root;
import com.badlogic.gdx.Gdx;
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
import com.badlogic.gdx.utils.Array;

public class AndroidGameScreen implements Screen, InputProcessor{

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

	class MyContactListener extends ContactListener {
		@Override
		public boolean onContactAdded(int userValue0, int partId0, int index0,
				int userValue1, int partId1, int index1) {
			instances.get(userValue0).setMoving(false);
			instances.get(userValue1).setMoving(false);
			// collision = true;
			return true;
		}
	}

	public AndroidGameScreen(Root root) {
		this.root = root;
		Gdx.input.setInputProcessor(this);
		
		Bullet.init();
	
		random = new Random();
		collisionConfig = new btDefaultCollisionConfiguration();
		dispatcher = new btCollisionDispatcher(collisionConfig);
		broadphase = new btDbvtBroadphase();
		collisionWorld = new btCollisionWorld(dispatcher, broadphase,
				collisionConfig);
		contactListener = new MyContactListener();

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

		// for (Item i : instances) {
		// if (i.transform.getTranslation(Vector3.Y).y > -100f
		// && i.getMoving() && !collision) {
		// i.setTransform(1f);
		// } else if (collision ||
		// currentItem.transform.getTranslation(Vector3.Y).y <= -100f){
		//
		//
		// collision = false;
		// }
		// }
		//
		// if ((spawnTimer -= d) < 0) {
		// currentItem = (new Item.Constructor("c")).construct();
		// currentItem.setMoving(true);
		// currentItem.setUserValue(instances.size);
		// instances.add(currentItem);
		// collisionWorld.addCollisionObject(currentItem.getBody(),
		// GROUND_FLAG, ALL_FLAG);
		// spawnTimer = 5f;
		// }

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

		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		worldCamera.update();

		fps.log();

		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		sprite.draw(batch);
		batch.end();

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
		}
		
		return true;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

}
