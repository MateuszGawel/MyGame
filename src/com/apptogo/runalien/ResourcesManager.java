package com.apptogo.runalien;

import java.io.IOException;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.Engine;
import org.andengine.engine.camera.BoundCamera;
//import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.TexturePack;
//import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.TexturePackLoader;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;

import com.apptogo.runalien.utils.GoogleBaseGameActivity;
import com.apptogo.runalien.utils.GameHelper;
import com.google.android.gms.games.Games;

import android.graphics.Color;

public class ResourcesManager {

	private static final ResourcesManager INSTANCE = new ResourcesManager();
	public int counter = 0;
	public GameHelper gameHelper;
	public Engine engine;
	public GameActivity activity;
	public BoundCamera camera;
	public VertexBufferObjectManager vbom;

	//GLOBAL RESOURCES
	public Font mainFont;
	public Font bestScoreFont;
	
	//  RESOURCES
	public ITextureRegion splash_region;
	private BitmapTextureAtlas splashTextureAtlas;
	
	//MENU RESOURCES
	public ITextureRegion menu_background_region;
	public ITextureRegion play_region;
	public ITextureRegion ranking_region;
	public ITextureRegion achievments_region;
	public ITextureRegion googlep_region;
	public ITextureRegion vibrations_region;
	public ITextureRegion sounds_region;
	private BuildableBitmapTextureAtlas menuTextureAtlas;
	
	//GAME RESOURCES
	public ITextureRegion coin_region;
	public ITextureRegion tire_region;
	public ITextureRegion stormcloud_region;
	public ITextureRegion thunder_region;
	public ITextureRegion moleHill_region;
	public ITextureRegion weasel_region;
	public ITextureRegion bottom_1_region;
	public ITextureRegion bottom_2_region;
	public ITextureRegion bottom_3_region;
	public ITextureRegion bottom_4_region;
	public ITextureRegion upper_1_region;
	public ITextureRegion upper_2_region;
	public ITextureRegion upper_3_region;
	public ITextureRegion upper_4_region;
	public ITextureRegion bottom_3_cut_region;
	public ITextureRegion crate_region;
	public ITextureRegion ball_region;
	public ITextureRegion tutorialTable_region;
	public ITextureRegion sign_region;
	public ITextureRegion grass_region;
	public ITextureRegion playerCover_region;
	public ITextureRegion ground_region;
	public ITiledTextureRegion player_region;
	public ITiledTextureRegion player_slide_region;
	public BitmapTextureAtlas gameTextureAtlas;
	public BuildableBitmapTextureAtlas playerTextureAtlas;
	
	//public BitmapTextureAtlas tutorialTextureAtlas;
	public ITextureRegion jumpTutorial_region;
	public ITextureRegion doubleJumpTutorial_region;
	public ITextureRegion slideTutorial_region ;
	public ITextureRegion chargeDownTutorial_region;
	
	public ITextureRegion game_over_region;
	public ITextureRegion replay_region;
	public ITextureRegion menu_region;
	public ITextureRegion submit_region;
	public ITextureRegion pause_region;
	
	BitmapTextureAtlas dirtRepeatingAtlas;
	public ITextureRegion dirt_texture_region;
	
	private BitmapTextureAtlas mAutoParallaxBackgroundTexture;
	public ITextureRegion mParallaxLayerBack;
	public ITextureRegion mParallaxLayerMid;
	public ITextureRegion mParallaxLayerFront;
	
	public Music menuMusic;
	public Music runSound;
	public Music screamSound;
	//public Music gameMusic;
	public Sound jumpSound;
	public Sound landingSound;
	public Sound slideSound;
	public Sound whooshSound;
	public Sound clickSound;
	public Sound dieSound;
	public Sound fallDownSound;
	public Sound doubleJumpSound;
	public Sound chargeDownSound;
	public Sound bellHit;
	
	public Sound fallingTreeSound;
	public Sound weaselSound;
	
	public String gameCatalog;
	

	
	
	
	//SPLASH RESOURCES METHODS
	public void loadSplashResources(){
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		splashTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
		splash_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(splashTextureAtlas, activity, "splash.png", 0, 0);
		splashTextureAtlas.load();
	}
	
