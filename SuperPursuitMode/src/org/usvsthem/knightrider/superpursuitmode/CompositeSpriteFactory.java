package org.usvsthem.knightrider.superpursuitmode;

import java.util.ArrayList;
import java.util.Collections;

import org.andengine.entity.sprite.Sprite;
import org.usvsthem.knightrider.superpursuitmode.entity.ISpriteFactory;

public class CompositeSpriteFactory implements ISpriteFactory{
	
	public CompositeSpriteFactory(){
		spriteFactories = new ArrayList<ISpriteFactory>();
	}

	private ArrayList<ISpriteFactory> spriteFactories;
	
	
	public void addSpriteFactory(ISpriteFactory spriteFactory){
		spriteFactories.add(spriteFactory);
	}
	
	@Override
	public Sprite create() {
		Collections.shuffle(spriteFactories);
		return spriteFactories.get(0).create();
	}

}
