package com.example.mygame.scenes;

import java.io.IOException;
import java.util.Random;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.primitive.TexturedPolygon;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.AutoParallaxBackground;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.extension.debugdraw.DebugRenderer;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.SAXUtils;
import org.andengine.util.color.Color;
import org.andengine.util.level.constants.LevelConstants;

import org.xml.sax.Attributes;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.MotionEvent;

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
import com.example.mygame.BaseScene;
import com.example.mygame.Player;
import com.example.mygame.ResourcesManager;
import com.example.mygame.SceneManager.SceneType;

public class GameScene extends BaseScene implements IOnSceneTouchListener {

	private HUD gameHUD;
	private Text scoreText;
	private int score = -2;
	private PhysicsWorld physicsWorld;
	private Player player;
	private boolean firstTouch = false;
	private Text gameOverText;
	private Text bestScoreText;
	private int bestScore;
	private boolean gameOverDisplayed = false;
	private int lastX, lastY;
	final int LEVEL_BLOCK_LENGTH = 5;
	private TexturedPolygon ground;
	private int groundBlockCounter = 10;
	private AutoParallaxBackground autoParallaxBackground;
	private ParallaxEntity frontParallaxBackground;
	private boolean firstUpdate = true;

	private static final String HIGHSCORE_DB_NAME = "MyGameHighscores";
	private static final String HIGHSCORE_LABEL = "score";
	private SharedPreferences mScoreDb = activity.getSharedPreferences(HIGHSCORE_DB_NAME, Context.MODE_PRIVATE);
	private SharedPreferences.Editor mScoreDbEditor = this.mScoreDb.edit();

	private Vector2[] levelCoordinates;

	Entity backgroundLayer;
	Entity foregroundLayer;

	@Override
	public void createScene() {
		backgroundLayer = new Entity();
		foregroundLayer = new Entity();
		createHUD();
		createPhysics();
		createPlayer();
		createGround(true);
		setOnSceneTouchListener(this);
		createBackground();
		createBestScoreTable();
		attachChild(backgroundLayer);
		attachChild(foregroundLayer);
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
		camera.setCenter(400, 240);
		camera.setChaseEntity(null);
	}

	// ADDITIONAL METHODS

	private void createBackground() {
		// setBackground(new Background(Color.CYAN));
		autoParallaxBackground = new AutoParallaxBackground(0, 0, 0, 5);
		autoParallaxBackground.attachParallaxEntity(new ParallaxEntity(0.0f, new Sprite(0, 320 - resourcesManager.mParallaxLayerBack.getHeight(),
				resourcesManager.mParallaxLayerBack, resourcesManager.vbom)));
		autoParallaxBackground.attachParallaxEntity(new ParallaxEntity(-5.0f, new Sprite(0, 80, resourcesManager.mParallaxLayerMid, resourcesManager.vbom)));

		frontParallaxBackground = new ParallaxEntity(-20.0f, new Sprite(0, 320 - resourcesManager.mParallaxLayerFront.getHeight(), resourcesManager.mParallaxLayerFront,
				resourcesManager.vbom));
		autoParallaxBackground.attachParallaxEntity(frontParallaxBackground);

		setBackground(autoParallaxBackground);
	}

	private void createBestScoreTable() {
		Sprite sSign = new Sprite(-300, -194, resourcesManager.sign_region, resourcesManager.vbom);
		foregroundLayer.attachChild(sSign);
		bestScoreText = new Text(0, 0, ResourcesManager.getInstance().font, "012345679", ResourcesManager.getInstance().vbom);
		bestScoreText.setPosition(-230, -115);
		foregroundLayer.attachChild(bestScoreText);
	}

	private void generateFirstLevelCoordinates() {
		levelCoordinates = new Vector2[LEVEL_BLOCK_LENGTH + 100];
		Random generator = new Random();

		// ___ poczatek levela
		// |
		levelCoordinates[0] = new Vector2(-500, 500);
		levelCoordinates[1] = new Vector2(-500, 0);
		levelCoordinates[2] = new Vector2(200, 0);
		int currentX = 0, currentY = 0;
		for (int i = 3; i < LEVEL_BLOCK_LENGTH + 100 - 1; i++) {
			currentX = i * 100 + generator.nextInt(90);
			currentY = generator.nextInt(30);
			levelCoordinates[i] = new Vector2(currentX, currentY);
		}
		lastX = currentX;
		lastY = currentY;
		levelCoordinates[LEVEL_BLOCK_LENGTH + 100 - 1] = new Vector2(lastX, 500); // koncowa
																					// linia
	}

