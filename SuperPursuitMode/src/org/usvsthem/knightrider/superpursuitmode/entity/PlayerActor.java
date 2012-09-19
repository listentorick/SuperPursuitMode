package org.usvsthem.knightrider.superpursuitmode.entity;

import java.util.ArrayList;

import org.andengine.audio.sound.SoundManager;
import org.andengine.engine.Engine;
import org.andengine.engine.handler.IUpdateHandler;
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
import org.usvsthem.knightrider.superpursuitmode.Constants;
import org.usvsthem.knightrider.superpursuitmode.Textures;

import android.opengl.GLES20;
import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
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

	private float x;
	private float y;
	private LevelScene levelScene;
	private TextureRegionLibrary textureRegionLibrary;
		
	private static float HERO_RADIUS = 16f;
	private static float HERO_DENSITY = 1.0f;
	private static float HERO_RESTITUTION = 0.0f;
	private static float HERO_FRICTION = 0.5f;
	
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
		
		heroShape = new Sprite(0,0,HERO_RADIUS * 2,HERO_RADIUS * 2, textureRegionLibrary.get(Textures.PlayerChasis), engine.getVertexBufferObjectManager());
		levelScene.attachChild(heroShape);
	
		heroPhysicsConnector = new PhysicsConnector(heroShape, heroBody, true, true);
		physicsWorld.registerPhysicsConnector(heroPhysicsConnector);

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
		chasisBodyDef.linearDamping = 0.5f;
		chasisBodyDef.angularDamping = 0.5f;
		chasisBodyDef.fixedRotation  = true;
		
		chasisBodyDef.position.x = (this.x + HERO_RADIUS)/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		chasisBodyDef.position.y = (this.y + HERO_RADIUS)/ PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		Body chasisBody = this.physicsWorld.createBody(chasisBodyDef);
		
		CircleShape circleShape = new CircleShape();
		circleShape.setRadius(HERO_RADIUS / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
		FixtureDef fixtureDef = PhysicsFactory.createFixtureDef(HERO_DENSITY, HERO_RESTITUTION, HERO_FRICTION);
		fixtureDef.shape = circleShape; 
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
		 
		    float minVelocityX = 5;
		    float minVelocityY = -40;
		    
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
	
	@Override
	public void onUpdate(float pSecondsElapsed) {
		
		
		if(jump==true) {
			
			heroBody.applyForce(new Vector2(5,-50), heroBody.getPosition());
		}
		
		limitVelocity();
		
		
		
		
		
		// TODO Auto-generated method stub
		//applyEngineForces();
		
		//if(frontWheelBody.getAngularVelocity()>1){
		//	particleSystem.setParticlesSpawnEnabled(true);
		//} else {
		//	particleSystem.setParticlesSpawnEnabled(false);
		//}
		
		//pointParticleEmitter.setCenter(frontWheelShape.getX(), frontWheelShape.getY());
		
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}
		
		

}
