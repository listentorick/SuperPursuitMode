package org.usvsthem.knightrider.superpursuitmode.loader.configuration;

import org.simpleframework.xml.Attribute;

public class SpriteReferenceConfiguration {

	@Attribute(name="spriteId")
	private int spriteId;
   
	public int getSpriteId() {
	   return this.spriteId;
	}
	
	@Attribute(name="width")
	private float width;
   
	public float getWidth() {
	   return this.width;
	}
	
	@Attribute(name="height")
	private float height;
   
	public float getHeight() {
	   return this.height;
	}
	

}
