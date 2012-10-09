package org.usvsthem.knightrider.superpursuitmode.loader.configuration;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(name="sprite")
public class SpriteConfiguration {

	
	@Attribute(name="id")
	private int id;
	
	public int getId() {
	   return id;
	}
   
	public void setId(int id) {
	   this.id = id;
	}

	@Attribute(name="src")
	private String src;
	
	public String getSrc() {
		return src;
	}
	   
	public void setSrc(String src) {
	   this.src = src;
	}


}
