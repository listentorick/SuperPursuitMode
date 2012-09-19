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
	
	private static float FRONT_WHEEL_X_OFFSET = 50f;
	private static float FRONT_WHEEL_Y_OFFSET = 7f;
	private static float FRONT_WHEEL_RADIUS = 12;

							
	
	private PhysicsConnector heroPhysicsConnector;
	
	private boolean jump;
	private boolean awake;
	
	public PlayerActor(float x, float y, Engine engine,  PhysicsWorld physicsWorld, LevelScene levelScene, TextureRegionLibrary textureRegionLibrary){
		this.x = x;
		this.y = y;
		this.engine = engine;
		this.levelScene = levelScene;
		this.physicsWorld = physicsWorld;
		this.textureRegionLibrary = textureRegionLibrary;
		
		heroBody = this.constructHero();
		
		heroShape = new Sprite(0,0,64,22, textureRegionLibrary.get(Textures.PlayerChasis), engine.getVertexBufferObjectManager());
		levelScene.attachChild(heroShape);
		
		scannerShape = new Sprite(SCANNER_X_OFFSET,SCANNER_Y_OFFSET,SCANNER_RADIUS*2,SCANNER_RADIUS*2, textureRegionLibrary.get(Textures.PlayerScanner), engine.getVertexBufferObjectManager());
		heroShape.attachChild(scannerShape);
		heroShape.setZIndex(-10);
		
		Sprite frontWheelShape = new Sprite(43,15,15,15, textureRegionLibrary.get(Textures.PlayerFrontWheel), engine.getVertexBufferObjectManager());
		heroShape.attachChild(frontWheelShape);
		
		Sprite rearWheelShape = new Sprite(8,14,17,17, textureRegionLibrary.get(Textures.PlayerRearWheel), engine.getVertexBufferObjectManager());
		heroShape.attachChild(rearWheelShape);
		//heroShape.setZIndex(-10);
	
		
		LoopEntityModifier scannerEntityModifier = new LoopEntityModifier( new SequenceEntityModifier(new ScaleModifier(1, 0.3f, 1f),new ScaleModifier(1, 1f, 0.3f)));
		scannerShape.registerEntityModifier(scannerEntityModifier);
	
		//heroPhysicsConnector = new PhysicsConnector(heroShape, heroBody, true, false);
	
		//physicsWorld.registerPhysicsConnector(heroPhysicsConnector);

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
		 
		- (void) limitVelocity {    
		    if (!_awake) return;
		 
		    const float minVelocityX = 5;
		    const float minVelocityY = -40;
		    b2Vec2 vel = _body->GetLinearVelocity();
		    if (vel.x < minVelocityX) {
		        vel.x = minVelocityX;
		    }
		    if (vel.y < minVelocityY) {
		        vel.y = minVelocityY;
		    }
		    _body->SetLinearVelocity(vel);
		}
	
	*/
	
	/*
	private Body constructChasisBody(){
		BodyDef chasisBodyDef = new BodyDef();
		chasisBodyDef.type = BodyType.DynamicBody;
		chasisBodyDef.position.x = (this.x + CHASIS_WIDTH/2)/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		chasisBodyDef.position.y = (this.y + CHASIS_HEIGHT/2)/ PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		Body chasisBody = this.physicsWorld.createBody(chasisBodyDef);
		
		chasisBody.createFixture(constructChasisFixtureDef());
		
		return chasisBody;
	}
	
	private FixtureDef constructChasisFixtureDef(){
		Vector2[] chasis = new Vector2[4];
		
		//chasis[0]=new Vector2(85,85);
		//chasis[1]=new Vector2(85,15);
		//chasis[2]=new Vector2(-85,15);
		//chasis[3]=new Vector2(-85,85);
		
		final float halfWidth = CHASIS_WIDTH * 0.5f / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		final float halfHeight = CHASIS_HEIGHT * 0.5f / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;

		PolygonShape boxPoly = new PolygonShape();
		boxPoly.setAsBox(halfWidth,halfHeight);//.set(chasis);
		
		FixtureDef fixtureDef = PhysicsFactory.createFixtureDef(CHASIS_DENSITY, CHASIS_RESTITUTION,CHASIS_FRICTION);	
		fixtureDef.shape = boxPoly; 
		fixtureDef.filter.groupIndex = PLAYER_ACTOR_GROUP_INDEX;
		
		return fixtureDef;
	}
	
	private Body constructWheelBody(float offsetX, float offsetY, float radius, float density, float elasticity, float friction){
		BodyDef wheelBodyDef = new BodyDef();
		wheelBodyDef.type = BodyType.DynamicBody;
		
		wheelBodyDef.position.x =(this.x + offsetX + radius)/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		wheelBodyDef.position.y = (this.y + offsetY + radius)/ PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		
		Body wheelBody = physicsWorld.createBody(wheelBodyDef);
		wheelBody.createFixture(constructWheelFixture(radius,density,elasticity,friction));
		
		return wheelBody;
	}
	
	private FixtureDef constructWheelFixture(float radius, float density, float elasticity, float friction){
		CircleShape circleShape = new CircleShape();
		circleShape.setRadius(radius / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
		FixtureDef fixtureDef = PhysicsFactory.createFixtureDef(density, elasticity, friction);
		fixtureDef.shape = circleShape; 
		fixtureDef.filter.groupIndex = PLAYER_ACTOR_GROUP_INDEX;
		return fixtureDef;
	}
	
	
	private Joint constructLineJoint(Body pWheel, Body pChassis, float pLowerTranslation, float pUpperTranslation, float maxSpeed, float maxForce){
		LineJointDef lineJointDef = new LineJointDef();
		lineJointDef.initialize(pChassis, pWheel, pWheel.getWorldCenter(),new Vector2(0f, 1f));
		lineJointDef.collideConnected = false;
		lineJointDef.lowerTranslation = pLowerTranslation;
		lineJointDef.upperTranslation = pUpperTranslation;
		lineJointDef.enableMotor = true;
		//
		lineJointDef.enableLimit = true;
		//lineJointDef.motorSpeed = 100f; //The spring portion of the shock absorber is modeled by creating friction using the motor variables
		//lineJointDef.maxMotorForce = 80f;
		
		lineJointDef.motorSpeed = maxSpeed; //10f; //The spring portion of the shock absorber is modeled by creating friction using the motor variables
		lineJointDef.maxMotorForce = maxForce; //100f;
		
		// jd.motorSpeed = 1.0f;
         //jd.maxMotorTorque = 10.0f;
         
		//lineJointDef.
		return physicsWorld.createJoint(lineJointDef);
	}
	
	
	private RevoluteJoint constructRevoluteJoint(Body pWheel, Body pChassis){
		RevoluteJointDef  revoluteJointDef = new RevoluteJointDef();
		revoluteJointDef.initialize(pChassis, pWheel, pWheel.getWorldCenter());
		revoluteJointDef.collideConnected = false;
		revoluteJointDef.maxMotorTorque = 150000;         // maximum torque
		//revoluteJointDef.motorSpeed = 100.0f;
		revoluteJointDef.enableMotor = true;
		//revoluteJointDef.enableLimit = true;
		
		//lineJointDef.lowerTranslation = pLowerTranslation;
		//lineJointDef.upperTranslation = pUpperTranslation;
		//lineJointDef.enableMotor = true;
		//lineJointDef.enableLimit = true;
		//lineJointDef.motorSpeed = 0;
		//lineJointDef.maxMotorForce = 10;
		return (RevoluteJoint) physicsWorld.createJoint(revoluteJointDef);
	}*/
	
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
	
	/*
	private void applyEngineForces(){
		
		Log.d("SPEED","" + chasisBody.getLinearVelocity().len());
		//rearWheelBody.applyTorque(2400);
		Log.d("MASS",chasisBody.getMass() + " " + rearWheelBody.getMass());
		
		
		//frontWheelBody.applyTorque(200);
		
		//if(pursuitMode==true) {
		//
		//}	
		if(chasisBody.getLinearVelocity().len()<maxSpeed){	
			
			rearWheelBody.applyTorque(200);
		//	frontWheelBody.applyTorque(1200);
			
			//rearWheelBody.applyForce(new Vector2(2000,0), forwardForceApplicationPoint);
		}	
		
		if(jump==true){
			//rearWheelRevoluteJoint.setMotorSpeed(1000f);
			
			
			jump = false;
			
			//if(isInContact()){ //used instead of mWheelInContact since mWheelInContact may be wrong!
				//Can we jump
				chasisBody.applyForce(new Vector2(0, -10000),chasisBody.getWorldCenter());
				Vector2 raiseNose = new Vector2(chasisBody.getWorldCenter().x-CHASIS_WIDTH/3,chasisBody.getWorldCenter().y);
				chasisBody.applyForce(new Vector2(0, -10),raiseNose);
			//}
				
		}
		
	}*/
	
/*
	public void setPursuitMode(boolean pursuitMode){
		this.pursuitMode = pursuitMode;
	}
	*/
	
	
	public void startJump(){
		jump = true;
	}
	
	public void endJump(){
		jump = false;
	}

	
	private void limitVelocity(){
		
		    if (!awake) return;
		 
		    float minVelocityX = 2;
		    float minVelocityY = -20;
		    
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
		
		Vector2 vel = heroBody.getLinearVelocity();
	
		double newAngle =  Math.toDegrees(bearing(vel)) ;
		double average = 0;
	
	    for(int i = 0; i < NUM_PREV_VELS; ++i) {
	    	average+=weightedAngle[i];
	    }
	    
	    average = average/NUM_PREV_VELS;
	    
	    if(isInContact()) {
	    	weightedAngle[_nextVel++] = newAngle;
	    	if (_nextVel >= NUM_PREV_VELS) _nextVel = 0;
	    }
		
	    return average;
	}
	

	
	@Override
	public void onUpdate(float pSecondsElapsed) {
		
	//	double angle = weightedAngle/NUM_PREV_VELS;
		
		
			
		double angle = calculateWeightedAngle2();
		//}
		
		if(jump==true) {
			
		//	Vector2 force =  new Vector2((float)Math.cos(angle), (float)Math.sin(angle));
			
		//	heroBody.applyForce(force.mul(500), heroBody.getPosition());
			
			heroBody.applyForce(new Vector2(40,-100), heroBody.getPosition());
		}
		
		limitVelocity();
		
		
		
		//final IShape shape = this.mShape;
		//final Body body = this.mBody;

		//if(this.mUpdatePosition) {
		
		//this.mShapeHalfBaseWidth = pAreaShape.getWidth() * 0.5f;
		//this.mShapeHalfBaseHeight = pAreaShape.getHeight() * 0.5f;
		
			final Vector2 position = heroBody.getPosition().cpy();
			
			//position.x += (2 * Math.cos(angle));
			//position.y += (2 * Math.sin(angle));
			//position.x += (2 * Math.cos(angle));
			position.y += 0.5f;
			//final float pixelToMeterRatio = this.mPixelToMeterRatio;
			heroShape.setPosition(position.x * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT - heroShape.getWidth() * 0.5f, position.y * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT - heroShape.getHeight() * 0.5f);
		//}

		//if(this.mUpdateRotation) {
		//	final float angle = body.getAngle();
		//	shape.setRotation(MathUtils.radToDeg(angle));
		//}
		
		
	
		
		heroShape.setRotation((float) angle);
		
		
		
		//vel.
		
		//Vector2 y = new Vector2(0, 1);
		
	//	float angle = y.cross(vel);
		
		//Log.d("ANGLE", angle + "");
		
		//Vector2 toVector2 = new Vector2(-1, 0);

		//vel.
		
	//	float ang = Vector2.Angle(fromVector2, toVector2);
		//Vector3 cross = Vector3.Cross(fromVector2, toVector2);

		//if (cross.z > 0)
		 //   ang = 360 - ang;

		//Debug.Log(ang);
		
		//Vector2 weightedVel = vel;
	    //float angle = ccpToAngle(ccp(vel.x, vel.y));  
	    //if (_awake) {  
	     //   self.rotation = -1 * CC_RADIANS_TO_DEGREES(angle);
	    //}
		
		
		
		
		// TODO Auto-generated method stub
		//applyEngineForces();
		
		//if(frontWheelBody.getAngularVelocity()>1){
		//	particleSystem.setParticlesSpawnEnabled(true);
		//} else {
		//	particleSystem.setParticlesSpawnEnabled(false);
		//}
		
		//pointParticleEmitter.setCenter(frontWheelShape.getX(), frontWheelShape.getY());
		
	}
	
	public double bearing(Vector2 v)
	{
	    // x and y args to atan2() swapped to rotate resulting angle 90 degrees
	    // (Thus angle in respect to +Y axis instead of +X axis)
	 //   double angle = Math.toDegrees(Math.atan2(v.x, v.y));

	    // Ensure result is in interval [0, 360)
	    // Subtract because positive degree angles go clockwise
	  // return (360-angle)% 360;
	   //return (360 -angle)%360 ;
		
		
		double angle = Math.atan(v.y/v.x);
		
		if(v.y>0 && v.x > 0) {
			Log.d("REGION", "1");
			return angle;// + Math.PI/2;
		} else if(v.y<0 && v.x > 0 ) {
			Log.d("REGION", "2");
			return angle ;//+ Math.PI/2;
		} else if(v.y<0 && v.x < 0 ) {
			Log.d("REGION", "3");
			return angle + Math.PI;
		} else {
			Log.d("REGION", "4");
			return angle +  Math.PI + Math.PI/2;
		}
		
	   
	   
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}
		
		

}
