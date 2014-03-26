package com.apptogo.runalien.obstacles;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.Entity;
import org.andengine.entity.scene.Scene;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;

import com.apptogo.runalien.Player;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;

public class ObstacleGenerator {
	
	private ObstaclesPoolManager obstaclesPoolManager;
	private Player player;
	private Scene scene;
	private List<Obstacle> usedObstacles;
	private int nextObstaclePosition = 30;
	public Random generator = new Random();
	
	public void log(String s){
		System.out.println("POOL " + s);
	}
	
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
	
	
	public int calculateObstaclePosition()
	{   //System.out.println("POOL : obstacleCalc = "+(nextObstaclePosition + 20 ) );
		//return (int)( (player.getBody().getPosition().x + 20 )); bo tak naprawde kolejne przeszkody chcemy planowac wzgledem siebie, a nie playera, ktory dopiero gdzies tam biegnie	
		int minSpace = 5;
		
		int playerVelocityX = (int)player.getBody().getLinearVelocity().x; 
		
		int difficulty = 10;
		
		if( playerVelocityX > 0)
			difficulty = (int)( 200 / playerVelocityX ); //podobnie jak tu ;) 

		int randomFactor = (int)( Math.random() * (difficulty + 1) ); //im wyzsze difficulty tym latwiej grac - wieksze odstepy
		
		return (int)( (nextObstaclePosition + minSpace + difficulty ));
	}
	
	public void startObstacleGenerationAlgorithm(){	
				
		scene.registerUpdateHandler(new IUpdateHandler() {
			@Override
			public void onUpdate(float pSecondsElapsed) {
				if(obstaclesPoolManager.IsNotEmpty())
				{
					if(player.getBody().getPosition().x + 50 > nextObstaclePosition && player.isAlive())
					{
						log("pozycja playera: " + player.getBody().getPosition().x + " +50 jest wieksza niz " + nextObstaclePosition);
						
						if(generator.nextInt(100)>50)
							setSingleBottomObstacle();
						else
							setSingleUpperObstacle(5); //wysokosc playera wynosi troche mniej niz 2. Taka jest jednostka
					}
					else if(!player.isAlive())
						ignoreAllCollisions();
					if(player.isAlive())
						setProperSlidingCollisions();
				}
				//System.out.println("POOL : oooops the pool is empty - waiting for release any");
				releaseUselessObstacles();
			}

			@Override
			public void reset() {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	
	//Obstacle block methods
	
	private void setSingleBottomObstacle()
	{
		Obstacle obstacle = null;
		
		if(!obstaclesPoolManager.crateBottomPool.isEmpty())
		{
			log("stawiam przeszkode bottom na " + nextObstaclePosition);
			obstacle = obstaclesPoolManager.crateBottomPool.pop();
			obstacle.getBody().setTransform(nextObstaclePosition, 7, 0);
			usedObstacles.add(obstacle);
			nextObstaclePosition = calculateObstaclePosition(); 
		}
	}
	
	private void setSingleUpperObstacle(int yPos)
	{
		Obstacle obstacle = null;
		
		if(!obstaclesPoolManager.crateBottomPool.isEmpty())
		{
			log("stawiam przeszkode upper na " + nextObstaclePosition);
			obstacle = obstaclesPoolManager.crateUpperPool.pop();
			obstacle.getBody().setTransform(nextObstaclePosition, yPos, 0);
			usedObstacles.add(obstacle);
			nextObstaclePosition = calculateObstaclePosition();
		}
	}
	
	private void generateBlockOne(){
		//ta metoda odpowiadalaby tylko za ustawienie listy obiektow w odpowiedni schemat
		//widze to tak ze np pobiera sobie popem z poola jedna crateBottom oraz trzy crate Upper i ustawia je tak: (liczac od dolu)
		//bottom,upper,przerwa,przerwa,przerwa,upper,upper
		//czyli taka sciana z luk¹ do przeskoczenia wysokosci trzech szkyrnek
		//tylko taki przyklad.
		
		//ona by sobie pobrala popem wszystkie niezbedne elementy do utworzenia bloku przeszkod
		//te elementy powinny byc zapisane do listy usedObstacles zeby wiedziec jakie zwolnic pushem
	}
	
	
	//Others
	
	private void ignoreAllCollisions(){
		for(Obstacle obstacle : usedObstacles){
			List<Fixture> fixtureList = obstacle.getBody().getFixtureList();
			for(Fixture fixture : fixtureList){
				fixture.setSensor(true);
		    }
		}
	}
	
	private void setProperSlidingCollisions(){
		for(Obstacle obstacle : usedObstacles){
			if(!obstacle.getBody().getUserData().equals("crateBottom")){
				List<Fixture> fixtureList = obstacle.getBody().getFixtureList();
				for(Fixture fixture : fixtureList){
					if(player.isSliding())
						fixture.setSensor(true);
					else
						fixture.setSensor(false);
			    }
			}
		}
	}
	
	private void releaseUselessObstacles()
	{
		for(int u = 0; u < usedObstacles.size(); u++) //tu byl problem z concurrency - uzywalismy foreach z iteratorem i on robil problemy UWAGA NA TO MOZE BYC DZIURAWE
		{                                             //nawet nie probowac synchronizowac :P probowalem synchronizowac metody/bloki ponad godzine i lipa a tak dziala
			Obstacle obstacle = usedObstacles.get(u);
			
			if( obstacle.getBody().getPosition().x < (player.getBody().getPosition().x - 10) ) //- 10 zeby znikaly juz poza ekranem
			{
				usedObstacles.remove(obstacle);
				if( ( (String)(obstacle.getBody().getUserData()) ).equals("crateUpper") )
				{
					log("Releasing upper: before " + obstaclesPoolManager.crateBottomPool.size());
					obstaclesPoolManager.crateUpperPool.push( (CrateUpper)obstacle );
					log("Releasing upper: after " + obstaclesPoolManager.crateBottomPool.size());
				}
				if( ( (String)(obstacle.getBody().getUserData()) ).equals("crateBottom") )
				{
					log("Releasing bottom: before " + obstaclesPoolManager.crateBottomPool.size());
					obstaclesPoolManager.crateBottomPool.push( (CrateBottom)obstacle );
					log("Releasing bottom: after " + obstaclesPoolManager.crateBottomPool.size());
				}
			}
		}
	}
}
