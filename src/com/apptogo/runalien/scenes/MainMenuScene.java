package com.apptogo.runalien.scenes;

import org.andengine.audio.music.Music;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.util.GLState;
import org.andengine.engine.camera.Camera;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Vibrator;

import com.apptogo.runalien.BaseScene;
import com.apptogo.runalien.R;
import com.apptogo.runalien.ResourcesManager;
import com.apptogo.runalien.SceneManager;
import com.apptogo.runalien.SceneManager.SceneType;
import com.apptogo.runalien.obstacles.ObstaclesPoolManager;
import com.apptogo.runalien.utils.AppRater;
import com.apptogo.runalien.utils.GoogleBaseGameActivity;
import com.google.android.gms.games.Games;
import com.google.android.gms.plus.PlusShare;

public class MainMenuScene extends BaseScene implements IOnMenuItemClickListener{

	private MenuScene menuChildScene;
	private final int MENU_PLAY = 0;
	private final int MENU_OPTIONS = 1;
	private final int MENU_ACHIEVMENTS = 2;
	private final int MENU_GOOGLEP = 3;
	private final int MENU_VIBRATIONS = 4;
	private final int MENU_SOUNDS = 5;
		
	private boolean prefV;
	private boolean prefS;
	private boolean musicIsPlaying = false;
	
	@Override
	public void createScene() {
		createBackground();
		createMenuChildScene();
	}

	@Override
	public void onBackKeyPressed() {
		ResourcesManager.getInstance().activity.displayInterstitialAndExit();
        //activity.finish();
        //System.exit(0);
	}
	
	@Override
	public void onMenuKeyPressed() {
		return;
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_MENU;
	}

	@Override
	public void disposeScene() {
		ResourcesManager.getInstance().menuMusic.pause();	
		this.detachSelf();
		this.dispose();
	}
	
