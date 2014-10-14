package com.attila.horvath.screen;

import javax.swing.event.ChangeEvent;

import com.attila.horvath.mov3d.Root;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.I18NBundle;

public class OptionScreen implements Screen{

	private static final float WIDTH = Gdx.graphics.getWidth();
	private static final float HEIGHT = Gdx.graphics.getHeight();
	private static final int CWIDTH = 170;
	private static final int CHEIGHT = 70;
	
	private Root root;
	private Stage stage;
	private Skin skin;
	private TextureAtlas buttonAtlas, optionskinAtlas;
	private Table table;
	private SpriteBatch spriteBatch;
	private Label option, language, difficulty, music;
	private Slider musicSlider;
	private SelectBox<String> sbLanguage;
	private SelectBox<String> sbDifficulty;
	
	public OptionScreen(Root root) {
		this.root = root;
		
		initComponent();
	}

	@Override
	public void render(float delta) {      
        Gdx.gl.glViewport(0, 0, (int) WIDTH, (int) HEIGHT);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		stage.act(delta);
		
		spriteBatch.begin();
		stage.draw();
		spriteBatch.end();
	}

	@Override
	public void resize(int width, int height) {
		
	}

	@Override
	public void show() {
		
	}

	@Override
	public void hide() {
		
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
		buttonAtlas.dispose();
	}
	
	private void initComponent() {
		if (stage == null) {
			stage = new Stage();
		}
		stage.clear();
		
		Gdx.input.setInputProcessor(stage);
		
		spriteBatch = new SpriteBatch();
		buttonAtlas = new TextureAtlas("ui/pack/button.pack");
		optionskinAtlas = new TextureAtlas("ui/pack/optionskin.pack");
		skin = new Skin(Gdx.files.internal("ui/json/optionSkin.json"), optionskinAtlas);
		table = new Table(skin);
		table.setBounds(0, 0, WIDTH, HEIGHT);
		table.setPosition(0, 0);
		
		FileHandle baseFileHandle = Gdx.files.internal("ui/localization/magyar");
		I18NBundle myBundle = I18NBundle.createBundle(baseFileHandle, "UTF8");
		
		option = new Label(myBundle.get("option"), skin);
		language = new Label(myBundle.get("language"), skin);
		difficulty = new Label(myBundle.get("difficulty"), skin);
		music = new Label(myBundle.get("music"), skin);
		
		sbLanguage = new SelectBox<String>(skin);
		sbLanguage.setItems(" "+ myBundle.get("hungarian"), " " + myBundle.get("english"));
		sbLanguage.setSelectedIndex(1);	
		sbLanguage.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.log("sck", "dsdf");
				
				super.clicked(event, x, y);				
			}
		});
		
		sbDifficulty = new SelectBox<String>(skin);
		sbDifficulty.setItems(" " + myBundle.get("easy"), " " + myBundle.get("medium"),
				" " + myBundle.get("hard"));
		sbDifficulty.setSelectedIndex(0);
		
		musicSlider = new Slider(0, 100, 5, false, skin);
		
		table.add(option).colspan(2).center().padBottom(10f).row();
		table.add(language).size(CWIDTH, CHEIGHT);
		table.add(sbLanguage).size(CWIDTH, CHEIGHT - 20f).center().row();
		table.add(difficulty).size(CWIDTH, CHEIGHT);
		table.add(sbDifficulty).size(CWIDTH, CHEIGHT - 20f).center().row();
		table.add(music).size(CWIDTH, CHEIGHT);
		table.add(musicSlider).size(CWIDTH, CHEIGHT).row();

		stage.addActor(table);
	}
}
