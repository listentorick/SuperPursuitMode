package org.usvsthem.knightrider.superpursuitmode;

import org.andengine.engine.Engine;
import org.andengine.engine.FixedStepEngine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.SmoothCamera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.util.FPSLogger;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TextureRegionLibrary;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.usvsthem.knightrider.superpursuitmode.entity.LevelScene;

import android.app.Activity;
import android.os.Bundle;
import android.os.Debug;

public class SuperPursuitModeActivity extends SimpleBaseGameActivity  {
   
	static final int CAMERA_WIDTH = 800;
	static final int CAMERA_HEIGHT = 480;
	
	private TextureRegionLibrary textureRegionLibrary = new TextureRegionLibrary(10);
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		  Camera mCamera = new SmoothCamera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT,1000,1000,1000);
		  return new EngineOptions(true, ScreenOrientation.LANDSCAPE_SENSOR,new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), mCamera);
	}
	
	@Override
	public Engine onCreateEngine(final EngineOptions pEngineOptions) {
		return new FixedStepEngine(pEngineOptions,60);
	}
	
	private void createTextureRegion(BuildableBitmapTextureAtlas textureAtlas, String path, int textureId){
		
		TextureRegion textureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, this, path);
		textureRegionLibrary.put(textureId, textureRegion);	
	}

	@Override
	protected void onCreateResources() {
		// TODO Auto-generated method stub
		
		TextureManager textureManager = this.mEngine.getTextureManager();
		
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");       
		BuildableBitmapTextureAtlas textureAtlas = new BuildableBitmapTextureAtlas(textureManager, 1024, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		
		createTextureRegion(textureAtlas, "kitt_chasis.png",Textures.PlayerChasis);
		createTextureRegion(textureAtlas, "rear_wheel.png",Textures.PlayerRearWheel);
		createTextureRegion(textureAtlas, "front_wheel.png",Textures.PlayerFrontWheel);
		createTextureRegion(textureAtlas, "scanner.png",Textures.PlayerScanner);
		createTextureRegion(textureAtlas, "dust.png",Textures.DUST_PARTICLE);
		createTextureRegion(textureAtlas, "cactus.png",Textures.CACTUS_1);
		createTextureRegion(textureAtlas, "cactus2.png",Textures.CACTUS_2);
		createTextureRegion(textureAtlas, "cactus3.png",Textures.CACTUS_3);
		createTextureRegion(textureAtlas, "cactus4.png",Textures.CACTUS_4);
		
		
		createTextureRegion(textureAtlas, "background.png",Textures.SKY);
		
		createTextureRegion(textureAtlas, "karr_chasis.png",Textures.KARR_CHASIS);
		createTextureRegion(textureAtlas, "karr_scanner.png",Textures.KARR_SCANNER);
		createTextureRegion(textureAtlas, "star_foreground.png", Textures.STAR_FOREGROUND);
		createTextureRegion(textureAtlas, "star_middle.png", Textures.STAR_MIDDLE);
		createTextureRegion(textureAtlas, "star_background.png", Textures.STAR_BACKGROUND);
		
		try {
			textureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0,1,1));
		} catch (TextureAtlasBuilderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); 
		}
		
		textureManager.loadTexture(textureAtlas);
		
	}

	@Override
	protected Scene onCreateScene() {
		//Debug.startMethodTracing();
		//this.getEngine().registerUpdateHandler(new FPSLogger());
		LevelScene level = new LevelScene(this.getEngine(), textureRegionLibrary);
		return level;
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		//Debug.stopMethodTracing();
		super.onDestroy();
	}
}