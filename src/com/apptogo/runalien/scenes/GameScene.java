package com.apptogo.runalien.scenes;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.Entity;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.entity.modifier.MoveYModifier;
import org.andengine.entity.primitive.TexturedPolygon;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.AutoParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
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

public class GameScene extends BaseScene implements IOnSceneTouchListener {
	
	//main objects
	private HUD gameHUD;
	private PhysicsWorld physicsWorld;
	private Player player;
	ObstacleGenerator obstacleGenerator;
	Vibrator vibrator;
	
	
	private LinkedList<Body> groundPool;
	private final int groundPoolAmount = 3; //min 3!!!
	
	
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

	//gameover sprites
	Sprite sGameOver;
	Sprite sReplay;
	Sprite sMenu;
	Sprite sSubmit;

	//tutorial
	private Text tapToJump;
	private Text tapToDoubleJump;
	private Text tapToSlide;
	private Text tapToChargeDown;
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
	private SharedPreferences mScoreDb = activity.getSharedPreferences(HIGHSCORE_DB_NAME, Context.MODE_PRIVATE);
	private SharedPreferences.Editor mScoreDbEditor = this.mScoreDb.edit();
	public int score = 0;
	private Text bestScoreText;
	private Text scoreText; //actual score top-left corner
	
	//layers
	Entity backgroundLayer;
	Entity foregroundLayer;

	//OVERRIDEN METHODS
	@Override
	public void createScene() {
		groundPool = new LinkedList<Body>();
		
		backgroundLayer = new Entity();
		foregroundLayer = new Entity();
		createHUD();
		createPhysics();
		createGround();
		setOnSceneTouchListener(this);
		createBackground();
		createBestScoreTable();
		createPlayer();
		attachChild(backgroundLayer);
		attachChild(foregroundLayer);
		ObstaclesPoolManager.getInstance().initializePoolManager(physicsWorld, foregroundLayer);
		obstacleGenerator = new ObstacleGenerator(this, player);
		vibrator = (Vibrator)activity.getSystemService(Context.VIBRATOR_SERVICE);
		
		//strasznie na pale :<
		//Coin c = null;
		//for(int i=0;i<10000; i+=100) c = new Coin(i, 280, foregroundLayer);
		
		//c.setPlayer(player);
	}

