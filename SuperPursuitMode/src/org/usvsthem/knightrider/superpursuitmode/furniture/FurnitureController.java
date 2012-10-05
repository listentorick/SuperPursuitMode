package org.usvsthem.knightrider.superpursuitmode.furniture;

import java.util.ArrayList;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.Sprite;
import org.usvsthem.knightrider.superpursuitmode.ILevel;


//this should only be responsible for asking for furniture and removing the furniture
//the positioning is handled by the provider?

public class FurnitureController implements IFurnitureController{
	
	private ArrayList<Sprite> furnitureInScene;
	private ILevel level;
	private IFurnitureProvider furnitureProvider;
	private Camera camera;
		
	
	public FurnitureController(ILevel level, IFurnitureProvider furnitureProvider) {
		this.level = level;
		this.camera = level.getEngine().getCamera();
		furnitureInScene = new ArrayList<Sprite>();
		this.furnitureProvider = furnitureProvider;
	}
		
	private void manageFurniture(){
		Sprite furniture;
		for(int i=0; i<furnitureInScene.size();i++) {
			furniture = furnitureInScene.get(i);
			if((furniture.getX() + furniture.getWidth())<camera.getXMin()){
				this.removeFurnitureFromSceneToPool(furniture);
			}
		}
		
		Sprite[] sprites = furnitureProvider.obtainFurniture(camera.getXMin(), camera.getXMax());
		if(sprites!=null){
			for(int i=0; i< sprites.length;i++){
				furniture = sprites[i];
				level.addFurniture(furniture);
				furnitureInScene.add(furniture);
			}
		}
	}
	
	private void removeFurnitureFromSceneToPool(Sprite furniture) {
		furnitureInScene.remove(furniture);
		furnitureProvider.furnitureRemoved(furniture);
		level.removeFurniture(furniture);
	}

	@Override
	public void onUpdate(float pSecondsElapsed) {
		manageFurniture();
	}


	@Override
	public void reset() {

	}
}
