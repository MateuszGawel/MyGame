package com.apptogo.runalien;

import java.io.IOException;
import java.io.RandomAccessFile;

import org.andengine.engine.Engine;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;

import com.apptogo.runalien.scenes.GameScene;
import com.apptogo.runalien.scenes.LoadingScene;
import com.apptogo.runalien.scenes.MainMenuScene;
import com.apptogo.runalien.scenes.SplashScene;

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
		gameScene = null;
		resourcesManager.unloadGameResources();
		resourcesManager.engine.registerUpdateHandler(new TimerHandler(0.2f, new ITimerCallback()
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
		menuScene = null;
		resourcesManager.unloadMenuResources();
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
		setScene(loadingScene);
		gameScene.disposeScene();
		//resourcesManager.unloadGameResources();
		resourcesManager.engine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback()
		{
			public void onTimePassed(final TimerHandler pTimerHandler)
			{
				resourcesManager.engine.unregisterUpdateHandler(pTimerHandler);
				//resourcesManager.loadGameResources();
				gameScene = new GameScene();
				setScene(gameScene);
			}
		}));
		
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
    
    //DEBUGGER
    private float readUsage() {
	    try {
	        RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
	        String load = reader.readLine();

	        String[] toks = load.split(" ");

	        long idle1 = Long.parseLong(toks[5]);
	        long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4])
	              + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

	        try {
	            Thread.sleep(360);
	        } catch (Exception e) {}

	        reader.seek(0);
	        load = reader.readLine();
	        reader.close();

	        toks = load.split(" ");

	        long idle2 = Long.parseLong(toks[5]);
	        long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4])
	            + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

	        return (float)(cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1));

	    } catch (IOException ex) {
	        ex.printStackTrace();
	    }

	    return 0;
	} 
}
