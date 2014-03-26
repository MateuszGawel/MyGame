package com.apptogo.runalien;

import org.andengine.audio.music.Music;
import org.andengine.engine.camera.BoundCamera;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.text.Text;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

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
	public Music jumpSound;
	public Music landingSound;
	public Music slideSound;
	public Music dieSound;
	public Music doubleJumpSound;
	public Music bigLandSound;
	public Music chargeDownSound;
	private boolean flag = true;
	private boolean flag2 = true;
	private float runningSpeed = 13;
	private float nextSpeedUp = 300;

	public Player(float pX, float pY, VertexBufferObjectManager vbo, BoundCamera camera, PhysicsWorld physicsWorld) {
		super(pX, pY, ResourcesManager.getInstance().player_region, vbo);
		
		createPhysics(camera, physicsWorld);
		camera.setChaseEntity(this);
		runSound = ResourcesManager.getInstance().runSound;
		screamSound = ResourcesManager.getInstance().screamSound;
		jumpSound = ResourcesManager.getInstance().jumpSound;
		landingSound = ResourcesManager.getInstance().landingSound;
		slideSound = ResourcesManager.getInstance().slideSound;
		dieSound = ResourcesManager.getInstance().dieSound;
		doubleJumpSound = ResourcesManager.getInstance().doubleJumpSound;
		bigLandSound = ResourcesManager.getInstance().bigLandSound;
		chargeDownSound = ResourcesManager.getInstance().chargeDownSound;
		breathe();
	}

	public abstract void onDie();

	private void createPhysics(final BoundCamera camera, PhysicsWorld physicsWorld) {
		body = PhysicsFactory.createBoxBody(physicsWorld, 0, 190, 40, 90, BodyType.DynamicBody, PhysicsFactory.createFixtureDef(10.0f, 0, 0.07f));
		body.setUserData("player");
		body.setFixedRotation(true);
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false) {
			@Override
			public void onUpdate(float pSecondsElapsed) {
				super.onUpdate(pSecondsElapsed);
				camera.onUpdate(0.1f);
				camera.setCenter(camera.getCenterX()+200, camera.getCenterY()-150);
				camera.setBounds(-850, 500, 100000000, -250);
		        camera.setBoundsEnabled(true);
				if (canRun) {
					body.setLinearVelocity(new Vector2(runningSpeed, body.getLinearVelocity().y));
				}
				else if(!alive && !canRun && flag){
					body.setLinearVelocity(new Vector2(runningSpeed, body.getLinearVelocity().y));
					flag = false;
				}
				if(body.getPosition().x > nextSpeedUp){
					nextSpeedUp+=300;
					runningSpeed++;
				}
			}
		});
	}


	public void setRunning() {
		canRun = true;
		runSound.setVolume(0.5f);
		if(!runSound.isPlaying())
			runSound.play();
		runSound.setLooping(true);
		if(!screamSound.isPlaying())
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
			if(!runSound.isPlaying())
				runSound.resume();
			if(!screamSound.isPlaying())
				screamSound.resume();
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
	    if (jumping || sliding || !alive) 
	    {
	        return; 
	    }
	    if(runSound.isPlaying())
	    	runSound.pause();
	    if(screamSound.isPlaying())
	    	screamSound.pause();
	    if(!jumpSound.isPlaying()){
	    	jumpSound.setVolume(0.3f);
	    	jumpSound.play();
	    }
	    jumping = true;
	    long[] PLAYER_ANIMATE = new long[13];
		for(int i=0; i<13; i++)
			PLAYER_ANIMATE[i]=20;
		PLAYER_ANIMATE[12]=5000;
		animate(PLAYER_ANIMATE, 49, 61, false);
		body.setLinearVelocity(new Vector2(body.getLinearVelocity().x, -30));
	}
	
	public void doubleJump(){
		if(!alive || sliding || !jumping || doubleJumped || chargingDown)
			return;
		if(runSound.isPlaying())
	    	runSound.pause();
	    if(screamSound.isPlaying())
	    	screamSound.pause();
	    doubleJumpSound.play();
		body.setLinearVelocity(new Vector2(body.getLinearVelocity().x, -30));
		doubleJumped = true;
	}
	
	public void chargeDown(){
		if(!alive || sliding || !jumping)
			return;
		if(runSound.isPlaying())
	    	runSound.pause();
	    if(screamSound.isPlaying())
	    	screamSound.pause();
	    chargeDownSound.setVolume(0.2f);
	    //chargeDownSound.play();
		body.setLinearVelocity(new Vector2(body.getLinearVelocity().x-15, 35));
		chargingDown = true;
	}
	
	public void land(){
		if(alive){
			if(chargingDown){
				bigLandSound.setVolume(0.5f);
				bigLandSound.play();
			}
			if(!landingSound.isPlaying())
				landingSound.play();
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
	    if (sliding || jumping || !alive) 
	    {
	        return; 
	    }
	    if(runSound.isPlaying())
	    	runSound.pause();
	    if(screamSound.isPlaying())
	    	screamSound.pause();
	    if(!slideSound.isPlaying())
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
	}
	
	public void standUp() {
	    sliding = false;
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
	        }
	    });
	}
	
	public void dieBottom(){
		if(alive){
			alive = false;
			canRun = false;
			if(screamSound.isPlaying())
				screamSound.stop();
			if(runSound.isPlaying())
				runSound.stop();
			if(!dieSound.isPlaying())
				dieSound.play();
			long[] PLAYER_ANIMATE = new long[13];
			for(int i=0; i<13; i++)
				PLAYER_ANIMATE[i]=20;
			animate(PLAYER_ANIMATE, 24, 36, false);
			onDie();
		}
	}
	
	public void dieTop(){
		if(alive){
			alive = false;
			canRun = false;
			if(screamSound.isPlaying())
				screamSound.stop();
			if(runSound.isPlaying())
				runSound.stop();
			if(!dieSound.isPlaying())
				dieSound.play();
			long[] PLAYER_ANIMATE = new long[13];
			for(int i=0; i<13; i++)
				PLAYER_ANIMATE[i]=20;
			animate(PLAYER_ANIMATE, 36, 48, false);
			onDie();
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
	
	
}
