package com.apptogo.runalien.obstacles;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.Entity;

public class ObstacleGenerator {
	
	private ObstaclesPoolManager obstaclesPoolManager;
	private Entity foregroundLayer;
	
	public ObstacleGenerator(Entity fL)
	{
		obstaclesPoolManager = ObstaclesPoolManager.getInstance();
		foregroundLayer = fL;
	}
	
	public void startObstacleGenerationAlgorithm(){		
	}
	
}
