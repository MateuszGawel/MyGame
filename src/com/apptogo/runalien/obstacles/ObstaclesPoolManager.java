package com.apptogo.runalien.obstacles;

import java.util.List;
import java.util.Stack;

import org.andengine.entity.Entity;
import org.andengine.extension.physics.box2d.PhysicsWorld;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;

public class ObstaclesPoolManager
{
	
	//Obstacles ammount
	private final int bottom_1_Ammount = 10;
	private final int bottom_2_Ammount = 7;
	private final int bottom_3_Ammount = 5;
	private final int bottom_4_Ammount = 3;
	
	private final int upper_1_Ammount = 5;
	private final int upper_2_Ammount = 5;
	private final int upper_3_Ammount = 0;
	private final int upper_4_Ammount = 5;
	
	private final int crateUpperAmmount = 0;
	private final int ballUpperAmmount = 3;
	private final int ballBottomAmmount = 3;
	
	//Pools
	public Stack<Bottom_1> bottom_1_Pool;
	public Stack<Bottom_2> bottom_2_Pool;
	public Stack<Bottom_3> bottom_3_Pool;
	public Stack<Bottom_4> bottom_4_Pool;
	
	public Stack<Upper_1> upper_1_Pool;
	public Stack<Upper_2> upper_2_Pool;
	public Stack<Upper_3> upper_3_Pool;
	public Stack<Upper_4> upper_4_Pool;
	
	public Stack<CrateUpper> crateUpperPool;
	public Stack<BallUpper> ballUpperPool;
	public Stack<BallBottom> ballBottomPool;

	public ObstaclesPoolManager(){

		bottom_1_Pool = new Stack<Bottom_1>();
		bottom_2_Pool = new Stack<Bottom_2>();
		bottom_3_Pool = new Stack<Bottom_3>();
		bottom_4_Pool = new Stack<Bottom_4>();
		
		upper_1_Pool = new Stack<Upper_1>();
		upper_2_Pool = new Stack<Upper_2>();
		upper_3_Pool = new Stack<Upper_3>();
		upper_4_Pool = new Stack<Upper_4>();
		
		crateUpperPool = new Stack<CrateUpper>();
		ballUpperPool = new Stack<BallUpper>();
		ballBottomPool = new Stack<BallBottom>();
	}
	
	public void initializePoolManager(PhysicsWorld physicsWorld, Entity foregroundLayer){
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
		
	}
	
	public void ignoreCollisions(Obstacle obstacle){
		List<Fixture> fixtureList = obstacle.getBody().getFixtureList();
		for(Fixture fixture : fixtureList){
				fixture.setSensor(true);
	    }
	}
	
	public void setCollisions(Obstacle obstacle){
		List<Fixture> fixtureList = obstacle.getBody().getFixtureList();
		for(Fixture fixture : fixtureList){
				fixture.setSensor(false);
	    }
	}
	//Singleton
	private static final ObstaclesPoolManager INSTANCE = new ObstaclesPoolManager();
	public static ObstaclesPoolManager getInstance(){
		return INSTANCE;
	}

	public boolean isNotEmpty() {
		if( bottom_1_Pool.isEmpty() && bottom_2_Pool.isEmpty() && bottom_3_Pool.isEmpty() && bottom_4_Pool.isEmpty() && upper_1_Pool.isEmpty() && upper_2_Pool.isEmpty() && upper_3_Pool.isEmpty() && upper_4_Pool.isEmpty() && crateUpperPool.isEmpty() )
		{
			return false;
		}
		return true;
	}
}