	//ADDITIONAL METHODS
	private void createMenuChildScene(){
		menuChildScene = new MenuScene(camera);
		menuChildScene.setPosition(400 - camera.getWidth()/2, 240 - camera.getHeight()/2);
		
		final IMenuItem playMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_PLAY, resourcesManager.play_region, vbom), 1.2f, 1);
		final IMenuItem rankingMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_OPTIONS, resourcesManager.ranking_region, vbom), 1.2f, 1);
		final IMenuItem achievmentsMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_ACHIEVMENTS, resourcesManager.achievments_region, vbom), 1.2f, 1);
		final IMenuItem googlepMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_GOOGLEP, resourcesManager.googlep_region, vbom), 1.2f, 1);
		final IMenuItem vibrationsMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_VIBRATIONS, resourcesManager.vibrations_region, vbom), 1.2f, 1);
		final IMenuItem soundsMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_SOUNDS, resourcesManager.sounds_region, vbom), 1.2f, 1);
		
		menuChildScene.addMenuItem(playMenuItem);
		menuChildScene.addMenuItem(rankingMenuItem);
		menuChildScene.addMenuItem(achievmentsMenuItem);
		menuChildScene.addMenuItem(googlepMenuItem);
		menuChildScene.addMenuItem(vibrationsMenuItem);
		menuChildScene.addMenuItem(soundsMenuItem);
		
		menuChildScene.buildAnimations();
		menuChildScene.setBackgroundEnabled(false);
		
		playMenuItem.setPosition(255, 270); 
		rankingMenuItem.setPosition(255, 365); 
		achievmentsMenuItem.setPosition(20, 410); 
		googlepMenuItem.setPosition(100, 410);
		vibrationsMenuItem.setPosition(670, 20);
		soundsMenuItem.setPosition(740, 20);
		
		menuChildScene.setOnMenuItemClickListener(this);
		setChildScene(menuChildScene);
		
		prefV = activity.preferences.getBoolean(activity.VIBRATIONS_LABEL, true);
		prefS = activity.preferences.getBoolean(activity.SOUNDS_LABEL, true);
		
		vibrationsMenuItem.clearEntityModifiers();
		soundsMenuItem.clearEntityModifiers();
		
		if(prefV) vibrationsMenuItem.setAlpha(1.0f);
		else      vibrationsMenuItem.setAlpha(0.2f);
		
		if(prefS) soundsMenuItem.setAlpha(1.0f);
		else      soundsMenuItem.setAlpha(0.2f);
		
		activity.preferencesEditor.putBoolean(activity.VIBRATIONS_LABEL, prefV);
		activity.preferencesEditor.putBoolean(activity.SOUNDS_LABEL, prefS);
		activity.preferencesEditor.commit();
		
		if( activity.preferences.getBoolean(activity.SOUNDS_LABEL, true) )
		{
			ResourcesManager.getInstance().menuMusic.play();
			ResourcesManager.getInstance().menuMusic.setLooping(true);
			musicIsPlaying = true;
		}
		activity.runOnUiThread(new Runnable() {
			public void run() {
				//AppRater.showRateDialog(ResourcesManager.getInstance().activity, null);
				AppRater.app_launched(ResourcesManager.getInstance().activity);
			}
		});

		
	}
	
	private void createBackground(){
		Sprite sBackground = new Sprite(400, 240, resourcesManager.menu_background_region, vbom)
		{
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera) {
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}
		};
		sBackground.setPosition(400 - sBackground.getWidth()/2, 240 - sBackground.getHeight()/2);
		attachChild(sBackground);
	}
	
	//LISTENERS
	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY) {
		
		switch(pMenuItem.getID()){
		case MENU_PLAY:
			
			if( activity.preferences.getBoolean(activity.SOUNDS_LABEL, true) ) ResourcesManager.getInstance().clickSound.play();
			
			ResourcesManager.getInstance().menuMusic.pause();
			
			sceneManager.loadGameScene();
			return true;
		case MENU_OPTIONS:
			
			if( activity.preferences.getBoolean(activity.SOUNDS_LABEL, true) ) ResourcesManager.getInstance().clickSound.play();
			
			if(((GoogleBaseGameActivity)activity).isSignedIn())
				((GoogleBaseGameActivity)activity).startActivityForResult(Games.Leaderboards.getLeaderboardIntent(((GoogleBaseGameActivity)activity).getApiClient(), activity.getResources().getString(R.string.leaderboard_highscores)), 0);
			else{
				resourcesManager.gameHelper.manualConnect();
				//((GoogleBaseGameActivity)activity).startActivityForResult(Games.Leaderboards.getLeaderboardIntent(((GoogleBaseGameActivity)activity).getApiClient(), activity.getResources().getString(R.string.leaderboard_highscores)), 0);
			}
			return true;
		case MENU_ACHIEVMENTS:
			
			if( activity.preferences.getBoolean(activity.SOUNDS_LABEL, true) ) ResourcesManager.getInstance().clickSound.play();
			
			if(((GoogleBaseGameActivity)activity).isSignedIn())
				((GoogleBaseGameActivity)activity).startActivityForResult(Games.Achievements.getAchievementsIntent(((GoogleBaseGameActivity)activity).getApiClient()), 0);
			else{
				resourcesManager.gameHelper.manualConnect();
			}
			return true;
		case MENU_GOOGLEP:
			if( activity.preferences.getBoolean(activity.SOUNDS_LABEL, true) ) ResourcesManager.getInstance().clickSound.play();
			
			Intent shareIntent = new PlusShare.Builder(activity)
			.setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.apptogo.runalien"))
			.setContentDeepLinkId("/pages/",null, null, null)
			.setText("I enjoy playing Run Alien! Try to beat my score.")
			.getIntent();
			activity.startActivityForResult(shareIntent, 0);
			
			return true;
		case MENU_VIBRATIONS:
			prefV = !activity.preferences.getBoolean(activity.VIBRATIONS_LABEL, true);
			
			activity.preferencesEditor.putBoolean(activity.VIBRATIONS_LABEL, prefV);
			activity.preferencesEditor.commit();
			
			prefV = activity.preferences.getBoolean(activity.VIBRATIONS_LABEL, true);

			if(prefV) 
			{
				pMenuItem.setAlpha(1.0f);
				((Vibrator)activity.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(500);
			}
			else      pMenuItem.setAlpha(0.2f);
			
			return true;
		case MENU_SOUNDS:
			prefS = !activity.preferences.getBoolean(activity.SOUNDS_LABEL, true);

			activity.preferencesEditor.putBoolean(activity.SOUNDS_LABEL, prefS);
			activity.preferencesEditor.commit();
			
			prefS = activity.preferences.getBoolean(activity.SOUNDS_LABEL, true);

			if(prefS) 
			{
				if(!musicIsPlaying)
				{
					musicIsPlaying = true;
					ResourcesManager.getInstance().menuMusic.play();
					ResourcesManager.getInstance().menuMusic.setLooping(true);
				}

				ResourcesManager.getInstance().menuMusic.setVolume(1f);

				pMenuItem.setAlpha(1.0f);
				if( activity.preferences.getBoolean(activity.SOUNDS_LABEL, true) ) ResourcesManager.getInstance().clickSound.play();
			}
			else      
			{
				ResourcesManager.getInstance().menuMusic.setVolume(0f);
				pMenuItem.setAlpha(0.2f);
			}
			
			return true;
		default:
			return false;
		}
	}
	
	
}
