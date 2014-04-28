package com.apptogo.runalien.scenes;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import javax.microedition.khronos.opengles.GL10;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.Entity;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.entity.modifier.MoveYModifier;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.primitive.TexturedPolygon;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.AutoParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.debugdraw.DebugRenderer;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.modifier.ease.EaseBounceInOut;
import org.andengine.util.modifier.ease.EaseElasticInOut;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.apptogo.runalien.BaseScene;
import com.apptogo.runalien.Player;
import com.apptogo.runalien.R;
import com.apptogo.runalien.ResourcesManager;
import com.apptogo.runalien.SceneManager;
import com.apptogo.runalien.SceneManager.SceneType;
import com.apptogo.runalien.coins.Coin;
import com.apptogo.runalien.obstacles.ObstacleGenerator;
import com.apptogo.runalien.obstacles.ObstaclesPoolManager;
import com.apptogo.runalien.utils.GoogleBaseGameActivity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.games.Games;
import com.google.android.gms.internal.ba;

public class GameScene extends BaseScene implements IOnSceneTouchListener {
	
	//main objects
	private HUD gameHUD;
	private PhysicsWorld physicsWorld;
	private Player player;
	ObstacleGenerator obstacleGenerator;
	
	
	//ground
	private int center = 0, center2;
	final int LEVEL_BLOCK_LENGTH = 5;
	private TexturedPolygon ground;
	private Vector2[] levelCoordinates;
	Body groundBody;
	
	//background
	private AutoParallaxBackground autoParallaxBackground;
	private ParallaxEntity frontParallaxBackground;
	private ParallaxEntity grassParallaxBackground;
	
	//flags
	private boolean firstUpdate = true;
	private boolean firstTouch = false;
	private boolean firstGroundBlock = true;
	private boolean gamePaused = false;
	private boolean displayTutorial = false;
	private boolean isTutorialTableShowed = false;

	//gameover sprites
	Sprite sGameOver;
	Sprite sReplay;
	Sprite sMenu;
	Sprite sSubmit;
	Sprite sPause;

	//tutorial
	private Sprite tapToJump;
	private Sprite tapToDoubleJump;
	private Sprite tapToSlide;
	private Sprite tapToChargeDown;
	private float nextTutorialPartDelay;
	private boolean[] partOfTutorialDisplayed = {false, false, false, false};
	private boolean[] partOfTutorialCompleted = {false, false, false, false};
	private int tutorialScoreOffset = 0;
	
	//ads
	private AdView adView;
	
	//local highscore 
	private static final String HIGHSCORE_DB_NAME = "MyGameHighscores";
	private static final String HIGHSCORE_LABEL = "score";
	private static final String TUTORIAL_DISPLAYED_LABEL = "firstGame";
	private static final String GAMES_COUNTER_LABEL = "gamesCounter";
	private SharedPreferences mScoreDb = activity.getSharedPreferences(HIGHSCORE_DB_NAME, Context.MODE_PRIVATE);
	private SharedPreferences.Editor mScoreDbEditor = this.mScoreDb.edit();
	public int score = 0;
	private Text bestScoreText;
	private Text scoreText; //actual score top-left corner

	
	//ustawienia

	private boolean playSound = activity.preferences.getBoolean(activity.SOUNDS_LABEL, true);
	
	//lines
	Line line;
	float nextLineX = 400f;
	
	//layers
	Entity backgroundLayer;
	Entity foregroundLayer;
	
	//pause menu
	private boolean canPause = true;

	//OVERRIDEN METHODS
	@Override
	public void createScene() {
		ResourcesManager.getInstance().engine.registerUpdateHandler(new FPSLogger());
		backgroundLayer = new Entity();
		foregroundLayer = new Entity();
		createHUD();
		createPhysics();
		//createGround();
		createGroundBody();
		setOnSceneTouchListener(this);
		createBackground();
		createBestScoreTable();
		attachChild(backgroundLayer);
		attachChild(foregroundLayer);

		createPlayer();
		ObstaclesPoolManager.getInstance().initializePoolManager(physicsWorld, foregroundLayer, backgroundLayer);
		obstacleGenerator = new ObstacleGenerator(this, player);
		
		
	}

	@Override
	public void onBackKeyPressed() {
		if(gamePaused)
		{
			resumeGame();
		}
		else
		{
			pauseGame(false);
			ResourcesManager.getInstance().activity.displayInterstitialAndLoadMenuScene();
		}
	}
	
