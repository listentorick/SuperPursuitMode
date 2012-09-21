package org.usvsthem.knightrider.superpursuitmode.entity;

import java.nio.ByteOrder;

import org.andengine.entity.primitive.Line;
import org.andengine.entity.primitive.Mesh;
import org.andengine.opengl.util.BufferUtils;
import org.andengine.opengl.vbo.DrawType;
import org.andengine.opengl.vbo.HighPerformanceVertexBufferObject;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.opengl.vbo.attribute.VertexBufferObjectAttributes;
import org.andengine.util.adt.DataConstants;

public class HighPerformancePathVertexBufferObject extends HighPerformanceVertexBufferObject implements IPathVertexBufferObject{

	public HighPerformancePathVertexBufferObject(VertexBufferObjectManager pVertexBufferObjectManager, float[] pBufferData, DrawType pDrawType, boolean pAutoDispose, VertexBufferObjectAttributes pVertexBufferObjectAttributes) {
		
		super(pVertexBufferObjectManager, pBufferData, pDrawType, pAutoDispose,pVertexBufferObjectAttributes);

	}

	@Override
	public void onUpdateColor(Path path) {
		
		final float[] bufferData = this.mBufferData;

		final float packedColor = path.getColor().getABGRPackedFloat();
		int length = bufferData.length/3;
		
		for(int i = 0; i < length; i++) {
		//for(int i = 0; i < path.getVertices().length; i++) {
			bufferData[(i * Path.VERTEX_SIZE) + Path.COLOR_INDEX] = packedColor;
		}
		

		this.setDirtyOnHardware();
		
	}

	
	@Override
	public void onUpdateVertices(Path path) {
		
		float[] bufferData = this.mBufferData;
		
		float[] verticesData = path.getVertices();
				
		int length = bufferData.length/3;
		int xIndex = 0;
		int yIndex = 0;
		for(int i = 0; i < length; i++) {
			xIndex = (i * Path.VERTEX_SIZE) + Path.VERTEX_INDEX_X;
			yIndex = (i * Path.VERTEX_SIZE) + Path.VERTEX_INDEX_Y;
			
			bufferData[xIndex] = verticesData[xIndex];
			bufferData[yIndex] = verticesData[yIndex];
		}
	

		this.setDirtyOnHardware();
		
	}

}
