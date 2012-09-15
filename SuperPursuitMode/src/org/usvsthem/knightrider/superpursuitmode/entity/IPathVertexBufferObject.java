package org.usvsthem.knightrider.superpursuitmode.entity;

import org.andengine.opengl.vbo.IVertexBufferObject;

public interface IPathVertexBufferObject  extends IVertexBufferObject {
	
	public void onUpdateColor(final Path pPath);
	public void onUpdateVertices(final Path pPath);

}
