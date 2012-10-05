package org.usvsthem.knightrider.superpursuitmode.terrain;

import org.andengine.entity.IEntity;

import com.badlogic.gdx.math.Vector2;

public interface ITerrain extends IEntity{
	
	float getMinTerrainHeightInRange(float x1, float x2);
	public float getMaxTerrainHeightInRange(float x1, float x2);
	Vector2 getVectorAtX(float x);
	float getYAt(float x);

}
