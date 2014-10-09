package com.attila.horvath.screen;

import com.attila.horvath.game.screen.AndroidGameScreen;
import com.attila.horvath.game.screen.WindowsGameScreen;
import com.attila.horvath.mov3d.Root;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.I18NBundle;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

public class MainScreen implements Screen {

	private static final float WIDTH = Gdx.graphics.getWidth();
	private static final float HEIGHT = Gdx.graphics.getHeight();
	private static final int BWIDTH = 170;
	private static final int BHEIGHT = 70;

	private Root root;
	private Stage stage;
	private Skin skin;
	private TextureAtlas atlas;
	private Table table;
	private SpriteBatch spriteBatch;
	private TextButton buttonPlay;
	private TextButton buttonOption;
	private TextButton buttonTutorial;
	private TextButton buttonQuit;

	private OrthographicCamera camera;
	private Sprite sprite;
	private Texture texture;
	private SpriteBatch batch;

	private Music menuMusic;

	public MainScreen(Root root) {
		this.root = root;
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glViewport(0, 0, (int) WIDTH, (int) HEIGHT);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		stage.act(delta);

		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		sprite.draw(batch);
		batch.end();

		spriteBatch.begin();
		stage.draw();
		spriteBatch.end();

	}

	@Override
	public void resize(int width, int height) {
		if (stage == null) {
			stage = new Stage();
		}
		stage.clear();
		table.clear();

		Gdx.input.setInputProcessor(stage);

		FileHandle baseFileHandle = Gdx.files.internal("ui/localization/magyar");
		I18NBundle myBundle = I18NBundle.createBundle(baseFileHandle, "UTF8");
		
		buttonPlay = new TextButton(myBundle.get("play"), skin);
		buttonPlay.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				stage.addAction(sequence(moveTo(0, -stage.getHeight(), 0.5f),
						run(new Runnable() {

							@Override
							public void run() {
								if (Gdx.app.getType() == ApplicationType.Android) {
									root.setScreen(new AndroidGameScreen(root));
								} else {
									root.setScreen(new WindowsGameScreen(root));
								}
							}
						})));
			}
		});
		buttonPlay.pad(7.5f);

		buttonOption = new TextButton(myBundle.get("option"), skin);
		buttonOption.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				stage.addAction(sequence(moveTo(-stage.getWidth(), 0, 0.5f),
						run(new Runnable() {

							@Override
							public void run() {
								root.setScreen(new OptionScreen(root));
							}
						})));
			}
		});
		buttonOption.pad(7.5f);

		buttonTutorial = new TextButton(myBundle.get("tutorial"), skin);
		buttonTutorial.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				stage.addAction(sequence(moveTo(-stage.getWidth(), 0, 0.5f),
						run(new Runnable() {

							@Override
							public void run() {
								root.setScreen(new TutorialScreen(root));
							}
						})));
			}
		});
		buttonTutorial.pad(7.5f);

		buttonQuit = new TextButton(myBundle.get("quit"), skin);
		buttonQuit.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				new Dialog("", skin) {
					{
						text("Do you want to exit?");
						button("Yes", "yes");
						button("No", "no");
					}

					@Override
					protected void result(Object object) {
						if (((String) object).equals("yes")) {
							stage.addAction(sequence(
									moveTo(0, -stage.getWidth(), 0.5f),
									run(new Runnable() {

										@Override
										public void run() {
											Gdx.app.exit();
										}
									})));
						}
					}
				}.show(stage);/*
							 * .setBackground(new TextureRegionDrawable( new
							 * TextureRegion( new
							 * Texture(Gdx.files.internal("ui/quitbackground.png"
							 * )), 0, 0, 800 , 600)));
							 */
			}
		});
		buttonQuit.pad(7.5f);

		table.add(buttonPlay);
		table.getCell(buttonPlay).height(BHEIGHT);
		table.getCell(buttonPlay).width(BWIDTH);
		table.row();
		table.add(buttonOption);
		table.getCell(buttonOption).height(BHEIGHT);
		table.getCell(buttonOption).width(BWIDTH);
		table.row();
		table.add(buttonTutorial);
		table.getCell(buttonTutorial).height(BHEIGHT);
		table.getCell(buttonTutorial).width(BWIDTH);
		table.row();
		table.add(buttonQuit);
		table.getCell(buttonQuit).height(BHEIGHT);
		table.getCell(buttonQuit).width(BWIDTH);
		// table.getCell(buttonQuit).space(20f);

		stage.addActor(table);
	}

	@Override
	public void show() {
		camera = new OrthographicCamera(1, HEIGHT / WIDTH);
		spriteBatch = new SpriteBatch();
		atlas = new TextureAtlas("ui/pack/button.pack");
		skin = new Skin(Gdx.files.internal("ui/json/mainSkin.json"), atlas);
		table = new Table(skin);
		table.setBounds(0, 0, WIDTH, HEIGHT);

		batch = new SpriteBatch();
		texture = new Texture(Gdx.files.internal("ui/background.jpg"));
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		TextureRegion region = new TextureRegion(texture, 0, 0, WIDTH, HEIGHT);
		sprite = new Sprite(region);
		sprite.setSize(1f, sprite.getHeight() / sprite.getWidth());
		sprite.setOrigin(sprite.getWidth() / 2, sprite.getHeight() / 2);
		sprite.setPosition(-sprite.getWidth() / 2, -sprite.getHeight() / 2);

		menuMusic = Gdx.audio.newMusic(Gdx.files
				.internal("music/menumusic.mp3"));
		menuMusic.play();
		menuMusic.setLooping(true);
	}

	@Override
	public void hide() {
		menuMusic.stop();
		dispose();
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {
		stage.dispose();
		skin.dispose();
		atlas.dispose();
		menuMusic.dispose();
	}

}
