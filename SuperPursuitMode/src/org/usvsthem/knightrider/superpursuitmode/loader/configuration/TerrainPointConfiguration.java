package org.usvsthem.knightrider.superpursuitmode.loader.configuration;

import org.simpleframework.xml.Attribute;

public class TerrainPointConfiguration {
	
	@Attribute(name="x")
	private float x;
	
	public float getX() {
	   return x;
	}
	
	@Attribute(name="y")
	private float y;
	
	public float getY() {
	   return y;
	}

}
