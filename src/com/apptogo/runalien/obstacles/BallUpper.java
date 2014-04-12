package com.apptogo.runalien.obstacles;

import java.util.List;

import org.andengine.entity.Entity;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.util.color.Color;

import com.apptogo.runalien.Player;
import com.apptogo.runalien.ResourcesManager;
import com.apptogo.runalien.SceneManager;
import com.apptogo.runalien.scenes.GameScene;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;

public class BallUpper extends Obstacle{
	
	private Sprite sprite;
	private Body body;
	private Sprite anchorSprite;
	private Body anchorBody;
	Line connectionLine;
	
	int ballY = 110;
	int ballX = -500;
	int anchorY = -140;
	int anchorX = -500;

	public BallUpper(PhysicsWorld physicsWorld, Entity foregroundLayer){
		
		anchorSprite = new Sprite(anchorX, anchorY, ResourcesManager.getInstance().crate_region, ResourcesManager.getInstance().vbom); 
		anchorSprite.setCullingEnabled(true);
		anchorSprite.setUserData("ballUpperAnchor");
		anchorBody = PhysicsFactory.createBoxBody(physicsWorld, anchorSprite, BodyType.StaticBody, PhysicsFactory.createFixtureDef(10.0f, 0, 0));
		anchorBody.setUserData("ballUpperAnchor");

		physicsWorld.registerPhysicsConnector(new PhysicsConnector(anchorSprite, anchorBody, true, false) {
			@Override
			public void onUpdate(float pSecondsElapsed) {
				super.onUpdate(pSecondsElapsed);
				}
		});
		
		
		connectionLine = new Line(anchorX, anchorY, ballX, ballY, ResourcesManager.getInstance().vbom);
		connectionLine.setColor(new Color(70f/255f, 50f/255f, 10f/255f));
		connectionLine.setLineWidth(4.0f);
		connectionLine.setCullingEnabled(false);
		connectionLine.setVisible(true);
		
		sprite = new Sprite(ballX, ballY, ResourcesManager.getInstance().ball_region, ResourcesManager.getInstance().vbom);
		sprite.setCullingEnabled(true);
		sprite.setUserData("ballUpper");
		body = PhysicsFactory.createCircleBody(physicsWorld, sprite, BodyType.DynamicBody, PhysicsFactory.createFixtureDef(200.5f, 0.2f, 0.5f));
		body.setUserData("ballUpper");
		//body.setType(BodyType.StaticBody);

		foregroundLayer.attachChild(connectionLine);
		foregroundLayer.attachChild(anchorSprite);
		foregroundLayer.attachChild(sprite);
		
		
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(sprite, body, true, false) {
			@Override
			public void onUpdate(float pSecondsElapsed) {
				super.onUpdate(pSecondsElapsed);
				final Vector2 ballBodyCenter = body.getWorldCenter();
				final Vector2 anchorBodyCenter = anchorBody.getWorldCenter();
				connectionLine.setPosition(anchorBodyCenter.x * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, anchorBodyCenter.y * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, ballBodyCenter.x * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, ballBodyCenter.y * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
			}
		});


		final RevoluteJointDef revoluteJointDef = new RevoluteJointDef();
		revoluteJointDef.initialize(anchorBody, body, anchorBody.getWorldCenter());
		physicsWorld.createJoint(revoluteJointDef);
		
		
		List<Fixture> fixtureList = body.getFixtureList();
		for(Fixture fixture : fixtureList){
				fixture.setSensor(true);
	    }
		
	}
	
	public void setTransformX(float xOffset){
		body.setTransform(xOffset + 10f, ballY/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 0);
		anchorBody.setTransform(xOffset, (anchorY+20)/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 0);
	}
	
	public float getAnchorPositionX(){
		return anchorBody.getPosition().x*PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		
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