	private void generateLevelCoordinates() {
		levelCoordinates = new Vector2[LEVEL_BLOCK_LENGTH];
		Random generator = new Random();
		// int obstacleCountdown = 5;

		levelCoordinates[0] = new Vector2(lastX - 500, 500);
		levelCoordinates[1] = new Vector2(lastX, lastY);
		int currentX = 0, currentY = 0;
		for (int i = 2; i < LEVEL_BLOCK_LENGTH - 1; i++) {
			currentX = i * 100 + generator.nextInt(90) + lastX;
			currentY = generator.nextInt(30);
			levelCoordinates[i] = new Vector2(currentX, currentY);

			/*
			 * if (obstacleCountdown > 0) obstacleCountdown--;
			 * 
			 * if (generator.nextInt(100) > 60 && obstacleCountdown == 0) {
			 * Sprite obstacle; if (generator.nextInt(100) > 50) { obstacle =
			 * new Sprite(lastX, currentY - 380,
			 * resourcesManager.obstacle_top_region, vbom) {
			 * 
			 * @Override protected void onManagedUpdate(float pSecondsElapsed) {
			 * super.onManagedUpdate(pSecondsElapsed);
			 * 
			 * if (player.collidesWith(this)) { player.onDie();
			 * this.setIgnoreUpdate(true); } } };
			 * 
			 * } else { obstacle = new Sprite(lastX, currentY - 40,
			 * resourcesManager.obstacle_bottom_region, vbom) {
			 * 
			 * @Override protected void onManagedUpdate(float pSecondsElapsed) {
			 * super.onManagedUpdate(pSecondsElapsed);
			 * 
			 * if (player.collidesWith(this)) { player.onDie();
			 * this.setIgnoreUpdate(true); } } }; }
			 * obstacle.setCullingEnabled(true);
			 * backgroundLayer.attachChild(obstacle); obstacleCountdown = 3; }
			 */
		}
		lastX = currentX;
		lastY = currentY;
		levelCoordinates[LEVEL_BLOCK_LENGTH - 1] = new Vector2(lastX, 500); // koncowa
																			// linia
																			// w
																			// dol
	}

	private void createGround(boolean first) {
		if (first)
			generateFirstLevelCoordinates();
		else
			generateLevelCoordinates();

		// CHAIN SHAPES - DRAW LINES BETWEEN ALL COORDINATES
		ChainShape myChain = new ChainShape();
		Vector2[] myV2 = new Vector2[levelCoordinates.length];
		for (int i = 0; i < levelCoordinates.length; i++) {
			myV2[i] = new Vector2(levelCoordinates[i].x / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, levelCoordinates[i].y / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
		}
		myChain.createChain(myV2);
		FixtureDef mFixtureDef = new FixtureDef();
		Body mChainBody;
		BodyDef mBodyDef = new BodyDef();
		mBodyDef.type = BodyType.KinematicBody;
		mChainBody = physicsWorld.createBody(mBodyDef);
		mFixtureDef.shape = myChain;
		mChainBody.createFixture(mFixtureDef);
		myChain.dispose();

		// TEXTURED POLYGON 2 - DIRT - TEXTURE REGION MUST BE FROM A REPEATING
		// ATLAS
		float[] vertexX2 = new float[myV2.length];
		float[] vertexY2 = new float[myV2.length];

		for (int i = 0; i < myV2.length; i++) {
			vertexX2[i] = myV2[i].x * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
			vertexY2[i] = myV2[i].y * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		}
		if (ground != null) {
			if (groundBlockCounter >= 0) {
				groundBlockCounter--;
			} else {
				System.out.println("wesz³em: " + backgroundLayer.getChildCount());
				backgroundLayer.detachChild(ground);
				System.out.println("usunalem: " + backgroundLayer.getChildCount());
			}
		}

		ground = new TexturedPolygon(0, 0, vertexX2, vertexY2, resourcesManager.dirt_texture_region, vbom);
		backgroundLayer.attachChild(ground);
		System.out.println("dodalem: " + backgroundLayer.getChildCount());
		ground.setUserData("ground");
		// backgroundLayer.attachChild(new DebugRenderer(physicsWorld, vbom));
	}

	private void createHUD() {
		gameHUD = new HUD();
		scoreText = new Text(0, 0, ResourcesManager.getInstance().font, "SCORE: 0123456789", new TextOptions(), vbom);
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
				score = (int) Math.round(player.getBody().getPosition().x);
				setScore(score);
				// System.out.println(player.getBody().getPosition().x*100 +
				// " == " + lastX);
				if (player.getBody().getPosition().x * 100 > lastX && lastX != 0) {
					createGround(false);
				}
			}
		});
	}

	private void createPlayer() {
		player = new Player(0, -50, vbom, camera, physicsWorld) {
			@Override
			public void onDie() {
				player.setAlive(false);
				gameOverText = new Text(0, 0, ResourcesManager.getInstance().font, "Game Over!", ResourcesManager.getInstance().vbom);
				gameOverText.setPosition(camera.getCenterX(), camera.getCenterY());
				camera.setChaseEntity(null);
				attachChild(gameOverText);
				gameOverDisplayed = true;
				player.stopRunning();
				physicsWorld.setGravity(new Vector2(0, 20f));
				saveHighScore();
				autoParallaxBackground.stop();
				// System.out.println("NAJLEPSZY WYNIK TO: " + loadHighScore());
			}
		};
		player.setUserData("player");
		foregroundLayer.attachChild(player);
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
			System.out.println("POBRA£EM: " + pSceneTouchEvent.getX());
			if (!firstTouch) {
				player.setRunning();
				firstTouch = true;
				autoParallaxBackground.start();
			} else if (pSceneTouchEvent.getX() > player.getX()) {
				player.jump();
			} else if (pSceneTouchEvent.getX() <= player.getX()) {
				player.slide();
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

				if ("player".equals(x1.getBody().getUserData()) || "ground".equals(x2.getBody().getUserData())) {
					if (player.isJumping())
						player.land();
				} else if ("player".equals(x2.getBody().getUserData()) || "ground".equals(x1.getBody().getUserData())) {
					if (player.isJumping())
						player.land();
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
