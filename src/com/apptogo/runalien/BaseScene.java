package com.apptogo.runalien;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.BoundCamera;
import org.andengine.entity.scene.Scene;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.apptogo.runalien.SceneManager.SceneType;

import android.app.Activity;

public abstract class BaseScene extends Scene{
	
	protected Engine engine;
	protected GameActivity activity;
	protected ResourcesManager resourcesManager;
	protected SceneManager sceneManager;
	protected VertexBufferObjectManager vbom;
	protected BoundCamera camera;
	
	public BaseScene(){
		this.resourcesManager = ResourcesManager.getInstance();
        this.engine = resourcesManager.engine;
        this.activity = resourcesManager.activity;
        this.vbom = resourcesManager.vbom;
        this.camera = resourcesManager.camera;
        this.sceneManager = SceneManager.getInstance();
        createScene();
	}

	public abstract void createScene();
	public abstract void onBackKeyPressed();
	public abstract void onMenuKeyPressed();
	public abstract SceneType getSceneType();
	public abstract void disposeScene();
}
