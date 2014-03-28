package com.apptogo.runalien.scenes;

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
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.modifier.ease.EaseBounceInOut;
import org.andengine.util.modifier.ease.EaseElasticInOut;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.apptogo.runalien.BaseScene;
import com.apptogo.runalien.Player;
import com.apptogo.runalien.R;
import com.apptogo.runalien.ResourcesManager;
import com.apptogo.runalien.SceneManager;
import com.apptogo.runalien.SceneManager.SceneType;
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
import com.google.android.gms.games.Games;

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
	
	//background
	private AutoParallaxBackground autoParallaxBackground;
	private ParallaxEntity frontParallaxBackground;
	private ParallaxEntity grassParallaxBackground;
	
	//flags
	private boolean firstUpdate = true;
	private boolean firstTouch = false;

	//gameover sprites
	Sprite sGameOver;
	Sprite sReplay;
	Sprite sMenu;
	Sprite sSubmit;

	//local highscore 
	private static final String HIGHSCORE_DB_NAME = "MyGameHighscores";
	private static final String HIGHSCORE_LABEL = "score";
	private SharedPreferences mScoreDb = activity.getSharedPreferences(HIGHSCORE_DB_NAME, Context.MODE_PRIVATE);
	private SharedPreferences.Editor mScoreDbEditor = this.mScoreDb.edit();
	public int score = -2;
	private Text bestScoreText;
	private Text scoreText; //actual score top-left corner
	
	//layers
	Entity backgroundLayer;
	Entity foregroundLayer;

	//OVERRIDEN METHODS
	@Override
	public void createScene() {
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
	}

	@Override
	public void onBackKeyPressed() {
		sceneManager.loadMenuScene();
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_GAME;
	}

	@Override
	public void disposeScene() {
		camera.setHUD(null);
		camera.setChaseEntity(null);
		camera.setBoundsEnabled(false);
		camera.setCenter(400, 240);
		//powywalac reszte 
	}

	//GROUND AND BACKGROUND
	private void createBackground() {
		autoParallaxBackground = new AutoParallaxBackground(0, 0, 0, 5);
		autoParallaxBackground.attachParallaxEntity(new ParallaxEntity(0.0f, new Sprite(0, 0, resourcesManager.mParallaxLayerBack, resourcesManager.vbom)));
		autoParallaxBackground.attachParallaxEntity(new ParallaxEntity(-2.0f, new Sprite(0, 10, resourcesManager.mParallaxLayerMid, resourcesManager.vbom)));

		frontParallaxBackground = new ParallaxEntity(-20.0f, new Sprite(0, 280, resourcesManager.mParallaxLayerFront, resourcesManager.vbom));
		autoParallaxBackground.attachParallaxEntity(frontParallaxBackground);
		
		grassParallaxBackground = new ParallaxEntity(-20.0f, new Sprite(0, 346, resourcesManager.mParallaxLayerGrass, resourcesManager.vbom));
		autoParallaxBackground.attachParallaxEntity(grassParallaxBackground);
		
		setBackground(autoParallaxBackground);
	}

	private void generateLevelCoordinates() {
		levelCoordinates = null;
		levelCoordinates = new Vector2[4];
		levelCoordinates[0] = new Vector2(center - 200, 360);
		levelCoordinates[1] = new Vector2(center - 200, 240);
		levelCoordinates[2] = new Vector2(center + 400, 240);
		levelCoordinates[3] = new Vector2(center + 400, 360);
		center += 400;
	}

	private void createGround() {
		generateLevelCoordinates();
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

		//backgroundLayer.attachChild(new DebugRenderer(physicsWorld, vbom));
	}

	//MAIN OBJECTS METHODS
	private void createHUD() {
		gameHUD = new HUD();
		scoreText = new Text(0, 0, ResourcesManager.getInstance().mainFont, "score: 0123456789", new TextOptions(), vbom);
		scoreText.setText("SCORE: 0");
		gameHUD.attachChild(scoreText);
		camera.setHUD(gameHUD);
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
				if(player.isAlive()){
					score = (int) Math.round(player.getBody().getPosition().x/10);
					setScore(score);
				}

				if (player.getBody().getPosition().x > center2) {
					createGround();

					if (backgroundLayer.getChildCount() == 4) {
						backgroundLayer.detachChild(backgroundLayer.getFirstChild());
					}

				}

			}
		});
	}

	private void createPlayer() {
		player = new Player(0, 0, vbom, camera, physicsWorld) {
			@Override
			public void onDie() {
				saveHighScore();
				autoParallaxBackground.stop();
				showGameOver();
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
				sceneManager.replayGameScene();
				return true;
			}
		};
		sMenu = new Sprite(0, 0, ResourcesManager.getInstance().menu_region, vbom) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				ResourcesManager.getInstance().clickSound.play();
				sceneManager.loadMenuScene();
				
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
			}
		}));
	}

	//SCORE METHODS
	private void createBestScoreTable() {
		Sprite sSign = new Sprite(600, 50, resourcesManager.sign_region, resourcesManager.vbom);
		foregroundLayer.attachChild(sSign);
		bestScoreText = new Text(0, 0, ResourcesManager.getInstance().bestScoreFont, "012345679", ResourcesManager.getInstance().vbom);
		bestScoreText.setPosition(660, 110);
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

	
	// LISTENERS
	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		if (pSceneTouchEvent.isActionDown()) {
			if (!firstTouch) {
				player.setRunning();
				firstTouch = true;
				autoParallaxBackground.start();
				obstacleGenerator.startObstacleGenerationAlgorithm();
				System.out.println("POOL "+"Player position at start: " + player.getBody().getPosition().x);
			} else if (pSceneTouchEvent.getX() > player.getX() + 200) {
				player.doubleJump();
				player.jump();
				System.out.println("POOL "+"Player position when juping: " + player.getBody().getPosition().x);
			} else if (pSceneTouchEvent.getX() <= player.getX() + 200) {
				player.slide();
				player.chargeDown();
				System.out.println("POOL "+"Player position when sliding: " + player.getBody().getPosition().x);
			}
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
						player.land();
					}
					if(!player.isAlive()){
						resourcesManager.fallDownSound.play();
					}
				} else if ("player".equals(x2.getBody().getUserData()) && "ground".equals(x1.getBody().getUserData())) {
					if (player.isJumping()) {
						player.land();
					}
					if(!player.isAlive()){
						resourcesManager.fallDownSound.play();
					}
				}

				if (player.isAlive() && (("player".equals(x1.getBody().getUserData()) && "crateBottom".equals(x2.getBody().getUserData())) || ("player".equals(x2.getBody().getUserData()) && "crateBottom".equals(x1.getBody().getUserData())))) {
					player.dieBottom();
				}
				if (player.isAlive() && !player.isSliding() && (("player".equals(x1.getBody().getUserData()) && "crateUpper".equals(x2.getBody().getUserData())) || ("player".equals(x2.getBody().getUserData()) && "crateUpper".equals(x1.getBody().getUserData())))) {
					player.dieTop();
				}
				if (player.isAlive() && (("player".equals(x1.getBody().getUserData()) && "ballBottom".equals(x2.getBody().getUserData())) || ("player".equals(x2.getBody().getUserData()) && "ballBottom".equals(x1.getBody().getUserData())))) {
					player.dieTop();
				}
				if (player.isAlive() && !player.isSliding() && (("player".equals(x1.getBody().getUserData()) && "ballUpper".equals(x2.getBody().getUserData())) || ("player".equals(x2.getBody().getUserData()) && "ballUpper".equals(x1.getBody().getUserData())))) {
					player.dieTop();
				}
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
}
