package com.apptogo.runalien.scenes;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.apptogo.runalien.ResourcesManager;

public class SpriteMeta 
{
	private String userData;
	private ITextureRegion textureRegion;
	private VertexBufferObjectManager vertexBuffer;
	
	public SpriteMeta(ITextureRegion pTextureRegion)
	{
		textureRegion = pTextureRegion;
		vertexBuffer = ResourcesManager.getInstance().vbom;
	}

	public void setUserData(String data) {
		userData = data;
	}

	public String getUserData() {
		return userData;
	}

	public Sprite CreateSprite(float x, float y) {
		Sprite sprite = new Sprite(x, y, textureRegion, vertexBuffer);
		sprite.setUserData(userData);
		return sprite;
	}
	
}
