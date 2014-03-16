package com.example.mygame;

import java.io.IOException;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
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

import android.graphics.Color;

public class ResourcesManager {

	private static final ResourcesManager INSTANCE = new ResourcesManager();
	
	public Engine engine;
	public GameActivity activity;
	public BoundCamera camera;
	public VertexBufferObjectManager vbom;

	//GLOBAL RESOURCES
	public Font mainFont;
	public Font bestScoreFont;
	
	//SPLASH RESOURCES
	public ITextureRegion splash_region;
	private BitmapTextureAtlas splashTextureAtlas;
	
	//MENU RESOURCES
	public ITextureRegion menu_background_region;
	public ITextureRegion play_region;
	public ITextureRegion options_region;
	private BuildableBitmapTextureAtlas menuTextureAtlas;
	
	//GAME RESOURCES
	public ITextureRegion obstacle_top_region;
	public ITextureRegion obstacle_bottom_region;
	public ITextureRegion sign_region;
	public ITiledTextureRegion player_region;
	public ITiledTextureRegion player_slide_region;
	public BuildableBitmapTextureAtlas gameTextureAtlas;
	public BuildableBitmapTextureAtlas playerTextureAtlas;
	
	public ITextureRegion game_over_region;
	public ITextureRegion replay_region;
	public ITextureRegion menu_region;
	
	BitmapTextureAtlas dirtRepeatingAtlas;
	public ITextureRegion dirt_texture_region;
	
	private BitmapTextureAtlas mAutoParallaxBackgroundTexture;
	public ITextureRegion mParallaxLayerBack;
	public ITextureRegion mParallaxLayerMid;
	public ITextureRegion mParallaxLayerFront;
	
	public Music runSound;
	public Music screamSound;
	public Music jumpSound;
	public Music landingSound;
	public Music slideSound;
	public Music whooshSound;
	public Music clickSound;
	public Music dieSound;
	public Music fallDownSound;
	

	
	
	
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
		options_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "options.png");
		
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
		
		mainFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), mainFontTexture, activity.getAssets(), "mainFont.ttf", 30, true, Color.WHITE, 2, Color.rgb(22, 144, 189));
		bestScoreFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), bestScoreTexture, activity.getAssets(), "bestScoreFont.ttf", 50, true, Color.WHITE, 2, Color.WHITE);
		mainFont.load();
		bestScoreFont.load();
	}
	
	public void loadMenuTextures(){
		menuTextureAtlas.load();
	}
	
	public void loadMenuSounds(){
		try {
			clickSound = MusicFactory.createMusicFromAsset(engine.getMusicManager(), activity, "mfx/clickSound.ogg");
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void unloadMenuTextures(){
		menuTextureAtlas.unload();
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
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/");
	    gameTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 512, 512, TextureOptions.DEFAULT);
	    playerTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 2048, 1024, TextureOptions.BILINEAR);
	    
	    //obstacle_top_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "obstacle_top.png");
	    obstacle_bottom_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "obstacle_bottom.png");
	    sign_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "sign.png");	        
	    game_over_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_over.png");
	    replay_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "replay.png");
	    menu_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "menu.png");
	    
	    player_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(playerTextureAtlas, activity, "player.png", 12, 11);
	    
        dirtRepeatingAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 311, 120, TextureOptions.REPEATING_BILINEAR_PREMULTIPLYALPHA);
        dirt_texture_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(dirtRepeatingAtlas, activity, "dirt.png", 0, 0);
        
		mAutoParallaxBackgroundTexture = new BitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.DEFAULT);
		mParallaxLayerFront = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mAutoParallaxBackgroundTexture, activity, "parallax_background_layer_front.png", 0, 0);
		mParallaxLayerBack = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mAutoParallaxBackgroundTexture, activity, "parallax_background_layer_back.png", 0, 200);
		mParallaxLayerMid = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mAutoParallaxBackgroundTexture, activity, "parallax_background_layer_mid.png", 0, 689);
		mAutoParallaxBackgroundTexture.load();
		
	    try 
	    {
	        this.gameTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
	        this.gameTextureAtlas.load();
	        this.dirtRepeatingAtlas.load();
	    } 
	    catch (final TextureAtlasBuilderException e)
	    {
	        Debug.e(e);
	    }
	}
	
	private void unloadGameGraphics(){
		gameTextureAtlas.unload();
		dirtRepeatingAtlas.unload();
		mAutoParallaxBackgroundTexture.unload();
		activity.getTextureManager().unloadTexture(gameTextureAtlas);
		activity.getTextureManager().unloadTexture(dirtRepeatingAtlas);
		activity.getTextureManager().unloadTexture(mAutoParallaxBackgroundTexture);
	}
	
	public void loadGameSounds(){
		try
		{
		    runSound = MusicFactory.createMusicFromAsset(engine.getMusicManager(), activity, "mfx/runSound.ogg");
		    screamSound = MusicFactory.createMusicFromAsset(engine.getMusicManager(), activity, "mfx/screamSound.ogg");
		    landingSound = MusicFactory.createMusicFromAsset(engine.getMusicManager(), activity, "mfx/landingSound.ogg");
		    jumpSound = MusicFactory.createMusicFromAsset(engine.getMusicManager(), activity, "mfx/jumpSound.ogg");
		    slideSound = MusicFactory.createMusicFromAsset(engine.getMusicManager(), activity, "mfx/slideSound.ogg");
		    whooshSound = MusicFactory.createMusicFromAsset(engine.getMusicManager(), activity, "mfx/whooshSound.ogg");
		    clickSound = MusicFactory.createMusicFromAsset(engine.getMusicManager(), activity, "mfx/clickSound.ogg");
		    dieSound = MusicFactory.createMusicFromAsset(engine.getMusicManager(), activity, "mfx/dieSound.ogg");
		    fallDownSound = MusicFactory.createMusicFromAsset(engine.getMusicManager(), activity, "mfx/fallDownSound.ogg");
		}
		catch (IOException e)
		{
		    e.printStackTrace();
		}
	}
	
	public void unloadGameSounds(){
		runSound.stop();
		screamSound.stop();
		landingSound.stop();
		jumpSound.stop();
		whooshSound.stop();
		slideSound.stop();
		dieSound.stop();
		fallDownSound.stop();

		
		runSound.release();
		screamSound.release();
		landingSound.release();
		jumpSound.release();
		whooshSound.release();
		dieSound.release();
		fallDownSound.release();
	}
	
	//OTHERS
	public static void prepareManager(Engine engine, GameActivity activity, BoundCamera camera, VertexBufferObjectManager vbom){
		getInstance().engine = engine;
		getInstance().activity = activity;
		getInstance().camera = camera;
		getInstance().vbom = vbom;
	}
	
	public static ResourcesManager getInstance(){
		return INSTANCE;
	}
}
