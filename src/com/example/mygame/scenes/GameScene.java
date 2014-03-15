package com.example.mygame.scenes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.MoveXModifier;
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
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.SAXUtils;
import org.andengine.util.color.Color;
import org.andengine.util.level.constants.LevelConstants;
import org.andengine.util.modifier.ease.EaseElasticInOut;

import org.xml.sax.Attributes;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;
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
import com.example.mygame.SceneManager;
import com.example.mygame.SceneManager.SceneType;

public class GameScene extends BaseScene implements IOnSceneTouchListener {

	private HUD gameHUD;
	private Text scoreText;
	private int score = -2;
	private PhysicsWorld physicsWorld;
	private Player player;
	private boolean firstTouch = false;
	private Text bestScoreText;
	private int center = 0, center2;
	final int LEVEL_BLOCK_LENGTH = 5;
	private TexturedPolygon ground;
	private AutoParallaxBackground autoParallaxBackground;
	private ParallaxEntity frontParallaxBackground;
	private boolean firstUpdate = true;
	Body crateBody;
	List<Body> cratesTop;
	List<Body> cratesBottom;

	Sprite sGameOver;
	Sprite sReplay;
	Sprite sMenu;

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
		cratesTop = new ArrayList<Body>();
		cratesBottom = new ArrayList<Body>();
		createHUD();
		createPhysics();
		createPlayer();
		createGround();
		setOnSceneTouchListener(this);
		createBackground();
		createBestScoreTable();
		attachChild(backgroundLayer);
		attachChild(foregroundLayer);
	}

	@Override
	public void onBackKeyPressed() {
		sceneManager.loadMenuScene();
		// System.exit(0);
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
	}

	// ADDITIONAL METHODS

	private void createBackground() {
		autoParallaxBackground = new AutoParallaxBackground(0, 0, 0, 5);
		autoParallaxBackground.attachParallaxEntity(new ParallaxEntity(0.0f, new Sprite(0, 0, resourcesManager.mParallaxLayerBack, resourcesManager.vbom)));
		autoParallaxBackground.attachParallaxEntity(new ParallaxEntity(-2.0f, new Sprite(0, 10, resourcesManager.mParallaxLayerMid, resourcesManager.vbom)));

		frontParallaxBackground = new ParallaxEntity(-20.0f, new Sprite(0, 280, resourcesManager.mParallaxLayerFront, resourcesManager.vbom));
		autoParallaxBackground.attachParallaxEntity(frontParallaxBackground);

		setBackground(autoParallaxBackground);
	}

	private void createBestScoreTable() {
		Sprite sSign = new Sprite(600, 50, resourcesManager.sign_region, resourcesManager.vbom);
		foregroundLayer.attachChild(sSign);
		bestScoreText = new Text(0, 0, ResourcesManager.getInstance().bestScoreFont, "012345679", ResourcesManager.getInstance().vbom);
		bestScoreText.setPosition(660, 130);
		foregroundLayer.attachChild(bestScoreText);
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

	private void generateObstacle() {
		if (new Random().nextInt(100) > 30) {
			Sprite crate;
			if (new Random().nextInt(100) >= 50) {
				crate = new Sprite(center, 190, ResourcesManager.getInstance().obstacle_bottom_region, vbom);
				crateBody = PhysicsFactory.createBoxBody(physicsWorld, crate, BodyType.StaticBody, PhysicsFactory.createFixtureDef(10.0f, 0, 0));
				crateBody.setUserData("obstacleBottom");
				cratesBottom.add(crateBody);
			} else {
				crate = new Sprite(center, 155, ResourcesManager.getInstance().obstacle_bottom_region, vbom);
				crateBody = PhysicsFactory.createBoxBody(physicsWorld, crate, BodyType.StaticBody, PhysicsFactory.createFixtureDef(10.0f, 0, 0));
				crateBody.setUserData("obstacleTop");
				cratesTop.add(crateBody);
			}

			foregroundLayer.attachChild(crate);
			physicsWorld.registerPhysicsConnector(new PhysicsConnector(crate, crateBody, true, false) {
				@Override
				public void onUpdate(float pSecondsElapsed) {
					super.onUpdate(pSecondsElapsed);
					for (int j = 0; j < cratesTop.size(); j++) {
						Body newCrate = (Body) cratesTop.get(j);
						for (int i = 0; i < newCrate.getFixtureList().size(); i++) {
							if (player.isSliding() && player.isAlive())
								newCrate.getFixtureList().get(i).setSensor(true);
							else if (player.isAlive())
								newCrate.getFixtureList().get(i).setSensor(false);
						}
					}
				}
			});
		}
	}

	private void createGround() {
		generateLevelCoordinates();
		generateObstacle();

		// CHAIN SHAPES - DRAW LINES BETWEEN ALL COORDINATES
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

		// TEXTURED POLYGON 2 - DIRT - TEXTURE REGION MUST BE FROM A REPEATING
		// ATLAS
		float[] vertexX2 = new float[myV2.length];
		float[] vertexY2 = new float[myV2.length];

		for (int i = 0; i < myV2.length; i++) {
			vertexX2[i] = myV2[i].x * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
			vertexY2[i] = myV2[i].y * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		}

		ground = new TexturedPolygon(0, 0, vertexX2, vertexY2, resourcesManager.dirt_texture_region, vbom);
		backgroundLayer.attachChild(ground);
		ground.setUserData("ground");

		// backgroundLayer.attachChild(new DebugRenderer(physicsWorld, vbom));
	}

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
				score = (int) Math.round(player.getBody().getPosition().x);
				setScore(score);

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
		player = new Player(0, 200, vbom, camera, physicsWorld) {
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

		this.registerUpdateHandler(new TimerHandler(1f, new ITimerCallback() {
			public void onTimePassed(final TimerHandler pTimerHandler) {
				SceneManager.getInstance().getCurrentScene().unregisterUpdateHandler(pTimerHandler);

				sGameOver.setPosition(camera.getCenterX() - 5000, camera.getCenterY() - 100);
				sReplay.setPosition(camera.getCenterX() - 2000, camera.getCenterY());
				sMenu.setPosition(camera.getCenterX() + 2300, camera.getCenterY());

				sGameOver.registerEntityModifier(new MoveXModifier(2.0f, sGameOver.getX(), camera.getCenterX(), EaseElasticInOut.getInstance()));
				sReplay.registerEntityModifier(new MoveXModifier(3.9f, sReplay.getX(), camera.getCenterX() - 50, EaseElasticInOut.getInstance()));
				sMenu.registerEntityModifier(new MoveXModifier(3.9f, sMenu.getX(), camera.getCenterX() + 290, EaseElasticInOut.getInstance()));

				SceneManager.getInstance().getCurrentScene().registerTouchArea(sMenu);
				SceneManager.getInstance().getCurrentScene().registerTouchArea(sReplay);
				foregroundLayer.attachChild(sGameOver);
				foregroundLayer.attachChild(sReplay);
				foregroundLayer.attachChild(sMenu);
			}
		}));
		this.registerUpdateHandler(new TimerHandler(2f, new ITimerCallback() {
			public void onTimePassed(final TimerHandler pTimerHandler) {
				SceneManager.getInstance().getCurrentScene().unregisterUpdateHandler(pTimerHandler);
				ResourcesManager.getInstance().whooshSound.play();
			}
		}));
		this.registerUpdateHandler(new TimerHandler(3.0f, new ITimerCallback() {
			public void onTimePassed(final TimerHandler pTimerHandler) {
				SceneManager.getInstance().getCurrentScene().unregisterUpdateHandler(pTimerHandler);
				ResourcesManager.getInstance().whooshSound.play();
			}
		}));
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
			} else if (pSceneTouchEvent.getX() > player.getX() + 200) {
				player.jump();
			} else if (pSceneTouchEvent.getX() <= player.getX() + 200) {
				player.slide();
			}
		}
		return false;
	}

	public void ignoreCratesCollision() {
		for (int j = 0; j < cratesTop.size(); j++) {
			Body newCrate = (Body) cratesTop.get(j);
			for (int i = 0; i < newCrate.getFixtureList().size(); i++) {
				newCrate.getFixtureList().get(i).setSensor(true);
				System.out.println("sensor top: " + i);
			}
		}
		for (int j = 0; j < cratesBottom.size(); j++) {
			Body newCrate = (Body) cratesBottom.get(j);
			for (int i = 0; i < newCrate.getFixtureList().size(); i++) {
				newCrate.getFixtureList().get(i).setSensor(true);
				System.out.println("sensor bottom: " + i);
			}
		}
	}

	private ContactListener contactListener() {
		ContactListener contactListener = new ContactListener() {
			@Override
			public void beginContact(Contact contact) {
				final Fixture x1 = contact.getFixtureA();
				final Fixture x2 = contact.getFixtureB();

				if ("player".equals(x1.getBody().getUserData()) || "ground".equals(x2.getBody().getUserData())) {
					if (player.isJumping()) {
						physicsWorld.setGravity(new Vector2(0, 100.0f));
						player.land();
					}
					if(!player.isAlive()){
						resourcesManager.fallDownSound.play();
					}
				} else if ("player".equals(x2.getBody().getUserData()) || "ground".equals(x1.getBody().getUserData())) {
					if (player.isJumping()) {
						physicsWorld.setGravity(new Vector2(0, 100.0f));
						player.land();
					}
					if(!player.isAlive()){
						resourcesManager.fallDownSound.play();
					}
				}

				if (player.isAlive()
						&& (("player".equals(x1.getBody().getUserData()) && "obstacleBottom".equals(x2.getBody().getUserData())) || ("player".equals(x2.getBody().getUserData()) && "obstacleBottom"
								.equals(x1.getBody().getUserData())))) {
					ignoreCratesCollision();
					player.dieBottom();
				}
				if (player.isAlive()
						&& !player.isSliding()
						&& (("player".equals(x1.getBody().getUserData()) && "obstacleTop".equals(x2.getBody().getUserData())) || ("player".equals(x2.getBody().getUserData()) && "obstacleTop"
								.equals(x1.getBody().getUserData())))) {
					ignoreCratesCollision();
					player.dieTop();
					System.out.println("contaclist");
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
