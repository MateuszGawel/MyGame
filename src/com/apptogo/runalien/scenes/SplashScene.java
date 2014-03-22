package com.apptogo.runalien.scenes;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.util.GLState;
import org.andengine.engine.camera.Camera;

import com.apptogo.runalien.BaseScene;
import com.apptogo.runalien.SceneManager.SceneType;

public class SplashScene extends BaseScene{

	private Sprite sSplash;
	
	@Override
	public void createScene() {
				
		sSplash = new Sprite(0, 0, resourcesManager.splash_region, vbom){
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera){
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}
		};
		sSplash.setScale(1.5f);
		sSplash.setPosition(400 - sSplash.getWidth()/2, 240 - sSplash.getHeight()/2);
		attachChild(sSplash);
	}

	@Override
	public void onBackKeyPressed() {
		
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_SPLASH;
	}

	@Override
	public void disposeScene() {
		sSplash.detachSelf();
		sSplash.dispose();
		this.detachSelf();
		this.dispose();
		resourcesManager.unloadSplashResources();
	}
	
	
}