	@Override
	public void onMenuKeyPressed() {
		if(canPause)
		{
			if(!gamePaused) 
			{
				pauseGame(true);
			}
			else         
			{
				resumeGame();
			}
		}
	}
	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_GAME;
	}

	@Override
	public void disposeScene() {
		player.setCameraShiftX(0);
		player.setCameraShiftY(0);
		player.setBoundsFlag(false);
		camera.setHUD(null);
		camera.setChaseEntity(null);
		camera.setBoundsEnabled(false);
		camera.setCenter(400, 240);
		activity.setgameBannerAdViewInvisibile();
		ObstaclesPoolManager.getInstance().clearPools();
	}

	//GROUND AND BACKGROUND
	private void createBackground() {
		autoParallaxBackground = new AutoParallaxBackground(0, 0, 0, 5);
		autoParallaxBackground.attachParallaxEntity(new ParallaxEntity(0.0f, new Sprite(0, 0, resourcesManager.mParallaxLayerBack, resourcesManager.vbom)));
		autoParallaxBackground.attachParallaxEntity(new ParallaxEntity(-2.0f, new Sprite(0, 10, resourcesManager.mParallaxLayerMid, resourcesManager.vbom)));

		frontParallaxBackground = new ParallaxEntity(-20.0f, new Sprite(0, 280, resourcesManager.mParallaxLayerFront, resourcesManager.vbom));
		autoParallaxBackground.attachParallaxEntity(frontParallaxBackground);
		
		//grassParallaxBackground = new ParallaxEntity(-30f, new Sprite(0, 346, resourcesManager.mParallaxLayerGrass, resourcesManager.vbom));
		//autoParallaxBackground.attachParallaxEntity(grassParallaxBackground);
		
		setBackground(autoParallaxBackground);
	}
	
	private void generateLevelCoordinates() {
		levelCoordinates = null;
		levelCoordinates = new Vector2[4];
		levelCoordinates[0] = new Vector2(center - 200, 360);
		levelCoordinates[1] = new Vector2(center - 200, 240);
		levelCoordinates[2] = new Vector2(center + 600, 240);
		levelCoordinates[3] = new Vector2(center + 600 , 360);
	}

	private void createGroundBody(){
 		Sprite groundCover = new Sprite(0, 0, ResourcesManager.getInstance().playerCover_region, ResourcesManager.getInstance().vbom);
 		groundCover.setUserData("ground");
		this.attachChild(groundCover);
		groundCover.setVisible(false);
 		groundBody = PhysicsFactory.createBoxBody(physicsWorld, 0, 250, 500, 10, BodyType.StaticBody, PhysicsFactory.createFixtureDef(10.0f, 0, 0.07f));
 		groundBody.setUserData("ground");
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(groundCover, groundBody, true, false) {
			@Override
			public void onUpdate(float pSecondsElapsed) {
				super.onUpdate(pSecondsElapsed);
				groundBody.setTransform(player.getBody().getPosition().x, groundBody.getPosition().y, 0);
			}
		});
	}
	private void createGround() {
 		generateLevelCoordinates();
 		
 		Sprite groundCover = new Sprite(0, 0, ResourcesManager.getInstance().playerCover_region, ResourcesManager.getInstance().vbom);
 		groundCover.setUserData("ground");
		this.attachChild(groundCover);
		groundCover.setVisible(false);
 		groundBody = PhysicsFactory.createBoxBody(physicsWorld, 0, 245, 100, 10, BodyType.StaticBody, PhysicsFactory.createFixtureDef(10.0f, 0, 0.07f));
 		groundBody.setUserData("ground");
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(groundCover, groundBody, true, false) {
			@Override
			public void onUpdate(float pSecondsElapsed) {
				super.onUpdate(pSecondsElapsed);
				groundBody.setTransform(player.getBody().getPosition().x, groundBody.getPosition().y, 0);
			}
		});
 		
 		ChainShape myChain = new ChainShape();
 		Vector2[] myV2 = new Vector2[levelCoordinates.length];
 		for (int i = 0; i < levelCoordinates.length; i++) {
 			myV2[i] = new Vector2(levelCoordinates[i].x / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, levelCoordinates[i].y / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
 		}
 		myChain.createChain(myV2);
 		center2 = (int) (myV2[0].x);
 
 		FixtureDef mFixtureDef = new FixtureDef();
 		mFixtureDef.shape = myChain;
 
 		BodyDef mBodyDef = new BodyDef();
 		mBodyDef.type = BodyType.StaticBody;
 		
 		Body mChainBody;
 		mChainBody = physicsWorld.createBody(mBodyDef);
 		mChainBody.createFixture(mFixtureDef);
 		myChain.dispose();
 		mChainBody.setUserData("ground");
  		
 		// TEXTURED POLYGON 2 - DIRT - TEXTURE REGION MUST BE FROM A REPEATING ATLAS
 		float[] vertexX2 = new float[myV2.length];
 		float[] vertexY2 = new float[myV2.length];
 
 		for (int i = 0; i < myV2.length; i++) {
 			vertexX2[i] = myV2[i].x * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
 			vertexY2[i] = myV2[i].y * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;

  		}
 		ground = new TexturedPolygon(0, 0, vertexX2, vertexY2, resourcesManager.dirt_texture_region, vbom);
 		backgroundLayer.attachChild(ground);
 		ground.setUserData("ground");
 		
 		Sprite grass = new Sprite(center - 200, 232, ResourcesManager.getInstance().grass_region, vbom);
 		ground.attachChild(grass);
 		center += 800;
  		//backgroundLayer.attachChild(new DebugRenderer(physicsWorld, vbom));
  	}

	//MAIN OBJECTS METHODS
	private void createHUD() {
		gameHUD = new HUD();
		scoreText = new Text(5, 0, ResourcesManager.getInstance().mainFont, "score: 0123456789", new TextOptions(), vbom);
		scoreText.setText("SCORE: 0");
		gameHUD.attachChild(scoreText);
		camera.setHUD(gameHUD);
		
		sPause = new Sprite(-1000, 0, ResourcesManager.getInstance().pause_region, vbom);
		gameHUD.attachChild(sPause);

		//tapToJump = new Text(0, 0, ResourcesManager.getInstance().mainFont, "Tap right site of the screen to jump", new TextOptions(), vbom);
		//tapToJump.setPosition(2000, 400);
		
		tapToJump = new Sprite(2000, 400, ResourcesManager.getInstance().jumpTutorial_region, vbom);
		tapToDoubleJump = new Sprite(2000, 400, ResourcesManager.getInstance().doubleJumpTutorial_region, vbom);
		tapToSlide = new Sprite(-2000, 400, ResourcesManager.getInstance().slideTutorial_region, vbom);
		tapToChargeDown = new Sprite(-2000, 400, ResourcesManager.getInstance().chargeDownTutorial_region, vbom);
		
		//tapToDoubleJump = new Text(0, 0, ResourcesManager.getInstance().mainFont, "Tap right side of the screen TWO TIMES to double jump", new TextOptions(), vbom);
		//tapToDoubleJump.setPosition(2000, 400);
		//tapToSlide = new Text(0, 0, ResourcesManager.getInstance().mainFont, "Tap left side of the screen to jump", new TextOptions(), vbom);
		//tapToSlide.setPosition(-2000, 400);
		//tapToChargeDown = new Text(0, 0, ResourcesManager.getInstance().mainFont, "Tap left site of the screen in air to charge down", new TextOptions(), vbom);
		//tapToChargeDown.setPosition(-2000, 400);
	}

	private void createPhysics() {
		physicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0, 100.0f), false);
		physicsWorld.setContactListener(contactListener());
		registerUpdateHandler(physicsWorld);
		registerUpdateHandler(new IUpdateHandler() {

			@Override
			public void reset() {
			}

			@Override
			public void onUpdate(float pSecondsElapsed) {
				camera.setChaseEntity(player);
				if(player.isAlive()){
					if(partOfTutorialCompleted[3]){
						if(tutorialScoreOffset == 0)
							tutorialScoreOffset = Math.round(player.getBody().getPosition().x/10);
						score = (int) Math.round(player.getBody().getPosition().x/10) - tutorialScoreOffset;
					}
					displayTutorial();
					setScore(score);
					setDistanceAchievements();
				}
								
				if (player.getBody().getPosition().x > center2) 
				{
					//createGround();
					//if (backgroundLayer.getChildCount() == 4) 
					//{
					//	backgroundLayer.detachChild(backgroundLayer.getFirstChild());
					//}
				}
				createTutorialTable();
				if(displayTutorial)
					generateTutorial();
				if(player.getY() > 1000)
					player.dieTop(false);
			}
		});
	}

	private void createPlayer() {
		boolean play = activity.preferences.getBoolean(activity.SOUNDS_LABEL, true);
		System.out.println("MUTE "+ (play?"1":"0") );
		player = new Player(120, 145, vbom, camera, physicsWorld, play) {
			@Override
			public void onDie() {
				canPause = false; //zeby nie mozna bylo juz wlaczyc pauzy
				saveHighScore();
				autoParallaxBackground.stop();
				showGameOver();
				mScoreDbEditor.putBoolean(TUTORIAL_DISPLAYED_LABEL, true);
				mScoreDbEditor.commit();
				incrementAchievements();
				//if(playSound) ResourcesManager.getInstance().gameMusic.pause();
			}
		};
		player.setUserData("player");
		foregroundLayer.attachChild(player);
	}

	private void showGameOver() {
		sGameOver = new Sprite(0, 0, ResourcesManager.getInstance().game_over_region, vbom);
		sReplay = new Sprite(0, 0, ResourcesManager.getInstance().replay_region, vbom) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if(playSound) ResourcesManager.getInstance().clickSound.play();
				//sceneManager.replayGameScene();
				activity.setgameBannerAdViewInvisibile();
				activity.displayInterstitialIfReadyAndReplay();
				return true;
			}
		};
		sMenu = new Sprite(0, 0, ResourcesManager.getInstance().menu_region, vbom) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if(playSound) ResourcesManager.getInstance().clickSound.play();
				//sceneManager.loadMenuScene();
				ResourcesManager.getInstance().activity.displayInterstitialAndLoadMenuScene();
				return true;
			}
		};
		sSubmit = new Sprite(0, 0, ResourcesManager.getInstance().submit_region, vbom) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if(playSound) ResourcesManager.getInstance().clickSound.play();

				if(((GoogleBaseGameActivity)activity).isSignedIn()){
					Games.Leaderboards.submitScore(ResourcesManager.getInstance().activity.getGoogleApiClient(), activity.getResources().getString(R.string.leaderboard_highscores), score);
					((GoogleBaseGameActivity)activity).startActivityForResult(Games.Leaderboards.getLeaderboardIntent(((GoogleBaseGameActivity)activity).getApiClient(), activity.getResources().getString(R.string.leaderboard_highscores)), 0);
				}
				else{
					resourcesManager.gameHelper.manualConnect();
					//Games.Leaderboards.submitScore(ResourcesManager.getInstance().activity.getGoogleApiClient(), activity.getResources().getString(R.string.leaderboard_highscores), score);
				}
				return true;
			}
		};

		this.registerUpdateHandler(new TimerHandler(1f, new ITimerCallback() {
			public void onTimePassed(final TimerHandler pTimerHandler) {
				resourcesManager.engine.unregisterUpdateHandler(pTimerHandler);
				SceneManager.getInstance().getCurrentScene().unregisterUpdateHandler(pTimerHandler);

				sGameOver.setPosition(400-sGameOver.getWidth()/2, -300);
				sMenu.setPosition(400-sMenu.getWidth()/2, -300);
				sSubmit.setPosition(400-sSubmit.getWidth()/2, -300);
				sReplay.setPosition(400-sReplay.getWidth()/2, -300);
				
				gameHUD.registerTouchArea(sMenu);
				gameHUD.registerTouchArea(sSubmit);
				gameHUD.registerTouchArea(sReplay);
				
				sGameOver.registerEntityModifier(new MoveYModifier(12.5f, sGameOver.getY(), 120, org.andengine.util.modifier.ease.EaseElasticInOut.getInstance()));
				sMenu.registerEntityModifier(new MoveYModifier(12.5f, sMenu.getY(), 200, EaseElasticInOut.getInstance()));
				sSubmit.registerEntityModifier(new MoveYModifier(12.5f, sMenu.getY(), 250, EaseElasticInOut.getInstance()));
				sReplay.registerEntityModifier(new MoveYModifier(12.5f, sReplay.getY(), 310, EaseElasticInOut.getInstance()));


				
				
				gameHUD.attachChild(sGameOver);
				gameHUD.attachChild(sReplay);
				gameHUD.attachChild(sMenu);
				gameHUD.attachChild(sSubmit);
			}
		}));
		this.registerUpdateHandler(new TimerHandler(2f, new ITimerCallback() {
			public void onTimePassed(final TimerHandler pTimerHandler) {
				resourcesManager.engine.unregisterUpdateHandler(pTimerHandler);
				SceneManager.getInstance().getCurrentScene().unregisterUpdateHandler(pTimerHandler);
				if(playSound) ResourcesManager.getInstance().whooshSound.play();
				activity.setgameBannerAdViewVisibile();
			}
		}));
		
	}
	
	private void showPause()
	{
		sPause.setPosition( player.playerCover.getX()+160, 120 );
	}
	
	private void hidePause()
	{
		sPause.setPosition( player.playerCover.getX() - 1000, 240 );
	}

	
	private void createTutorialTable() {
		if(!displayTutorial && !isTutorialTableShowed){
			Sprite sTable = new Sprite(-130, 270, resourcesManager.tutorialTable_region, resourcesManager.vbom);
			backgroundLayer.attachChild(sTable);
			isTutorialTableShowed = true;
		}
	}
	
	
	//SCORE METHODS
	private void createBestScoreTable() {
		Sprite sSign = new Sprite(600, 50, resourcesManager.sign_region, resourcesManager.vbom);
		foregroundLayer.attachChild(sSign);
		bestScoreText = new Text(0, 0, ResourcesManager.getInstance().bestScoreFont, "012345679", ResourcesManager.getInstance().vbom);
		bestScoreText.setPosition(670, 110);
		foregroundLayer.attachChild(bestScoreText);
	}
	
	private void setScore(int score) {
		scoreText.setText("SCORE: " + score);
		if (firstUpdate) {
			bestScoreText.setText(String.valueOf(loadHighScore()));
			firstUpdate = false;
		}
	}

	public void setDistanceAchievements(){
		if(((GoogleBaseGameActivity)ResourcesManager.getInstance().activity).isSignedIn()){
			if(score == 50)
				Games.Achievements.unlock(ResourcesManager.getInstance().activity.getGoogleApiClient(), "CgkIpZ2MjMkXEAIQAw");	
			else if(score == 200)
				Games.Achievements.unlock(ResourcesManager.getInstance().activity.getGoogleApiClient(), "CgkIpZ2MjMkXEAIQBA");	
			else if(score == 1000)
				Games.Achievements.unlock(ResourcesManager.getInstance().activity.getGoogleApiClient(), "CgkIpZ2MjMkXEAIQBQ");	
		}
	}
	public void incrementAchievements(){
		/*int counter = this.mScoreDb.getInt(GAMES_COUNTER_LABEL, 0);
		System.out.println("COUNTER pobralem: " + counter);
		counter++;
		this.mScoreDbEditor.putInt(GAMES_COUNTER_LABEL, counter);
		this.mScoreDbEditor.commit();
		if(((GoogleBaseGameActivity)ResourcesManager.getInstance().activity).isSignedIn()){
			if(counter>=10)
				Games.Achievements.unlock(ResourcesManager.getInstance().activity.getGoogleApiClient(), "CgkIpZ2MjMkXEAIQEQ");	
			else if(counter>=50)
				Games.Achievements.unlock(ResourcesManager.getInstance().activity.getGoogleApiClient(), "CgkIpZ2MjMkXEAIQBg");	
			else if(counter>=200)
				Games.Achievements.unlock(ResourcesManager.getInstance().activity.getGoogleApiClient(), "CgkIpZ2MjMkXEAIQBw");	
		}*/
		if(((GoogleBaseGameActivity)ResourcesManager.getInstance().activity).isSignedIn()){
			Games.Achievements.increment(ResourcesManager.getInstance().activity.getGoogleApiClient(), "CgkIpZ2MjMkXEAIQBw", 1);
			Games.Achievements.increment(ResourcesManager.getInstance().activity.getGoogleApiClient(), "CgkIpZ2MjMkXEAIQEQ", 1);
			Games.Achievements.increment(ResourcesManager.getInstance().activity.getGoogleApiClient(), "CgkIpZ2MjMkXEAIQBg", 1);
		}
	}
	public boolean saveHighScore() {
		if (score > loadHighScore())
			this.mScoreDbEditor.putInt(HIGHSCORE_LABEL, this.score);
		return this.mScoreDbEditor.commit();
	}

	public int loadHighScore() {
		if (mScoreDb == null)
			return 0;
		else
			return this.mScoreDb.getInt(HIGHSCORE_LABEL, 0);
	}
	//TUTORIAL METHODS
	private void displayTutorial(){
		if (firstUpdate) {
			
			if (mScoreDb != null){
				if(!this.mScoreDb.getBoolean(TUTORIAL_DISPLAYED_LABEL, false)){ //dodac wykrzyknik
					System.out.println("tutorial ma dzialac");
					displayTutorial = true;
				}
				else{
					System.out.println("TUTORIAL JUZ BYL POKAZANY");
					displayTutorial = false;
				}
			}
		}
	}
	
	public void pauseGame(boolean displayPause){
		setOnSceneTouchListener(null);
		if(displayPause) showPause();
		gamePaused = true;
		camera.setChaseEntity(null);
		this.setIgnoreUpdate(true);
		player.runSound.pause();
		player.screamSound.pause();
		//ResourcesManager.getInstance().gameMusic.pause();
	}
	
	public void resumeGame(){
		hidePause();
		setOnSceneTouchListener(this);	
		//firstTouch = true;
		gamePaused = false;
		camera.setChaseEntity(player);
		this.setIgnoreUpdate(false);
		//ResourcesManager.getInstance().gameMusic.resume();
	}
	
	private void generateTutorial(){
		player.setCanSpeedUp(false);
		if(player.getBody().getPosition().x > 5 && !partOfTutorialDisplayed[0]){
			//tapToJump.setText("Tap right side of the screen to jump");
			gameHUD.attachChild(tapToJump);
			tapToJump.registerEntityModifier(new MoveXModifier(12.5f, tapToJump.getX(), 10, EaseElasticInOut.getInstance()));
			partOfTutorialDisplayed[0] = true;
		}
		else if(player.getBody().getPosition().x > nextTutorialPartDelay && !partOfTutorialDisplayed[1] && partOfTutorialCompleted[0]){
			tapToJump.registerEntityModifier(new MoveXModifier(12.5f, tapToJump.getX(), -1000, EaseElasticInOut.getInstance()));
			//tapToDoubleJump.setText("Tap right side of the screen TWO TIMES to double jump");
			gameHUD.attachChild(tapToDoubleJump);
			tapToDoubleJump.registerEntityModifier(new MoveXModifier(12.5f, tapToDoubleJump.getX(), 10, EaseElasticInOut.getInstance()));
			partOfTutorialDisplayed[1] = true;
		}
		else if(player.getBody().getPosition().x > nextTutorialPartDelay && !partOfTutorialDisplayed[2] && partOfTutorialCompleted[1]){
			tapToDoubleJump.registerEntityModifier(new MoveXModifier(12.5f, tapToDoubleJump.getX(), -1000, EaseElasticInOut.getInstance()));
			//tapToSlide.setText("Tap left side of the screen to slide");
			gameHUD.attachChild(tapToSlide);
			tapToSlide.registerEntityModifier(new MoveXModifier(12.5f, tapToSlide.getX(), 10, EaseElasticInOut.getInstance()));
			partOfTutorialDisplayed[2] = true;
		}
		else if(player.getBody().getPosition().x > nextTutorialPartDelay && !partOfTutorialDisplayed[3] && partOfTutorialCompleted[2]){
			tapToSlide.registerEntityModifier(new MoveXModifier(12.5f, tapToSlide.getX(), 1000, EaseElasticInOut.getInstance()));
			//tapToChargeDown.setText("Tap left side of the screen in air to charge down");
			gameHUD.attachChild(tapToChargeDown);
			tapToChargeDown.registerEntityModifier(new MoveXModifier(12.5f, tapToChargeDown.getX(), 10, EaseElasticInOut.getInstance()));
			partOfTutorialDisplayed[3] = true;
		}
		else if(player.getBody().getPosition().x > nextTutorialPartDelay && partOfTutorialCompleted[3]){
			tapToChargeDown.registerEntityModifier(new MoveXModifier(12.5f, tapToChargeDown.getX(), 1000, EaseElasticInOut.getInstance()));
			nextTutorialPartDelay = player.getBody().getPosition().x + 10;
			displayTutorial = false;
			obstacleGenerator.setNextObstaclePosition(player.getBody().getPosition().x + 50);
			System.out.println("offset " + tutorialScoreOffset + " player: " + player.getX());
			obstacleGenerator.startGeneratingObstacles(player.getX());
			player.setCanSpeedUp(true);
		}
	}
	
	// LISTENERS
	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		if (pSceneTouchEvent.isActionDown()) {
			if(gamePaused) 
			{
				resumeGame();
			}
			if (!firstTouch) {
				player.setRunning();
				obstacleGenerator.startObstacleGenerationAlgorithm();
				firstTouch = true;
				autoParallaxBackground.start();
				//if(playSound){
				//	ResourcesManager.getInstance().gameMusic.setLooping(true);
				//	ResourcesManager.getInstance().gameMusic.seekTo(0);
				//	ResourcesManager.getInstance().gameMusic.play();
				//}
				
				if(!displayTutorial){
					obstacleGenerator.startGeneratingObstacles(player.getX());
					for(int i=0; i<partOfTutorialCompleted.length; i++)
						partOfTutorialCompleted[i] = true;
					for(int i=0; i<partOfTutorialDisplayed.length; i++)
						partOfTutorialDisplayed[i] = true;
				}
			} else if (pSceneTouchEvent.getX() > player.getX() + 200) {
				if(partOfTutorialCompleted[0] && partOfTutorialDisplayed[1]){
					player.doubleJump();
					if(player.isDoubleJumped()){
						partOfTutorialCompleted[1] = true;
					}
				}
				player.jump();
				if(player.isJumping())
					partOfTutorialCompleted[0] = true;
			} else if (pSceneTouchEvent.getX() <= player.getX() + 200) {
				if(partOfTutorialCompleted[2] && partOfTutorialDisplayed[3]){
					player.chargeDown();
					if(player.isChargingDown())
						partOfTutorialCompleted[3] = true;
				}
				if(partOfTutorialCompleted[1]){
					player.slide();
					if(player.isSliding())
						partOfTutorialCompleted[2] = true;
				}
			}
			if(!partOfTutorialCompleted[0])
				nextTutorialPartDelay = player.getBody().getPosition().x + 10;
			else if(!partOfTutorialCompleted[1])
				nextTutorialPartDelay = player.getBody().getPosition().x + 10;
			else if(!partOfTutorialCompleted[2])
				nextTutorialPartDelay = player.getBody().getPosition().x + 10;
			else if(!partOfTutorialCompleted[3])
				nextTutorialPartDelay = player.getBody().getPosition().x + 10;
		}
		return false;
	}
	
	
	private ContactListener contactListener() {
		ContactListener contactListener = new ContactListener() {
			@Override
			public void beginContact(Contact contact) {
				final Fixture x1 = contact.getFixtureA();
				final Fixture x2 = contact.getFixtureB();

				if ("player".equals(x1.getBody().getUserData()) && "ground".equals(x2.getBody().getUserData()) || "player".equals(x2.getBody().getUserData()) && "ground".equals(x1.getBody().getUserData())) {
					if (player.isJumping()) {
						if(player.isSlideAfterLanding()){
							player.setSlideAfterLanding(false);
							player.setJumping(false);
							player.setDoubleJumped(false);
							player.setChargingDown(false);
							player.slide();
						}
						else
							player.land();
					}
					if(!player.isAlive()){
						player.fallDownSound.play();
					}
				}
				
				//Obstacle Collisions
				//if(player.isAlive() && (("player".equals(x1.getBody().getUserData()) && "ballBottom".equals(x2.getBody().getUserData())) || ("player".equals(x2.getBody().getUserData()) && "ballBottom".equals(x1.getBody().getUserData()))))
				//	player.dieTop(false);
				//if(player.isAlive() && !player.isSliding() && (("player".equals(x1.getBody().getUserData()) && "ballUpper".equals(x2.getBody().getUserData())) || ("player".equals(x2.getBody().getUserData()) && "ballUpper".equals(x1.getBody().getUserData()))))
				//	player.dieTop(false);
			}
			
			@Override
			public void endContact(Contact contact) {

			}

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
				// TODO Auto-generated method stub

			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
				// TODO Auto-generated method stub

			}
		};
		return contactListener;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
	
	
}
