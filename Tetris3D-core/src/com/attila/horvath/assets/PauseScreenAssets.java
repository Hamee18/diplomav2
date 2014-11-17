package com.attila.horvath.assets;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.attila.horvath.config.Config;
import com.attila.horvath.game.screen.AndroidGameScreen;
import com.attila.horvath.game.screen.WindowsGameScreen;
import com.attila.horvath.screen.MainScreen;
import com.attila.horvath.screen.PauseScreen;
import com.attila.horvath.screen.QuitScreen;
import com.attila.horvath.tetris3d.Root;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.I18NBundle;

public class PauseScreenAssets {

	private Root root;
	private WindowsGameScreen gameScreenWindows;
	private AndroidGameScreen gameScreenAndroid;
	private PauseScreen previousScreen;

	// Buttons and UI
	private Stage stage;
	private Skin skin;
	private TextureAtlas buttonAtlas;
	private TextButton buttonResume, buttonRestart, buttonMain, buttonQuit;
	private Table table;

	// Preferences
	private FileHandle baseFileHandle;
	private I18NBundle myBundle;
	private Preferences preferences;

	// Animation
	private OrthographicCamera camera;
	private Texture animationI, animationL, animationO, animationZ;
	private TextureRegion[] framesAnimI, framesAnimL, framesAnimO, framesAnimZ;
	private TextureRegion currentFrameAnimI, currentFrameAnimL,
			currentFrameAnimO, currentFrameAnimZ;
	private Animation loadAnimationI, loadAnimationL, loadAnimationO, loadAnimationZ;

	public PauseScreenAssets(Root root, WindowsGameScreen gameScreen,
			PauseScreen previousScreen) {
		this.root = root;
		this.gameScreenWindows = gameScreen;
		this.previousScreen = previousScreen;

		load();
	}
	
	public PauseScreenAssets(Root root, AndroidGameScreen gameScreen,
			PauseScreen previousScreen) {
		this.root = root;
		this.gameScreenAndroid = gameScreen;
		this.previousScreen = previousScreen;

		load();
	}

	private void load() {
		if (stage == null) {
			stage = new Stage();
		}
		stage.clear();

		loadCamera();
		loadAnimations();
		loadPreferences();
		loadComponents();
		setTable();

		stage.addActor(table);
		Gdx.input.setInputProcessor(stage);
	}
	
	private void loadCamera() {
		camera = new OrthographicCamera();
		camera.setToOrtho(true, Config.WIDTH, Config.HEIGHT);
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
								if (Gdx.app.getType() == ApplicationType.Android) {
									root.setScreen(gameScreenAndroid);
								} else {
									root.setScreen(gameScreenWindows);
								}
								
							}
						})));
			}
		});
		buttonResume.pad(7.5f);

		buttonRestart = new TextButton(myBundle.get("restart"), skin);
		buttonRestart.addListener(new ClickListener() {
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
		buttonRestart.pad(7.5f);

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
								root.setScreen(new QuitScreen(root,
										previousScreen));
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
		table.add(buttonRestart).size(Config.BWIDTH, Config.BHEIGHT).center()
				.row();
		table.add(buttonMain).size(Config.BWIDTH, Config.BHEIGHT).center()
				.row();
		table.add(buttonQuit).size(Config.BWIDTH, Config.BHEIGHT).center()
				.row();
	}

	public Stage getStage() {
		return stage;
	}
	
	public OrthographicCamera getCamera() {
		return camera;
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
		buttonAtlas.dispose();
		skin.dispose();
		stage.dispose();
	}
}
