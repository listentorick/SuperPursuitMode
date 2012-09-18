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
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;

public class PlayerActor implements IUpdateHandler{
	
	
	private Engine engine;
	private PhysicsWorld physicsWorld;
	
	private Body chasisBody;
	private IAreaShape chasisShape;
	
	private Body rearWheelBody;
	private IAreaShape rearWheelShape;

	private Body frontWheelBody;
	private IAreaShape frontWheelShape;
	
	private Body scannerBody;
	private IAreaShape scannerShape;

	private boolean jump;

	private PhysicsConnector chasisPhysicsConnector;
	private PhysicsConnector rearWheelPhysicsConnector;
	private PhysicsConnector frontWheelPhysicsConnector;
	
	//wheels are MUCH heavier (factor of 10) than the chasis to help prevent the wheels 'dragging'

	private static float REAR_WHEEL_RADIUS 	= 17f;
	
	private static float REAR_WHEEL_FRICTION = 0.9f;
	private static float REAR_WHEEL_RESTITUTION 	= 0f;
	private static float REAR_WHEEL_DENSITY 	= 100f;//50f;//25f;
	
	private static float FRONT_WHEEL_RADIUS 	= 15f;
	
	private static float FRONT_WHEEL_FRICTION = 0.9f;
	private static float FRONT_WHEEL_RESTITUTION 	= 0f;
	private static float FRONT_WHEEL_DENSITY 	= 100f;//60f;

	private static float SCANNER_RADIUS =  25f;
	
	private static float CHASIS_WIDTH 	= 147f;
	private static float CHASIS_HEIGHT 	= 55f;
	
	private static float CHASIS_DENSITY =  10f;//5f; //25f;  
	private static float CHASIS_RESTITUTION = 0f;
	private static float CHASIS_FRICTION = 0.2f;
	
	private static float SCANNER_X_OFFSET = 115f;
	private static float SCANNER_Y_OFFSET = 15f;
	
	private static short PLAYER_ACTOR_GROUP_INDEX 	= -1;
	
	float maxSpeed = 30f;
	private boolean engineRunning = true;
	private Vector2 forwardForce = new Vector2(50, 0);
	private Vector2 forwardForceApplicationPoint = new Vector2(0,5);

	private float x;
	private float y;
	private LevelScene levelScene;
	
	private TextureRegionLibrary textureRegionLibrary;
	
	private boolean pursuitMode = false;
	
	
	public PlayerActor(float x, float y, Engine engine,  PhysicsWorld physicsWorld, LevelScene levelScene, TextureRegionLibrary textureRegionLibrary){
		this.x = x;
		this.y = y;
		this.engine = engine;
		this.levelScene = levelScene;
		this.physicsWorld = physicsWorld;
		this.textureRegionLibrary = textureRegionLibrary;
		
		//Chasis
		chasisBody = this.constructChasisBody();
		chasisShape = new Sprite(0,0,CHASIS_WIDTH,CHASIS_HEIGHT, textureRegionLibrary.get(Textures.PlayerChasis), engine.getVertexBufferObjectManager());
		levelScene.attachChild(chasisShape);
		chasisPhysicsConnector = new PhysicsConnector(chasisShape, chasisBody, true, true);
		physicsWorld.registerPhysicsConnector(chasisPhysicsConnector);

		//Rear wheel body
		rearWheelBody = this.constructWheelBody(18,33,REAR_WHEEL_RADIUS,REAR_WHEEL_DENSITY, REAR_WHEEL_RESTITUTION, REAR_WHEEL_FRICTION);
		rearWheelShape = new Sprite(0,0,REAR_WHEEL_RADIUS*2,REAR_WHEEL_RADIUS*2, textureRegionLibrary.get(Textures.PlayerRearWheel), engine.getVertexBufferObjectManager());
		levelScene.attachChild(rearWheelShape);
		rearWheelPhysicsConnector = new PhysicsConnector(rearWheelShape, rearWheelBody, true, true);
		physicsWorld.registerPhysicsConnector(rearWheelPhysicsConnector);
		
		//Front Wheel
		frontWheelBody = this.constructWheelBody(100,40,FRONT_WHEEL_RADIUS,FRONT_WHEEL_DENSITY, FRONT_WHEEL_RESTITUTION, FRONT_WHEEL_FRICTION);
		frontWheelShape = new Sprite(0,0,FRONT_WHEEL_RADIUS*2,FRONT_WHEEL_RADIUS*2, textureRegionLibrary.get(Textures.PlayerRearWheel), engine.getVertexBufferObjectManager());
		levelScene.attachChild(frontWheelShape);
		frontWheelPhysicsConnector = new PhysicsConnector(frontWheelShape, frontWheelBody, true, true);
		physicsWorld.registerPhysicsConnector(frontWheelPhysicsConnector);
	
		constructLineJoint(rearWheelBody,chasisBody,-0.05f,0.05f);
		constructLineJoint(frontWheelBody,chasisBody,-0.05f,0.05f);

		//construct scanner
		scannerShape = new Sprite(SCANNER_X_OFFSET,SCANNER_Y_OFFSET,SCANNER_RADIUS*2,SCANNER_RADIUS*2, textureRegionLibrary.get(Textures.PlayerScanner), engine.getVertexBufferObjectManager());
		chasisShape.attachChild(scannerShape);
		scannerShape.setZIndex(-1);
		
		LoopEntityModifier scannerEntityModifier = new LoopEntityModifier( new SequenceEntityModifier(new ScaleModifier(1, 0.3f, 1f),new ScaleModifier(1, 1f, 0.3f)));
		scannerShape.registerEntityModifier(scannerEntityModifier);
		
		constructDustParticleSystem();
	
	}
	
