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
						int random = generator.nextInt(9);
						switch(random){
						case 0:
							if(!firstObstacleFlag)
								generateBottomUpperSegment(); //z jakiegos powodu jezeli ten segment generuje sie jako pierwszy to nie ma odstepu miedzy nimi
							firstObstacleFlag = false;
							break;
						case 1:
							generateUpperWall(false);
							break;
						case 2:
							generateBottomWall(false);
							break;
						case 3:
							generateBigWall();
							break;
						case 4:
							generatePyramid();
							break;
						case 5:
							generateSingleBottomObstacle();
							break;
						case 6:
							generateSingleUpperObstacle(5.5f);
							break;
						case 7:
							generateBallBottom();
							break;
						case 8:
							generateBallTop();
							break;
						}
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
	
	
	//Obstacle block methods (wysokosc skrzynki to 1.3f)
	
	private void generateSingleBottomObstacle()
	{
		Obstacle obstacle = null;
		
		if(!obstaclesPoolManager.bottom_1_Pool.isEmpty())
		{
			obstacle = obstaclesPoolManager.bottom_1_Pool.pop();
			obstacle.getBody().setTransform(nextObstaclePosition, 6.8f, 0);
			usedObstacles.add(obstacle);
			nextObstaclePosition = calculateObstaclePosition(); 
			ObstaclesPoolManager.getInstance().setCollisions(obstacle);
		}
	}
	
	private void generateSingleUpperObstacle(float yPos)
	{
		Obstacle obstacle = null;
		
		if(!obstaclesPoolManager.bottom_1_Pool.isEmpty()) //!
		{
			obstacle = obstaclesPoolManager.bottom_1_Pool.pop();
			obstacle.getBody().setTransform(nextObstaclePosition, yPos, 0);
			usedObstacles.add(obstacle);
			nextObstaclePosition = calculateObstaclePosition();
			ObstaclesPoolManager.getInstance().setCollisions(obstacle);
		}
	}
	
	private void generatePyramid(){
		List<Obstacle> obstacles = new ArrayList<Obstacle>();
		Obstacle obstacle;
		
		if(obstaclesPoolManager.bottom_1_Pool.size() >= 2 && obstaclesPoolManager.bottom_2_Pool.size() >= 1)
		{

			obstacle = obstaclesPoolManager.bottom_1_Pool.pop();
			obstacles.add(obstacle);
			usedObstacles.add(obstacle);
			ObstaclesPoolManager.getInstance().setCollisions(obstacle);

			obstacle = obstaclesPoolManager.bottom_2_Pool.pop();
			obstacles.add(obstacle);
			usedObstacles.add(obstacle);
			ObstaclesPoolManager.getInstance().setCollisions(obstacle);
			
			obstacle = obstaclesPoolManager.bottom_1_Pool.pop();
			obstacles.add(obstacle);
			usedObstacles.add(obstacle);
			ObstaclesPoolManager.getInstance().setCollisions(obstacle);

			obstacles.get(0).getBody().setTransform(nextObstaclePosition, 6.8f, 0);
			obstacles.get(1).getBody().setTransform(nextObstaclePosition+1.40f, 6.8f, 0);
			obstacles.get(2).getBody().setTransform(nextObstaclePosition+2.8f, 6.8f, 0);
			nextObstaclePosition = calculateObstaclePosition(); //to oznacza ze nastepna przeszkoda pojawi sie za 100 jednostek. Trzeba to wyliczac na podstawie predkosci playera (mozna tez dodawac zmienna losowa)
		}
	}
	
	private void generateBigWall(){
		List<Obstacle> obstacles = new ArrayList<Obstacle>();
		Obstacle obstacle;
		
		if(obstaclesPoolManager.bottom_2_Pool.size() >= 2)
		{
			for(int i=0; i<2; i++){
				obstacle = obstaclesPoolManager.bottom_2_Pool.pop();
				obstacles.add(obstacle);
				usedObstacles.add(obstacle);
				ObstaclesPoolManager.getInstance().setCollisions(obstacle);
			}

			obstacles.get(0).getBody().setTransform(nextObstaclePosition, 6.8f, 0);
			obstacles.get(1).getBody().setTransform(nextObstaclePosition, -1.5f, 0);
			nextObstaclePosition = calculateObstaclePosition(); //to oznacza ze nastepna przeszkoda pojawi sie za 100 jednostek. Trzeba to wyliczac na podstawie predkosci playera (mozna tez dodawac zmienna losowa)
		}
	}
	
	private void generateBottomWall(boolean customMargin){
		List<Obstacle> obstacles = new ArrayList<Obstacle>();
		Obstacle obstacle;
		
		if(obstaclesPoolManager.bottom_4_Pool.size() >= 1)
		{
			obstacle = obstaclesPoolManager.bottom_4_Pool.pop();
			obstacles.add(obstacle);
			usedObstacles.add(obstacle);
			ObstaclesPoolManager.getInstance().setCollisions(obstacle);

			obstacles.get(0).getBody().setTransform(nextObstaclePosition, 6.8f, 0);
			
			if(!customMargin)
				nextObstaclePosition = calculateObstaclePosition(); //to oznacza ze nastepna przeszkoda pojawi sie za 100 jednostek. Trzeba to wyliczac na podstawie predkosci playera (mozna tez dodawac zmienna losowa)
		}
	}
	
	private void generateUpperWall(boolean customMargin){
		List<Obstacle> obstacles = new ArrayList<Obstacle>();
		Obstacle obstacle;
		
		if(obstaclesPoolManager.bottom_4_Pool.size() >= 2)
		{
			
			for(int i=0; i<2; i++){
				obstacle = obstaclesPoolManager.bottom_4_Pool.pop();
				obstacles.add(obstacle);
				usedObstacles.add(obstacle);
				ObstaclesPoolManager.getInstance().setCollisions(obstacle);
			}
			
			obstacles.get(0).getBody().setTransform(nextObstaclePosition, 1.6f, 0);
			obstacles.get(1).getBody().setTransform(nextObstaclePosition, -3.6f, 0);
			
			if(!customMargin)
				nextObstaclePosition = calculateObstaclePosition(); //to oznacza ze nastepna przeszkoda pojawi sie za 100 jednostek. Trzeba to wyliczac na podstawie predkosci playera (mozna tez dodawac zmienna losowa)
		}
	}
	
	private void generateBottomUpperSegment(){
		if(obstaclesPoolManager.bottom_4_Pool.size() >= 3 )
		generateBottomWall(true);
		nextObstaclePosition += 0.5f*player.getBody().getLinearVelocity().x; 
		generateUpperWall(true);
		nextObstaclePosition = calculateObstaclePosition();
	}
	
	private void generateBallTop(){
		if(!obstaclesPoolManager.ballUpperPool.isEmpty()){
			BallUpper ball = obstaclesPoolManager.ballUpperPool.pop();
			ObstaclesPoolManager.getInstance().setCollisions(ball);
			usedObstacles.add(ball);
			ball.setTransformX(nextObstaclePosition+10);
			nextObstaclePosition = calculateObstaclePosition()+10;
		}
	}
	
	private void generateBallBottom(){
		if(!obstaclesPoolManager.ballBottomPool.isEmpty()){
			BallBottom ball = obstaclesPoolManager.ballBottomPool.pop();
			ObstaclesPoolManager.getInstance().setCollisions(ball);
			usedObstacles.add(ball);
			ball.setTransformX(nextObstaclePosition+10);
			nextObstaclePosition = calculateObstaclePosition()+15;
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
			if(!( "Bottom1".equals(obstacle.getBody().getUserData()) || "Bottom2".equals(obstacle.getBody().getUserData()) || "Bottom3".equals(obstacle.getBody().getUserData()) || "Bottom4".equals(obstacle.getBody().getUserData()) ) && !obstacle.getBody().getUserData().equals("ballBottom")){
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
			
			if( obstacle.getBody().getPosition().x < (player.getBody().getPosition().x) -5) //- 10 zeby znikaly juz poza ekranem
			{
				usedObstacles.remove(obstacle);
				if( ( (String)(obstacle.getBody().getUserData()) ).equals("crateUpper") )
				{
					obstaclesPoolManager.crateUpperPool.push( (CrateUpper)obstacle );
				}
				if(  "Bottom1".equals(obstacle.getBody().getUserData()) )
				{
					obstaclesPoolManager.bottom_1_Pool.push( (Bottom_1)obstacle );
				}
				if(  "Bottom2".equals(obstacle.getBody().getUserData()) )
				{
					obstaclesPoolManager.bottom_2_Pool.push( (Bottom_2)obstacle );
				}
				if(  "Bottom3".equals(obstacle.getBody().getUserData()) )
				{
					obstaclesPoolManager.bottom_3_Pool.push( (Bottom_3)obstacle );
				}
				if(  "Bottom4".equals(obstacle.getBody().getUserData()) )
				{
					obstaclesPoolManager.bottom_4_Pool.push( (Bottom_4)obstacle );
				}
				if( ( (String)(obstacle.getBody().getUserData()) ).equals("ballUpper") )
				{
					obstaclesPoolManager.ballUpperPool.push( (BallUpper)obstacle );
				}
				if( ( (String)(obstacle.getBody().getUserData()) ).equals("ballBottom") )
				{
					obstaclesPoolManager.ballBottomPool.push( (BallBottom)obstacle );
				}
				ObstaclesPoolManager.getInstance().ignoreCollisions(obstacle);
			}
		}
	}

}
