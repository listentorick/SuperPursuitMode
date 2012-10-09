package org.usvsthem.knightrider.superpursuitmode.loader.configuration;

import org.simpleframework.xml.Attribute;

public class SpriteReferenceConfiguration {

	@Attribute(name="spriteId")
	private int spriteId;
   
	public int getSpriteId() {
	   return this.spriteId;
	}
	
	public void setSpriteId(int spriteId) {
		this.spriteId = spriteId;
	}
}
