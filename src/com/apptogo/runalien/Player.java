package com.apptogo.runalien;

import java.util.Random;

import org.andengine.audio.music.Music;
import org.andengine.audio.sound.Sound;
import org.andengine.engine.camera.BoundCamera;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import com.apptogo.runalien.utils.GoogleBaseGameActivity;
import android.content.Context;
import android.os.Vibrator;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.google.android.gms.games.Games;

public abstract class Player extends AnimatedSprite {

	private Body body;
	private boolean canRun = false;
	private boolean jumping = false;
	private boolean alive = true;
	private boolean sliding = false;
	private boolean doubleJumped = false;
	private boolean chargingDown = false;
	public Music runSound;
	public Music screamSound;
	public Sound jumpSound;
	public Sound landingSound;
	public Sound slideSound;
	public Sound dieSound;
	public Sound doubleJumpSound;
	public Sound chargeDownSound;
	public Sound bellHit;
	public Sound fallDownSound;
	private boolean flag = true;
	private boolean flag2 = true;
	public float runningSpeed = 13;  //13 16 19 22 25 27
	private float nextSpeedUp = 50;
	private float cameraShiftY = -150;
	private float cameraShiftX = 200;
	private boolean boundsFlag = true;
	private boolean canSpeedUp = true;
	private boolean slideAfterLanding = false;
	public Random generator = new Random();
	Vibrator vibrator;
	public Sprite playerCover;
	private IEntityModifier playerCoverModifier;
	private boolean standingUp = false;
	private boolean vibrate = ResourcesManager.getInstance().activity.preferences.getBoolean(ResourcesManager.getInstance().activity.VIBRATIONS_LABEL, true);
	
	public Player(float pX, float pY, VertexBufferObjectManager vbo, BoundCamera camera, PhysicsWorld physicsWorld, boolean playS) {
		super(pX, pY, ResourcesManager.getInstance().player_region, vbo);
		createPhysics(camera, physicsWorld);
		camera.setChaseEntity(this);
		
		bellHit = ResourcesManager.getInstance().bellHit;
		runSound = ResourcesManager.getInstance().runSound;
		screamSound = ResourcesManager.getInstance().screamSound;
		jumpSound = ResourcesManager.getInstance().jumpSound;
		landingSound = ResourcesManager.getInstance().landingSound;
		slideSound = ResourcesManager.getInstance().slideSound;
		dieSound = ResourcesManager.getInstance().dieSound;
		doubleJumpSound = ResourcesManager.getInstance().doubleJumpSound;
		chargeDownSound = ResourcesManager.getInstance().chargeDownSound;
		fallDownSound = ResourcesManager.getInstance().fallDownSound;
		
		setSoundVolume(playS);
		
		breathe();
		
		playerCover = new Sprite(getX() - (getWidth() - 50), getY() - (getHeight() + 40), ResourcesManager.getInstance().playerCover_region, ResourcesManager.getInstance().vbom);
		this.attachChild(playerCover);
		playerCover.setVisible(false);
		vibrator = (Vibrator)ResourcesManager.getInstance().activity.getSystemService(Context.VIBRATOR_SERVICE);
		
	}

	private void setSoundVolume(boolean play)
	{
		if(play)
		{
			landingSound.setVolume(0.5f);
			dieSound.setVolume(0.6f);
			doubleJumpSound.setVolume(0.5f);
		}
		else
		{
			bellHit.setVolume(0.0f);
			runSound.setVolume(0.0f);
			screamSound.setVolume(0.0f);
			jumpSound.setVolume(0.0f);
			landingSound.setVolume(0.0f);
			slideSound.setVolume(0.0f);
			dieSound.setVolume(0.0f);
			doubleJumpSound.setVolume(0.0f);
			chargeDownSound.setVolume(0.0f);
			fallDownSound.setVolume(0.0f);
		}
	}
	
	public abstract void onDie();

