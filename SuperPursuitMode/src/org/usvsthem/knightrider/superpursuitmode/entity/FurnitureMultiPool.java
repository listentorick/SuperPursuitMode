package org.usvsthem.knightrider.superpursuitmode.entity;

import java.util.HashMap;

import org.andengine.engine.Engine;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.TextureRegionLibrary;
import org.andengine.util.adt.pool.GenericPool;
import org.andengine.util.adt.pool.MultiPool;
import org.usvsthem.knightrider.superpursuitmode.DesertFurnitureFactory;
import org.usvsthem.knightrider.superpursuitmode.Theme;

public class FurnitureMultiPool {
	
	private TextureRegionLibrary textureRegionLibrary;
	private Engine engine;
	//private FurniturePool desertFurniturePool;
	private int NUM_FURNITURE = 10;
	//private MultiPool<Sprite> multiPool;
	private HashMap<Integer,FurniturePool> furniturePools;
	private HashMap<Sprite,Integer> sprites;
	
	public FurnitureMultiPool(){
		furniturePools = new HashMap<Integer, FurniturePool>();
		sprites = new HashMap<Sprite, Integer>();
		
		//desertFurniturePool =  new FurniturePool(new DesertFurnitureFactory(engine, textureRegionLibrary));
		//desertFurniturePool.batchAllocatePoolItems(NUM_FURNITURE);
		//furniturePools.put(Theme.DESERT.ordinal(), desertFurniturePool);
	}
	
	public void registerPool(int pId, FurniturePool pool){
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
