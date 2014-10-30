package com.attila.horvath.assets;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.attila.horvath.config.Config;
import com.attila.horvath.mov3d.Root;
import com.attila.horvath.screen.MainScreen;
import com.attila.horvath.screen.PauseScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.I18NBundle;

public class QuitScreenAssets {

	private Root root;
	private PauseScreen previousPScreen;
	
	// Stage
	private Stage stage;
	private Skin skin;
	private TextureAtlas atlas;
	private Table table;
	private TextButton buttonYes, buttonNo;
	private Label question;

	// Preferences
	private FileHandle baseFileHandle;
	private I18NBundle myBundle;
	private Preferences preferences;

	public QuitScreenAssets(Root root) {
		this.root = root;
		
		load();
	}
	
	public QuitScreenAssets(Root root, PauseScreen previousPScreen) {
		this.root = root;
		this.previousPScreen = previousPScreen;
		
		load();
	}

	private void load() {
		atlas = new TextureAtlas("ui/pack/button.pack");
		skin = new Skin(Gdx.files.internal("ui/json/mainSkin.json"), atlas);

		if (stage == null) {
			stage = new Stage();
		}
		stage.clear();
		
		loadPreferences();
		loadButtons();
		
		question = new Label(myBundle.get("question"), skin);
		setTable();
		
		stage.addActor(table);
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
	
	private void loadButtons() {
		buttonYes = new TextButton(myBundle.get("yes"), skin);
		buttonYes.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				stage.addAction(sequence(moveTo(0, -stage.getHeight(), 0.5f),
						run(new Runnable() {
							@Override
							public void run() {
								Gdx.app.exit();
							}
						})));
			}
		});
		buttonYes.pad(7.5f);

		buttonNo = new TextButton(myBundle.get("no"), skin);
		buttonNo.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				stage.addAction(sequence(moveTo(0, -stage.getHeight(), 0.5f),
						run(new Runnable() {

							@Override
							public void run() {
								if(previousPScreen == null) {
									root.setScreen(new MainScreen(root));
								} else {
									root.setScreen(previousPScreen);
								}
								
							}
						})));
			}
		});
		buttonNo.pad(7.5f);
	}
	
	private void setTable() {
		table = new Table(skin);
		table.setBounds(0, 0, Config.WIDTH, Config.HEIGHT);
		
		table.add(question).colspan(2).center().row();
		table.add(buttonYes).size(Config.BWIDTH, Config.BHEIGHT).center();
		table.add(buttonNo).size(Config.BWIDTH, Config.BHEIGHT).center().row();
	}

	public Stage getStage() {
		return stage;
	}
	
	public void dispose() {
		skin.dispose();
		stage.dispose();
	}
}
