package org.usvsthem.knightrider.superpursuitmode.furniture;

import java.util.ArrayList;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.Sprite;
import org.usvsthem.knightrider.superpursuitmode.ILevel;
import org.usvsthem.knightrider.superpursuitmode.Theme;
import org.usvsthem.knightrider.superpursuitmode.entity.SpriteMultiPool;

public class FurnitureController implements IFurnitureController{
	
	private ArrayList<Sprite> furnitureInScene;
	private ILevel level;
	private SpriteMultiPool furniturePool;
	private Camera camera;
	private float minFurnitureX = 0;
	private Theme theme = Theme.DESERT;
	
	
	public FurnitureController(ILevel level, SpriteMultiPool furniturePool) {
		this.level = level;
		this.camera = level.getEngine().getCamera();
		furnitureInScene = new ArrayList<Sprite>();
	}
		
	private void manageFurniture(){
		Sprite furniture;
		for(int i=0; i<furnitureInScene.size();i++) {
			furniture = furnitureInScene.get(i);
			if((furniture.getX() + furniture.getWidth())<camera.getXMin()){
				this.removeFurnitureFromSceneToPool(furniture);
			}
		}
		
		for(int i=0; i<furniturePool.getAvailableItemCount(theme.ordinal());i++){
			//grabs a random item from the pool and adds it to the scene.
			this.addFurnitureFromPoolToScene(theme);
		}
	}

	private void addFurnitureFromPoolToScene(Theme theme) {
		Sprite furniture = furniturePool.obtainPoolItem(theme.ordinal());
		level.addFurniture(furniture);
		positionTerrainFuniture(furniture);
		furnitureInScene.add(furniture);
	}
	
	private void removeFurnitureFromSceneToPool(Sprite furniture) {
		furnitureInScene.remove(furniture);
		furniturePool.recyclePoolItem(furniture);
		level.removeFurniture(furniture);
	}
	
	private void positionTerrainFuniture(Sprite furniture){
		if(minFurnitureX < camera.getXMax()) {
			minFurnitureX = camera.getXMax();
		}
		minFurnitureX+= furniture.getWidth() + ((float)Math.random()*200);
		
		float xPos = minFurnitureX;
		
		float yPos = level.getTerrain().getYAt(xPos);
		float yPos2 = level.getTerrain().getYAt(xPos+ furniture.getWidth());
		if(yPos2> yPos) yPos = yPos2; 
		furniture.setPosition(xPos, yPos - furniture.getHeight());
	}


	@Override
	public void onUpdate(float pSecondsElapsed) {
		manageFurniture();
	}


	@Override
	public void reset() {

	}
}
