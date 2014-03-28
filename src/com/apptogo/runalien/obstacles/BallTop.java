package com.apptogo.runalien.obstacles;

import org.andengine.entity.Entity;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;

import com.apptogo.runalien.ResourcesManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;

public class BallTop extends Obstacle{
	
	private Sprite sprite;
	private Body body;
	private Sprite anchorSprite;
	private Body anchorBody;
	Line connectionLine;
	int lineLength = 250;
	
	public BallTop(PhysicsWorld physicsWorld, Entity foregroundLayer){
		
		anchorSprite = new Sprite(-1000, -120, ResourcesManager.getInstance().crate_region, ResourcesManager.getInstance().vbom); 
		//narazie pozycja x jest zero ale ostateznie musi byc minus wpizdu zeby na poczatku ich nie bylo widac
		anchorSprite.setUserData("ballAnchor");
		anchorBody = PhysicsFactory.createBoxBody(physicsWorld, anchorSprite, BodyType.StaticBody, PhysicsFactory.createFixtureDef(10.0f, 0, 0));
		anchorBody.setUserData("ballAnchor");
		foregroundLayer.attachChild(anchorSprite);
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(anchorSprite, anchorBody, true, false) {
			@Override
			public void onUpdate(float pSecondsElapsed) {
				super.onUpdate(pSecondsElapsed);
				final Vector2 movingBodyWorldCenter = body.getWorldCenter();
				connectionLine.setPosition(connectionLine.getX1(), connectionLine.getY1(), movingBodyWorldCenter.x * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, movingBodyWorldCenter.y * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
			}
		});
		
		
		connectionLine = new Line(anchorSprite.getX()+10, -115, anchorSprite.getX()+10, -115, ResourcesManager.getInstance().vbom);
		sprite = new Sprite(anchorSprite.getX()+lineLength, -115, ResourcesManager.getInstance().ball_region, ResourcesManager.getInstance().vbom);
		//narazie pozycja x jest zero ale ostateznie musi byc minus wpizdu zeby na poczatku ich nie bylo widac
		sprite.setUserData("upper");
		body = PhysicsFactory.createBoxBody(physicsWorld, sprite, BodyType.DynamicBody, PhysicsFactory.createFixtureDef(200.5f, 0.2f, 0.5f));
		body.setUserData("upper");
		foregroundLayer.attachChild(sprite);
		foregroundLayer.attachChild(connectionLine);
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(sprite, body, true, false) {
			@Override
			public void onUpdate(float pSecondsElapsed) {
				super.onUpdate(pSecondsElapsed);
				final Vector2 movingBodyWorldCenter = body.getWorldCenter();
				connectionLine.setPosition(anchorSprite.getX()+10, -115, movingBodyWorldCenter.x * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, movingBodyWorldCenter.y * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
			}
		});
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(sprite, body, true, true));


		final RevoluteJointDef revoluteJointDef = new RevoluteJointDef();
		revoluteJointDef.initialize(anchorBody, body, anchorBody.getWorldCenter());
		
		physicsWorld.createJoint(revoluteJointDef);
	}
	
	public void setTransformX(float posX){
		body.setTransform(posX+lineLength/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, -115/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 0);
		anchorBody.setTransform(posX, -120/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 0);
	}
	
	
	
	public int getLineLength() {
		return lineLength;
	}

	public void setLineLength(int lineLength) {
		this.lineLength = lineLength;
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

}
