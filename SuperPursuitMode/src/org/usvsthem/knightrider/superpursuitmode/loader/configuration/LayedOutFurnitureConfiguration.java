package org.usvsthem.knightrider.superpursuitmode.loader.configuration;

import org.simpleframework.xml.Attribute;

public class LayedOutFurnitureConfiguration extends FurnitureConfiguration{

	@Attribute(name="x")
	private float x;
	
	public float getX() {
	   return x;
	}
	
	public void setX(float x) {
		this.x = x;
	}
	
	@Attribute(name="spriteId")
	private int spriteId;
   
	public int getSpriteId() {
	   return this.spriteId;
	}
	
	public void setSpriteId(int spriteId) {
		this.spriteId = spriteId;
	}


}