	private void createPhysics(final BoundCamera camera, PhysicsWorld physicsWorld) {
		body = PhysicsFactory.createBoxBody(physicsWorld, 0, 195, 40, 90, BodyType.DynamicBody, PhysicsFactory.createFixtureDef(10.0f, 0, 0.3f));
		body.setUserData("player");
		body.setFixedRotation(true);
		
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false) {
			@Override
			public void onUpdate(float pSecondsElapsed) {
				super.onUpdate(pSecondsElapsed);

				camera.setBounds(-850, 500, 100000000, -250);
		        camera.setBoundsEnabled(boundsFlag);
				camera.onUpdate(0.1f); 
				camera.setCenter(camera.getCenterX()+cameraShiftX, camera.getCenterY()-cameraShiftY);
				if (canRun) {
					body.setLinearVelocity(new Vector2(runningSpeed, body.getLinearVelocity().y));
				}
				else if(!alive && !canRun && flag){
					body.setLinearVelocity(new Vector2(runningSpeed, body.getLinearVelocity().y));
					flag = false;
				}
				if(canSpeedUp && body.getPosition().x > nextSpeedUp){
					System.out.println("PRZYSPIESZAM " + body.getLinearVelocity().x);
					nextSpeedUp = body.getPosition().x + 100;
					runningSpeed++;
					
					if(runningSpeed >= 24) canSpeedUp = false; //bo szybciej to padaka
				}
			}
		});
	}

	public boolean isRunning(){
		return canRun;
	}
	
	public void setRunning() {
		canRun = true;
		runSound.play();
		runSound.setLooping(true);
		screamSound.play();
		screamSound.setLooping(true);
		
		long[] PLAYER_ANIMATE = new long[13];
		for(int i=0; i<13; i++)
			PLAYER_ANIMATE[i]=10;
		animate(PLAYER_ANIMATE, 119, 131, false, new IAnimationListener() {

            public void onAnimationStarted(AnimatedSprite pAnimatedSprite, int pInitialLoopCount) {
            	
            }
            public void onAnimationLoopFinished(AnimatedSprite pAnimatedSprite, int pRemainingLoopCount, int pInitialLoopCount) {

            }
            public void onAnimationFrameChanged( AnimatedSprite pAnimatedSprite, int pOldFrameIndex, int pNewFrameIndex) {

            }
            public void onAnimationFinished(AnimatedSprite pAnimatedSprite) {
        		long[] PLAYER_ANIMATE = new long[31];
        		for(int i=0; i<31; i++)
        			PLAYER_ANIMATE[i]=14;
        		animate(PLAYER_ANIMATE, 75, 105, true);
            }
		});
	}
	
	public void run(){
		if(alive){
			System.out.println("PLAYER biegne");
			runSound.resume();
			//screamSound.resume();
			long[] PLAYER_ANIMATE = new long[31];
			for(int i=0; i<31; i++)
				PLAYER_ANIMATE[i]=14;
			animate(PLAYER_ANIMATE, 75, 105, true);
		}
	}
	
	public void breathe(){
		long[] PLAYER_ANIMATE = new long[18];
		for(int i=0; i<18; i++)
			PLAYER_ANIMATE[i]=100;
		animate(PLAYER_ANIMATE, 6, 23, 5, new IAnimationListener() {

            public void onAnimationStarted(AnimatedSprite pAnimatedSprite, int pInitialLoopCount) {
            	
            }
            public void onAnimationLoopFinished(AnimatedSprite pAnimatedSprite, int pRemainingLoopCount, int pInitialLoopCount) {

            }
            public void onAnimationFrameChanged(AnimatedSprite pAnimatedSprite, int pOldFrameIndex, int pNewFrameIndex) {

            }
            public void onAnimationFinished(AnimatedSprite pAnimatedSprite) {
                blink();
	        }
	    });
	}
	public void blink(){
		long[] PLAYER_ANIMATE = {50, 1000, 50, 50, 50, 50};
		animate(PLAYER_ANIMATE, 0, 5, false, new IAnimationListener() {
			
            public void onAnimationStarted(AnimatedSprite pAnimatedSprite, int pInitialLoopCount) {

            }
            public void onAnimationLoopFinished(AnimatedSprite pAnimatedSprite, int pRemainingLoopCount, int pInitialLoopCount) {

            }
            public void onAnimationFrameChanged(AnimatedSprite pAnimatedSprite, int pOldFrameIndex, int pNewFrameIndex) {
            	
            }
            public void onAnimationFinished(AnimatedSprite pAnimatedSprite) {
                breathe();
	        }
	    });
	}

	public void jump() {
	    if (jumping || !alive) 
	    {
	        return; 
	    }
	    else if(sliding){
	    	standUp();
	    	sliding = false;
	    }
    	runSound.pause();
    	screamSound.pause();
    	jumpSound.play();
	    jumping = true;
	    long[] PLAYER_ANIMATE = new long[13];
		for(int i=0; i<13; i++)
			PLAYER_ANIMATE[i]=20;
		PLAYER_ANIMATE[12]=5000;
		animate(PLAYER_ANIMATE, 49, 61, false);
		body.setLinearVelocity(new Vector2(body.getLinearVelocity().x, -30));
		if(((GoogleBaseGameActivity)ResourcesManager.getInstance().activity).isSignedIn()){
			Games.Achievements.increment(ResourcesManager.getInstance().activity.getGoogleApiClient(), "CgkIpZ2MjMkXEAIQNA", 1);
		}
	}
	
	public void doubleJump(){
		if(!alive || sliding || !jumping || doubleJumped || chargingDown)
			return;
    	runSound.pause();
    	
    	if(  generator.nextInt(10)%2 == 0 ) 
		{
    		screamSound.pause();
    		doubleJumpSound.play();
		}
    	else{
    		screamSound.pause();
    		jumpSound.play();
    	}
    	
		body.setLinearVelocity(new Vector2(body.getLinearVelocity().x, -25));
		doubleJumped = true;
	}
	
	public void chargeDown(){
		System.out.println("PLAYER wysokosc: " + this.getY() + " sila " + this.getBody().getLinearVelocity().y);
		if(!alive || sliding || !jumping)
			return;
	    else if(jumping && this.getY() > 100 && this.getBody().getLinearVelocity().y > 0){
	    		slideAfterLanding = true;
	    		System.out.println("PLAYER ustawiam flage");
	    }
	    else{
	    	runSound.pause();
	    	screamSound.pause();
		    //chargeDownSound.play();
			body.setLinearVelocity(new Vector2(body.getLinearVelocity().x-15, 35));
			chargingDown = true;
	    }
	}
	
	public void land(){
		if(alive){
			if(chargingDown){
				chargeDownSound.play();
			}
			landingSound.play();
			screamSound.resume();
			long[] PLAYER_ANIMATE = new long[13];
			for(int i=0; i<13; i++)
				PLAYER_ANIMATE[i]=20;
			animate(PLAYER_ANIMATE, 62, 74, false, new IAnimationListener() {
				
	            public void onAnimationStarted(AnimatedSprite pAnimatedSprite, int pInitialLoopCount) {
	
	            }
	            public void onAnimationLoopFinished(AnimatedSprite pAnimatedSprite, int pRemainingLoopCount, int pInitialLoopCount) {
	
	            }
	            public void onAnimationFrameChanged(AnimatedSprite pAnimatedSprite, int pOldFrameIndex, int pNewFrameIndex) {
	            	
	            }
	            public void onAnimationFinished(AnimatedSprite pAnimatedSprite) {
	        	    run();
		        }
		    });
			jumping = false;
			doubleJumped = false;
			chargingDown = false;
		}
	}
	
	public void slide() {
	    if (jumping || !alive) 
	    {
	        return; 
	    }
	    if(!sliding){
	    	standingUp = false;
		    playerCover.setRotationCenterY(playerCover.getY() + 50);
		    playerCover.registerEntityModifier(new RotationModifier(0.2f, 0, -90));
		    
		    System.out.println("PLAYER wlasnie slizgam");
	    	runSound.pause();
	    	screamSound.pause();
	    	slideSound.play();
		    sliding = true;
		    long[] PLAYER_ANIMATE = new long[7];
			for(int i=0; i<7; i++)
				PLAYER_ANIMATE[i]=20;
			PLAYER_ANIMATE[6]=500;
			animate(PLAYER_ANIMATE, 106, 112, false, new IAnimationListener() {
				
	            public void onAnimationStarted(AnimatedSprite pAnimatedSprite, int pInitialLoopCount) {
	            		
	            }
	            public void onAnimationLoopFinished(AnimatedSprite pAnimatedSprite, int pRemainingLoopCount, int pInitialLoopCount) {
	
	            }
	            public void onAnimationFrameChanged(AnimatedSprite pAnimatedSprite, int pOldFrameIndex, int pNewFrameIndex) {
	            	
	            }
	            public void onAnimationFinished(AnimatedSprite pAnimatedSprite) {
	                standUp();
		        }
		    });
			if(((GoogleBaseGameActivity)ResourcesManager.getInstance().activity).isSignedIn()){
				Games.Achievements.increment(ResourcesManager.getInstance().activity.getGoogleApiClient(), "CgkIpZ2MjMkXEAIQNQ", 1);
			}
	    }
	    else{
	    	playerCover.unregisterEntityModifier(playerCoverModifier);
			playerCover.registerEntityModifier(new RotationModifier(0.2f, playerCover.getRotation(), -90));
	    	//playerCover.setRotation(-90);
	    	runSound.pause();
	    	screamSound.pause();
			if(standingUp) slideSound.play();
			standingUp = false;
		    sliding = true;
		    long[] PLAYER_ANIMATE = new long[2];
		    PLAYER_ANIMATE[0]=500;
			PLAYER_ANIMATE[1]=1;
			animate(PLAYER_ANIMATE, 111, 112, false, new IAnimationListener() {
				
	            public void onAnimationStarted(AnimatedSprite pAnimatedSprite, int pInitialLoopCount) {
	            		
	            }
	            public void onAnimationLoopFinished(AnimatedSprite pAnimatedSprite, int pRemainingLoopCount, int pInitialLoopCount) {
	
	            }
	            public void onAnimationFrameChanged(AnimatedSprite pAnimatedSprite, int pOldFrameIndex, int pNewFrameIndex) {
	            	
	            }
	            public void onAnimationFinished(AnimatedSprite pAnimatedSprite) {
	                standUp();
		        }
		    });
	    }
	}
	
	public void standUp() {
		playerCover.setRotationCenterY(playerCover.getY() + 50);
		playerCoverModifier = new RotationModifier(0.2f, -90, 0);
		playerCover.registerEntityModifier(playerCoverModifier);
		standingUp = true;
    	
	    screamSound.resume();
	    System.out.println("PLAYER wstaje");
	    long[] PLAYER_ANIMATE = new long[6];
		for(int i=0; i<6; i++)
			PLAYER_ANIMATE[i]=20;
		animate(PLAYER_ANIMATE, 113, 118, false, new IAnimationListener() {
			
            public void onAnimationStarted(AnimatedSprite pAnimatedSprite, int pInitialLoopCount) {
            	
            }
            public void onAnimationLoopFinished(AnimatedSprite pAnimatedSprite, int pRemainingLoopCount, int pInitialLoopCount) {

            }
            public void onAnimationFrameChanged(AnimatedSprite pAnimatedSprite, int pOldFrameIndex, int pNewFrameIndex) {
            	
            }
            public void onAnimationFinished(AnimatedSprite pAnimatedSprite) {
            	run();
            	standingUp = false;
            	sliding = false;
	        }
	    });
	}
	
	public void dieBottom(){
		if(alive){
			playerCover.setRotationCenterY(playerCover.getY() + 50);
		    playerCover.registerEntityModifier(new RotationModifier(0.2f, 0, 90));
		    
			alive = false;
			canRun = false;
			screamSound.pause();
			runSound.pause();
			dieSound.play();
			if(vibrate) vibrator.vibrate(300);
			long[] PLAYER_ANIMATE = new long[13];
			for(int i=0; i<13; i++)
				PLAYER_ANIMATE[i]=20;
			animate(PLAYER_ANIMATE, 24, 36, false, new IAnimationListener() {
				
	            public void onAnimationStarted(AnimatedSprite pAnimatedSprite, int pInitialLoopCount) {
	            	
	            }
	            public void onAnimationLoopFinished(AnimatedSprite pAnimatedSprite, int pRemainingLoopCount, int pInitialLoopCount) {

	            }
	            public void onAnimationFrameChanged(AnimatedSprite pAnimatedSprite, int pOldFrameIndex, int pNewFrameIndex) {
	            	
	            }
	            public void onAnimationFinished(AnimatedSprite pAnimatedSprite) {
	    			onDie();
		        }
		    });
		}
	}
	
	public void dieTop(boolean itIsBell){
		if(alive){
			playerCover.setRotationCenterY(playerCover.getY() + 50);
		    playerCover.registerEntityModifier(new RotationModifier(0.2f, 0, -90));
			
			alive = false;
			canRun = false;
			screamSound.pause();
			runSound.pause();
			dieSound.play();
			if(itIsBell) bellHit.play();
			long[] PLAYER_ANIMATE = new long[13];
			if(vibrate) vibrator.vibrate(300);
			for(int i=0; i<13; i++)
				PLAYER_ANIMATE[i]=20;
			
			animate(PLAYER_ANIMATE, 36, 48, false, new IAnimationListener() {
				
	            public void onAnimationStarted(AnimatedSprite pAnimatedSprite, int pInitialLoopCount) {
	            	
	            }
	            public void onAnimationLoopFinished(AnimatedSprite pAnimatedSprite, int pRemainingLoopCount, int pInitialLoopCount) {

	            }
	            public void onAnimationFrameChanged(AnimatedSprite pAnimatedSprite, int pOldFrameIndex, int pNewFrameIndex) {
	            	
	            }
	            public void onAnimationFinished(AnimatedSprite pAnimatedSprite) {
	    			onDie();
		        }
		    });
		}
	}
	
	public boolean isJumping() {
		return jumping;
	}

	public void setJumping(boolean jumping) {
		this.jumping = jumping;
	}

	public boolean isAlive() {
		return alive;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}

	public Body getBody() {
		return body;
	}

	public void setBody(Body body) {
		this.body = body;
	}

	public boolean isSliding() {
		return sliding;
	}

	public void setSliding(boolean sliding) {
		this.sliding = sliding;
	}

	public float getRunningSpeed() {
		return runningSpeed;
	}

	public void setRunningSpeed(float runningSpeed) {
		this.runningSpeed = runningSpeed;
	}

	public float getCameraShiftY() {
		return cameraShiftY;
	}

	public void setCameraShiftY(float cameraShiftY) {
		this.cameraShiftY = cameraShiftY;
	}

	public float getCameraShiftX() {
		return cameraShiftX;
	}

	public void setCameraShiftX(float cameraShiftX) {
		this.cameraShiftX = cameraShiftX;
	}

	public boolean isBoundsFlag() {
		return boundsFlag;
	}

	public void setBoundsFlag(boolean boundsFlag) {
		this.boundsFlag = boundsFlag;
	}

	public boolean isChargingDown() {
		return chargingDown;
	}

	public void setChargingDown(boolean chargingDown) {
		this.chargingDown = chargingDown;
	}

	public boolean isDoubleJumped() {
		return doubleJumped;
	}

	public void setDoubleJumped(boolean doubleJumped) {
		this.doubleJumped = doubleJumped;
	}

	public boolean isCanSpeedUp() {
		return canSpeedUp;
	}

	public void setCanSpeedUp(boolean canSpeedUp) {
		this.canSpeedUp = canSpeedUp;
	}

	public boolean isSlideAfterLanding() {
		return slideAfterLanding;
	}

	public void setSlideAfterLanding(boolean slideAfterLanding) {
		this.slideAfterLanding = slideAfterLanding;
	}

	
	
}
