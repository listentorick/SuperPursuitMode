package org.usvsthem.knightrider.superpursuitmode;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.SmoothCamera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
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

public class SuperPursuitModeActivity extends SimpleBaseGameActivity  {
   
	static final int CAMERA_WIDTH = 800;
	static final int CAMERA_HEIGHT = 480;
	
	private TextureRegionLibrary textureRegionLibrary = new TextureRegionLibrary(10);
	
	@Override
	public EngineOptions onCreateEngineOptions() {

		  Camera mCamera = new SmoothCamera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT,1000,1000,1000);
		  return new EngineOptions(true, ScreenOrientation.LANDSCAPE_SENSOR,new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), mCamera);
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
		
		
		try {
			textureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0,0,0));
		} catch (TextureAtlasBuilderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); 
		}
		
		textureManager.loadTexture(textureAtlas);
		
	}

	@Override
	protected Scene onCreateScene() {
		LevelScene level = new LevelScene(this.getEngine(), textureRegionLibrary);
		return level;
	}
}