package com.apptogo.runalien.obstacles;

import java.util.List;
import java.util.Stack;

import javax.microedition.khronos.opengles.GL10;

import org.andengine.entity.Entity;
import org.andengine.entity.sprite.batch.DynamicSpriteBatch;
import org.andengine.entity.sprite.batch.SpriteBatch;
import org.andengine.entity.sprite.batch.SpriteGroup;
import org.andengine.extension.physics.box2d.PhysicsWorld;

import com.apptogo.runalien.ResourcesManager;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;

public class ObstaclesPoolManager
{
	
	//Obstacles ammount
	private final int bottom_1_Ammount = 12;
	private final int bottom_2_Ammount = 12;
	private final int bottom_3_Ammount = 12;
	private final int bottom_4_Ammount = 12;
	
	private final int bottom_3_cut_Ammount = 5;
	
	private final int bottom_moleHill_Ammount = 5;
	private final int bottom_weasel_Ammount = 5;
	
	private final int upper_1_Ammount = 12;
	private final int upper_2_Ammount = 12;
	private final int upper_3_Ammount = 12;
	private final int upper_4_Ammount = 12;
	
	private final int crateUpperAmmount = 0;
	private final int ballUpperAmmount = 2;
	private final int ballBottomAmmount = 2;
	
	private final int groundSegmentAmmount = 3;
	
	//Pools
	public Stack<Bottom_1> bottom_1_Pool;
	public Stack<Bottom_2> bottom_2_Pool;
	public Stack<Bottom_3> bottom_3_Pool;
	public Stack<Bottom_4> bottom_4_Pool;
	
	public Stack<Bottom_3_cut> bottom_3_cut_Pool;
	
	public Stack<Bottom_moleHill> bottom_moleHill_Pool;
	public Stack<Bottom_weasel> bottom_weasel_Pool;
	
	public Stack<Upper_1> upper_1_Pool;
	public Stack<Upper_2> upper_2_Pool;
	public Stack<Upper_3> upper_3_Pool;
	public Stack<Upper_4> upper_4_Pool;
	
	public Stack<CrateUpper> crateUpperPool;
	public Stack<BallUpper> ballUpperPool;
	public Stack<BallBottom> ballBottomPool;
	
	public Stack<GroundSegment> groundSegmentPool;

	public DynamicSpriteBatch dynamicSpriteBatch;
	public SpriteGroup spriteGroup;
	public SpriteGroup groundSpriteGroup;
	private Entity foregroundLayer;
	private Entity backgroundLayer;
	//private boolean initialized = false;
	
	public ObstaclesPoolManager(){

		bottom_1_Pool = new Stack<Bottom_1>();
		bottom_2_Pool = new Stack<Bottom_2>();
		bottom_3_Pool = new Stack<Bottom_3>();
		bottom_4_Pool = new Stack<Bottom_4>();
		
		bottom_3_cut_Pool = new Stack<Bottom_3_cut>();
		
		bottom_moleHill_Pool = new Stack<Bottom_moleHill>();
		bottom_weasel_Pool = new Stack<Bottom_weasel>();
		
		upper_1_Pool = new Stack<Upper_1>();
		upper_2_Pool = new Stack<Upper_2>();
		upper_3_Pool = new Stack<Upper_3>();
		upper_4_Pool = new Stack<Upper_4>();
		
		crateUpperPool = new Stack<CrateUpper>();
		ballUpperPool = new Stack<BallUpper>();
		ballBottomPool = new Stack<BallBottom>();
		
		groundSegmentPool = new Stack<GroundSegment>();
		
	}
	
