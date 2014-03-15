package com.example.mygame;

import org.andengine.engine.Engine;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;

import com.example.mygame.scenes.GameScene;
import com.example.mygame.scenes.LoadingScene;
import com.example.mygame.scenes.MainMenuScene;
import com.example.mygame.scenes.SplashScene;

public class SceneManager {

	private static final SceneManager INSTANCE = new SceneManager();
	
	private BaseScene splashScene;
	private BaseScene menuScene;
	private BaseScene gameScene;
	private BaseScene loadingScene;
	
	private SceneType currentSceneType = SceneType.SCENE_SPLASH;
	private BaseScene currentScene;
	private Engine engine = ResourcesManager.getInstance().engine;
	
	private ResourcesManager resourcesManager = ResourcesManager.getInstance();
	
	public enum SceneType{
		SCENE_SPLASH,
		SCENE_MENU,
		SCENE_GAME,
		SCENE_LOADING,
	}
	
	//SPLASH SCENE
	public BaseScene createSplashScene(){
		resourcesManager.loadSplashResources();
		splashScene = new SplashScene();
		setScene(SceneType.SCENE_SPLASH);
		return this.splashScene;
	}
	
	//MENU SCENE
	public void createMenuScene(){
		resourcesManager.loadMenuResources();
		menuScene = new MainMenuScene();
		loadingScene = new LoadingScene();
		setScene(menuScene);
		splashScene.disposeScene();
	}
	
	public void loadMenuScene(){
		setScene(loadingScene);
		gameScene.disposeScene();
		resourcesManager.unloadGameResources();
		resourcesManager.engine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback()
		{
			public void onTimePassed(final TimerHandler pTimerHandler)
			{
				resourcesManager.engine.unregisterUpdateHandler(pTimerHandler);
				ResourcesManager.getInstance().loadMenuResources();
				menuScene = new MainMenuScene(); 
				setScene(menuScene);
			}
		}));
	}
	
	//GAME SCENE
	public void loadGameScene(){
		setScene(loadingScene);
		menuScene.disposeScene();
		resourcesManager.unloadMenuTextures();
		resourcesManager.engine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback()
		{
			public void onTimePassed(final TimerHandler pTimerHandler)
			{
				resourcesManager.engine.unregisterUpdateHandler(pTimerHandler);
				resourcesManager.loadGameResources();
				gameScene = new GameScene();
				setScene(gameScene);
			}
		}));
	}
	
	public void replayGameScene(){
		resourcesManager.loadGameSounds();
		gameScene = new GameScene();
		setScene(gameScene);
	}
	
	//SETTERS
	public void setScene(BaseScene scene){
		engine.setScene(scene);
		currentScene = scene;
		currentSceneType = scene.getSceneType();
	}
	
	public void setScene(SceneType sceneType){
		switch(sceneType){
	      case SCENE_MENU:
              setScene(menuScene);
              break;
          case SCENE_GAME:
              setScene(gameScene);
              break;
          case SCENE_SPLASH:
              setScene(splashScene);
              break;
          case SCENE_LOADING:
              setScene(loadingScene);
              break;
          default:
              break;
		}
	}
	
	//GETTERS
    public static SceneManager getInstance()
    {
        return INSTANCE;
    }
    
    public SceneType getCurrentSceneType()
    {
        return currentSceneType;
    }
    
    public BaseScene getCurrentScene()
    {
        return currentScene;
    }
}
