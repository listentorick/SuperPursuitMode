package org.usvsthem.knightrider.superpursuitmode.entity;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import org.andengine.audio.sound.SoundManager;
import org.andengine.engine.Engine;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.particle.ParticleSystem;
import org.andengine.entity.particle.SpriteParticleSystem;
import org.andengine.entity.particle.emitter.PointParticleEmitter;
import org.andengine.entity.particle.initializer.AlphaParticleInitializer;
import org.andengine.entity.particle.initializer.BlendFunctionParticleInitializer;
import org.andengine.entity.particle.initializer.RotationParticleInitializer;
import org.andengine.entity.particle.initializer.VelocityParticleInitializer;
import org.andengine.entity.particle.modifier.AlphaParticleModifier;
import org.andengine.entity.particle.modifier.ExpireParticleInitializer;
import org.andengine.entity.particle.modifier.ScaleParticleModifier;
import org.andengine.entity.shape.IAreaShape;
import org.andengine.entity.shape.IShape;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.opengl.font.FontLibrary;
import org.andengine.opengl.texture.region.TextureRegionLibrary;
import org.andengine.util.math.MathUtils;
import org.usvsthem.knightrider.superpursuitmode.Constants;
import org.usvsthem.knightrider.superpursuitmode.Textures;

import android.opengl.GLES20;
import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.joints.LineJointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;

public class PlayerActor implements IUpdateHandler{
	
	
	private Engine engine;
	private PhysicsWorld physicsWorld;
	
	private Body heroBody;
	private Sprite rearWheelShape;
	private Sprite frontWheelShape;
	
	private IAreaShape heroShape;
	private Sprite scannerShape;

	private float x;
	private float y;
	private LevelScene levelScene;
	private TextureRegionLibrary textureRegionLibrary;
		
	private static float HERO_RADIUS = 32f;
	private static float HERO_DENSITY = 1.0f;
	private static float HERO_RESTITUTION = 0.0f;
	private static float HERO_FRICTION = 0.0f;
	
	private static float SCANNER_X_OFFSET = 50f;
	private static float SCANNER_Y_OFFSET = 7f;
	private static float SCANNER_RADIUS = 12;
	
	private static float FRONT_WHEEL_X_OFFSET = 42f;
	private static float FRONT_WHEEL_Y_OFFSET = 16f;
	private static float FRONT_WHEEL_HANGING_Y_OFFSET = 18f;
	
	
	private static float REAR_WHEEL_X_OFFSET = 7f;
	private static float REAR_WHEEL_Y_OFFSET = 15f;
	private static float REAR_WHEEL_HANGING_Y_OFFSET = 17f;
	
	private static float FRONT_WHEEL_RADIUS = 12;

	private PhysicsConnector heroPhysicsConnector;
	
	private boolean chargingTurboBoost = false;
	private boolean dischargeTurboBoost = false;
	private static float MAX_VERTICAL_TURBO_BOOST = 3000f;
	private boolean awake;
	private Terrain terrain;
	
	public PlayerActor(float x, float y, Engine engine,  PhysicsWorld physicsWorld, Terrain terrain, LevelScene levelScene, TextureRegionLibrary textureRegionLibrary){
		this.x = x;
		this.y = y;
		this.engine = engine;
		this.levelScene = levelScene;
		this.physicsWorld = physicsWorld;
		this.textureRegionLibrary = textureRegionLibrary;
		this.terrain = terrain;
		
		heroBody = this.constructHero();
		
		heroShape = new Sprite(0,0,64,24, textureRegionLibrary.get(Textures.PlayerChasis), engine.getVertexBufferObjectManager());
		levelScene.attachChild(heroShape);
		
		scannerShape = new Sprite(SCANNER_X_OFFSET,SCANNER_Y_OFFSET,SCANNER_RADIUS*2,SCANNER_RADIUS*2, textureRegionLibrary.get(Textures.PlayerScanner), engine.getVertexBufferObjectManager());
		heroShape.attachChild(scannerShape);
		heroShape.setZIndex(-10);
		
		frontWheelShape = new Sprite(FRONT_WHEEL_X_OFFSET,FRONT_WHEEL_Y_OFFSET,15,15, textureRegionLibrary.get(Textures.PlayerFrontWheel), engine.getVertexBufferObjectManager());
		heroShape.attachChild(frontWheelShape);
		
		rearWheelShape = new Sprite(REAR_WHEEL_X_OFFSET,REAR_WHEEL_Y_OFFSET,17,17, textureRegionLibrary.get(Textures.PlayerRearWheel), engine.getVertexBufferObjectManager());
		heroShape.attachChild(rearWheelShape);

		LoopEntityModifier scannerEntityModifier = new LoopEntityModifier( new SequenceEntityModifier(new ScaleModifier(1, 0.3f, 1f),new ScaleModifier(1, 1f, 0.3f)));
		scannerShape.registerEntityModifier(scannerEntityModifier);
	
	}
	
