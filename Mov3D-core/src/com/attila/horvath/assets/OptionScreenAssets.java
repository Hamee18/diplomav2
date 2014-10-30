package com.attila.horvath.assets;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.attila.horvath.config.Config;
import com.attila.horvath.mov3d.Root;
import com.attila.horvath.screen.MainScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.I18NBundle;

public class OptionScreenAssets {

	private Root root;

	//Stage
	private Stage stage;
	private Skin optionSkin, buttonSkin;
	private TextureAtlas buttonAtlas, optionskinAtlas;
	private Table table;
	private Label option, language, difficulty, volume, music, keyboard;
	private Slider musicSlider;
	private SelectBox<String> sbLanguage;
	private SelectBox<String> sbDifficulty;
	private CheckBox cbMusic, cbKeyboard;
	private TextButton buttonSave;

	// Preferences
	private FileHandle baseFileHandle;
	private I18NBundle myBundle;
	private Preferences preferences;

	public OptionScreenAssets(Root root) {
		this.root = root;

		load();
	}

	private void load() {
		buttonAtlas = new TextureAtlas("ui/pack/button.pack");
		optionskinAtlas = new TextureAtlas("ui/pack/optionskin.pack");
		buttonSkin = new Skin(Gdx.files.internal("ui/json/mainSkin.json"),
				buttonAtlas);
		optionSkin = new Skin(Gdx.files.internal("ui/json/optionSkin.json"),
				optionskinAtlas);
		
		if (stage == null) {
			stage = new Stage();
		}
		stage.clear();
		
		loadPreferences();
		loadComponents();
		setTable();
		
		stage.addActor(table);
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
	
	private void loadComponents() {
		int intLang = preferences.getInteger("language", -1);
		int intDiff = preferences.getInteger("difficulty", -1);
		float floatVolume = preferences.getFloat("volume", -1);
		boolean isMusic = preferences.getBoolean("music", true);
		boolean isKeyboard = preferences.getBoolean("keyboard", true);
		
		option = new Label(myBundle.get("option"), optionSkin);
		language = new Label(myBundle.get("language"), optionSkin);
		difficulty = new Label(myBundle.get("difficulty"), optionSkin);
		volume = new Label(myBundle.get("volume"), optionSkin);
		music = new Label(myBundle.get("music"), optionSkin);
		keyboard = new Label(myBundle.get("keyboard"), optionSkin);
		
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
		if (floatVolume == -1) {
			musicSlider.setValue(0);
		} else {
			musicSlider.setValue(floatVolume);
		}
		
		cbMusic = new CheckBox("", optionSkin);
		cbMusic.setChecked(isMusic);
		
		cbKeyboard = new CheckBox("", optionSkin);
		cbKeyboard.setChecked(isKeyboard);

		buttonSave = new TextButton(myBundle.get("save"), buttonSkin);
		buttonSave.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				preferences.putInteger("language",
						sbLanguage.getSelectedIndex());
				preferences.putInteger("difficulty",
						sbDifficulty.getSelectedIndex());
				preferences.putFloat("volume", musicSlider.getValue());
				if (sbLanguage.getSelectedIndex() == 0) {
					preferences.putString("display", "magyar");
				} else {
					preferences.putString("display", "english");
				}
				preferences.putBoolean("music", cbMusic.isChecked());
				preferences.putBoolean("keyboard", cbKeyboard.isChecked());
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
	}
	
	private void setTable() {
		table = new Table(optionSkin);
		table.setBounds(0, 0, Config.WIDTH, Config.HEIGHT);
		table.setPosition(0, 0);

		table.add(option).colspan(2).center().padBottom(10f).row();
		table.add(language).size(Config.CWIDTH, Config.CHEIGHT);
		table.add(sbLanguage).size(Config.CWIDTH, Config.CHEIGHT - 20f).center().row();
		table.add(difficulty).size(Config.CWIDTH, Config.CHEIGHT);
		table.add(sbDifficulty).size(Config.CWIDTH, Config.CHEIGHT - 20f).center().row();
		table.add(volume).size(Config.CWIDTH, Config.CHEIGHT);
		table.add(musicSlider).size(Config.CWIDTH, Config.CHEIGHT).row();
		table.add(music).size(Config.CWIDTH, Config.CHEIGHT);
		table.add(cbMusic).size(Config.CWIDTH, Config.CHEIGHT).row();
		table.add(keyboard).size(Config.CWIDTH, Config.CHEIGHT);
		table.add(cbKeyboard).size(Config.CWIDTH, Config.CHEIGHT).row();
		table.add(buttonSave).size(Config.BWIDTH, Config.BHEIGHT).colspan(2).center();
	}

	public Stage getStage() {
		return stage;
	}
	
	public void dispose() {
		stage.dispose();
		buttonSkin.dispose();
		optionSkin.dispose();
		buttonAtlas.dispose();
		optionskinAtlas.dispose();
	}
}