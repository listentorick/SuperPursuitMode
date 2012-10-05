package org.usvsthem.knightrider.superpursuitmode.entity;

import java.util.HashMap;

import org.andengine.engine.Engine;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.TextureRegionLibrary;
import org.andengine.util.adt.pool.GenericPool;
import org.andengine.util.adt.pool.MultiPool;
import org.usvsthem.knightrider.superpursuitmode.Theme;
import org.usvsthem.knightrider.superpursuitmode.furniture.DesertFurnitureFactory;

public class SpriteMultiPool {
	
	private HashMap<Integer,SpritePool> furniturePools;
	private HashMap<Sprite,Integer> sprites;
	
	public SpriteMultiPool(){
		furniturePools = new HashMap<Integer, SpritePool>();
		sprites = new HashMap<Sprite, Integer>();

	}
	
	public void registerPool(int pId, SpritePool pool){
		furniturePools.put(pId, pool);
	}
	
	public Sprite obtainPoolItem(final int pID) {
		Sprite obj = furniturePools.get(pID).obtainPoolItem();
		sprites.put(obj, pID);
		return obj;
	}
	
	public void recyclePoolItem(final Sprite pItem) {
		Integer pid = sprites.get(pItem);
		furniturePools.get(pid).recyclePoolItem(pItem);
		sprites.remove(pItem);		
	}
	
	public int getAvailableItemCount(int pId){
		return furniturePools.get(pId).getAvailableItemCount();
		
	}
	

}
