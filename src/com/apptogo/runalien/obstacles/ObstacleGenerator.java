package com.apptogo.runalien.obstacles;

import java.util.ArrayList;
import java.util.List;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.Entity;
import org.andengine.entity.scene.Scene;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;

import com.apptogo.runalien.Player;

public class ObstacleGenerator {
	
	private ObstaclesPoolManager obstaclesPoolManager;
	private Player player;
	private Scene scene;
	private List<Obstacle> usedObstacles;
	
	public ObstacleGenerator(Scene scene, Player player)
	{
		this.obstaclesPoolManager = ObstaclesPoolManager.getInstance();
		this.scene = scene;
		this.player = player;
		usedObstacles = new ArrayList();
	}
	
	private float getPlayerPositionX()
	{
		return player.getX()/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
	}
	
	private void releaseUselessObstacles()
	{
		System.out.println("POOL "+"Releasing");
		int ctr = 0;
		for(Obstacle obstacle: usedObstacles)
		{
			if( obstacle.getBody().getPosition().x < getPlayerPositionX() - 20 )
			{ctr++;
				usedObstacles.remove(obstacle);
				if( ( (String)(obstacle.getBody().getUserData()) ).equals("crateUpper") )
				{
					obstaclesPoolManager.crateUpperPool.push( (CrateUpper)obstacle );
				}
				if( ( (String)(obstacle.getBody().getUserData()) ).equals("crateBottom") )
				{
					obstaclesPoolManager.crateBottomPool.push( (CrateBottom)obstacle );
				}
			}
		}
		System.out.println("POOL "+"Released obstacles: "+ctr);
	}
	
	public int calculateObstaclePosition()
	{ 
		return (int)( (player.getX() + 800)/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT );
	}
	
	private void setObstacle()
	{
		System.out.println("POOL "+"Setting obstacles");
		Obstacle obstacle = null;
		
		if( !obstaclesPoolManager.crateBottomPool.isEmpty() )
		{
			obstacle = obstaclesPoolManager.crateBottomPool.pop();
			System.out.println("POOL "+"Getting Bottom obstacle | left: "+obstaclesPoolManager.crateBottomPool.size());
		}
		else if( !obstaclesPoolManager.crateUpperPool.isEmpty() )
		{
			obstacle = obstaclesPoolManager.crateUpperPool.pop();
			System.out.println("POOL "+"Getting Upper obstacle | left: "+obstaclesPoolManager.crateUpperPool.size());
		}
		
		obstacle.getBody().setTransform( calculateObstaclePosition(), 100, 0);
		usedObstacles.add(obstacle);
		System.out.println("POOL "+"Obstacle set: "+obstacle.getBody().getPosition().x+" player = "+player.getX());
		System.out.println("POOL "+"Used obstacles count: "+usedObstacles.size() );
	}
	
	public void startObstacleGenerationAlgorithm(){	
				
		scene.registerUpdateHandler(new IUpdateHandler() {
			@Override
			public void onUpdate(float pSecondsElapsed) {
				//ReleaseUselessObstacles(); - to powoduje ConcurrentModificationEception oO
				if( obstaclesPoolManager.IsNotEmpty() )
				{
					setObstacle();
				}
			    //tutaj metody ktore sprawdzalyby pozycje playera, w celu wstawienia nowego bloku przeszkod lub pojedynczej przeszkody (osobne metody)
				//oraz usuwalyby te przeszkody z usedObstacles ktore sa poza ekranem robiac im po prostu push();
				//jakies losowanie czy cos latwo jest robic randem, z reszta wiesz:)
				
				//usunalem foregorundlayer bo one sa attachowane od razu w kopsntruktorze przeszkody
			}

			@Override
			public void reset() {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	//Obstacle block methods
	
	private void generateBlockOne(){
		//ta metoda odpowiadalaby tylko za ustawienie listy obiektow w odpowiedni schemat
		//widze to tak ze np pobiera sobie popem z poola jedna crateBottom oraz trzy crate Upper i ustawia je tak: (liczac od dolu)
		//bottom,upper,przerwa,przerwa,przerwa,upper,upper
		//czyli taka sciana z luk¹ do przeskoczenia wysokosci trzech szkyrnek
		//tylko taki przyklad.
		
		//ona by sobie pobrala popem wszystkie niezbedne elementy do utworzenia bloku przeszkod
		//te elementy powinny byc zapisane do listy usedObstacles zeby wiedziec jakie zwolnic pushem
	}
	
	
}
