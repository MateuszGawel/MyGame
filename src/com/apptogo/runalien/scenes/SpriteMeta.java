package com.apptogo.runalien.scenes;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.apptogo.runalien.ResourcesManager;

public class SpriteMeta 
{
	private ITextureRegion textureRegion;
	private VertexBufferObjectManager vertexBuffer;
	private Object userData;
	
	public SpriteMeta(ITextureRegion pTextureRegion, Object pUserData)
	{
		textureRegion = pTextureRegion;
		vertexBuffer = ResourcesManager.getInstance().vbom;
		userData = pUserData;
	}

	public void setUserData(Object data) {
		userData = data;
	}

	public Object getUserData() {
		return userData;
	}

	public Sprite CreateSprite(float x, float y) {
		Sprite sprite = new Sprite(x, y, textureRegion, vertexBuffer);
		sprite.setUserData(userData);
		return sprite;
	}
	
}