	public float getX() {
		return this.chasisShape.getX();
	}
	
	public float getY() {
		return this.chasisShape.getY();
	}
	
	public IEntity getPrincipleEntity(){
		return chasisShape;
	}
	
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
	
	
	private Joint constructLineJoint(Body pWheel, Body pChassis, float pLowerTranslation, float pUpperTranslation){
		LineJointDef lineJointDef = new LineJointDef();
		lineJointDef.initialize(pChassis, pWheel, pWheel.getWorldCenter(),new Vector2(0f, 1f));
		lineJointDef.collideConnected = false;
		lineJointDef.lowerTranslation = pLowerTranslation;
		lineJointDef.upperTranslation = pUpperTranslation;
		lineJointDef.enableMotor = true;
		//
		lineJointDef.enableLimit = true;
		lineJointDef.motorSpeed = 10f; //The spring portion of the shock absorber is modeled by creating friction using the motor variables
		lineJointDef.maxMotorForce = 200f;
		
		// jd.motorSpeed = 1.0f;
         //jd.maxMotorTorque = 10.0f;
         
		//lineJointDef.
		return physicsWorld.createJoint(lineJointDef);
	}
	
	/*
	private Joint constructRevoluteJoint(Body pWheel, Body pChassis){
		RevoluteJointDef  revoluteJointDef = new RevoluteJointDef();
		revoluteJointDef.initialize(pChassis, pWheel, pWheel.getWorldCenter());
		revoluteJointDef.collideConnected = false;
		
		//lineJointDef.lowerTranslation = pLowerTranslation;
		//lineJointDef.upperTranslation = pUpperTranslation;
		//lineJointDef.enableMotor = true;
		//lineJointDef.enableLimit = true;
		//lineJointDef.motorSpeed = 0;
		//lineJointDef.maxMotorForce = 10;
		return physicsWorld.createJoint(revoluteJointDef);
	}*/
	
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
	
	
	
	private void applyEngineForces(){
		
		Log.d("APPLYING TORQUE","");
		rearWheelBody.applyTorque(100);
		//frontWheelBody.applyTorque(200);
		
		//if(pursuitMode==true) {
		//	rearWheelBody.applyForce(new Vector2(200,0), forwardForceApplicationPoint);
		//}	
		//} else if(chasisBody.getLinearVelocity().len()<maxSpeed){	
			//rearWheelBody.applyForce(forwardForce, forwardForceApplicationPoint);
			//frontWheelBody.applyForce(forwardForce, forwardForceApplicationPoint);
		//}	
		
		if(jump==true){
			jump = false;
			
			//if(isInContact()){ //used instead of mWheelInContact since mWheelInContact may be wrong!
				//Can we jump
				chasisBody.applyForce(new Vector2(0, -75000),chasisBody.getWorldCenter());
			//}
		}
		
	}
	

	public void setPursuitMode(boolean pursuitMode){
		this.pursuitMode = pursuitMode;
	}
	
	public void jump(){
		jump = true;
	}

	@Override
	public void onUpdate(float pSecondsElapsed) {
		// TODO Auto-generated method stub
		applyEngineForces();
		
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