	public void unloadSplashResources(){
		splashTextureAtlas.unload();
		splash_region = null;
	}
	
	//MENU RESOURCES METHODS
	public void loadMenuResources(){
		loadMenuGraphics();
		loadMenuFonts();
		loadMenuSounds();
	}

	public void loadMenuGraphics(){
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/");
		menuTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
		menu_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "menu_background.png");
		play_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "play.png");
		ranking_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "ranking.png");
		achievments_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "achievments.png");
		googlep_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "googlep.png");
		vibrations_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "vibrations.png");
		sounds_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "sounds.png");
		
		try{
			this.menuTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.menuTextureAtlas.load();
		}
		catch(final TextureAtlasBuilderException e){
			Debug.e(e);
		}
	}
	
	public void loadMenuFonts(){
		FontFactory.setAssetBasePath("font/");
		final ITexture mainFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		final ITexture bestScoreTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		
		mainFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), mainFontTexture, activity.getAssets(), "mainFont.ttf", 30, true, Color.WHITE, 2, Color.rgb(85, 155, 255));
		bestScoreFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), bestScoreTexture, activity.getAssets(), "bestScoreFont.ttf", 50, true, Color.GRAY, 2, Color.GRAY);
		mainFont.load();
		bestScoreFont.load();
	}
	
	public void loadMenuTextures(){
		menuTextureAtlas.load();
	}
	
	public void loadMenuSounds(){
		try {
			clickSound = SoundFactory.createSoundFromAsset(engine.getSoundManager(), activity, "mfx/clickSound.ogg");
			menuMusic = MusicFactory.createMusicFromAsset(engine.getMusicManager(), activity, "mfx/runalien.ogg");
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void unloadMenuResources(){
		unloadMenuTextures();
		unloadMenuSounds();
	}
	
	private void unloadMenuTextures(){
		menuTextureAtlas.unload();
	}
	
	public void unloadMenuSounds(){
		menuMusic.stop();
		
		menuMusic.release();
	}

	//GAME RESOURCES METHODS
	public void loadGameResources(){
		loadGameGraphics();
		loadGameSounds();
	}
	
	public void unloadGameResources(){
		unloadGameSounds();
		unloadGameGraphics();
	}
	
	public void loadGameGraphics(){
		
		int randTime = (int)(Math.random() * 4);
		if(randTime == 0){
			gameCatalog = "sunrise/";
			if(((GoogleBaseGameActivity)ResourcesManager.getInstance().activity).isSignedIn())
				Games.Achievements.unlock(ResourcesManager.getInstance().activity.getGoogleApiClient(), "CgkIpZ2MjMkXEAIQMg");	
		}
		else if(randTime == 1){
			gameCatalog = "daytime/";
			if(((GoogleBaseGameActivity)ResourcesManager.getInstance().activity).isSignedIn())
				Games.Achievements.unlock(ResourcesManager.getInstance().activity.getGoogleApiClient(), "CgkIpZ2MjMkXEAIQMQ");	
		}
		else if(randTime == 2){
			gameCatalog = "sunset/";
			if(((GoogleBaseGameActivity)ResourcesManager.getInstance().activity).isSignedIn())
				Games.Achievements.unlock(ResourcesManager.getInstance().activity.getGoogleApiClient(), "CgkIpZ2MjMkXEAIQMw");	
		}
		else{
			gameCatalog = "night/";
			if(((GoogleBaseGameActivity)ResourcesManager.getInstance().activity).isSignedIn())
				Games.Achievements.unlock(ResourcesManager.getInstance().activity.getGoogleApiClient(), "CgkIpZ2MjMkXEAIQMA");	
		}
		
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/");
	    
		//tutorialTextureAtlas =  new BitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		//tutorialTextureAtlas.addEmptyTextureAtlasSource(0, 0, 1024, 1024);
		
		//jumpTutorial_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(tutorialTextureAtlas, activity, "tutorial/jump.png", 1, 1);
		//doubleJumpTutorial_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(tutorialTextureAtlas, activity, "tutorial/doubleJump.png", 1, 41);
		//slideTutorial_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(tutorialTextureAtlas, activity, "tutorial/slide.png", 1, 81);
		//chargeDownTutorial_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(tutorialTextureAtlas, activity, "tutorial/chargeDown.png", 1, 121);
		gameTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
	    gameTextureAtlas.addEmptyTextureAtlasSource(0, 0, 1024, 1024);
	    
	    playerTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 2048, 2048, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
	    
	    //obstacle_top_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "obstacle_top.png");
	    coin_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "coin.png", 1, 1);
	    bottom_1_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, gameCatalog+"bottom1.png", 22, 1);
	    bottom_2_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, gameCatalog+"bottom2.png", 68, 1);
	    bottom_3_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, gameCatalog+"bottom3.png", 114, 1);
	    bottom_4_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, gameCatalog+"bottom4.png", 160, 1);
	    upper_1_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, gameCatalog+"upper1.png", 206, 1);
	    upper_2_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, gameCatalog+"upper2.png", 244, 1);
	    upper_3_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, gameCatalog+"upper3.png", 282, 1);
	    upper_4_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, gameCatalog+"upper4.png", 320, 1);
	    crate_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, gameCatalog+"crate.png", 366, 1);	
	    sign_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, gameCatalog+"sign.png", 561, 1);
	    bottom_3_cut_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, gameCatalog+"bottom3_cut.png", 757, 1);
	    ball_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, gameCatalog+"ball.png", 1, 296 );
	    replay_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "replay.png", 76, 296);
	    menu_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "menu.png", 280, 296);
	    submit_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "submit.png", 452, 296);
	    //grass_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "grass.png", 1, 374);
	    ground_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, gameCatalog+"dirt.png", 1, 374);
	    game_over_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_over.png", 1, 510);
	    pause_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "pause.png", 1, 570);
	    
	    playerCover_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "playerCover.png", 650, 296 );
	    
		jumpTutorial_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "tutorial/jump.png", 1, 631);
		doubleJumpTutorial_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "tutorial/doubleJump.png", 1, 677);
		slideTutorial_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "tutorial/slide.png", 1, 723);
		chargeDownTutorial_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "tutorial/chargeDown.png", 1, 769);
		tutorialTable_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "tutorial/tutorialTable.png", 1, 817);
				
		moleHill_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, gameCatalog+"moleHill.png", 1, 908);
		weasel_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, gameCatalog+"weasel.png", 1, 939);
		
		tire_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, gameCatalog+"tire.png", 400, 908);
		
		stormcloud_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, gameCatalog+"stormcloud.png", 600, 817);
		thunder_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, gameCatalog+"thunder.png", 850, 817);
			
	    player_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(playerTextureAtlas, activity, gameCatalog+"player.png", 12, 11);
	    
        //dirtRepeatingAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 311, 120, TextureOptions.REPEATING_BILINEAR_PREMULTIPLYALPHA);
        //dirt_texture_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(dirtRepeatingAtlas, activity, "dirt.png", 0, 0);
        
        
		mAutoParallaxBackgroundTexture = new BitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		mAutoParallaxBackgroundTexture.addEmptyTextureAtlasSource(0, 0, 1024, 1024);
		//mParallaxLayerGrass = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mAutoParallaxBackgroundTexture, activity, "grass.png", 0, 0);
		mParallaxLayerFront = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mAutoParallaxBackgroundTexture, activity, gameCatalog+"parallax_background_layer_front.png", 1, 15);
		mParallaxLayerBack = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mAutoParallaxBackgroundTexture, activity, gameCatalog+"parallax_background_layer_back.png", 1, 200);
		mParallaxLayerMid = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mAutoParallaxBackgroundTexture, activity, gameCatalog+"parallax_background_layer_mid.png", 1, 689);
		mAutoParallaxBackgroundTexture.load();
		
	    try 
	    {
	        //this.gameTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(5, 5, 5));
	        //this.tutorialTextureAtlas.load();
	    	this.gameTextureAtlas.load();
	        this.playerTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
	        this.playerTextureAtlas.load();
	        //this.dirtRepeatingAtlas.load();
	    } 
	    catch (final TextureAtlasBuilderException e)
	    {
	        Debug.e(e);
	    }
	}
	
	private void unloadGameGraphics(){
		//tutorialTextureAtlas.unload();
		gameTextureAtlas.unload();
		playerTextureAtlas.unload();
		mAutoParallaxBackgroundTexture.unload();
		//activity.getTextureManager().unloadTexture(tutorialTextureAtlas);
		activity.getTextureManager().unloadTexture(gameTextureAtlas);
		activity.getTextureManager().unloadTexture(mAutoParallaxBackgroundTexture);
	}
	
	public void loadGameSounds(){
		try{
		    runSound = MusicFactory.createMusicFromAsset(engine.getMusicManager(), activity, "mfx/runSound.ogg");
		    screamSound = MusicFactory.createMusicFromAsset(engine.getMusicManager(), activity, "mfx/screamSound.ogg");
		    //gameMusic = MusicFactory.createMusicFromAsset(engine.getMusicManager(), activity, "mfx/gameMusic.ogg");
		    landingSound = SoundFactory.createSoundFromAsset(engine.getSoundManager(), activity, "mfx/landingSound.ogg");
		    jumpSound = SoundFactory.createSoundFromAsset(engine.getSoundManager(), activity, "mfx/jumpSound.ogg");
		    slideSound = SoundFactory.createSoundFromAsset(engine.getSoundManager(), activity, "mfx/slideSound.ogg");
		    whooshSound = SoundFactory.createSoundFromAsset(engine.getSoundManager(), activity, "mfx/whooshSound.ogg");
		    clickSound = SoundFactory.createSoundFromAsset(engine.getSoundManager(), activity, "mfx/clickSound.ogg");
		    dieSound = SoundFactory.createSoundFromAsset(engine.getSoundManager(), activity, "mfx/dieSound.ogg");
		    fallDownSound = SoundFactory.createSoundFromAsset(engine.getSoundManager(), activity, "mfx/fallDownSound.ogg");
		    doubleJumpSound = SoundFactory.createSoundFromAsset(engine.getSoundManager(), activity, "mfx/doubleJumpSound.ogg");
		    chargeDownSound = SoundFactory.createSoundFromAsset(engine.getSoundManager(), activity, "mfx/chargeDownSound.ogg");
		    bellHit = SoundFactory.createSoundFromAsset(engine.getSoundManager(), activity, "mfx/bell.ogg");
		    
		    fallingTreeSound = SoundFactory.createSoundFromAsset(engine.getSoundManager(), activity, "mfx/fallingTreeSound.ogg");
		    weaselSound = SoundFactory.createSoundFromAsset(engine.getSoundManager(), activity, "mfx/weaselSound.ogg");
		}
		catch (IOException e)
		{
		    e.printStackTrace();
		}
		System.out.println("POSZLO2");
	}
	

	
	public void unloadGameSounds(){

		runSound.stop();
		screamSound.stop();
		//gameMusic.stop();
		landingSound.stop();
		jumpSound.stop();
		whooshSound.stop();
		slideSound.stop();
		dieSound.stop();
		fallDownSound.stop();
		doubleJumpSound.stop();
		chargeDownSound.stop();
		bellHit.stop();
		
		fallingTreeSound.stop();
		weaselSound.stop();
		

		runSound.release();
		screamSound.release();
		//gameMusic.release();
		landingSound.release();
		jumpSound.release();
		whooshSound.release();
		dieSound.release();
		fallDownSound.release();
		doubleJumpSound.release();
		chargeDownSound.release();
		bellHit.release();
		
		fallingTreeSound.release();
		weaselSound.release();
	}
	
	//OTHERS
	public static void prepareManager(Engine engine, GameActivity activity, BoundCamera camera, VertexBufferObjectManager vbom, GameHelper gameHelper){
		getInstance().engine = engine;
		getInstance().activity = activity;
		getInstance().camera = camera;
		getInstance().vbom = vbom;
		getInstance().gameHelper = gameHelper;
	}
	
	public static ResourcesManager getInstance(){
		return INSTANCE;
	}
}
