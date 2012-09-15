package org.usvsthem.knightrider.superpursuitmode.entity;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.primitive.Mesh;
import org.andengine.entity.primitive.vbo.HighPerformanceLineVertexBufferObject;
import org.andengine.entity.primitive.vbo.ILineVertexBufferObject;
import org.andengine.entity.shape.IShape;
import org.andengine.entity.shape.RectangularShape;
import org.andengine.entity.shape.Shape;
import org.andengine.opengl.shader.PositionColorShaderProgram;
import org.andengine.opengl.shader.ShaderProgram;
import org.andengine.opengl.shader.constants.ShaderProgramConstants;
import org.andengine.opengl.util.GLState;
import org.andengine.opengl.vbo.DrawType;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.opengl.vbo.attribute.VertexBufferObjectAttributes;
import org.andengine.opengl.vbo.attribute.VertexBufferObjectAttributesBuilder;
import org.andengine.util.algorithm.collision.LineCollisionChecker;
import org.andengine.util.algorithm.collision.RectangularShapeCollisionChecker;
import org.andengine.util.exception.MethodNotSupportedException;

import android.opengl.GLES20;

public class Path extends Shape {

	private IPathVertexBufferObject pathVertexBufferObject;
	private float[] bufferData;
	
	public static final int VERTEX_INDEX_X = 0;
	public static final int VERTEX_INDEX_Y = Path.VERTEX_INDEX_X + 1;
	public static final int COLOR_INDEX = Path.VERTEX_INDEX_Y + 1;

	public static final int VERTEX_SIZE = 2 + 1;
	
	public static final VertexBufferObjectAttributes VERTEXBUFFEROBJECTATTRIBUTES_DEFAULT = new VertexBufferObjectAttributesBuilder(2)
	.add(ShaderProgramConstants.ATTRIBUTE_POSITION_LOCATION, ShaderProgramConstants.ATTRIBUTE_POSITION, 2, GLES20.GL_FLOAT, false)
	.add(ShaderProgramConstants.ATTRIBUTE_COLOR_LOCATION, ShaderProgramConstants.ATTRIBUTE_COLOR, 4, GLES20.GL_UNSIGNED_BYTE, true)
	.build();
	
	
	
	public Path(final float pX, final float pY, float[] buffer, final VertexBufferObjectManager pVertexBufferObjectManager) {
		this(pX, pY, buffer, pVertexBufferObjectManager, DrawType.STATIC);
	}

	public Path(final float pX, final float pY, float[] buffer, final VertexBufferObjectManager pVertexBufferObjectManager, final DrawType pDrawType) {
		this(pX, pY, buffer, new HighPerformancePathVertexBufferObject(pVertexBufferObjectManager, buffer, pDrawType, true, Path.VERTEXBUFFEROBJECTATTRIBUTES_DEFAULT));
	}
	
	
	
	public Path(final float pX, final float pY, float[] buffer, final IPathVertexBufferObject pathVertexBufferObject) {
		super(pX, pY, PositionColorShaderProgram.getInstance());

		bufferData = buffer;
		
		this.pathVertexBufferObject = pathVertexBufferObject;

		this.onUpdateVertices();
		this.onUpdateColor();

		//final float centerX = (this.mX2 - this.mX) * 0.5f;
		//final float centerY = (this.mY2 - this.mY) * 0.5f;

		//this.mRotationCenterX = centerX;
		//this.mRotationCenterY = centerY;

		//this.mScaleCenterX = this.mRotationCenterX;
		//this.mScaleCenterY = this.mRotationCenterY;

		this.setBlendingEnabled(true);
	}
	
	public void setBufferData(float[] bufferData){
		this.bufferData = bufferData;
		//this.onUpdateColor();
		this.onUpdateVertices();
	}
	
	public float[] getVertices(){
		return bufferData;
	}
	

	
	@Override
	public IPathVertexBufferObject getVertexBufferObject() {
		return this.pathVertexBufferObject;
	}

	//@Override
	//public boolean isCulled(final Camera pCamera) {
	//	return pCamera.isLineVisible(this);
	//}/

	@Override
	protected void preDraw(final GLState pGLState, final Camera pCamera) {
		super.preDraw(pGLState, pCamera);

		pGLState.lineWidth(2);

		this.pathVertexBufferObject.bind(pGLState, this.mShaderProgram);
	}

	@Override
	protected void draw(final GLState pGLState, final Camera pCamera) {
		this.pathVertexBufferObject.draw(GLES20.GL_LINE_STRIP,this.bufferData.length/3);
	}

	@Override
	protected void postDraw(final GLState pGLState, final Camera pCamera) {
		this.pathVertexBufferObject.unbind(pGLState, this.mShaderProgram);

		super.postDraw(pGLState, pCamera);
	}

	@Override
	protected void onUpdateColor() {
		this.pathVertexBufferObject.onUpdateColor(this);
	}

	@Override
	protected void onUpdateVertices() {
		this.pathVertexBufferObject.onUpdateVertices(this);
	}

	@Override
	public float[] getSceneCenterCoordinates() {
		throw new MethodNotSupportedException();
	}

	@Override
	public float[] getSceneCenterCoordinates(final float[] pReuse) {
		throw new MethodNotSupportedException();
	}

	@Override
	public boolean collidesWith(IShape pOtherShape) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean contains(float pX, float pY) {
		// TODO Auto-generated method stub
		return false;
	}





}
