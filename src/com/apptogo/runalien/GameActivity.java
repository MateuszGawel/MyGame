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
import org.andengine.opengl.view.RenderSurfaceView;
import org.andengine.ui.activity.BaseGameActivity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import com.apptogo.runalien.utils.GameHelper;
import com.apptogo.runalien.utils.GoogleBaseGameActivity;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.games.Games;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.IntentSender.SendIntentException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.RelativeLayout;

public class GameActivity extends GoogleBaseGameActivity implements ConnectionCallbacks, OnConnectionFailedListener {

	BoundCamera camera;
	ResourcesManager resourcesManager;
	SceneManager sceneManager;
	AdView gameBannerAdView;
	private InterstitialAd interstitial;
	private int gameCounter = 0;
    private FrameLayout frameLayout;
    private FrameLayout.LayoutParams gameBannerAdViewLayoutParams;
    
    public static final String VIBRASOUND_PREFERENCES = "VIBRASOUND_PREFERENCES";
    public SharedPreferences preferences;
    public SharedPreferences.Editor preferencesEditor;
    public static final String VIBRATIONS_LABEL = "VIBRATIONS";
    public static final String SOUNDS_LABEL = "SOUNDS";
    
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
	    
	    //workaround
	    this.preferences = this.getSharedPreferences(VIBRASOUND_PREFERENCES, Context.MODE_PRIVATE);
	    this.preferencesEditor = this.preferences.edit();
	    
	    preferences.getBoolean(VIBRATIONS_LABEL, true);
	    preferences.getBoolean(SOUNDS_LABEL, true);
	    
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
	@SuppressLint("NewApi")
	@Override
	protected void onSetContentView(){
		super.onSetContentView();
        frameLayout = new FrameLayout(this);
		gameBannerAdViewLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER | Gravity.BOTTOM);
		
		//main activity view configuration
        final FrameLayout.LayoutParams surfaceViewLayoutParams = new FrameLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT);
        surfaceViewLayoutParams.gravity = Gravity.CENTER;
        
        this.mRenderSurfaceView = new RenderSurfaceView(this);
        mRenderSurfaceView.setRenderer(mEngine, this);
        
        frameLayout.addView(this.mRenderSurfaceView, surfaceViewLayoutParams);
        
        final FrameLayout.LayoutParams frameLayoutLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT, Gravity.FILL);
        this.setContentView(frameLayout, frameLayoutLayoutParams);
        
        //GAME BANNER
        loadGameBanner();
        
        //INTERSTITIAL
        loadInterstitial();
	}
	 
	public void loadGameBanner(){
		
        //ustawienie widoku
        gameBannerAdView = new AdView(this);
        gameBannerAdView.setAdUnitId("ca-app-pub-9012477671085567/2504348939");
        gameBannerAdView.setAdSize(AdSize.BANNER);
        gameBannerAdView.setVisibility(AdView.INVISIBLE);
        gameBannerAdView.refreshDrawableState();
        
        //zadanie o reklamê
	    AdRequest gameBannerAdRequest = new AdRequest.Builder()
	    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
        .addTestDevice("4C8EA9CBB8BB046BCA4404AEAC8B25CA")
        .addTestDevice("5F32613150315ED40B2E9FDCC884795A")
        .addTestDevice("B4CDA4E066371C22AA6C995811986124")
        .build();
	    
	    //wczytanie reklamy do widoku
	    gameBannerAdView.loadAd(gameBannerAdRequest);
        
	    //ustawienie widoku w layoucie
        frameLayout.addView(gameBannerAdView, gameBannerAdViewLayoutParams);
	}
	
	public void setgameBannerAdViewInvisibile() {
	     this.runOnUiThread(new Runnable() {
	        @Override
	        public void run() {
	            gameBannerAdView.setVisibility(AdView.INVISIBLE);
	        }
	     });
	  }
	
	public void setgameBannerAdViewVisibile() {
		this.runOnUiThread(new Runnable() {
		    @Override
		    public void run() {
	            gameBannerAdView.setVisibility(AdView.VISIBLE);
		    }
		});
	}
	
	public void loadInterstitial(){
		//Create the interstitial.
        interstitial = new InterstitialAd(this);
        interstitial.setAdUnitId("ca-app-pub-9012477671085567/1049465337");

        // Create ad request.
	    AdRequest interestitialAdRequest = new AdRequest.Builder()
        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
        .addTestDevice("4C8EA9CBB8BB046BCA4404AEAC8B25CA")
        .addTestDevice("5F32613150315ED40B2E9FDCC884795A")
        .addTestDevice("B4CDA4E066371C22AA6C995811986124")
        .build();
        // Begin loading your interstitial.
        interstitial.loadAd(interestitialAdRequest);
	}	

	public void displayInterstitialAndLoadMenuScene() {
		this.runOnUiThread(new Runnable() {
	         @Override
 			public void run() {
				interstitial.setAdListener(new AdListener() {
					@Override
					public void onAdClosed() {
						super.onAdClosed();
						SceneManager.getInstance().loadMenuScene();
						loadInterstitial();
					}
				});
	     		if (interstitial.isLoaded()) {
	    			interstitial.show();
	    		}
	     		else{
	     			System.out.println("No interestitial loaded");
	     			SceneManager.getInstance().loadMenuScene();
	     		}
    			
	         }
		});
	}
	
	public void displayInterstitialAndExit() {
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				interstitial.setAdListener(new AdListener() {
					@Override
					public void onAdClosed() {
						super.onAdClosed();
						finish();
						System.exit(0);
					}
				});
					
				if (interstitial.isLoaded()) {
					interstitial.show();
				}
				else{
					System.out.println("No interestitial loaded");
					finish();
					System.exit(0);
			 	}
		 		
		     }
		});
	}
	
	public void displayInterstitialIfReadyAndReplay() {
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				interstitial.setAdListener(new AdListener() {
					@Override
					public void onAdClosed() {
						super.onAdClosed();
						SceneManager.getInstance().replayGameScene();
						loadInterstitial();
					}
				});
				gameCounter++;
				if (interstitial.isLoaded() && gameCounter % 8 == 0) {
					//interstitial.show();
				}
				else{
					System.out.println("No interestitial loaded");
					SceneManager.getInstance().replayGameScene();
			 	}
			 		
		    }
		});
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
