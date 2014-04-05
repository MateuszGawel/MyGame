package com.apptogo.runalien.coins;

import org.andengine.engine.Engine;
import org.andengine.entity.Entity;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.vbo.ISpriteVertexBufferObject;
import org.andengine.opengl.texture.region.ITextureRegion;

import com.apptogo.runalien.Player;
import com.apptogo.runalien.ResourcesManager;

public class Coin extends Sprite {

	private static Player player;
	private float x,y;
	private Entity foregroundLayer;
	
	public Coin(float pX, float pY, Entity f) {
		super(pX, pY, ResourcesManager.getInstance().coin_region, ResourcesManager.getInstance().vbom);
		f.attachChild(this);
		
		x = pX;
		y = pY;
		foregroundLayer = f;
	}
	
	public void setPlayer(Player p)
	{
		player = p;
	}
	
	@Override
    protected void onManagedUpdate(final float pSecondsElapsed)
    {              
            ResourcesManager.getInstance().engine.runOnUpdateThread(new Runnable()
            {
                    @Override
                    public void run()
                    {
                            if (player.getBody().getPosition().x > x)
                            {
                                    foregroundLayer.detachChild(Coin.this);
                            }
                    }
            });
            super.onManagedUpdate(pSecondsElapsed);
    }

}