	public void initializePoolManager(PhysicsWorld physicsWorld, Entity foregroundLayer, Entity backgroundLayer){
		//if(!initialized){
			spriteGroup = new SpriteGroup(ResourcesManager.getInstance().gameTextureAtlas, 115, ResourcesManager.getInstance().vbom);
			foregroundLayer.attachChild(spriteGroup);
			
			groundSpriteGroup = new SpriteGroup(ResourcesManager.getInstance().gameTextureAtlas, 3, ResourcesManager.getInstance().vbom);
			backgroundLayer.attachChild(groundSpriteGroup);
			
			for(int i=bottom_1_Ammount; i>0; i--){
				bottom_1_Pool.push(new Bottom_1(physicsWorld, foregroundLayer));
			}
			for(int i=bottom_2_Ammount; i>0; i--){
				bottom_2_Pool.push(new Bottom_2(physicsWorld, foregroundLayer));
			}
			for(int i=bottom_3_Ammount; i>0; i--){
				bottom_3_Pool.push(new Bottom_3(physicsWorld, foregroundLayer));
			}
			for(int i=bottom_4_Ammount; i>0; i--){
				bottom_4_Pool.push(new Bottom_4(physicsWorld, foregroundLayer));
			}
			for(int i=bottom_3_cut_Ammount; i>0; i--){
				bottom_3_cut_Pool.push(new Bottom_3_cut(physicsWorld, foregroundLayer));
			}
			//taka kolejnosc zeby lasice byly pod kopcami - przesylam tez back jako fore - brzydkie ale nie chce mi sie zmieniac :P
			for(int i=bottom_weasel_Ammount; i>0; i--){
				bottom_weasel_Pool.push(new Bottom_weasel(physicsWorld, backgroundLayer));
			}
			for(int i=bottom_moleHill_Ammount; i>0; i--){
				bottom_moleHill_Pool.push(new Bottom_moleHill(physicsWorld, backgroundLayer));
			}
			for(int i=upper_1_Ammount; i>0; i--){
				upper_1_Pool.push(new Upper_1(physicsWorld, foregroundLayer));
			}
			for(int i=upper_2_Ammount; i>0; i--){
				upper_2_Pool.push(new Upper_2(physicsWorld, foregroundLayer));
			}
			for(int i=upper_3_Ammount; i>0; i--){
				upper_3_Pool.push(new Upper_3(physicsWorld, foregroundLayer));
			}
			for(int i=upper_4_Ammount; i>0; i--){
				upper_4_Pool.push(new Upper_4(physicsWorld, foregroundLayer));
			}
			for(int i=crateUpperAmmount; i>0; i--){
				crateUpperPool.push(new CrateUpper(physicsWorld, foregroundLayer));
			}
			for(int i=ballBottomAmmount; i>0; i--){
				ballBottomPool.push(new BallBottom(physicsWorld, foregroundLayer));
			}
			for(int i=ballUpperAmmount; i>0; i--){
				ballUpperPool.push(new BallUpper(physicsWorld, foregroundLayer));
			}
			for(int i=groundSegmentAmmount; i>0; i--){
				groundSegmentPool.push(new GroundSegment(physicsWorld, backgroundLayer));
			}
			
			//this.initialized = true;
			this.foregroundLayer = foregroundLayer;
		//}
		//spriteGroup.detachSelf();
		//foregroundLayer.attachChild(spriteGroup);
		//groundSpriteGroup.detachSelf();
		//backgroundLayer.attachChild(groundSpriteGroup);
	}
	
	public void clearPools(){
		//foregroundLayer.detachChild(spriteGroup);
		spriteGroup = null;
		
		bottom_1_Pool.clear();
		bottom_2_Pool.clear();
		bottom_3_Pool.clear();
		bottom_4_Pool.clear();
		
		bottom_3_cut_Pool.clear();
		
		bottom_moleHill_Pool.clear();
		bottom_weasel_Pool.clear();
		
		upper_1_Pool.clear();
		upper_2_Pool.clear();
		upper_3_Pool.clear();
		upper_4_Pool.clear();
		
		crateUpperPool.clear();
		ballUpperPool.clear();
		ballBottomPool.clear();
		
		groundSegmentPool.clear();
	}
	
	//Singleton
	private static final ObstaclesPoolManager INSTANCE = new ObstaclesPoolManager();
	public static ObstaclesPoolManager getInstance(){
		return INSTANCE;
	}

	public boolean isNotEmpty() {
		if( bottom_1_Pool.isEmpty() && bottom_2_Pool.isEmpty() && bottom_3_Pool.isEmpty() && bottom_4_Pool.isEmpty() && bottom_3_cut_Pool.isEmpty() && bottom_moleHill_Pool.isEmpty() && bottom_weasel_Pool.isEmpty() && upper_1_Pool.isEmpty() && upper_2_Pool.isEmpty() && upper_3_Pool.isEmpty() && upper_4_Pool.isEmpty() && crateUpperPool.isEmpty() && groundSegmentPool.isEmpty())
		{
			return false;
		}
		return true;
	}
}
