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

public class Bottom_moleHill extends Obstacle{
	private Sprite sprite;
	private Body body;
	
	public Bottom_moleHill(PhysicsWorld physicsWorld, Entity foregroundLayer){
		sprite = new Sprite(-1000, 110, ResourcesManager.getInstance().moleHill_region, ResourcesManager.getInstance().vbom);
		//narazie pozycja x jest zero ale ostateznie musi byc minus wpizdu zeby na poczatku ich nie bylo widac
		sprite.setUserData("bottom_moleHill");
		sprite.setCullingEnabled(true);
		while(true){
			if(ObstaclesPoolManager.getInstance()!=null && ObstaclesPoolManager.getInstance().spriteGroup!=null){
				ObstaclesPoolManager.getInstance().groundSpriteGroup.attachChild(sprite);
				break;
			}
		}
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
		sprite.setPosition(-1000, 110);
		
	}
}
