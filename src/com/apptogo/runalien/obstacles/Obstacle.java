package com.apptogo.runalien.obstacles;

import org.andengine.entity.sprite.Sprite;

import com.badlogic.gdx.physics.box2d.Body;

public abstract class Obstacle {

	//mozna pomyslec nad przeniesieniem tutaj elementow wspolnych wszystkich przeszkod.
	public abstract Body getBody();
	public abstract Sprite getSprite();
	public abstract void resetPosition();
}
