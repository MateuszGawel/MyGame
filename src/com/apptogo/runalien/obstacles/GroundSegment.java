package com.apptogo.runalien.obstacles;

import org.andengine.entity.Entity;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;

import com.apptogo.runalien.ResourcesManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class GroundSegment extends Obstacle{
	
	private Sprite sprite;
	private Body body;
	
	public GroundSegment(PhysicsWorld physicsWorld, Entity backgroundLayer){
		sprite = new Sprite(-200, 232, ResourcesManager.getInstance().ground_region, ResourcesManager.getInstance().vbom);
		sprite.setUserData("ground");
		sprite.setCullingEnabled(true);
		ObstaclesPoolManager.getInstance().groundSpriteGroup.attachChild(sprite);
	}
	
	@Override
	public Sprite getSprite()
	{
		return sprite;
	}
	
	
	@Override
	public Body getBody()
	{
		return null;
	}
	@Override
	public void resetPosition() {
		sprite.setPosition(-200, 232);
		
	}

}
