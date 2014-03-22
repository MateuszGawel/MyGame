package com.apptogo.runalien.obstacles;

import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;

import com.apptogo.runalien.ResourcesManager;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class CrateBottom {
	
	private Sprite sprite;
	private Body body;
	
	public CrateBottom(PhysicsWorld physicsWorld){
		sprite = new Sprite(0, 100, ResourcesManager.getInstance().crate_region, ResourcesManager.getInstance().vbom);
		sprite.setUserData("crateBottom");
		body = PhysicsFactory.createBoxBody(physicsWorld, sprite, BodyType.StaticBody, PhysicsFactory.createFixtureDef(10.0f, 0, 0));
		body.setUserData("crateBottom");
	}

}