	public float getX() {
		return this.heroShape.getX();
	}
	
	public float getY() {
		return this.heroShape.getY();
	}
	
	public IEntity getPrincipleEntity(){
		return heroShape;
	}
	

	private Body constructHero(){
		
		BodyDef chasisBodyDef = new BodyDef();
		chasisBodyDef.type = BodyType.DynamicBody;
		chasisBodyDef.linearDamping = 0.1f;
		//chasisBodyDef.angularDamping = 0.5f;
		chasisBodyDef.fixedRotation  = true;
		
		chasisBodyDef.position.x = (this.x + HERO_RADIUS)/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		chasisBodyDef.position.y = (this.y + HERO_RADIUS)/ PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		Body chasisBody = this.physicsWorld.createBody(chasisBodyDef);
		
		CircleShape circleShape = new CircleShape();
		circleShape.setRadius(HERO_RADIUS / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
		
		//PolygonShape polygonShape = new PolygonShape();
		//polygonShape.setAsBox(32, 11);
		FixtureDef fixtureDef = PhysicsFactory.createFixtureDef(HERO_DENSITY, HERO_RESTITUTION, HERO_FRICTION);
		fixtureDef.shape = circleShape; 
		//fixtureDef.shape = polygonShape;
		chasisBody.createFixture(fixtureDef);
		
		return chasisBody;
	
	}
	
	

	
	/*
	PointParticleEmitter pointParticleEmitter;
	SpriteParticleSystem particleSystem;
	
	private void constructDustParticleSystem(){	
		//Why add and start the particles so that we dont get a judder the first time they
		//appear on screen. Shit no?
		pointParticleEmitter = new PointParticleEmitter(Constants.OFF_SCREEN_X,Constants.OFF_SCREEN_Y);
		
		particleSystem = new SpriteParticleSystem(pointParticleEmitter,10, 20, 100, textureRegionLibrary.get(Textures.DUST_PARTICLE), engine.getVertexBufferObjectManager());
		//particleSystem.addParticleInitializer(new BlendFunctionParticleInitializer<Sprite>(GLES20.GL_SRC_ALPHA,GLES20.GL_ONE));
		//particleSystem.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		//particleSystem.addParticleInitializer(new  AlphaParticleInitializer(0.5f));
		particleSystem.addParticleInitializer(new ExpireParticleInitializer(3, 6));
		particleSystem.addParticleInitializer(new VelocityParticleInitializer(-2, 2, -40, -20));
		//particleSystem.addParticleModifier(new ScaleParticleModifier(1f, 2f, 0, 5));
		//particleSystem.addParticleModifier(new AlphaParticleModifier(0, 10, 0.5f, 0f));
		particleSystem.addParticleInitializer(new RotationParticleInitializer(0.0f, 360.0f));
        levelScene.attachChild(particleSystem);
        particleSystem.setParticlesSpawnEnabled(true);
       
			
	}
*/	
	
	private boolean isAccelerating = false;
	public void startAccelerating(){
		isAccelerating = true;
	}
	
	public void stopAccelerating(){
		isAccelerating = false;
	}
	
	public void manageAcceleration() {
		
		if(isAccelerating && isInContact){
		
			//should be a torque?
			heroBody.applyForce(new Vector2(20,0), heroBody.getPosition());
		}
	}
	
	public void startChargingTurboBoost(){
		
		turboBoost = new Vector2();
		if(!isInContact) {
			chargingTurboBoost = false;
			
		}else {
			chargingTurboBoost = true;
			Log.d("CHARGING", "START");
		}
		
	}
	
	public void stopChargingTurboBoost(){
		chargingTurboBoost = false;
		dischargeTurboBoost = true;
		Log.d("CHARGING", "STOP");
	}
	
	private void manageMinimumVelocity(){
		
		    if (!awake) return;
		 
		    float minVelocityX = 2;
		    float minVelocityY = -50;
		    
		    Vector2 vel = heroBody.getLinearVelocity();
		    
		    if (vel.x < minVelocityX) {
		        vel.x = minVelocityX;
		    }
		    if (vel.y < minVelocityY) {
		        vel.y = minVelocityY;
		    }
		    heroBody.setLinearVelocity(vel);
		
	}
	
	public void wake () {
	    awake = true;
	    heroBody.setActive(true);
	    //heroBody.applyLinearImpulse(new Vector2(1,2), heroBody.getPosition());
	}
	
	private int NUM_PREV_VELS  = 30;//35;
	Vector2[] prevVels = new Vector2[NUM_PREV_VELS];
	
	double[] weightedAngle = new double[NUM_PREV_VELS];
	
	int _nextVel = 0;
	
	private double calculateWeightedAngle1() {
		
		Vector2 vel = heroBody.getLinearVelocity();
		
		Vector2 weightedVelocity = new Vector2();
		 
	    for(int i = 0; i < NUM_PREV_VELS; ++i) {
	    	
	    	if( prevVels[i]!=null){
	    		weightedVelocity.x += prevVels[i].x;
	    		weightedVelocity.y += prevVels[i].y;
	    	}
	    }
	    
	    weightedVelocity.x =  weightedVelocity.x/NUM_PREV_VELS;
	    weightedVelocity.y = weightedVelocity.y/NUM_PREV_VELS;    
	      
	    prevVels[_nextVel++] = vel;
	    if (_nextVel >= NUM_PREV_VELS) _nextVel = 0;
		
	    return  Math.toDegrees(bearing(weightedVelocity));
	    
	}
	
	private boolean isInContact(){
		List<Contact> contacts = heroBody.getWorld().getContactList();
		for( Contact c: contacts){
			Body fixtureABody = c.getFixtureA().getBody();
			Body fixtureBBody = c.getFixtureB().getBody();
			
			if(fixtureABody == heroBody || fixtureBBody == heroBody){
				return true;
			}
		}
		return false;
	}
	
	private double calculateWeightedAngle2() {
	
		
		//instead of using weighted velocities
		
		//could look at the track
		
		//Vector2 vel = heroBody.getLinearVelocity();
	
		Vector2 vel = terrain.getVectorAtX(heroShape.getX() + heroShape.getWidth()/2);
		
		
		double newAngle =  Math.toDegrees(bearing(vel)) ;
		double average = 0;
	
	    for(int i = 0; i < NUM_PREV_VELS; ++i) {
	    	average+=weightedAngle[i];
	    }
	    
	    average = average/NUM_PREV_VELS;
	    
	    if(isInContact) {
	    	weightedAngle[_nextVel++] = newAngle;
	    	if (_nextVel >= NUM_PREV_VELS) _nextVel = 0;
	    }
		
	    return average;
	}
	

	private Vector2 turboBoost;
	
	
	
	
	
	private void manageTurboBoost(){
		if(chargingTurboBoost==true) {
			if(turboBoost.y>-MAX_VERTICAL_TURBO_BOOST){
				turboBoost.add(new Vector2(5,-100));
			}
		}
		
		if(dischargeTurboBoost == true) {
			heroBody.applyForce(turboBoost, heroBody.getPosition());
			dischargeTurboBoost = false;
		}
	}
	
	private void positionHero(){
		
		//Position and rotate kitt
		final Vector2 position = heroBody.getPosition().cpy();
		position.y += 0.55f;
		heroShape.setPosition(position.x * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT - heroShape.getWidth() * 0.5f, position.y * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT - heroShape.getHeight() * 0.5f);
		double angle = calculateWeightedAngle2();
		heroShape.setRotation((float) angle);
		
	}

	private void updateSuspension(){
		
		float fwy = FRONT_WHEEL_Y_OFFSET;
		float rwy = REAR_WHEEL_Y_OFFSET;
		
		if(!isInContact){
			fwy = FRONT_WHEEL_HANGING_Y_OFFSET;
			rwy = REAR_WHEEL_HANGING_Y_OFFSET;
		} 
		
		rearWheelShape.setPosition(REAR_WHEEL_X_OFFSET, rwy);
		frontWheelShape.setPosition(FRONT_WHEEL_X_OFFSET, fwy);
		
	}
	
	private boolean isInContact = false;
	@Override
	public void onUpdate(float pSecondsElapsed) {
		
		isInContact = isInContact();
		
		manageMinimumVelocity();
		manageAcceleration();
		updateSuspension();
		
		
		manageTurboBoost();
	
		positionHero();
		

		
		
		//if(frontWheelBody.getAngularVelocity()>1){
		//	particleSystem.setParticlesSpawnEnabled(true);
		//} else {
		//	particleSystem.setParticlesSpawnEnabled(false);
		//}
		
		//pointParticleEmitter.setCenter(frontWheelShape.getX(), frontWheelShape.getY());
		
	}
	
	public double bearing(Vector2 v)
	{
		return Math.atan(v.y/v.x);
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}
		
		

}
