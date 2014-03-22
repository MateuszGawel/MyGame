package com.apptogo.runalien;

import java.io.IOException;

import org.andengine.engine.Engine;
import org.andengine.engine.LimitedFPSEngine;
import org.andengine.engine.camera.BoundCamera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.BaseGameActivity;

import com.apptogo.runalien.utils.GoogleBaseGameActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.view.KeyEvent;

public class GameActivity extends GoogleBaseGameActivity implements ConnectionCallbacks, OnConnectionFailedListener {

	BoundCamera camera;
	ResourcesManager resourcesManager;
	SceneManager sceneManager;
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		camera = new BoundCamera(0, 0, 800, 480);
		EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_SENSOR, new FillResolutionPolicy(), this.camera); //RatioResolutionPolicy(800,480) to co jest teraz raaczej nie jest dobrym rozwiazaniem - powinnismy pomyslec o CropResolutionPolicy
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
		ResourcesManager.prepareManager(mEngine, this, camera, getVertexBufferObjectManager(), this.mHelper);
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
	
	public GoogleApiClient getGoogleApiClient(){
		return getApiClient();
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			sceneManager.getCurrentScene().onBackKeyPressed();
		}
		return false;
	}
    
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		System.out.println("I DUPA " + result.getErrorCode() + " mes: " + result.toString());
		try {
			result.startResolutionForResult(this, 4);
			result.startResolutionForResult(this, 8);
		} catch (SendIntentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnectionSuspended(int cause) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSignInFailed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSignInSucceeded() {
		// TODO Auto-generated method stub
		
	}
}
