package com.attila.horvath.assets;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.attila.horvath.config.Config;
import com.attila.horvath.game.screen.AndroidGameScreen;
import com.attila.horvath.game.screen.WindowsGameScreen;
import com.attila.horvath.mov3d.Root;
import com.attila.horvath.screen.OptionScreen;
import com.attila.horvath.screen.TutorialScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
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

public class MainScreenAssets {
	
	private Root root;

	// Stage
	private Stage stage;
	private Skin skin;
	private TextureAtlas atlas;
	private Table table;
	private TextButton buttonPlay;
	private TextButton buttonOption;
	private TextButton buttonTutorial;
	private TextButton buttonQuit;

	// Desktop background
	private OrthographicCamera camera;
	private Sprite sprite;
	private Texture texture;
	
	// Animation
	private Texture animationI;
	private Texture animationL;
	private Texture animationO;
	private Texture animationZ;
	private TextureRegion[] framesAnimI;
	private TextureRegion[] framesAnimL;
	private TextureRegion[] framesAnimO;
	private TextureRegion[] framesAnimZ;
	private TextureRegion currentFrameAnimI;
	private TextureRegion currentFrameAnimL;
	private TextureRegion currentFrameAnimO;
	private TextureRegion currentFrameAnimZ;
	private Animation loadAnimationI;
	private Animation loadAnimationL;
	private Animation loadAnimationO;
	private Animation loadAnimationZ;

	// Preferences
	private FileHandle baseFileHandle;
	private I18NBundle myBundle;
	private Preferences preferences;

	// Music
	private Music menuMusic;

	public MainScreenAssets(Root root) {
		this.root = root;

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
		loadBackground();
		loadAnimations();
		loadButtons();
		loadMusic();
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

	private void loadBackground() {
		camera = new OrthographicCamera();
		camera.setToOrtho(true, Config.WIDTH, Config.HEIGHT);
		texture = new Texture(Gdx.files.internal("ui/background.png"));
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		TextureRegion region = new TextureRegion(texture, 0, 0, Config.WIDTH, Config.HEIGHT);
		sprite = new Sprite(region);
		sprite.setSize(1f, sprite.getHeight() / sprite.getWidth());
		sprite.setOrigin(sprite.getWidth() / 2, sprite.getHeight() / 2);
		sprite.setPosition(-sprite.getWidth() / 2, -sprite.getHeight() / 2);
		sprite.flip(false, true);
	}

	private void loadAnimations() {
		animationL = new Texture(Gdx.files.internal("ui/animations/hatteranim1.png"));
		animationO = new Texture(Gdx.files.internal("ui/animations/hatteranim2.png"));
		animationZ = new Texture(Gdx.files.internal("ui/animations/hatteranim3.png"));
		animationI = new Texture(Gdx.files.internal("ui/animations/hatteranim4.png"));
		
		TextureRegion[][] temp = TextureRegion.split(animationL, 48, 480);
		TextureRegion[][] temp2 = TextureRegion.split(animationO, 48, 480);
		TextureRegion[][] temp3 = TextureRegion.split(animationZ, 96, 480);
		TextureRegion[][] temp4 = TextureRegion.split(animationI, 48, 480);
		
		framesAnimL = new TextureRegion[20];
		framesAnimO = new TextureRegion[20];
		framesAnimZ = new TextureRegion[10];
		framesAnimI = new TextureRegion[20];
		
		int index = 0;
		for(int i = 0; i < temp.length; i++) {
			for(int j = 0; j < temp[i].length; j++) {
				framesAnimL[index] = temp[i][j];
				framesAnimL[index].flip(false, true);
				framesAnimO[index] = temp2[i][j];
				framesAnimO[index].flip(false, true);
				framesAnimI[index] = temp4[i][j];
				framesAnimI[index].flip(false, true);
				index++;
			}		
		}
		
		index = 0;
		for(int i = 0; i < temp3.length; i++) {
			for(int j = 0; j < temp3[i].length; j++) {
				framesAnimZ[index] = temp3[i][j];
				framesAnimZ[index].flip(false, true);
				index++;
			}		
		}
		
		loadAnimationL = new Animation(1F, framesAnimL);
		loadAnimationO = new Animation(1F, framesAnimO);
		loadAnimationZ = new Animation(1F, framesAnimZ);
		loadAnimationI = new Animation(1F, framesAnimI);
	}
	
	private void loadButtons() {
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
	}

	private void loadMusic() {
		menuMusic = Gdx.audio.newMusic(Gdx.files
				.internal("music/menumusic.mp3"));
		menuMusic.play();
		menuMusic.setLooping(true);
		
		float volume = preferences.getFloat("music", -1);
		
		if (volume == -1) {
			menuMusic.setVolume(0);
		} else {
			menuMusic.setVolume(volume/100);
		}
		
	}
	
	private void setTable() {
		table = new Table(skin);
		table.setBounds(0, 0, Config.WIDTH, Config.HEIGHT);
		table.add(buttonPlay);
		table.getCell(buttonPlay).height(Config.BHEIGHT);
		table.getCell(buttonPlay).width(Config.BWIDTH);
		table.row();
		table.add(buttonOption);
		table.getCell(buttonOption).height(Config.BHEIGHT);
		table.getCell(buttonOption).width(Config.BWIDTH);
		table.row();
		table.add(buttonTutorial);
		table.getCell(buttonTutorial).height(Config.BHEIGHT);
		table.getCell(buttonTutorial).width(Config.BWIDTH);
		table.row();
		table.add(buttonQuit);
		table.getCell(buttonQuit).height(Config.BHEIGHT);
		table.getCell(buttonQuit).width(Config.BWIDTH);
	}
	
	public Stage getStage() {
		return stage;
	}

	public OrthographicCamera getCamera() {
		return camera;
	}

	public Sprite getSprite() {
		return sprite;
	}

	public Music getMenuMusic() {
		return menuMusic;
	}
	
	public TextureRegion getCurrentFrameAnimL() {
		return currentFrameAnimL;
	}
	
	public TextureRegion getCurrentFrameAnimO() {
		return currentFrameAnimO;
	}
	
	public TextureRegion getCurrentFrameAnimZ() {
		return currentFrameAnimZ;
	}
	
	public TextureRegion getCurrentFrameAnimI() {
		return currentFrameAnimI;
	}

	public void setCurrentFrameAnim(float stateTime) {
		currentFrameAnimL = loadAnimationL.getKeyFrame(stateTime, true);
		currentFrameAnimO = loadAnimationO.getKeyFrame(stateTime, true);
		currentFrameAnimZ = loadAnimationZ.getKeyFrame(stateTime, true);
		currentFrameAnimI = loadAnimationI.getKeyFrame(stateTime, true);
	}

	public void dispose() {
		stage.dispose();
		menuMusic.dispose();
		skin.dispose();
		atlas.dispose();
		texture.dispose();
	}
}