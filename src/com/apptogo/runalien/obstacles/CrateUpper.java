package com.apptogo.runalien.obstacles;

import org.andengine.entity.Entity;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;

import com.apptogo.runalien.ResourcesManager;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class CrateUpper extends Obstacle{
	
	private Sprite sprite;
	private Body body;
	
	public CrateUpper(PhysicsWorld physicsWorld, Entity foregroundLayer){
		sprite = new Sprite(-1000, 100, ResourcesManager.getInstance().crate_region, ResourcesManager.getInstance().vbom); 
		//narazie pozycja x jest zero ale ostateznie musi byc minus wpizdu zeby na poczatku ich nie bylo widac
		sprite.setUserData("crateUpper");
		sprite.setCullingEnabled(true);
		body = PhysicsFactory.createBoxBody(physicsWorld, sprite, BodyType.StaticBody, PhysicsFactory.createFixtureDef(10.0f, 0, 0));
		body.setUserData("crateUpper");
		foregroundLayer.attachChild(sprite);
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(sprite, body, true, false) {
			@Override
			public void onUpdate(float pSecondsElapsed) {
				super.onUpdate(pSecondsElapsed);
			}
		});
	}
	
	@Override
	public Sprite getSprite()
	{
		return sprite;
	}
	
	@Override
	public Body getBody()
	{
		return body;
	}
	@Override
	public void resetPosition() {
		sprite.setPosition(-1000, 100);
		
	}
}
