package com.attila.horvath.screen;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import javax.swing.event.ChangeEvent;

import com.attila.horvath.mov3d.Root;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.IntMap;

public class OptionScreen implements Screen {

	private static final float WIDTH = Gdx.graphics.getWidth();
	private static final float HEIGHT = Gdx.graphics.getHeight();
	private static final int CWIDTH = 170;
	private static final int CHEIGHT = 70;

	private Root root;
	private Stage stage;
	private Skin optionSkin, buttonSkin;
	private TextureAtlas buttonAtlas, optionskinAtlas;
	private Table table;
	private SpriteBatch spriteBatch;
	private Label option, language, difficulty, music;
	private Slider musicSlider;
	private SelectBox<String> sbLanguage;
	private SelectBox<String> sbDifficulty;
	private TextButton buttonBack;

	private Preferences preferences;

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
		optionSkin.dispose();
		buttonAtlas.dispose();
	}

	private void initComponent() {
		preferences = Gdx.app.getPreferences("tetris3d.settings");
		String displayLang = preferences.getString("display", "");
		int intLang = preferences.getInteger("language", -1);
		int intDiff = preferences.getInteger("difficulty", -1);
		float floatMusic = preferences.getFloat("music", -1);

		if (stage == null) {
			stage = new Stage();
		}
		stage.clear();

		Gdx.input.setInputProcessor(stage);

		spriteBatch = new SpriteBatch();
		buttonAtlas = new TextureAtlas("ui/pack/button.pack");
		optionskinAtlas = new TextureAtlas("ui/pack/optionskin.pack");
		buttonSkin = new Skin(Gdx.files.internal("ui/json/mainSkin.json"),
				buttonAtlas);
		optionSkin = new Skin(Gdx.files.internal("ui/json/optionSkin.json"),
				optionskinAtlas);
		table = new Table(optionSkin);
		table.setBounds(0, 0, WIDTH, HEIGHT);
		table.setPosition(0, 0);

		FileHandle baseFileHandle;
		if (displayLang == "") {
			baseFileHandle = Gdx.files.internal("ui/localization/magyar");
		} else {
			baseFileHandle = Gdx.files.internal("ui/localization/" + displayLang);
		}
		I18NBundle myBundle = I18NBundle.createBundle(baseFileHandle, "UTF8");

		option = new Label(myBundle.get("option"), optionSkin);
		language = new Label(myBundle.get("language"), optionSkin);
		difficulty = new Label(myBundle.get("difficulty"), optionSkin);
		music = new Label(myBundle.get("music"), optionSkin);

		sbLanguage = new SelectBox<String>(optionSkin);
		sbLanguage.setItems(" " + myBundle.get("hungarian"),
				" " + myBundle.get("english"));
		if (intLang == -1) {
			sbLanguage.setSelectedIndex(0);
		} else {
			sbLanguage.setSelectedIndex(intLang);
		}

		sbDifficulty = new SelectBox<String>(optionSkin);
		sbDifficulty.setItems(" " + myBundle.get("easy"),
				" " + myBundle.get("medium"), " " + myBundle.get("hard"));
		if (intDiff == -1) {
			sbDifficulty.setSelectedIndex(0);
		} else {
			sbDifficulty.setSelectedIndex(intDiff);
		}

		musicSlider = new Slider(0, 100, 5, false, optionSkin);
		if (floatMusic == -1) {
			musicSlider.setValue(0);
		} else {
			musicSlider.setValue(floatMusic);
		}

		buttonBack = new TextButton(myBundle.get("back"), buttonSkin);
		buttonBack.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				preferences.putInteger("language",
						sbLanguage.getSelectedIndex());
				preferences.putInteger("difficulty",
						sbDifficulty.getSelectedIndex());
				preferences.putFloat("music", musicSlider.getValue());
				if (sbLanguage.getSelectedIndex() == 0) {
					preferences.putString("display", "magyar");
				} else {
					preferences.putString("display", "english");
				}
				preferences.flush();

				stage.addAction(sequence(moveTo(stage.getWidth(), 0, 0.5f),
						run(new Runnable() {

							@Override
							public void run() {
								root.setScreen(new MainScreen(root));
							}
						})));
			}
		});

		table.add(option).colspan(2).center().padBottom(10f).row();
		table.add(language).size(CWIDTH, CHEIGHT);
		table.add(sbLanguage).size(CWIDTH, CHEIGHT - 20f).center().row();
		table.add(difficulty).size(CWIDTH, CHEIGHT);
		table.add(sbDifficulty).size(CWIDTH, CHEIGHT - 20f).center().row();
		table.add(music).size(CWIDTH, CHEIGHT);
		table.add(musicSlider).size(CWIDTH, CHEIGHT).row();
		table.add().row();
		table.add(buttonBack).size(CWIDTH, CHEIGHT).colspan(2).center();

		stage.addActor(table);
	}
}
