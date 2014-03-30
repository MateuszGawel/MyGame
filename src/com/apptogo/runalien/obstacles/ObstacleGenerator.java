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
	private float nextObstaclePosition = 30;
	public Random generator = new Random();
	private boolean firstObstacleFlag = true;
	
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
	{   
		//return (int)( (player.getBody().getPosition().x + 20 )); bo tak naprawde kolejne przeszkody chcemy planowac wzgledem siebie, a nie playera, ktory dopiero gdzies tam biegnie	
		int minSpace = 5;
		
		int playerVelocityX = (int)player.getBody().getLinearVelocity().x; 
		int difficulty = 10;
		
		if( playerVelocityX > 0)
			difficulty = (int) playerVelocityX ; //podobnie jak tu ;) 

		int randomFactor = (int)( Math.random() * (difficulty + 1) ); //im wyzsze difficulty tym latwiej grac - wieksze odstepy
		
		return (int)( (nextObstaclePosition + difficulty ));
	}
	
	public void startObstacleGenerationAlgorithm(){	
		scene.registerUpdateHandler(new IUpdateHandler() {
			@Override
			public void onUpdate(float pSecondsElapsed) {
				if(obstaclesPoolManager.isNotEmpty())
				{
					if(player.getBody().getPosition().x + 50 > nextObstaclePosition && player.isAlive())
					{
						int random = generator.nextInt(8);
						switch(random){
						case 0:
							generateBottomObstacle(1, -1);
							break;
						case 1:
							generateBottomObstacle(2, -1);
							break;
						case 2:
							generateBottomObstacle(3, -1);
							break;
						case 3:
							generateBottomObstacle(4, -1);
							break;
						case 4:
							generateSmallPyramid();
							break;
						case 5:
							generateBigPyramid();
							break;
						case 6:
							generateBallBottom();
							break;
						case 7:
							generateBallUpper();
							break;
						}
					}
					else if(!player.isAlive())
						ignoreAllCollisions();
				}
				if(player.isAlive())
					setProperSlidingCollisions();
				releaseUselessObstacles();
			}

			@Override
			public void reset() {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	
	//Obstacle block methods (wysokosc skrzynki to 1.3f)
	//margin -1 means auto
	private void generateBottomObstacle(int height, float customMargin)
	{
		Obstacle obstacle = null;
		switch(height){
		case 1:
			if(!obstaclesPoolManager.bottom_1_Pool.isEmpty())
			{
				obstacle = obstaclesPoolManager.bottom_1_Pool.pop();
				obstacle.getBody().setTransform(nextObstaclePosition, 6.9f, 0);
				usedObstacles.add(obstacle);
				if(customMargin<0) nextObstaclePosition = calculateObstaclePosition(); 
				else nextObstaclePosition = customMargin;
				ObstaclesPoolManager.getInstance().setCollisions(obstacle);
			} else System.out.println("POOL zabrak這 bottom1");
			break;
		case 2:
			if(!obstaclesPoolManager.bottom_2_Pool.isEmpty())
			{
				obstacle = obstaclesPoolManager.bottom_2_Pool.pop();
				obstacle.getBody().setTransform(nextObstaclePosition, 6.22f, 0);
				usedObstacles.add(obstacle);
				if(customMargin<0) nextObstaclePosition = calculateObstaclePosition(); 
				else nextObstaclePosition = customMargin;
				ObstaclesPoolManager.getInstance().setCollisions(obstacle);
			} else System.out.println("POOL zabrak這 bottom2");
			break;
		case 3:
			if(!obstaclesPoolManager.bottom_3_Pool.isEmpty())
			{
				obstacle = obstaclesPoolManager.bottom_3_Pool.pop();
				obstacle.getBody().setTransform(nextObstaclePosition, 5.54f, 0);
				usedObstacles.add(obstacle);
				if(customMargin<0) nextObstaclePosition = calculateObstaclePosition(); 
				else nextObstaclePosition = customMargin;
				ObstaclesPoolManager.getInstance().setCollisions(obstacle);
			} else System.out.println("POOL zabrak這 bottom3");
			break;
		case 4:
			if(!obstaclesPoolManager.bottom_4_Pool.isEmpty())
			{
				obstacle = obstaclesPoolManager.bottom_4_Pool.pop();
				obstacle.getBody().setTransform(nextObstaclePosition, 4.82f, 0);
				usedObstacles.add(obstacle);
				if(customMargin<0) nextObstaclePosition = calculateObstaclePosition(); 
				else nextObstaclePosition = customMargin; 
				ObstaclesPoolManager.getInstance().setCollisions(obstacle);
			} else System.out.println("POOL zabrak這 bottom4");
			break;
		default:
			System.out.println("No such height available");
			break;
		}
	}
	
	private void generateSmallPyramid(){
		generateBottomObstacle(1, nextObstaclePosition+1.40f);
		generateBottomObstacle(2, nextObstaclePosition+1.40f);
		generateBottomObstacle(1, -1);
	}
	
	private void generateBigPyramid(){
		generateBottomObstacle(1, nextObstaclePosition+1.40f);
		generateBottomObstacle(2, nextObstaclePosition+1.40f);
		generateBottomObstacle(3, nextObstaclePosition+1.40f);
		generateBottomObstacle(2, nextObstaclePosition+1.40f);
		generateBottomObstacle(1, -1);
	}

	private void generateBallUpper(){
		if(!obstaclesPoolManager.ballUpperPool.isEmpty()){
			System.out.println("POOL, generuje upper na x: " + nextObstaclePosition+10);
			BallUpper ball = obstaclesPoolManager.ballUpperPool.pop();
			ObstaclesPoolManager.getInstance().setCollisions(ball);
			ball.setTransformX(nextObstaclePosition+10);
			nextObstaclePosition = calculateObstaclePosition()+10;
			usedObstacles.add(ball);
		}
	}
	
	private void generateBallBottom(){
		if(!obstaclesPoolManager.ballBottomPool.isEmpty()){
			System.out.println("POOL, generuje bottom na x: " + nextObstaclePosition+10);
			BallBottom ball = obstaclesPoolManager.ballBottomPool.pop();
			ObstaclesPoolManager.getInstance().setCollisions(ball);
			ball.setTransformX(nextObstaclePosition+10);
			nextObstaclePosition = calculateObstaclePosition()+15;
			usedObstacles.add(ball);
		}
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
			if(obstacle.getBody().getUserData().equals("ballUpper")){
				List<Fixture> fixtureList = obstacle.getBody().getFixtureList();
				for(Fixture fixture : fixtureList){
					if(player.isSliding()){
						fixture.setSensor(true);
						System.out.println("TEST przed");
					}
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
			
			if(obstacle.getBody().getPosition().x < (player.getBody().getPosition().x -10)) //- 10 zeby znikaly juz poza ekranem
			{
				System.out.println("POOL UZYTYCH " + usedObstacles.size());
				usedObstacles.remove(obstacle);
				if(((String)(obstacle.getBody().getUserData())).equals("crateUpper"))
				{
					obstaclesPoolManager.crateUpperPool.push((CrateUpper)obstacle);
				}
				if("bottom1".equals(obstacle.getBody().getUserData()))
				{
					obstaclesPoolManager.bottom_1_Pool.push((Bottom_1)obstacle);
				}
				if("bottom2".equals(obstacle.getBody().getUserData()))
				{
					obstaclesPoolManager.bottom_2_Pool.push((Bottom_2)obstacle);
				}
				if("bottom3".equals(obstacle.getBody().getUserData()))
				{
					obstaclesPoolManager.bottom_3_Pool.push((Bottom_3)obstacle);
				}
				if("bottom4".equals(obstacle.getBody().getUserData()))
				{
					obstaclesPoolManager.bottom_4_Pool.push((Bottom_4)obstacle);
				}
				if(((obstacle.getBody().getUserData())).equals("ballUpperAnchor"))
				{
					obstaclesPoolManager.ballUpperPool.push((BallUpper)obstacle);
				}
				if((obstacle.getBody().getUserData()).equals("ballBottomAnchor"))
				{
					obstaclesPoolManager.ballBottomPool.push((BallBottom)obstacle);
				}
				ObstaclesPoolManager.getInstance().ignoreCollisions(obstacle);
			}
		}
	}

}
