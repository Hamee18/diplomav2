package com.attila.horvath.assets;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import java.util.Arrays;

import com.attila.horvath.config.Config;
import com.attila.horvath.screen.MainScreen;
import com.attila.horvath.tetris3d.Root;
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

public class HighscoreScreenAssets {

	private Root root;
	private int newScore;
	private String score;

	// Stage
	private Stage stage;
	private Skin skin;
	private TextureAtlas atlas;
	private Table table;
	private TextButton buttonBack;
	private Label highscore;

	// Preferences
	private FileHandle baseFileHandle;
	private I18NBundle myBundle;
	private Preferences preferences;

	public HighscoreScreenAssets(Root root, int newScore) {
		this.root = root;
		this.newScore = newScore;
		
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
			baseFileHandle = Gdx.files.internal("ui/localization/"
					+ displayLang);
		}

		myBundle = I18NBundle.createBundle(baseFileHandle, "UTF8");
	}

	private void loadComponents() {
		highscore = new Label(myBundle.get("highscore"), skin);

		buttonBack = new TextButton(myBundle.get("back"), skin);
		buttonBack.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {

				stage.addAction(sequence(moveTo(stage.getWidth(), 0, 0.5f),
						run(new Runnable() {

							@Override
							public void run() {
								preferences.putString("score", score);
								preferences.flush();
								root.setScreen(new MainScreen(root));
							}
						})));
			}
		});
	}

	private void setTable() {
		score = preferences.getString("score", "");
		
		if (newScore != 0) {			
			score = score + ";" + String.valueOf(newScore);
		}
		
		String[] scores = score.split(";");
		Arrays.sort(scores);

		table = new Table(skin);
		table.setBounds(0, 0, Config.WIDTH, Config.HEIGHT);
		table.setPosition(0, 0);
		
		if (scores.length > 0) {
			
			table.add(highscore).colspan(2).center().padBottom(10f).row();
			for(int i = (scores.length-1); i > 0; i--) {
				if(i > 0) {
					table.add(String.valueOf(scores.length - i) + ".").size(Config.CWIDTH, Config.CHEIGHT);
					table.add(scores[i]).size(Config.CWIDTH, Config.CHEIGHT - 20f).center().row();
				}
			}
		}
		
		table.add(buttonBack).size(Config.BWIDTH, Config.BHEIGHT).colspan(2).center();
	}

	public Stage getStage() {
		return stage;
	}
	
	public void dispose() {
		stage.dispose();
		skin.dispose();
		atlas.dispose();
	}
}
