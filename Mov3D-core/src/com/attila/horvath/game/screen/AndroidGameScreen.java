package com.attila.horvath.game.screen;

import java.util.Random;

import com.attila.horvath.game.GameCamera;
import com.attila.horvath.game.GameEnvironment;
import com.attila.horvath.item.Item;
import com.attila.horvath.mov3d.Root;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;

public class AndroidGameScreen implements Screen{

	private final static float WIDTH = Gdx.graphics.getWidth();
	private final static float HEIGHT = Gdx.graphics.getHeight();
	private final static String[] objects = { "I", "I", "O", "T", "T" };
	private static Random random = new Random();
	
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

	private OrthographicCamera camera;
	private Sprite sprite;
	private SpriteBatch batch;
	private Texture texture;
	private Stage stage;
	private TextureAtlas atlas;
	private Skin skin;
	private Table table;
	private Label x,y,z;
	
	private float zeroX, zeroY, zeroZ;
	private boolean moveLeft = false;
	private boolean moveRight = false;
	private boolean moveUp = false;
	private boolean moveDown = false;
	
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
		
		Bullet.init();
	
		random = new Random();
		collisionConfig = new btDefaultCollisionConfiguration();
		dispatcher = new btCollisionDispatcher(collisionConfig);
		broadphase = new btDbvtBroadphase();
		collisionWorld = new btCollisionWorld(dispatcher, broadphase,
				collisionConfig);
		contactListener = new MyContactListener();

		instances = new Array<Item>();
		String actObj = objects[random.nextInt(objects.length)];
		try {
			modelBatch = new ModelBatch();
			worldCamera = new GameCamera();
			worldEnv = new GameEnvironment();
			currentItem = (new Item.Constructor(actObj)).construct();
			currentItem.setMoving(true);
			instances.add(currentItem);
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
		
		stage = new Stage();
		atlas = new TextureAtlas("ui/pack/button.pack");
		skin = new Skin(Gdx.files.internal("ui/json/mainSkin.json"), atlas);
		
		table = new Table(skin);
		table.setBounds(0, 0, WIDTH, HEIGHT);
		table.setPosition(0, 0);
		
		x = new Label("x: ", skin);
		y = new Label("y: ", skin);
		z = new Label("z: ", skin);
		
		table.add(x).row();
		table.add(y).row();
		table.add(z).row();
		
		stage.addActor(table);
		
		zeroY = Gdx.input.getAccelerometerY();
		zeroZ = Gdx.input.getAccelerometerZ();
	}

	@Override
	public void render(float delta) {
		final float d = Math.min(1f / 30f, Gdx.graphics.getDeltaTime());

		collisionWorld.performDiscreteCollisionDetection();

		if ((spawnTimer -= d) < 0) {
			for (Item i : instances) {
				if (i.transform.getTranslation(Vector3.Y).y > -100f
						&& i.getMoving() && !collision) {
					i.setTransform(5f);
				} else if (collision
						|| currentItem.transform.getTranslation(Vector3.Y).y <= -100f) {
					String actObj = objects[random.nextInt(objects.length)];
					currentItem = (new Item.Constructor(actObj)).construct();
					currentItem.setMoving(true);
					instances.add(currentItem);

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
		
		stage.act(delta);
		batch.begin();
//		Gdx.input.getRotationMatrix(rotationMatrix);
//		StringBuilder sb = new StringBuilder();
//		for(int i = 0; i < rotationMatrix.length; i++) {
//			if (i % 15 == 0)
//				sb.append(rotationMatrix[i] + "\n");
//			else 
//				sb.append(rotationMatrix[i] + " ");
//		}
		x.setText("Azmuth: " + Gdx.input.getAccelerometerX());
		y.setText("Pitch: " + Gdx.input.getAccelerometerY());
		z.setText("Roll: " + Gdx.input.getAccelerometerZ());
		stage.draw();
		batch.end();
		
		if (Gdx.input.getAccelerometerY() < (zeroY - 5)) {
			moveLeft = true;
		}
		
		if (Gdx.input.getAccelerometerY() > (zeroY + 5)) {
			moveRight = true;
		}
		
		if (Gdx.input.getAccelerometerZ() < (zeroZ - 5)) {
			moveDown = true;
		}
		
		if (Gdx.input.getAccelerometerZ() > (zeroZ + 5)) {
			moveUp = true;
		}
		
		if (moveLeft && !moveUp && (Gdx.input.getAccelerometerY() > (zeroY - 1)) &&
				(Gdx.input.getAccelerometerY() < (zeroY + 1))) {
			currentItem.transform.trn(0, 0, -5);
			moveLeft = false;
		}
		
		if (moveRight && (Gdx.input.getAccelerometerY() > (zeroY - 1)) &&
				(Gdx.input.getAccelerometerY() < (zeroY + 1))) {
			currentItem.transform.trn(0, 0, 5);
			moveRight = false;
		}
		
		if (moveDown && (Gdx.input.getAccelerometerZ() > (zeroZ - 1)) &&
				(Gdx.input.getAccelerometerZ() < (zeroZ + 1))) {
			currentItem.transform.trn(-5, 0, 0);
			moveDown = false;
		}

		if (moveUp && !moveLeft && (Gdx.input.getAccelerometerZ() > (zeroZ - 1)) &&
				(Gdx.input.getAccelerometerZ() < (zeroZ + 1))) {
			currentItem.transform.trn(5, 0, 0);
			moveUp = false;
		}
		
		if (moveLeft && moveUp && (Gdx.input.getAccelerometerZ() > (zeroZ - 1)) &&
		(Gdx.input.getAccelerometerZ() < (zeroZ + 1)) && (Gdx.input.getAccelerometerY() > (zeroY - 1)) &&
		(Gdx.input.getAccelerometerY() < (zeroY + 1))) {
			
			currentItem.transform.rotate(1, 0, 0, 90);
			
			moveLeft = false;
			moveUp = false;
		}
		
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
}
