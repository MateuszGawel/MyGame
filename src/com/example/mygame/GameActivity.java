package com.example.mygame;

import java.io.IOException;//sdfsdf

import org.andengine.engine.Engine;
import org.andengine.engine.LimitedFPSEngine;
import org.andengine.engine.camera.BoundCamera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.BaseGameActivity;

import android.view.KeyEvent;

public class GameActivity extends BaseGameActivity {

	BoundCamera camera;
	ResourcesManager resourcesManager;
	SceneManager sceneManager;
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		camera = new BoundCamera(0, 0, 800, 480);
		EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(800, 480), this.camera);
		engineOptions.getAudioOptions().setNeedsMusic(true).setNeedsSound(true);
		engineOptions.setWakeLockOptions(WakeLockOptions.SCREEN_ON);
		return engineOptions;
	}
	
	@Override
	public Engine onCreateEngine(EngineOptions pEngineOptions){
		return new LimitedFPSEngine(pEngineOptions, 60);
	}
	
	@Override
	public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) throws IOException {
		ResourcesManager.prepareManager(mEngine, this, camera, getVertexBufferObjectManager());
		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) throws IOException {
		sceneManager = SceneManager.getInstance();
		BaseScene splashScene = sceneManager.createSplashScene();
		pOnCreateSceneCallback.onCreateSceneFinished(splashScene);
	}

	@Override
	public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) throws IOException {
	    mEngine.registerUpdateHandler(new TimerHandler(2f, new ITimerCallback() {
	            public void onTimePassed(final TimerHandler pTimerHandler) {
	                mEngine.unregisterUpdateHandler(pTimerHandler);
	                sceneManager.createMenuScene();
	            }
	    }));
	    pOnPopulateSceneCallback.onPopulateSceneFinished();
	}
	

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			sceneManager.getCurrentScene().onBackKeyPressed();
		}
		return false;
	}
}
