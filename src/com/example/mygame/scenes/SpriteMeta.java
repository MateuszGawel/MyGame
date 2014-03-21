package com.example.mygame.scenes;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class SpriteMeta 
{
	private String userData;
	private ITextureRegion textureRegion;
	private VertexBufferObjectManager vertexBuffer;
	
	public SpriteMeta(ITextureRegion pTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager)
	{
		textureRegion = pTextureRegion;
		vertexBuffer = pVertexBufferObjectManager;
	}

	public void setUserData(String data) {
		userData = data;
		
	}

	public String getUserData() {
		return userData;
	}

	public Sprite CreateSprite(float x, float y) {
		return new Sprite(x, y, textureRegion, vertexBuffer);
	}
	
}
