package com.attila.horvath.assets;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.attila.horvath.config.Config;
import com.attila.horvath.game.screen.WindowsGameScreen;
import com.attila.horvath.mov3d.Root;
import com.attila.horvath.screen.MainScreen;
import com.attila.horvath.screen.PauseScreen;
import com.attila.horvath.screen.QuitScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.I18NBundle;

public class PauseScreenAssets {

	private Root root;
	private WindowsGameScreen gameScreen;
	private PauseScreen previousScreen;

	// Buttons and UI
	private Stage stage;
	private Skin skin;
	private TextureAtlas buttonAtlas;
	private TextButton buttonResume, buttonMain, buttonQuit;
	private Table table;

	// Preferences
	private FileHandle baseFileHandle;
	private I18NBundle myBundle;
	private Preferences preferences;
	
	public PauseScreenAssets(Root root, WindowsGameScreen gameScreen, PauseScreen previousScreen) {
		this.root = root;
		this.gameScreen = gameScreen;
		this.previousScreen = previousScreen;
		
		load();
	}
	
	private void load() {
		if (stage == null) {
			stage = new Stage();
		}
		stage.clear();

		loadPreferences();
		loadComponents();
		setTable();

		stage.addActor(table);
		Gdx.input.setInputProcessor(stage);
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

	private void loadComponents() {
		buttonAtlas = new TextureAtlas("ui/pack/button.pack");
		skin = new Skin(Gdx.files.internal("ui/json/mainSkin.json"),
				buttonAtlas);

		buttonResume = new TextButton(myBundle.get("resume"), skin);
		buttonResume.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				stage.addAction(sequence(moveTo(0, -stage.getHeight(), 0.5f),
						run(new Runnable() {

							@Override
							public void run() {
								root.setScreen(gameScreen);
							}
						})));
			}
		});
		buttonResume.pad(7.5f);

		buttonMain = new TextButton(myBundle.get("main"), skin);
		buttonMain.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				stage.addAction(sequence(moveTo(-stage.getWidth(), 0, 0.5f),
						run(new Runnable() {

							@Override
							public void run() {
								root.setScreen(new MainScreen(root));
							}
						})));
			}
		});
		buttonMain.pad(7.5f);

		buttonQuit = new TextButton(myBundle.get("quit"), skin);
		buttonQuit.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				stage.addAction(sequence(moveTo(0, stage.getHeight(), 0.5f),
						run(new Runnable() {

							@Override
							public void run() {
								root.setScreen(new QuitScreen(root, previousScreen));
							}
						})));
			}
		});
		buttonQuit.pad(7.5f);
	}

	private void setTable() {
		table = new Table(skin);
		table.setBounds(0, 0, Config.WIDTH, Config.HEIGHT);

		table.add(buttonResume).size(Config.BWIDTH, Config.BHEIGHT).center()
				.row();
		table.add(buttonMain).size(Config.BWIDTH, Config.BHEIGHT).center()
				.row();
		table.add(buttonQuit).size(Config.BWIDTH, Config.BHEIGHT).center()
				.row();
	}
	
	public Stage getStage() {
		return stage;
	}
	
	public void dispose() {
		buttonAtlas.dispose();
		skin.dispose();
		stage.dispose();
	}
}
