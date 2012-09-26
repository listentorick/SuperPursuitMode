package org.usvsthem.knightrider.superpursuitmode.entity;

import org.andengine.engine.Engine;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.particle.ParticleSystem;
import org.andengine.entity.particle.SpriteParticleSystem;
import org.andengine.entity.particle.emitter.PointParticleEmitter;
import org.andengine.entity.particle.initializer.RotationParticleInitializer;
import org.andengine.entity.particle.initializer.VelocityParticleInitializer;
import org.andengine.entity.particle.modifier.ExpireParticleInitializer;
import org.andengine.entity.shape.IAreaShape;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.opengl.texture.region.TextureRegionLibrary;
import org.usvsthem.knightrider.superpursuitmode.Constants;
import org.usvsthem.knightrider.superpursuitmode.Textures;

import android.R.bool;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public abstract class TerrainAlignedActor implements IUpdateHandler{
	
	private int NUM_PREV_ANGLES  = 30;
	Vector2[] prevAngles = new Vector2[NUM_PREV_ANGLES];
	double[] weightedAngle = new double[NUM_PREV_ANGLES];
	int _nextAngle = 0;
	
	private static float DENSITY = 1.0f;
	private static float RESTITUTION = 0.0f;
	private static float FRICTION = 0.0f;
	
	
	protected Terrain terrain;
	protected IAreaShape shape;
	protected Body body;
	protected PhysicsWorld physicsWorld;
	protected Engine engine;
	protected TextureRegionLibrary textureRegionLibrary;
	protected Direction direction;
	
	protected float x;
	protected float y;
	
//	private PointParticleEmitter dustParticleEmitter;
//	private ParticleSystem dustParticleSystem;

	public TerrainAlignedActor(float x, float y,Direction direction, Engine engine, PhysicsWorld physicsWorld, Terrain terrain,  LevelScene levelScene, TextureRegionLibrary textureRegionLibrary){
		this.terrain = terrain;
		this.textureRegionLibrary = textureRegionLibrary;
		this.physicsWorld = physicsWorld;
		this.engine = engine;
		this.direction = direction;
		
		//this.dustParticleEmitter = constructDustParticleEmitter();
		//this.dustParticleSystem = constructDustParticleSystem(dustParticleEmitter, direction);
		//levelScene.attachChild(dustParticleSystem);	
		
		this.shape = constructShape(textureRegionLibrary);
		
		BodyDef bodyDef = constructBody(x,y);
		body = physicsWorld.createBody(bodyDef);
		
		FixtureDef fixtureDef = constructFixtureDef();
		body.createFixture(fixtureDef);
		
		constructFixtureDef();
		levelScene.attachChild(shape);
	}
	
	
	public void setPosition(float x, float y) {
		body.setTransform(x/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, y/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, body.getAngle());
	}
	
	public float getX(){
		return shape.getX();
	}
	
	public float getWidth(){
		return shape.getWidth();
	}
	
	public float getHeight(){
		return shape.getHeight();
	}
	
	private BodyDef constructBody(float x, float y) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.linearDamping = 0.1f;
		bodyDef.fixedRotation  = true;
		bodyDef.position.x = (x + shape.getWidth())/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		bodyDef.position.y = (y + shape.getWidth())/ PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		return bodyDef;
	}
	
	protected FixtureDef constructFixtureDef() {
		CircleShape circleShape = new CircleShape();
		circleShape.setRadius((shape.getWidth()/2) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
		FixtureDef fixtureDef = PhysicsFactory.createFixtureDef(DENSITY, RESTITUTION, FRICTION);
		fixtureDef.shape = circleShape;
		return fixtureDef;
	}
	
	/*
	 private ParticleSystem constructDustParticleSystem(PointParticleEmitter dustParticleEmitter, Direction direction){
		ParticleSystem dustParticleSystem = new SpriteParticleSystem(dustParticleEmitter,50, 75, 100, textureRegionLibrary.get(Textures.DUST_PARTICLE), engine.getVertexBufferObjectManager());
		dustParticleSystem.addParticleInitializer(new ExpireParticleInitializer(1, 3));
		if(direction == Direction.LEFT_TO_RIGHT){
			dustParticleSystem.addParticleInitializer(new VelocityParticleInitializer(-2, 2, -40, -20));
		} else {
			dustParticleSystem.addParticleInitializer(new VelocityParticleInitializer(2, 2, -40, -20));
		}
		dustParticleSystem.addParticleInitializer(new RotationParticleInitializer(0.0f, 360.0f));
        dustParticleSystem.setParticlesSpawnEnabled(true);
        return dustParticleSystem;
	 }
	
	private PointParticleEmitter constructDustParticleEmitter(){	
		//Why add and start the particles so that we dont get a judder the first time they
		//appear on screen. Shit no?
		PointParticleEmitter dustParticleEmitter = new PointParticleEmitter(Constants.OFF_SCREEN_X,Constants.OFF_SCREEN_Y);
		return dustParticleEmitter;	
	}
	*/
	protected abstract IAreaShape constructShape(TextureRegionLibrary textureRegionLibrary);
	
	private double bearing(Vector2 v)
	{
		return Math.atan(v.y/v.x);
	}

	private double calculateWeightedAngle() {
		double average = 0;
		Vector2 vel = terrain.getVectorAtX(shape.getX() + shape.getWidth()/2);
		if(vel!=null) {
			double newAngle =  Math.toDegrees(bearing(vel)) ;

		    for(int i = 0; i < NUM_PREV_ANGLES; ++i) {
		    	average+=weightedAngle[i];
		    }
		    
		    average = average/NUM_PREV_ANGLES;
		    
	   // if(isInContact) {
	    	weightedAngle[_nextAngle++] = newAngle;
	    	if (_nextAngle >= NUM_PREV_ANGLES) _nextAngle = 0;
	    //}
		}
	    return average;
	}
	
	protected void positionShape(){
		final Vector2 position = body.getPosition().cpy();
		position.y += 0.55f;
		shape.setPosition(position.x * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT - shape.getWidth() * 0.5f, position.y * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT - shape.getHeight() * 0.5f);
		double angle = calculateWeightedAngle();
		shape.setRotation((float) angle);
	}
	
//	protected void positionDust(){
//		dustParticleSystem.setParticlesSpawnEnabled(true);
//		dustParticleEmitter.setCenter(shape.getX(), shape.getY()+20f);
//	}
	
	private void manageMinimumVelocity(){
		
	    float minVelocityX = -2;
	    float minVelocityY = -50;
	    
	    Vector2 vel = body.getLinearVelocity();
	    
	    if(direction == Direction.LEFT_TO_RIGHT){
	    	if (vel.x < minVelocityX) {
	 	        vel.x = minVelocityX;
	 	    }

	    } else {
	    	
	    	minVelocityX = -8;
	    	minVelocityY = 0;
	    	
	    	if (vel.x > minVelocityX) {
	 	        vel.x = minVelocityX;
	 	    }
	    }
	    
    	
    	if (vel.y < minVelocityY) {
 	        vel.y = minVelocityY;
 	    }
	    
	    body.setLinearVelocity(vel);
	
}
	
	@Override
	public void onUpdate(float pSecondsElapsed) {
		
		if(!ignoreUpdate){
			positionShape();
			//positionDust();
			manageMinimumVelocity();
		}
	}
	
	@Override
	public void reset() {
		ignoreUpdate = false;
	}

	private boolean ignoreUpdate = false;
	public void setIgnoreUpdate(boolean setIgnoreUpdate) {
		this.ignoreUpdate = setIgnoreUpdate;
		//dustParticleSystem.setIgnoreUpdate(ignoreUpdate);
		shape.setIgnoreUpdate(ignoreUpdate);
	}

}
