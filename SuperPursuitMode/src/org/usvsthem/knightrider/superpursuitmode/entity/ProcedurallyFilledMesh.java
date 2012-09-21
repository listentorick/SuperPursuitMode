package org.usvsthem.knightrider.superpursuitmode.entity;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.primitive.DrawMode;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.primitive.Mesh;
import org.andengine.opengl.util.GLState;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.opengl.GLES20;

public class ProcedurallyFilledMesh extends Mesh {

	private int mVertexCountToDraw;
	
	public ProcedurallyFilledMesh(float pX, float pY, float[] pBufferData,
			int pVertexCount, DrawMode pDrawMode,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		
		super(pX, pY, pBufferData, pVertexCount, pDrawMode, pVertexBufferObjectManager);
		mVertexCountToDraw = pVertexCount;
	}
	
	@Override
	protected void draw(final GLState pGLState, final Camera pCamera) {
		this.mMeshVertexBufferObject.draw(GLES20.GL_TRIANGLE_STRIP, mVertexCountToDraw);
	}

}
