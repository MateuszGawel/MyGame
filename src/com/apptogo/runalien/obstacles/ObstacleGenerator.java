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
	private float nextObstaclePosition = 900;
	public Random generator = new Random();
	private boolean firstObstacleFlag = true;
	private ArrayList<Integer> lastRandoms;
	
	public ObstacleGenerator(Scene scene, Player player)
	{
		this.obstaclesPoolManager = ObstaclesPoolManager.getInstance();
		this.scene = scene;
		this.player = player;
		usedObstacles = new ArrayList();
		lastRandoms = new ArrayList<Integer>();
	}
	
	private float getPlayerPositionX()
	{
		return player.getX()/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
	}
	
	private int getVelocityOffset()
	{
		int playerVelocityX = (int)player.getBody().getLinearVelocity().x; 
		int offset = (playerVelocityX - 13) * 16;
		
		return offset;
	}
	
	public int calculateObstaclePosition()
	{   
		int minSpace = 500;
		int velocityOffset = getVelocityOffset();
		
		return (int)( (nextObstaclePosition + minSpace + velocityOffset ));
	}
	
	public void startObstacleGenerationAlgorithm(final int tutorialScoreOffset){	
		scene.registerUpdateHandler(new IUpdateHandler() {
			@Override
			public void onUpdate(float pSecondsElapsed) {
				checkCollisions();
				if(obstaclesPoolManager.isNotEmpty())
				{
					if(player.getX() + 800 > nextObstaclePosition && player.isAlive())
					{
						int score = (int) Math.round(player.getBody().getPosition().x/10) - tutorialScoreOffset;
						int minSpace = 4 + (int)(player.getBody().getLinearVelocity().x / 2 );
						
						int maxRand = (((int)(score/6))%15 );
						
						if(maxRand == 0) maxRand = 1;
						
						int random = generator.nextInt( maxRand );

						if( lastRandoms.size() >= 2 ) 
						{ 
							//moznaby to przerobic na jakiegos for-a zeby mozna bylo definiowac ilosc mozliwych powtorzen przeszkod ale mysle ze 2 pod rzad to rozsadna ilosc
							int t_last = lastRandoms.get( lastRandoms.size() - 1).intValue();
							int t_plast = lastRandoms.get( lastRandoms.size() - 2).intValue();
							
							if( (t_last == random && t_plast == random) || (t_last%2 == 0 && random%2 == 0 ) || (t_last%2 == 1 && random%2 == 1) )
							{
								if(random == 0) random++;
								else            random--;
								
								lastRandoms.clear();
							}
						}
						
						lastRandoms.add(random);
						
						int INSEQUENCEDISTANCE = 250;
						
						switch(random){
						case 0:
							generateBottomObstacle(1, -1);
							break;
						case 1:
							generateUpperObstacle(1, -1);
							break;
						case 2:
							generateLoongPyramid(-1);
							break;
						case 3:
							generateUpperBottomWall(-1);
							break;
						case 4:
							mustDoubleJump(-1);
							break;
						case 5:
							generateUpDownSequence();
							break;
						case 6:
							generateRightBigPyramid();
							break;
						case 7:
							generateBallUpper();
							break;
						case 8:
							generateBallBottom();
							break;
						case 9:
							generateLeftBigPyramid();							
							break;
						case 10:
							generateMuchJumpingSequence(INSEQUENCEDISTANCE);							
							break;
						case 11:
							generateRightVeryBigPyramid();
							break;
						case 12:
							generateMadWallOpenedSequence(INSEQUENCEDISTANCE);
							break;
						case 13:
							generateJumpThenSlideSequence(INSEQUENCEDISTANCE);
							break;
						case 14:
							generateWhatTheSmackSequence(INSEQUENCEDISTANCE);
							break;
						case 15:
							generateMadWallOpenedSequence(INSEQUENCEDISTANCE);
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
	
	private void checkCollisions(){
		for(Obstacle obstacle : usedObstacles){
			if(obstacle.getSprite().collidesWith(player.playerCover)){
				if(obstacle.getSprite().getUserData().toString().contains("bottom")){
					player.dieBottom();
				}
				else if(obstacle.getSprite().getUserData().toString().contains("upper")){
					player.dieTop(true);
				}
			}
		}
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
				obstacle.getSprite().setX(nextObstaclePosition);
				System.out.println("LOG ustawiam na: " + nextObstaclePosition);
				usedObstacles.add(obstacle);
				if(customMargin<0) nextObstaclePosition = calculateObstaclePosition(); 
				else nextObstaclePosition = customMargin;
				//ObstaclesPoolManager.getInstance().setCollisions(obstacle);
			} else System.out.println("POOL zabrak這 bottom1");
			break;
		case 2:
			if(!obstaclesPoolManager.bottom_2_Pool.isEmpty())
			{
				obstacle = obstaclesPoolManager.bottom_2_Pool.pop();
				obstacle.getSprite().setX(nextObstaclePosition);
				usedObstacles.add(obstacle);
				if(customMargin<0) nextObstaclePosition = calculateObstaclePosition(); 
				else nextObstaclePosition = customMargin;
				//ObstaclesPoolManager.getInstance().setCollisions(obstacle);
			} else System.out.println("POOL zabrak這 bottom2");
			break;
		case 3:
			if(!obstaclesPoolManager.bottom_3_Pool.isEmpty())
			{
				obstacle = obstaclesPoolManager.bottom_3_Pool.pop();
				obstacle.getSprite().setX(nextObstaclePosition);
				usedObstacles.add(obstacle);
				if(customMargin<0) nextObstaclePosition = calculateObstaclePosition(); 
				else nextObstaclePosition = customMargin;
				//ObstaclesPoolManager.getInstance().setCollisions(obstacle);
			} else System.out.println("POOL zabrak這 bottom3");
			break;
		case 4:
			if(!obstaclesPoolManager.bottom_4_Pool.isEmpty())
			{
				obstacle = obstaclesPoolManager.bottom_4_Pool.pop();
				obstacle.getSprite().setX(nextObstaclePosition);
				usedObstacles.add(obstacle);
				if(customMargin<0) nextObstaclePosition = calculateObstaclePosition(); 
				else nextObstaclePosition = customMargin; 
				//ObstaclesPoolManager.getInstance().setCollisions(obstacle);
			} else System.out.println("POOL zabrak這 bottom4");
			break;
		default:
			System.out.println("No such height available");
			break;
		}
	}
	
	private void generateUpperObstacle(int height, float customMargin)
	{
		Obstacle obstacle = null;
		switch(height){
		case 1:
			if(!obstaclesPoolManager.upper_1_Pool.isEmpty())
			{
				obstacle = obstaclesPoolManager.upper_1_Pool.pop();
				obstacle.getSprite().setX(nextObstaclePosition);
				usedObstacles.add(obstacle);
				if(customMargin<0) nextObstaclePosition = calculateObstaclePosition(); 
				else nextObstaclePosition = customMargin;
				//ObstaclesPoolManager.getInstance().setCollisions(obstacle);
			} else System.out.println("POOL zabrak這 upper1");
			break;
		case 2:
			if(!obstaclesPoolManager.upper_2_Pool.isEmpty())
			{
				obstacle = obstaclesPoolManager.upper_2_Pool.pop();
				obstacle.getSprite().setX(nextObstaclePosition);
				usedObstacles.add(obstacle);
				if(customMargin<0) nextObstaclePosition = calculateObstaclePosition(); 
				else nextObstaclePosition = customMargin;
				//ObstaclesPoolManager.getInstance().setCollisions(obstacle);
			} else System.out.println("POOL zabrak這 upper2");
			break;
		case 3: // w sumie do niczego nie przydatne chyba
			if(!obstaclesPoolManager.upper_3_Pool.isEmpty())
			{
				obstacle = obstaclesPoolManager.upper_3_Pool.pop();
				obstacle.getSprite().setX(nextObstaclePosition);
				usedObstacles.add(obstacle);
				if(customMargin<0) nextObstaclePosition = calculateObstaclePosition(); 
				else nextObstaclePosition = customMargin;
				//ObstaclesPoolManager.getInstance().setCollisions(obstacle);
			} else System.out.println("POOL zabrak這 upper3");
			break;
		case 4:
			if(!obstaclesPoolManager.upper_4_Pool.isEmpty())
			{
				obstacle = obstaclesPoolManager.upper_4_Pool.pop();
				obstacle.getSprite().setX(nextObstaclePosition);
				usedObstacles.add(obstacle);
				if(customMargin<0) nextObstaclePosition = calculateObstaclePosition(); 
				else nextObstaclePosition = customMargin; 
				//ObstaclesPoolManager.getInstance().setCollisions(obstacle);
			} else System.out.println("POOL zabrak這 upper4");
			break;
		default:
			System.out.println("No such height available");
			break;
		}
	}
	
	private void generateSmallPyramid(float distance){
		generateBottomObstacle(1, nextObstaclePosition+45);
		generateUpperObstacle(2, nextObstaclePosition);
		generateBottomObstacle(2, nextObstaclePosition+45);
		if(distance > 0) generateBottomObstacle(1, nextObstaclePosition + distance);
		else             generateBottomObstacle(1, -1);  
	}
	
	private void mustDoubleJump(float distance){
		generateBottomObstacle(1, nextObstaclePosition+45);
		generateUpperObstacle(2, nextObstaclePosition);
		generateBottomObstacle(2, nextObstaclePosition+45);
		if(distance > 0) generateBottomObstacle(2, nextObstaclePosition + distance);
		else             generateBottomObstacle(2, -1);  
	}
	
	private void generateUpperBottomWall(float distance){
		generateUpperObstacle(2, nextObstaclePosition);
		if(distance > 0) generateBottomObstacle(2, nextObstaclePosition + distance);
		else             generateBottomObstacle(2, -1);
	}
	
	private void generateUpDownSequence(){
		generateBottomObstacle(4, nextObstaclePosition + 200 + getVelocityOffset());
		generateUpperObstacle(4, -1);
	}
	
	private void generateRightBigPyramid(){
		generateBottomObstacle(1, nextObstaclePosition+45f);
		generateBottomObstacle(2, nextObstaclePosition+45f);
		generateBottomObstacle(3, -1);
	}
	
	private void generateRightVeryBigPyramid(){
		generateBottomObstacle(1, nextObstaclePosition+45f);
		generateBottomObstacle(2, nextObstaclePosition+45f);
		generateBottomObstacle(3, nextObstaclePosition+45f);
		generateBottomObstacle(4, -1);
	}
	
	private void generateLeftBigPyramid(){
		generateBottomObstacle(3, nextObstaclePosition+45f);
		generateBottomObstacle(2, nextObstaclePosition+45f);
		generateBottomObstacle(1, -1);
	}
	
	private void generateMuchJumpingSequence(float distance){
		int t_offset = getVelocityOffset();
		generateBottomObstacle(1, nextObstaclePosition + distance + t_offset - 1f);
		
		generateBottomObstacle(1, nextObstaclePosition + 45f);
		generateBottomObstacle(2, nextObstaclePosition + distance + t_offset);
		
		generateUpperObstacle(1, nextObstaclePosition + distance + t_offset);
		
		generateRightBigPyramid();
	}
	
	private void generateMadWallOpenedSequence(float distance){
		
		int t_offset = getVelocityOffset();
		
		generateUpperBottomWall(distance + t_offset);
		
		generateUpperObstacle(4, nextObstaclePosition + distance + t_offset + 50);
		
		generateSmallPyramid(distance);
		
		generateUpperObstacle(4, nextObstaclePosition + distance + t_offset + 50);
		
		generateRightVeryBigPyramid();
		
		generateLeftBigPyramid();
	}

	private void generateJumpThenSlideSequence(float distance){
		
		int t_offset = getVelocityOffset();
		
		generateBottomObstacle(1, nextObstaclePosition + t_offset + distance);
		generateUpperObstacle(1, nextObstaclePosition + t_offset + distance + 50);
		generateBottomObstacle(1, nextObstaclePosition + t_offset + distance);
		generateUpperObstacle(1, nextObstaclePosition + t_offset + distance + 50);
		generateBottomObstacle(1, nextObstaclePosition + t_offset + distance);
		generateUpperObstacle(1, nextObstaclePosition + t_offset + distance + 50);
		generateBottomObstacle(1, -1);
	}
	
	private void generateWhatTheSmackSequence(float distance){
		
		int t_offset = getVelocityOffset();
		
		generateBottomObstacle(3, nextObstaclePosition + distance + t_offset + 20);
		generateUpperBottomWall(distance);
		generateUpperObstacle(1, nextObstaclePosition + distance + t_offset + 50);
		generateSmallPyramid(distance);
		generateUpperObstacle(1, nextObstaclePosition + distance + t_offset + 50);
		generateRightBigPyramid();
		generateUpperObstacle(4, -1);
	}
	
	private void generateBallUpper(){
		if(!obstaclesPoolManager.ballUpperPool.isEmpty()){
			BallUpper ball = obstaclesPoolManager.ballUpperPool.pop();
			ball.setTransformX(nextObstaclePosition/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT+10);
			nextObstaclePosition = calculateObstaclePosition()+200;
			usedObstacles.add(ball);
		}
		else System.out.println("POOL zabrak這 ball upper");
	}
	
	private void generateBallBottom(){
		if(!obstaclesPoolManager.ballBottomPool.isEmpty()){
			BallBottom ball = obstaclesPoolManager.ballBottomPool.pop();
			ball.setTransformX(nextObstaclePosition/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT+10);
			nextObstaclePosition = calculateObstaclePosition()+200;
			usedObstacles.add(ball);
		}
		else System.out.println("POOL zabrak這 ball bottom");
	}
	//Others
	
	private void generateLoongPyramid(float distance){
		generateBottomObstacle(1, nextObstaclePosition+45);
		generateUpperObstacle(2, nextObstaclePosition);
		generateBottomObstacle(2, nextObstaclePosition+45);
		generateBottomObstacle(2, nextObstaclePosition+45);
		generateUpperObstacle(2, nextObstaclePosition);
		generateBottomObstacle(2, nextObstaclePosition+45);
		generateUpperObstacle(2, nextObstaclePosition);
		generateBottomObstacle(2, nextObstaclePosition+45);
		if(distance > 0) generateBottomObstacle(1, nextObstaclePosition + distance);
		else             generateBottomObstacle(1, -1);  
	}
	
	private void ignoreAllCollisions(){
		//for(Obstacle obstacle : usedObstacles){
		//	List<Fixture> fixtureList = obstacle.getBody().getFixtureList();
		//	for(Fixture fixture : fixtureList){
		//		fixture.setSensor(true);
		//    }
		//}
	}
	
	private void setProperSlidingCollisions(){
		//System.out.println("KOLIZJA check");
		for(Obstacle obstacle : usedObstacles){
			//System.out.println("KOLIZJA obstacle: " + obstacle.getSprite().getUserData());
			if(obstacle.getBody()!=null && obstacle.getBody().getUserData().equals("ballUpper")){
				System.out.println("KOLIZJA mam kulke: " + obstacle.getSprite().getUserData() + "fixtures: " + obstacle.getBody().getFixtureList().size());
				List<Fixture> fixtureList = obstacle.getBody().getFixtureList();
				for(Fixture fixture : fixtureList){
					if(player.isSliding()){
						System.out.println("KOLIZJA WYLACZAM KOLIZJE");
						fixture.setSensor(true);
					}
					else{
						System.out.println("KOLIZJA WYLACZAM KOLIZJE SPOWROTEM");
						fixture.setSensor(false);
					}
			    }
			}
		}
	}
	
	private void releaseUselessObstacles()
	{
		for(int u = 0; u < usedObstacles.size(); u++) //tu byl problem z concurrency - uzywalismy foreach z iteratorem i on robil problemy UWAGA NA TO MOZE BYC DZIURAWE
		{                                             //nawet nie probowac synchronizowac :P probowalem synchronizowac metody/bloki ponad godzine i lipa a tak dziala
			Obstacle obstacle = usedObstacles.get(u);
			
			if(!((String)obstacle.getSprite().getUserData()).contains("ball") && obstacle.getSprite().getX() < (player.getX() -50)) //- 10 zeby znikaly juz poza ekranem
			{
				usedObstacles.remove(obstacle);
				if(obstacle.getSprite().getUserData().equals("crateUpper"))
				{
					obstaclesPoolManager.crateUpperPool.push((CrateUpper)obstacle);
				}
				if("bottom1".equals(obstacle.getSprite().getUserData()))
				{
					obstaclesPoolManager.bottom_1_Pool.push((Bottom_1)obstacle);
				}
				if("bottom2".equals(obstacle.getSprite().getUserData()))
				{
					obstaclesPoolManager.bottom_2_Pool.push((Bottom_2)obstacle);
				}
				if("bottom3".equals(obstacle.getSprite().getUserData()))
				{
					obstaclesPoolManager.bottom_3_Pool.push((Bottom_3)obstacle);
				}
				if("bottom4".equals(obstacle.getSprite().getUserData()))
				{
					obstaclesPoolManager.bottom_4_Pool.push((Bottom_4)obstacle);
				}
				if("upper1".equals(obstacle.getSprite().getUserData()))
				{
					obstaclesPoolManager.upper_1_Pool.push((Upper_1)obstacle);
				}
				if("upper2".equals(obstacle.getSprite().getUserData()))
				{
					obstaclesPoolManager.upper_2_Pool.push((Upper_2)obstacle);
				}
				if("upper3".equals(obstacle.getSprite().getUserData()))
				{
					obstaclesPoolManager.upper_3_Pool.push((Upper_3)obstacle);
				}
				if("upper4".equals(obstacle.getSprite().getUserData()))
				{
					obstaclesPoolManager.upper_4_Pool.push((Upper_4)obstacle);
				}
			}
			else if(obstacle.getSprite().getUserData().equals("ballUpperAnchor") && ((BallUpper)obstacle).getAnchorPositionX() < (player.getX() -50)){
				usedObstacles.remove(obstacle);
				obstaclesPoolManager.ballUpperPool.push((BallUpper)obstacle);
			}
			else if(obstacle.getSprite().getUserData().equals("ballBottomAnchor") && ((BallBottom)obstacle).getAnchorPositionX() < (player.getX() -50)){
				usedObstacles.remove(obstacle);
				obstaclesPoolManager.ballBottomPool.push((BallBottom)obstacle);
			}
		}
	}

	public float getNextObstaclePosition() {
		return nextObstaclePosition;
	}

	public void setNextObstaclePosition(float nextObstaclePosition) {
		this.nextObstaclePosition = nextObstaclePosition;
	}

}
