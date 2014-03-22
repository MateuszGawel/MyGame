package com.apptogo.runalien.scenes;

import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.util.GLState;
import org.andengine.engine.camera.Camera;

import com.apptogo.runalien.BaseScene;
import com.apptogo.runalien.ResourcesManager;
import com.apptogo.runalien.SceneManager;
import com.apptogo.runalien.SceneManager.SceneType;
import com.apptogo.runalien.obstacles.ObstaclesPoolManager;

public class MainMenuScene extends BaseScene implements IOnMenuItemClickListener{

	private MenuScene menuChildScene;
	private final int MENU_PLAY = 0;
	private final int MENU_OPTIONS = 1;
	
	@Override
	public void createScene() {
		createBackground();
		createMenuChildScene();
	}

	@Override
	public void onBackKeyPressed() {
		System.exit(0);
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_MENU;
	}

	@Override
	public void disposeScene() {
		this.detachSelf();
		this.dispose();
	}
	
	//ADDITIONAL METHODS
	private void createMenuChildScene(){
		menuChildScene = new MenuScene(camera);
		menuChildScene.setPosition(400 - camera.getWidth()/2, 240 - camera.getHeight()/2);
		
		final IMenuItem playMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_PLAY, resourcesManager.play_region, vbom), 1.2f, 1);
		final IMenuItem rankingMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_OPTIONS, resourcesManager.ranking_region, vbom), 1.2f, 1);
		
		menuChildScene.addMenuItem(playMenuItem);
		menuChildScene.addMenuItem(rankingMenuItem);
		
		menuChildScene.buildAnimations();
		menuChildScene.setBackgroundEnabled(false);
		
		playMenuItem.setPosition(255, 270); 
		rankingMenuItem.setPosition(255, 365); 
		
		menuChildScene.setOnMenuItemClickListener(this);
		setChildScene(menuChildScene);
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
		ResourcesManager.getInstance().clickSound.play();
		switch(pMenuItem.getID()){
		case MENU_PLAY:
			sceneManager.loadGameScene();
			return true;
		case MENU_OPTIONS:
			resourcesManager.gameHelper.manualConnect();
			return true;
		default:
			return false;
		}
	}
	
	
}
