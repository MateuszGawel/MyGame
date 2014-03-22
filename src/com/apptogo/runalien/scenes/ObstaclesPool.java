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

	private PhysicsWorld physicsWorld;
	private Player player;
	private Entity foregroundLayer;
	private ResourcesManager resourcesManager;
	private Body body;
	
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
	
	private float getPlayerPositionX()
	{
		return player.getX()/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
	}
	
	public int CalculateObstaclePosition()
	{
		return (int)((player.getX()+800)/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
	}
	
	public void AddSprite(SpriteMeta spriteMeta)
	{
		spritesList.add(spriteMeta);
	}
	
	private Sprite GetSprite(String spriteName, float x, float y)
	{
		for(SpriteMeta s: spritesList)
		{
			System.out.println("LOG"+spriteName+"     "+s.getUserData());
			if( spriteName.equals( (String)s.getUserData() ) )
			{
				return s.CreateSprite(x,y);
			}
		}
		
		return null; //tu ofc powinien byc wyjatek ale to potem ;P
	}
		
	public void CreateObstacles(int amount, String obstacleTypeName, String spriteName, int y)
	{
		for(int i=0; i<amount; i++)
		{			
			Sprite sprite = GetSprite(spriteName, 150, y);

			body = PhysicsFactory.createBoxBody(physicsWorld, sprite, BodyType.StaticBody, PhysicsFactory.createFixtureDef(10.0f, 0, 0));
			body.setUserData(obstacleTypeName);
			System.out.println("LOG: UD "+body.getUserData());
			foregroundLayer.attachChild( sprite ); //attaching sprite

			//doczytac czy ok
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
							//System.out.println("LOG: GORNA "+body.getUserData().toString());
							if (player.isSliding() && player.isAlive() && body.getUserData().toString().toLowerCase().contains("upper") )
								body.getFixtureList().get(i).setSensor(true);
							else if (player.isAlive())
								body.getFixtureList().get(i).setSensor(false);
						}
					//}
				}
			});
			
			bodiesList.add(body);
		}
		
	}
	
	public void setObstacle(String obstacleTypeName) //int wrapper
	{int ctr = 0;
		for(Body b: bodiesList)
		{ctr++;
			int bodyX = (int)b.getPosition().x;
			int playerX = (int)getPlayerPositionX();
			
			if( obstacleTypeName.equals( (String)b.getUserData() ) && bodyX < playerX )
			{System.out.println("LOG: wzialem numer "+ctr+" player="+playerX+" b position="+bodyX);
				b.setTransform( CalculateObstaclePosition(), b.getPosition().y, 0);
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
	}
	
}
