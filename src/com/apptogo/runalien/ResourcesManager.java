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

import com.apptogo.runalien.utils.GameHelper;

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
	private BuildableBitmapTextureAtlas menuTextureAtlas;
	
	//GAME RESOURCES
	public ITextureRegion coin_region;
	public ITextureRegion bottom_1_region;
	public ITextureRegion bottom_2_region;
	public ITextureRegion bottom_3_region;
	public ITextureRegion bottom_4_region;
	public ITextureRegion upper_1_region;
	public ITextureRegion upper_2_region;
	public ITextureRegion upper_3_region;
	public ITextureRegion upper_4_region;
	public ITextureRegion crate_region;
	public ITextureRegion ball_region;
	public ITextureRegion sign_region;
	public ITextureRegion grass_region;
	public ITextureRegion playerCover_region;
	public ITiledTextureRegion player_region;
	public ITiledTextureRegion player_slide_region;
	public BuildableBitmapTextureAtlas gameTextureAtlas;
	public BuildableBitmapTextureAtlas playerTextureAtlas;
	
	public ITextureRegion game_over_region;
	public ITextureRegion replay_region;
	public ITextureRegion menu_region;
	public ITextureRegion submit_region;
	
	BitmapTextureAtlas dirtRepeatingAtlas;
	public ITextureRegion dirt_texture_region;
	public ITextureRegion ground_region;
	
	private BitmapTextureAtlas mAutoParallaxBackgroundTexture;
	public ITextureRegion mParallaxLayerBack;
	public ITextureRegion mParallaxLayerMid;
	public ITextureRegion mParallaxLayerFront;
	public ITextureRegion mParallaxLayerGrass;
	
	public Music runSound;
	public Music screamSound;
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
	    gameTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 2048, 2048, TextureOptions.DEFAULT);
	    playerTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 2048, 2048, TextureOptions.BILINEAR);
	    
	    //obstacle_top_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "obstacle_top.png");
	    coin_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "coin.png");
	    bottom_1_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "bottom1.png");
	    bottom_2_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "bottom2.png");
	    bottom_3_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "bottom3.png");
	    bottom_4_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "bottom4.png");
	    upper_1_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "upper1.png");
	    upper_2_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "upper2.png");
	    upper_3_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "upper3.png");
	    upper_4_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "upper4.png");
	    crate_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "crate.png");
	    sign_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "sign.png");	 
	    ball_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "ball.png");
	    game_over_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_over.png");
	    replay_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "replay.png");
	    menu_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "menu.png");
	    submit_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "submit.png");
	    grass_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "grass.png");
	    ground_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "ground.png");
	    
	    playerCover_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "playerCover.png");
	    player_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(playerTextureAtlas, activity, "player.png", 12, 11);
	    
        dirtRepeatingAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 311, 122, TextureOptions.REPEATING_BILINEAR_PREMULTIPLYALPHA);
        //dirt_texture_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(dirtRepeatingAtlas, activity, "dirt.png", 0, 0);
        
        
		mAutoParallaxBackgroundTexture = new BitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.DEFAULT);
		//mParallaxLayerGrass = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mAutoParallaxBackgroundTexture, activity, "grass.png", 0, 0);
		mParallaxLayerFront = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mAutoParallaxBackgroundTexture, activity, "parallax_background_layer_front.png", 0, 15);
		mParallaxLayerBack = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mAutoParallaxBackgroundTexture, activity, "parallax_background_layer_back.png", 0, 200);
		mParallaxLayerMid = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mAutoParallaxBackgroundTexture, activity, "parallax_background_layer_mid.png", 0, 689);
		mAutoParallaxBackgroundTexture.load();
		
	    try 
	    {
	        this.gameTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
	        this.gameTextureAtlas.load();
	        this.playerTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
	        this.playerTextureAtlas.load();
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
		try{
			
		    runSound = MusicFactory.createMusicFromAsset(engine.getMusicManager(), activity, "mfx/runSound.ogg");
		    screamSound = MusicFactory.createMusicFromAsset(engine.getMusicManager(), activity, "mfx/screamSound.ogg");
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
		doubleJumpSound.stop();
		chargeDownSound.stop();
		bellHit.stop();

		
		runSound.release();
		screamSound.release();
		landingSound.release();
		jumpSound.release();
		whooshSound.release();
		dieSound.release();
		fallDownSound.release();
		doubleJumpSound.release();
		chargeDownSound.release();
		bellHit.release();
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
