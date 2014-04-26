package com.apptogo.runalien.obstacles;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.Entity;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.debugdraw.DebugRenderer;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;

import com.apptogo.runalien.Player;
import com.apptogo.runalien.R;
import com.apptogo.runalien.ResourcesManager;
import com.apptogo.runalien.utils.GoogleBaseGameActivity;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.google.android.gms.games.Games;

public class ObstacleGenerator {
	
	private ObstaclesPoolManager obstaclesPoolManager;
	private Player player;
	private Scene scene;
	private List<Obstacle> usedObstacles;
	private float nextObstaclePosition = 900;
	private float nextGroundPosition = -200;
	public Random generator = new Random();
	private boolean firstObstacleFlag = true;
	private ArrayList<Integer> lastRandoms;
	private long ctr = 0; //do generowania co x-ty raz kazdej przeszkody
	private boolean firstObstacle = true;
	private boolean firstObstacleDeadTrigger = false;
	private boolean startGeneratingObstacles = false;
	private int offsetAfterTutorial;
	
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
		int minSpace = 360;
		
		if( this.player.runningSpeed < 16 ) minSpace = 320;
		
		int velocityOffset = getVelocityOffset();
		
		return (int)( (nextObstaclePosition + minSpace + velocityOffset ));
	}
	
	public void startGeneratingObstacles(float offsetAfterTutorial){
		nextObstaclePosition = offsetAfterTutorial+900;
		startGeneratingObstacles = true;
	}
	
	public void startObstacleGenerationAlgorithm(){	
		scene.registerUpdateHandler(new IUpdateHandler() {
			@Override
			public void onUpdate(float pSecondsElapsed) {
				checkCollisions();
				if(obstaclesPoolManager.isNotEmpty())
				{
					if(player.getX() + 800 > nextObstaclePosition && player.isAlive())
					{
						int score = (int) Math.round(player.getBody().getPosition().x/10);
						int minSpace = 5 + (int)(player.getBody().getLinearVelocity().x / 2 );
						ctr++;
						
						int maxRand = (int) (player.runningSpeed*1.5 - 14);
						int minRand = maxRand - 5;
						//if (maxRand == 15) maxRand = 21; //osiagnieto max predkosc
						if(minRand < 0 || ctr % 4 == 0) minRand = 0;
						int random;
						if(startGeneratingObstacles)
							random = minRand + (int)(Math.random() * ((maxRand - minRand) + 1));
						else
							random = -1;
						
						//sprawdzic wydajnosc tutaj
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
							generateSmallLeftPyramid(-1);
							break;
						case 3:
							generateUpperObstacle(4, -1);
							break;
						case 4:
							generateUpperBottomWall(-1);
							break;
						case 5:
							generateDoubleSlideWithJump(INSEQUENCEDISTANCE);
							break;
						case 6:
							generateSmallPyramid(-1);
							break;
						case 7:
							generateMuchJumpingSequence(INSEQUENCEDISTANCE);
							break;
						case 8:
							generateRightBigPyramid();				
						case 9:
							generateInvertedSmallPyramid(-1);
							break;
						case 10:
							generateJumpThenSlideSequence(INSEQUENCEDISTANCE);	
							break;
						case 11:
							double x = player.runningSpeed;
							double ballOffset = ((-(5.0/98.0))*(((13.0*x)*((19.0*x)-753.0))+85220.0));
							generateBallUpper((int)ballOffset);
							break;
						case 12:
							x = player.runningSpeed;
							ballOffset = ((-(5.0/98.0))*(((13.0*x)*((19.0*x)-753.0))+85220.0));
							generateBallBottom((int)ballOffset);
							break;
						case 13:
							generateUpDownSequence();	
							break;
						case 14:
							generateSmallRightBigPyramid();
							break;
						case 15:
							mustDoubleJump(-1);				
							break;
						case 16:
							generateRightVeryBigPyramid();
							break;
						case 17:
							generateEgyptSequence(INSEQUENCEDISTANCE);
							break;
						case 18:
							generateMadWallOpenedSequence(INSEQUENCEDISTANCE);
							break;
						case 19: 
							generateWhatTheSmackSequence(INSEQUENCEDISTANCE);
							break;
						case 20:
							generateLongJumpThenSlideSequence(INSEQUENCEDISTANCE);
							break;
						case 21:
							generateLoongPyramid(-1);
							break;
						default:
							break;
						}
					}
					if(player.getX() + 800-1 > nextGroundPosition){
						generateGroundSegment();
						System.out.println("GROUND generuje");
					}
				}
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
			if(player.isAlive() && obstacle.getSprite().collidesWith(player.playerCover)){
				if(obstacle.getSprite().getUserData().toString().contains("bottom")){
					player.dieBottom();
					if(obstacle.getSprite().getX() < 1000){
						System.out.println("DIE ON FIRST");
						if(((GoogleBaseGameActivity)ResourcesManager.getInstance().activity).isSignedIn()){
							Games.Achievements.unlock(ResourcesManager.getInstance().activity.getGoogleApiClient(), "CgkIpZ2MjMkXEAIQAg");						
						}
					}
				}
				else if(obstacle.getSprite().getUserData().toString().contains("upper")){
					player.dieTop(true);
					if(obstacle.getSprite().getX() < 1000){
						System.out.println("DIE ON FIRST");
						if(((GoogleBaseGameActivity)ResourcesManager.getInstance().activity).isSignedIn()){
							Games.Achievements.unlock(ResourcesManager.getInstance().activity.getGoogleApiClient(), "CgkIpZ2MjMkXEAIQAg");						
						}
					}
				}
				else if(obstacle.getSprite().getUserData().toString().equals("ballUpper")){
					player.dieTop(false);
				}
				else if(obstacle.getSprite().getUserData().toString().equals("ballBottom")){
					player.dieTop(false);
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
	
	private void generateSmallLeftPyramid(float distance){
		if(obstaclesPoolManager.bottom_1_Pool.size() >= 1 && obstaclesPoolManager.bottom_2_Pool.size() >= 1)
		{
			generateBottomObstacle(1, nextObstaclePosition+45);
			if(distance > 0) generateBottomObstacle(2, nextObstaclePosition + distance);
			else             generateBottomObstacle(2, -1); 
		}
	}
	
	private void generateSmallRightPyramid(float distance){
		if(obstaclesPoolManager.bottom_1_Pool.size() >= 1 && obstaclesPoolManager.bottom_2_Pool.size() >= 1)
		{
			generateBottomObstacle(2, nextObstaclePosition+45);
			if(distance > 0) generateBottomObstacle(1, nextObstaclePosition + distance);
			else             generateBottomObstacle(1, -1);  
		}
	}
	
	private void generateSmallPyramid(float distance){
		if(obstaclesPoolManager.bottom_1_Pool.size() >= 2 && obstaclesPoolManager.bottom_2_Pool.size() >= 1 && obstaclesPoolManager.upper_2_Pool.size() >= 1)
		{
			generateBottomObstacle(1, nextObstaclePosition+45);
			
			if( this.player.runningSpeed > 16 ) generateUpperObstacle(2, nextObstaclePosition); //zeby nie generowaly sie na samym poczatku
			generateBottomObstacle(2, nextObstaclePosition+45);
			if(distance > 0) generateBottomObstacle(1, nextObstaclePosition + distance);
			else             generateBottomObstacle(1, -1); 
		}
	}
	
	private void generateInvertedSmallPyramid(float distance){
		if(obstaclesPoolManager.bottom_1_Pool.size() >= 1 && obstaclesPoolManager.bottom_2_Pool.size() >= 2)
		{
			generateBottomObstacle(2, nextObstaclePosition+45);
			generateBottomObstacle(1, nextObstaclePosition+45);
			if(distance > 0) generateBottomObstacle(2, nextObstaclePosition + distance);
			else             generateBottomObstacle(2, -1);  
		}
	}
	
	private void mustDoubleJump(float distance){
		if(obstaclesPoolManager.bottom_1_Pool.size() >= 2 && obstaclesPoolManager.bottom_2_Pool.size() >= 2 && obstaclesPoolManager.bottom_4_Pool.size() >= 1 && obstaclesPoolManager.upper_2_Pool.size() >= 1)
		{
			generateBottomObstacle(1, nextObstaclePosition+45);
			generateUpperObstacle(2, nextObstaclePosition);
			generateBottomObstacle(2, nextObstaclePosition+45);
			generateBottomObstacle(1, nextObstaclePosition+45);
			generateBottomObstacle(2, nextObstaclePosition+45);
			if(distance > 0) generateBottomObstacle(4, nextObstaclePosition + distance);
			else             generateBottomObstacle(4, -1);
		}
	}
	
	private void generateUpperBottomWall(float distance){
		if(obstaclesPoolManager.bottom_2_Pool.size() >= 1 && obstaclesPoolManager.upper_2_Pool.size() >= 1)
		{
			generateUpperObstacle(2, nextObstaclePosition);
			if(distance > 0) generateBottomObstacle(2, nextObstaclePosition + distance);
			else             generateBottomObstacle(2, -1);
		}
	}
	
	private void generateUpDownSequence(){
		if(obstaclesPoolManager.bottom_4_Pool.size() >= 1 && obstaclesPoolManager.upper_4_Pool.size() >= 1)
		{
			generateBottomObstacle(4, nextObstaclePosition + 300 + getVelocityOffset());
			generateUpperObstacle(4, -1);
		}
	}
	
	private void generateRightBigPyramid(){
		if(obstaclesPoolManager.bottom_1_Pool.size() >= 1 && obstaclesPoolManager.bottom_2_Pool.size() >= 1 && obstaclesPoolManager.bottom_3_Pool.size() >= 1)
		{
			generateBottomObstacle(1, nextObstaclePosition+45f);
			generateBottomObstacle(2, nextObstaclePosition+45f);
			generateBottomObstacle(3, -1);
		}
	}
	
	private void generateSmallLeftBigPyramid(){
		if(obstaclesPoolManager.bottom_2_Pool.size() >= 1 && obstaclesPoolManager.bottom_3_Pool.size() >= 1)
		{
			generateBottomObstacle(2, nextObstaclePosition+45f);
			generateBottomObstacle(3, -1);
		}
	}
	
	private void generateSmallRightBigPyramid(){
		if(obstaclesPoolManager.bottom_2_Pool.size() >= 1 && obstaclesPoolManager.bottom_3_Pool.size() >= 1)
		{
			generateBottomObstacle(3, nextObstaclePosition+45f);
			generateBottomObstacle(2, -1);
		}
	}
	
	private void generateRightVeryBigPyramid(){
		if(obstaclesPoolManager.bottom_1_Pool.size() >= 1 && obstaclesPoolManager.bottom_2_Pool.size() >= 1 && obstaclesPoolManager.bottom_3_Pool.size() >= 1 && obstaclesPoolManager.bottom_4_Pool.size() >= 1)
		{
			generateBottomObstacle(1, nextObstaclePosition+45f);
			generateBottomObstacle(2, nextObstaclePosition+45f);
			generateBottomObstacle(3, nextObstaclePosition+45f);
			generateBottomObstacle(4, -1);
		}
	}
	
	private void generateLeftBigPyramid(){
		if(obstaclesPoolManager.bottom_1_Pool.size() >= 1 && obstaclesPoolManager.bottom_2_Pool.size() >= 1 && obstaclesPoolManager.bottom_3_Pool.size() >= 1)
		{
			generateBottomObstacle(3, nextObstaclePosition+45f);
			generateBottomObstacle(2, nextObstaclePosition+45f);
			generateBottomObstacle(1, -1);
		}
	}
	
	private void generateMuchJumpingSequence(float distance){
		if(obstaclesPoolManager.bottom_1_Pool.size() >= 3 && obstaclesPoolManager.bottom_2_Pool.size() >= 2 && obstaclesPoolManager.bottom_3_Pool.size() >= 1 && obstaclesPoolManager.upper_1_Pool.size() >= 1)
		{
			int t_offset = getVelocityOffset();
			generateBottomObstacle(1, nextObstaclePosition + distance + t_offset - 1f);
			
			generateBottomObstacle(1, nextObstaclePosition + 45f);
			generateBottomObstacle(2, nextObstaclePosition + distance + t_offset);
			
			generateUpperObstacle(1, nextObstaclePosition + distance + t_offset);
			
			generateRightBigPyramid();
		}
	}
	
	private void generateMadWallOpenedSequence(float distance){
		if(obstaclesPoolManager.bottom_1_Pool.size() >= 4 && obstaclesPoolManager.bottom_2_Pool.size() >= 4 && obstaclesPoolManager.bottom_3_Pool.size() >= 2 && obstaclesPoolManager.bottom_4_Pool.size() >= 1 && obstaclesPoolManager.upper_2_Pool.size() >= 2 && obstaclesPoolManager.upper_4_Pool.size() >= 2)
		{
			int t_offset = getVelocityOffset();
			
			generateUpperBottomWall(distance + t_offset);
			
			generateUpperObstacle(4, nextObstaclePosition + distance + t_offset + 50);
			
			generateSmallPyramid(distance);
			
			generateUpperObstacle(4, nextObstaclePosition + distance + t_offset + 50);
			
			generateRightVeryBigPyramid();
			
			generateLeftBigPyramid();
		}
	}
	
	private void generateDoubleSlideWithJump(float distance){
		if(obstaclesPoolManager.bottom_1_Pool.size() >= 2 && obstaclesPoolManager.bottom_2_Pool.size() >= 1 && obstaclesPoolManager.upper_1_Pool.size() >= 2 && obstaclesPoolManager.upper_2_Pool.size() >= 1)
		{
			int t_offset = getVelocityOffset();
	
			generateUpperObstacle(1, nextObstaclePosition + distance + t_offset + 50);
			generateSmallPyramid(distance + t_offset);
			generateUpperObstacle(1, -1);
		}

	}
	
	private void generateEgyptSequence(float distance){
			if(obstaclesPoolManager.bottom_1_Pool.size() >= 5 && obstaclesPoolManager.bottom_2_Pool.size() >= 4 && obstaclesPoolManager.bottom_3_Pool.size() >= 3 && obstaclesPoolManager.bottom_4_Pool.size() >= 2 && obstaclesPoolManager.upper_2_Pool.size() >= 1)
			{
				int t_offset = getVelocityOffset();
				
				generateSmallPyramid(distance + t_offset);
				generateRightVeryBigPyramid();
				generateLeftBigPyramid();
				generateRightVeryBigPyramid();
			}
		}

	private void generateJumpThenSlideSequence(float distance){
		if(obstaclesPoolManager.bottom_1_Pool.size() >= 4 && obstaclesPoolManager.upper_1_Pool.size() >= 3)
		{
			int t_offset = getVelocityOffset();
			
			generateBottomObstacle(1, nextObstaclePosition + t_offset + distance);
			generateUpperObstacle(1, nextObstaclePosition + t_offset + distance + 50);
			generateBottomObstacle(1, nextObstaclePosition + t_offset + distance);
			generateUpperObstacle(1, nextObstaclePosition + t_offset + distance + 50);
			generateBottomObstacle(1, nextObstaclePosition + t_offset + distance);
			generateUpperObstacle(1, nextObstaclePosition + t_offset + distance + 50);
			generateBottomObstacle(1, -1);
		}
	}
	
private void generateWhatTheSmackSequence(float distance){
		if(obstaclesPoolManager.bottom_1_Pool.size() >= 3 && obstaclesPoolManager.bottom_2_Pool.size() >= 3 && obstaclesPoolManager.bottom_3_Pool.size() >= 2 && obstaclesPoolManager.upper_1_Pool.size() >= 2 && obstaclesPoolManager.upper_2_Pool.size() >= 2 && obstaclesPoolManager.upper_4_Pool.size() >= 1)
		{
			int t_offset = getVelocityOffset();
			
			generateBottomObstacle(3, nextObstaclePosition + distance + t_offset + 20);
			generateUpperBottomWall(distance + t_offset);
			generateUpperObstacle(1, nextObstaclePosition + distance + t_offset + 50);
			generateSmallPyramid(distance + t_offset);
			generateUpperObstacle(1, nextObstaclePosition + distance + t_offset + 50);
			generateRightBigPyramid();
			generateUpperObstacle(4, -1);
		}
	}

	private void generateLongJumpThenSlideSequence(float distance){
		if(obstaclesPoolManager.upper_4_Pool.size() >= 1 && obstaclesPoolManager.bottom_2_Pool.size() >= 2 && obstaclesPoolManager.bottom_3_Pool.size() >= 3)
		{
			int t_offset = getVelocityOffset();
			
			generateBottomObstacle(3, nextObstaclePosition + 45);
			generateBottomObstacle(2, nextObstaclePosition + 45);
			generateBottomObstacle(3, nextObstaclePosition + 45);
			generateBottomObstacle(2, nextObstaclePosition + 45);
			generateBottomObstacle(3, nextObstaclePosition + t_offset + distance);
			generateUpperObstacle(4, -1);
		}
	}
	
	private void generateBallUpper(int ballOffset){
		if(!obstaclesPoolManager.ballUpperPool.isEmpty()){
			BallUpper ball = obstaclesPoolManager.ballUpperPool.pop();
			ball.setTransformX((nextObstaclePosition + ballOffset)/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
			nextObstaclePosition = calculateObstaclePosition() + ballOffset;
			usedObstacles.add(ball);
		}
		else System.out.println("POOL zabrak這 ball upper");
	}
	
	private void generateBallBottom(int ballOffset){
		if(!obstaclesPoolManager.ballBottomPool.isEmpty()){
			BallBottom ball = obstaclesPoolManager.ballBottomPool.pop();
			ball.setTransformX((nextObstaclePosition + ballOffset)/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
			nextObstaclePosition = calculateObstaclePosition() + ballOffset;
			usedObstacles.add(ball);
		}
		else System.out.println("POOL zabrak這 ball bottom");
	}
	
	private void generateGroundSegment(){
		GroundSegment ground = obstaclesPoolManager.groundSegmentPool.pop();
		ground.getSprite().setX(nextGroundPosition);
		usedObstacles.add(ground);
		nextGroundPosition += 800-1;
	}

	//Others
	
	private void generateLoongPyramid(float distance){
		if(obstaclesPoolManager.bottom_1_Pool.size() >= 3 && obstaclesPoolManager.bottom_2_Pool.size() >= 3 && obstaclesPoolManager.upper_2_Pool.size() >= 4)
		{
			generateBottomObstacle(1, nextObstaclePosition+45);
			generateUpperObstacle(2, nextObstaclePosition);
			generateBottomObstacle(2, nextObstaclePosition+45);
			generateUpperObstacle(2, nextObstaclePosition);
			generateBottomObstacle(1, nextObstaclePosition+45);
			generateUpperObstacle(2, nextObstaclePosition);
			generateBottomObstacle(2, nextObstaclePosition+45);
			generateUpperObstacle(2, nextObstaclePosition);
			generateBottomObstacle(1, nextObstaclePosition+45);
			if(distance > 0) generateBottomObstacle(2, nextObstaclePosition + distance);
			else             generateBottomObstacle(2, -1);  
		}
	}	

	
	private void releaseUselessObstacles()
	{
		for(int u = 0; u < usedObstacles.size(); u++) //tu byl problem z concurrency - uzywalismy foreach z iteratorem i on robil problemy UWAGA NA TO MOZE BYC DZIURAWE
		{                                             //nawet nie probowac synchronizowac :P probowalem synchronizowac metody/bloki ponad godzine i lipa a tak dziala
			Obstacle obstacle = usedObstacles.get(u);
			
			if(!((String)obstacle.getSprite().getUserData()).contains("ball") && !((String)obstacle.getSprite().getUserData()).equals("ground") && obstacle.getSprite().getX() < (player.getX() - 300)) //- 10 zeby znikaly juz poza ekranem
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
			if(obstacle.getSprite().getUserData().equals("ballUpper") && ((BallUpper)obstacle).getAnchorPositionX() < (player.getX() -100)){
				usedObstacles.remove(obstacle);
				System.out.println("POOL usuwam uipper");
				obstaclesPoolManager.ballUpperPool.push((BallUpper)obstacle);
			}
			if(obstacle.getSprite().getUserData().equals("ballBottom") && ((BallBottom)obstacle).getAnchorPositionX() < (player.getX() -100)){
				usedObstacles.remove(obstacle);
				System.out.println("POOL usuwam bottom");
				obstaclesPoolManager.ballBottomPool.push((BallBottom)obstacle);
			}
			if(obstacle.getSprite().getUserData().equals("ground") && obstacle.getSprite().getX() < player.getX() - 800-1){
				usedObstacles.remove(obstacle);
				System.out.println("GROUND usuwam");
				obstaclesPoolManager.groundSegmentPool.push((GroundSegment)obstacle);
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