	@Override
	public void onBackKeyPressed() {
		ResourcesManager.getInstance().activity.displayInterstitialAndLoadMenuScene();
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
		//powywalac reszte 
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

	private void createGround() {
		
		for(int i = 0; i < groundPoolAmount; i++)
		{
			Sprite groundSprite = new Sprite( ( (-200) + (i * 800) ), 240, ResourcesManager.getInstance().ground_region, ResourcesManager.getInstance().vbom);
			groundSprite.setCullingEnabled(false);
			Body ground = PhysicsFactory.createBoxBody(physicsWorld, groundSprite, BodyType.StaticBody, PhysicsFactory.createFixtureDef(10.0f, 0, 0.2f));
			ground.setUserData("ground");
			
			groundPool.add(ground);
			foregroundLayer.attachChild(groundSprite);
			
			physicsWorld.registerPhysicsConnector(new PhysicsConnector(groundSprite, ground, true, false) {
				@Override
				public void onUpdate(float pSecondsElapsed) {
					super.onUpdate(pSecondsElapsed);
				}
			});
		}
		
		//backgroundLayer.attachChild(new DebugRenderer(physicsWorld, vbom));
	}

	//MAIN OBJECTS METHODS
	private void createHUD() {
		gameHUD = new HUD();
		scoreText = new Text(5, 0, ResourcesManager.getInstance().mainFont, "score: 0123456789", new TextOptions(), vbom);
		scoreText.setText("SCORE: 0");
		gameHUD.attachChild(scoreText);
		camera.setHUD(gameHUD);

		tapToJump = new Text(0, 0, ResourcesManager.getInstance().mainFont, "Tap right site of the screen to jump", new TextOptions(), vbom);
		tapToJump.setPosition(2000, 400);
		tapToDoubleJump = new Text(0, 0, ResourcesManager.getInstance().mainFont, "Tap right side of the screen TWO TIMES to double jump", new TextOptions(), vbom);
		tapToDoubleJump.setPosition(2000, 400);
		tapToSlide = new Text(0, 0, ResourcesManager.getInstance().mainFont, "Tap left side of the screen to jump", new TextOptions(), vbom);
		tapToSlide.setPosition(-2000, 400);
		tapToChargeDown = new Text(0, 0, ResourcesManager.getInstance().mainFont, "Tap left site of the screen in air to charge down", new TextOptions(), vbom);
		tapToChargeDown.setPosition(-2000, 400);
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
				}
				
				Body t_firstGround = groundPool.getFirst();
				
				if (player.getBody().getPosition().x > (t_firstGround.getPosition().x + (800.0f / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT)) ) {
					Body temp = groundPool.removeFirst();
					temp.setTransform( temp.getPosition().x + ((800.0f / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT) * (groundPoolAmount)), (temp.getPosition().y), 0);
					temp.setUserData("ground");
					groundPool.add(temp);
				}
				if(displayTutorial)
					generateTutorial();

			}
		});
	}

	private void createPlayer() {
		player = new Player(120, 145, vbom, camera, physicsWorld) {
			@Override
			public void onDie() {
				saveHighScore();
				autoParallaxBackground.stop();
				showGameOver();
				mScoreDbEditor.putBoolean(TUTORIAL_DISPLAYED_LABEL, true);
				mScoreDbEditor.commit();
				vibrator.vibrate(500);
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
				ResourcesManager.getInstance().clickSound.play();
				//sceneManager.replayGameScene();
				activity.setgameBannerAdViewInvisibile();
				activity.displayInterstitialIfReadyAndReplay();
				return true;
			}
		};
		sMenu = new Sprite(0, 0, ResourcesManager.getInstance().menu_region, vbom) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				ResourcesManager.getInstance().clickSound.play();
				//sceneManager.loadMenuScene();
				ResourcesManager.getInstance().activity.displayInterstitialAndLoadMenuScene();
				return true;
			}
		};
		sSubmit = new Sprite(0, 0, ResourcesManager.getInstance().submit_region, vbom) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				ResourcesManager.getInstance().clickSound.play();

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
				ResourcesManager.getInstance().whooshSound.play();
				activity.setgameBannerAdViewVisibile();
			}
		}));
		
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
	
	private void pauseGame(){
		gamePaused = true;
		camera.setChaseEntity(null);
		this.setIgnoreUpdate(true);
	}
	
	private void resumeGame(){
		gamePaused = false;
		camera.setChaseEntity(player);
		this.setIgnoreUpdate(false);
	}
	
	private void generateTutorial(){
		player.setCanSpeedUp(false);
		if(player.getBody().getPosition().x > 5 && !partOfTutorialDisplayed[0]){
			tapToJump.setText("Tap right side of the screen to jump");
			gameHUD.attachChild(tapToJump);
			tapToJump.registerEntityModifier(new MoveXModifier(12.5f, tapToJump.getX(), 320, EaseElasticInOut.getInstance()));
			partOfTutorialDisplayed[0] = true;
		}
		else if(player.getBody().getPosition().x > nextTutorialPartDelay && !partOfTutorialDisplayed[1] && partOfTutorialCompleted[0]){
			tapToJump.registerEntityModifier(new MoveXModifier(12.5f, tapToJump.getX(), -1000, EaseElasticInOut.getInstance()));
			tapToDoubleJump.setText("Tap right side of the screen TWO TIMES to double jump");
			gameHUD.attachChild(tapToDoubleJump);
			tapToDoubleJump.registerEntityModifier(new MoveXModifier(12.5f, tapToDoubleJump.getX(), 50, EaseElasticInOut.getInstance()));
			partOfTutorialDisplayed[1] = true;
		}
		else if(player.getBody().getPosition().x > nextTutorialPartDelay && !partOfTutorialDisplayed[2] && partOfTutorialCompleted[1]){
			tapToDoubleJump.registerEntityModifier(new MoveXModifier(12.5f, tapToDoubleJump.getX(), -1000, EaseElasticInOut.getInstance()));
			tapToSlide.setText("Tap left side of the screen to slide");
			gameHUD.attachChild(tapToSlide);
			tapToSlide.registerEntityModifier(new MoveXModifier(12.5f, tapToSlide.getX(), 10, EaseElasticInOut.getInstance()));
			partOfTutorialDisplayed[2] = true;
		}
		else if(player.getBody().getPosition().x > nextTutorialPartDelay && !partOfTutorialDisplayed[3] && partOfTutorialCompleted[2]){
			tapToSlide.registerEntityModifier(new MoveXModifier(12.5f, tapToSlide.getX(), 1000, EaseElasticInOut.getInstance()));
			tapToChargeDown.setText("Tap left side of the screen in air to charge down");
			gameHUD.attachChild(tapToChargeDown);
			tapToChargeDown.registerEntityModifier(new MoveXModifier(12.5f, tapToChargeDown.getX(), 10, EaseElasticInOut.getInstance()));
			partOfTutorialDisplayed[3] = true;
		}
		else if(player.getBody().getPosition().x > nextTutorialPartDelay && partOfTutorialCompleted[3]){
			tapToChargeDown.registerEntityModifier(new MoveXModifier(12.5f, tapToChargeDown.getX(), 1000, EaseElasticInOut.getInstance()));
			nextTutorialPartDelay = player.getBody().getPosition().x + 10;
			displayTutorial = false;
			obstacleGenerator.setNextObstaclePosition(player.getBody().getPosition().x + 50);
			obstacleGenerator.startObstacleGenerationAlgorithm( tutorialScoreOffset );
			player.setCanSpeedUp(true);
		}
	}
	
	// LISTENERS
	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		if (pSceneTouchEvent.isActionDown()) {
			if (!firstTouch) {
				player.setRunning();
				firstTouch = true;
				autoParallaxBackground.start();
				if(!displayTutorial){
					obstacleGenerator.startObstacleGenerationAlgorithm(tutorialScoreOffset);
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

				if ("player".equals(x1.getBody().getUserData()) && "ground".equals(x2.getBody().getUserData())) {
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
						resourcesManager.fallDownSound.play();
					}
				} else if ("player".equals(x2.getBody().getUserData()) && "ground".equals(x1.getBody().getUserData())) {
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
						resourcesManager.fallDownSound.play();
					}
				}
				
				//Obstacle Collisions
				
				if(player.isAlive() && (("player".equals(x1.getBody().getUserData()) && ("bottom1".equals(x2.getBody().getUserData()))) || (("player".equals(x2.getBody().getUserData())) && ("bottom1".equals(x1.getBody().getUserData())))))
					player.dieBottom();
				if(player.isAlive() && (("player".equals(x1.getBody().getUserData()) && ("bottom2".equals(x2.getBody().getUserData()))) || (("player".equals(x2.getBody().getUserData())) && ("bottom2".equals(x1.getBody().getUserData())))))
					player.dieBottom();
				if(player.isAlive() && (("player".equals(x1.getBody().getUserData()) && ("bottom3".equals(x2.getBody().getUserData()))) || (("player".equals(x2.getBody().getUserData())) && ("bottom3".equals(x1.getBody().getUserData())))))
					player.dieBottom();
				if(player.isAlive() && (("player".equals(x1.getBody().getUserData()) && ("bottom4".equals(x2.getBody().getUserData()))) || (("player".equals(x2.getBody().getUserData())) && ("bottom4".equals(x1.getBody().getUserData())))))
					player.dieBottom();
				if(player.isAlive() && (("player".equals(x1.getBody().getUserData()) && "ballBottom".equals(x2.getBody().getUserData())) || ("player".equals(x2.getBody().getUserData()) && "ballBottom".equals(x1.getBody().getUserData()))))
					player.dieTop(false);
				if(player.isAlive() && !player.isSliding() && (("player".equals(x1.getBody().getUserData()) && "ballUpper".equals(x2.getBody().getUserData())) || ("player".equals(x2.getBody().getUserData()) && "ballUpper".equals(x1.getBody().getUserData()))))
					player.dieTop(false);
				if(player.isAlive() && !player.isSliding() && (("player".equals(x1.getBody().getUserData()) && ("upper1".equals(x2.getBody().getUserData()))) || (("player".equals(x2.getBody().getUserData())) && ("upper1".equals(x1.getBody().getUserData())))) )
					player.dieTop(true);
				if(player.isAlive() && !player.isSliding() && (("player".equals(x1.getBody().getUserData()) && ("upper2".equals(x2.getBody().getUserData()))) || (("player".equals(x2.getBody().getUserData())) && ("upper2".equals(x1.getBody().getUserData())))) )
					player.dieTop(true);
				if(player.isAlive() && !player.isSliding() && (("player".equals(x1.getBody().getUserData()) && ("upper3".equals(x2.getBody().getUserData()))) || (("player".equals(x2.getBody().getUserData())) && ("upper3".equals(x1.getBody().getUserData())))) )
					player.dieTop(true);
				if(player.isAlive() && !player.isSliding() && (("player".equals(x1.getBody().getUserData()) && ("upper4".equals(x2.getBody().getUserData()))) || (("player".equals(x2.getBody().getUserData())) && ("upper4".equals(x1.getBody().getUserData())))) )
					player.dieTop(true);
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
