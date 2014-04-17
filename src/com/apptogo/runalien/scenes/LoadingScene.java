package com.apptogo.runalien.scenes;

import org.andengine.entity.scene.background.Background;
import org.andengine.entity.text.Text;
import org.andengine.util.color.Color;

import com.apptogo.runalien.BaseScene;
import com.apptogo.runalien.ResourcesManager;
import com.apptogo.runalien.SceneManager.SceneType;

public class LoadingScene extends BaseScene{

	@Override
	public void createScene() {
		setBackground(new Background(new Color(164, 218, 138)));
		attachChild(new Text(400,240, ResourcesManager.getInstance().mainFont, "LOADING...", vbom));
	}

	@Override
	public void onBackKeyPressed() {
		return;
	}
	
	@Override
	public void onMenuKeyPressed() {
		return;
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_LOADING;
	}

	@Override
	public void disposeScene() {
		
	}

}
