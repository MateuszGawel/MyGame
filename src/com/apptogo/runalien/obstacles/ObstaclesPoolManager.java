package com.apptogo.runalien.obstacles;

import java.util.Stack;

import org.andengine.entity.Entity;
import org.andengine.extension.physics.box2d.PhysicsWorld;

import com.badlogic.gdx.physics.box2d.Body;

public class ObstaclesPoolManager
{
	//Obstacles ammount
	private final int crateBottomAmmount = 30;
	private final int crateUpperAmmount = 30;
	private final int ballTopAmmount = 3;
	private final int ballBottomAmmount = 3;
	
	//Pools
	public Stack<CrateBottom> crateBottomPool;
	public Stack<CrateUpper> crateUpperPool;
	public Stack<BallTop> ballTopPool;
	public Stack<BallBottom> ballBottomPool;

	public ObstaclesPoolManager(){

		crateBottomPool = new Stack<CrateBottom>();
		crateUpperPool = new Stack<CrateUpper>();
		ballTopPool = new Stack<BallTop>();
		ballBottomPool = new Stack<BallBottom>();
	}
	
	public void initializePoolManager(PhysicsWorld physicsWorld, Entity foregroundLayer){
		for(int i=crateBottomAmmount; i>0; i--){
			crateBottomPool.push(new CrateBottom(physicsWorld, foregroundLayer));
		}
		for(int i=crateUpperAmmount; i>0; i--){
			crateUpperPool.push(new CrateUpper(physicsWorld, foregroundLayer));
		}
		for(int i=ballBottomAmmount; i>0; i--){
			ballBottomPool.push(new BallBottom(physicsWorld, foregroundLayer));
		}
		for(int i=ballTopAmmount; i>0; i--){
			ballTopPool.push(new BallTop(physicsWorld, foregroundLayer));
		}
		
	}
	
	
	//Singleton
	private static final ObstaclesPoolManager INSTANCE = new ObstaclesPoolManager();
	public static ObstaclesPoolManager getInstance(){
		return INSTANCE;
	}

	public boolean isNotEmpty() {
		if( crateBottomPool.isEmpty() && crateUpperPool.isEmpty() )
		{
			return false;
		}
		return true;
	}
}