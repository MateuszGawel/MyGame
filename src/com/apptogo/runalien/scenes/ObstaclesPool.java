package com.apptogo.runalien.scenes;

import java.util.ArrayList;
import java.util.List;

import org.andengine.entity.Entity;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;

import com.apptogo.runalien.Player;
import com.apptogo.runalien.ResourcesManager;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class ObstaclesPool
{
	public List<Body> bodiesList;
	public List<SpriteMeta> spritesList;
	private List<String> upperObstaclesList;
	private PhysicsWorld physicsWorld;
	private Player player;
	private Entity foregroundLayer;
	private ResourcesManager resourcesManager;
	
	private float windowWidth;
	private int characterIndent;
	private int lastObstacleX;
	
	public ObstaclesPool(PhysicsWorld pw, Player p, Entity f, float ww)
	{
		physicsWorld = pw;
		player = p;
		foregroundLayer = f;
		resourcesManager = ResourcesManager.getInstance();
		windowWidth = ww;
		
		bodiesList = new ArrayList<Body>();
		spritesList = new ArrayList<SpriteMeta>();
		upperObstaclesList = new ArrayList<String>();
		lastObstacleX = 20;
	}
	
	public void setWindowWidth(int w)
	{
		windowWidth = w;
	}
	
	public void setcharacterIndent(int c)
	{
		characterIndent = c;
	}
	
	public int CalculateObstaclePosition()
	{
		return (int)(player.getX() + 2000);
	}
	
	public void AddSprite(SpriteMeta spriteMeta, String spriteName)
	{
		spriteMeta.setUserData(spriteName);
		spritesList.add(spriteMeta);
	}
	
	private Sprite GetSprite(String spriteName, float x, float y)
	{
		for(SpriteMeta s: spritesList)
		{
			if( spriteName.equals( (String)s.getUserData() ) )
			{
				return s.CreateSprite(x,y);
			}
		}
		
		return null; //tu ofc powinien byc wyjatek ale to potem ;P
	}
	
	private boolean IsObstacleUpper(String obstacleTypeName)
	{
		return upperObstaclesList.contains(obstacleTypeName);
	}
	
	public void CreateObstacles(int amount, String obstacleTypeName, String spriteName, final boolean isUpper)
	{
		for(int i=0; i<amount; i++)
		{			
			Sprite sprite = GetSprite(spriteName, -1000, 0);
			
			final Body body = PhysicsFactory.createBoxBody(physicsWorld, sprite, BodyType.StaticBody, PhysicsFactory.createFixtureDef(10.0f, 0, 0));
			//Body body = PhysicsFactory.createBoxBody(physicsWorld, new Sprite(-100, 190, ResourcesManager.getInstance().obstacle_bottom_region, resourcesManager.vbom), BodyType.StaticBody, PhysicsFactory.createFixtureDef(10.0f, 0, 0));
			
			//--
			System.out.println("LOG:!!!!!!!!!!! pozycjaX="+body.getPosition().x+" pozycjaY="+body.getPosition().y);

			foregroundLayer.attachChild( sprite ); //attaching sprite
			
			physicsWorld.registerPhysicsConnector(new PhysicsConnector(sprite, body, true, false) 
			{
				@Override
				public void onUpdate(float pSecondsElapsed) 
				{
					super.onUpdate(pSecondsElapsed);
					//for(Body bl: bodiesList)
					//{
						for (int i = 0; i < body.getFixtureList().size(); i++) 
						{
							if (player.isSliding() && player.isAlive() && isUpper )
								body.getFixtureList().get(i).setSensor(true); 
							else if (player.isAlive())
								body.getFixtureList().get(i).setSensor(false);
						}
					//}
				}
			});
			
			
			body.setUserData(obstacleTypeName);
			bodiesList.add(body);
		}
		
		if(isUpper)
		{
			upperObstaclesList.add(obstacleTypeName);
		}
		
	}
	
	public void setObstacle(String obstacleTypeName, String spriteName) //int wrapper
	{
		for(Body b: bodiesList)
		{
			if( obstacleTypeName.equals( (String)b.getUserData() ) && b.getPosition().x < player.getX() )
			{System.out.println("LOG: ZNALAZLEM! "+b.getPosition().x+" a player: "+player.getX() );

				b.setTransform( CalculateObstaclePosition(), b.getPosition().y, 0);

				
				
				System.out.println("LOG: PO = " + b.getPosition().x+" a player: "+player.getX() );
			}
			
			break; //nothing to do here :>
		}
	
	}
	
	public void IgnoreCollision() 
	{
		for(Body b: bodiesList)
		{
			for (int i = 0; i < b.getFixtureList().size(); i++) 
			{
				b.getFixtureList().get(i).setSensor(true);
			}
		}
		
		/*
		for (int j = 0; j < pigs.size(); j++) {
			Body pig = (Body) pigs.get(j);
			for (int i = 0; i < pig.getFixtureList().size(); i++) {
				pig.getFixtureList().get(i).setSensor(true);
			}
		}
		*/
	}
	
}
