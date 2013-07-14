/* @license
 * This file is part of the Game Closure SDK.
 *
 * The Game Closure SDK is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * The Game Closure SDK is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with the Game Closure SDK.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.tealeaf;

import android.content.pm.ActivityInfo;
import com.tealeaf.event.BackButtonEvent;
import com.tealeaf.event.LaunchTypeEvent;
import com.tealeaf.event.OnUpdatedEvent;
import com.tealeaf.event.PauseEvent;
import com.tealeaf.event.WindowFocusAcquiredEvent;
import com.tealeaf.event.WindowFocusLostEvent;
import com.tealeaf.event.JSUpdateNotificationEvent;
import com.tealeaf.event.MarketUpdateNotificationEvent;
import com.tealeaf.plugin.PluginManager;
import com.tealeaf.util.ILogger;
import com.tealeaf.advertisement.Ads;

import android.content.BroadcastReceiver;
import android.content.res.Configuration;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import android.support.v4.app.FragmentActivity;

import com.flurry.android.FlurryAgent;

/*
 * FIXME general things
 * there's /a lot/ of stuff in this class.  Much of it needs to be moved into
 * other places so that each unit only has one concern.
 *
 * Longer term, this activity should become the activity that always starts and
 * then figures out which game activity to run.
 */
public class TeaLeaf extends FragmentActivity {
	// flurry api key
	private static final String FLURRY_API_KEY = "CVMZ84CYM4M6PXBFCHCD";

	// admob mediation id
	private static final String ADMOB_API_KEY = "174d3728e9d14eb0";

	private TeaLeafOptions options;

	public TeaLeafGLSurfaceView glView;

	protected FrameLayout group;
	protected Overlay overlay;
	protected TextInputView textboxview;

	private Uri launchURI;

	private ContactList contactList;
	private SoundQueue soundQueue;
	protected LocalStorage localStorage;
	private ResourceManager resourceManager;
	private Settings settings;
	private IMenuButtonHandler menuButtonHandler;

	private ILogger remoteLogger;

	public boolean takeScreenshot = false;

	private static TeaLeaf instance = null;
	public static TeaLeaf get() { return instance; }

	private Ads ads;

	private boolean isRuntimeActive = false;

	public ILogger getLoggerInstance(Context context) {
		return new RemoteLogger(context);
	}

	public String getLaunchUri() { return launchURI.toString(); }
	public String getCodeHost() { return "http://" + options.getCodeHost() + ":" + options.getCodePort() + "/"; }
	public TeaLeafOptions getOptions() { return options; }
	public ILogger getRemoteLogger() { return remoteLogger; }
	public ContactList getContactList() { return contactList; }
	public SoundQueue getSoundQueue() { return soundQueue; }
	public Settings getSettings() { return settings; }
	public LocalStorage getLocalStorage() { return localStorage; }
	public ResourceManager getResourceManager() { return resourceManager; }

	// FIXME this shouldn't be necessary, but TeaLeafGLSurfaceView needs to know if there's a textbox layer
	public boolean hasTextInputView() {
		return textboxview != null;
	}

	// FIXME this shouldn't be necessary, but TeaLeafGLSurfaceView needs to know if there's an overlay
	public boolean hasOverlay() {
		return overlay != null;
	}

	public synchronized Overlay getOverlay() {
		if(overlay == null) {
			overlay = new Overlay(this);
			group.addView(overlay, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			overlay.bringToFront();
		}
		return overlay;
	}

	public synchronized TextInputView getTextInputView() {
		if(textboxview == null) {
			textboxview = new TextInputView(this);
			group.addView(textboxview, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			if(overlay != null) {
				overlay.bringToFront();
			}
		}
		return textboxview;
	}

	protected void moveViewsToFront() {
		// if the textbox view is available, it should be ontop of the gl view
		if(textboxview != null) {
			textboxview.bringToFront();
		}
		// if the overlay is available, it should be the absolute topmost layer
		if(overlay != null) {
			overlay.bringToFront();
		}
	}

	public void clearLocalStorage() {
		localStorage.clear();
	}

	public void clearTextures() {
		glView.clearTextures();
	}
	public void restartGLView() {
		glView.restart();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		if (getOptions().isDevelop()) {
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.debugmenu, menu);
			return true;
		} else {
			return false;
		}
	}

	@Override public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (!menuButtonHandler.onPress(id)) {
			return super.onOptionsItemSelected(item);
		} else {
			return true;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		configureActivity();
		String appID = findAppID();
		options = new TeaLeafOptions(this);

		PluginManager.callAll("onCreate", this, savedInstanceState);

		//check intent for test app info
		Bundle bundle = getIntent().getExtras();
		boolean isTestApp = false;

		if (bundle != null) {
		   isTestApp = bundle.getBoolean("isTestApp", false);

		   if (isTestApp) {
			   options.setAppID(appID);
			   boolean isPortrait = bundle.getBoolean("isPortrait", false);
			   if (isPortrait) {
				   setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			   } else {
				   setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			   }
			   options.setCodeHost(bundle.getString("hostValue"));
			   options.setCodePort(bundle.getInt("portValue"));
			   String simulateID = bundle.getString("simulateID");
			   options.setSimulateID(simulateID);
		   }
		}

		group = new FrameLayout(this);
		setContentView(group);

		settings = new Settings(this);
		remoteLogger = (ILogger)getLoggerInstance(this);

		checkUpdate();
		compareVersions();
		setLaunchUri();

		// defer building all of these things until we have the absolutely correct options
		logger.buildLogger(this, remoteLogger);
		resourceManager = new ResourceManager(this, options);
		contactList = new ContactList(this, resourceManager);
		soundQueue = new SoundQueue(this, resourceManager);
		localStorage = new LocalStorage(this, options);

		// start push notifications, but defer for 10 seconds to give us time to start up
		PushBroadcastReceiver.scheduleNext(this, 10);

		// ads
		this.ads = new Ads( this, TeaLeaf.ADMOB_API_KEY );

		glView = new TeaLeafGLSurfaceView( this, this.ads );

		this.ads.onCreate( glView );

		int orientation = getRequestedOrientation();
		android.view.Display display = getWindow().getWindowManager().getDefaultDisplay();
		int width = display.getWidth();
		int height = display.getHeight();
		if ((orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE &&  height > width) || (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT && width > height)) {
			int tempWidth = width;
			width = height;
			height = tempWidth;
		}

		android.widget.AbsoluteLayout absLayout = new android.widget.AbsoluteLayout(this);
		absLayout.setLayoutParams(new android.view.ViewGroup.LayoutParams(width, height));
		absLayout.addView(glView, new android.view.ViewGroup.LayoutParams(width, height));
		group.addView(absLayout);

		if (isTestApp) {
			startGame();
		}

		doFirstRun();
		remoteLogger.sendLaunchEvent(this);

		menuButtonHandler = MenuButtonHandlerFactory.getButtonHandler(this);
	}

	private void checkUpdate() {
		if(settings.isUpdateReady(options.getBuildIdentifier())) {
			if(settings.isMarketUpdate(options.getBuildIdentifier())) {
				// redirect the user to the market
				logger.log("{updates} Got a startup market update");
				EventQueue.pushEvent(new MarketUpdateNotificationEvent());
			}
		}
	}

	public void setServer(String host, int port) {
		options.setCodeHost(host);
		options.setTcpHost(host);
		options.setCodePort(port);

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		Editor editor = prefs.edit();
		editor.putString("@__prev_host__", host);
		editor.putInt("@__prev_port__", port);
		editor.commit();
		setLaunchUri();
	}

	@Override
	public void onNewIntent(Intent intent) {
		PluginManager.callAll("onNewIntent", intent);
	}

	private void getLaunchType(Intent intent) {
		Uri data = intent.getData();
		LaunchTypeEvent event;
		//launch type "notification" has been taken out
		if (data != null) {
			logger.log("{tealeaf} Launched with intent url:", data.toString());
			event = new LaunchTypeEvent("url", data.toString());
		} else {
			event = new LaunchTypeEvent("standard");
		}
		EventQueue.pushEvent(event);
	}

	public void onConfigurationChanged( Configuration config ) {
		super.onConfigurationChanged( config );
	}

	public void pauseGL() {
		logger.log( " * TeaLeaf::pauseGL()" );

		if( isRuntimeActive ) {
			isRuntimeActive = false;

			glView.onPause();

			// TODO: only dispatch this event when JS is running.
			if( glView.running() &&
				!glView.isResumeEventQueued() ) {

				String[] events = { new PauseEvent().pack() };

				// DANGER: Calling dispatchEvents() is NOT thread-safe. Doing it here because the GLThread is paused.
				NativeShim.dispatchEvents( events );

				glView.setRendererStateReloading();
			}
		}
	}

	public void resumeGL() {
		logger.log( " * TeaLeaf::resumeGL()" );

		if( !isRuntimeActive ) {
			isRuntimeActive = true;

			glView.queueResumeEvent();
			glView.onResume();
		}
	}

	private void resume() {
		soundQueue.onResume();
		resumeGL();

		EventQueue.pushEvent( new WindowFocusAcquiredEvent() );
	}

	private void pause() {
		soundQueue.onPause();
		pauseGL();

		String[] events = { new WindowFocusLostEvent().pack() };

		// DANGER: Calling dispatchEvents() is NOT thread-safe. Doing it here because the GLThread is paused.
		NativeShim.dispatchEvents( events );
	}

	/**
	 * Usage of onWindowFocusChanged is recommended over onPause/onResume. See https://github.com/cocos2d/cocos2d-x/pull/2658.
	 *
	 * @param hasFocus
	 */
	@Override
	public void onWindowFocusChanged( boolean hasFocus ) {
		super.onWindowFocusChanged( hasFocus );

		if( hasFocus ) {
			logger.log( "{focus} Gained focus" );

			resume();

		} else {
			logger.log( "{focus} Lost focus" );

			pause();
		}
	}

	@Override
	protected void onStart() {
		logger.log( " * TeaLeaf::onStart()" );

		super.onStart();

		FlurryAgent.onStartSession( this, TeaLeaf.FLURRY_API_KEY );

		PluginManager.callAll( "onStart" );
	}

	@Override
	protected void onResume() {
		logger.log( " * TeaLeaf::onResume()" );

		super.onResume();
		PluginManager.callAll( "onResume" );

		if( settings.isUpdateReady( options.getBuildIdentifier() ) ) {
			if( settings.isMarketUpdate( options.getBuildIdentifier() ) ) {
				// market update
				logger.log( "{updates} Got a resume market update" );
				EventQueue.pushEvent( new MarketUpdateNotificationEvent() );

			} else {
				// js update
				logger.log( "{updates} Got a resume JS update" );
				EventQueue.pushEvent( new JSUpdateNotificationEvent() );
			}
		}

		getLaunchType( getIntent() );
	}

	@Override
	protected void onPause() {
		logger.log( " * TeaLeaf::onPause()" );

		super.onPause();
		PluginManager.callAll( "onPause" );
	}

	@Override
	protected void onStop() {
		logger.log( " * TeaLeaf::onStop()" );

		super.onStop();

		FlurryAgent.onEndSession( this );

		PluginManager.callAll( "onStop" );
	}

	@Override
	public void onBackPressed() {
		Object [] objs = PluginManager.callAll("consumeOnBackPressed");

		boolean consume = true;
		for (Object o : objs) {
			if (((Boolean) o).booleanValue()) {
				consume = true;
				break;
			}
			consume = false;
		}

		if (consume) {
			EventQueue.pushEvent(new BackButtonEvent());

		} else {
			PluginManager.callAll("onBackPressed");
		}
	}

	@Override
	public void onDestroy() {
		logger.log( " * TeaLeaf::onDestroy()" );

		super.onDestroy();
		PluginManager.callAll("onDestroy");
        logger.log("{tealeaf} Destroy");
		glView.destroy();
		NativeShim.reset();
	}

	private String findAppID() {
		String appid = getIntent().getStringExtra("appid");
		if(appid != null) {
			return appid;
		}
		// FIXME HACK: find a better way to determine the appID
		try {
			Bundle metaData = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA).metaData;
			return metaData.containsKey("appID") ? metaData.getString("appID") : "tealeaf";
		} catch (NameNotFoundException e) {
			logger.log(e);
			return "tealeaf";
		}
	}

	private void configureActivity() {
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
	}

	private void compareVersions() {
		String newVersion = options.getBuildIdentifier();
		String oldVersion = settings.getString("version", null);
		boolean firstRun = oldVersion == null;
		if(!newVersion.equals(oldVersion)) {
			// new version, send an event and clean the old files
			EventQueue.pushEvent(new OnUpdatedEvent(oldVersion, newVersion, firstRun));
			settings.setString("version", newVersion);
		}
	}

	private void doFirstRun() {
		if (settings.isFirstRun()) {
			remoteLogger.sendFirstLaunchEvent(this);
			settings.markFirstRun();
		}
	}

	private void setLaunchUri() {
		launchURI = getIntent().getData();
		if (launchURI == null) {
			launchURI = Uri.parse(getCodeHost() + options.getAppID() + "/");
		} else {
			if(launchURI.isRelative()) {
				launchURI = Uri.parse("http:" + launchURI.toString());
			} else {
				launchURI = Uri.parse(launchURI.toString().replace(launchURI.getScheme(), "http"));
			}
		}
	}

	public void startGame() {
		glView.start();
		glView.setVisibility(View.VISIBLE);
	}

	protected void reset() {
		group.removeView(glView);
		glView.destroy();
		NativeShim.reset();
		Intent intent = getIntent();
		overridePendingTransition(0, 0);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		finish();
		overridePendingTransition(0, 0);
		startActivity(intent);
	}

	// TODO: can this be called after your activity is recycled, meaning we're never going to see these events?
	protected void onActivityResult(int request, int result, Intent data) {
		super.onActivityResult(request, result, data);
		PluginManager.callAll("onActivityResult", request, result, data);

		int id = (request & 0xFFFFFF);
		request = request >> 24;
		switch(request) {
			case PhotoPicker.CAPTURE_IMAGE:
				if(result == RESULT_OK) {
					glView.getTextureLoader().saveCameraPhoto(id, (Bitmap)data.getExtras().get("data"));
					glView.getTextureLoader().finishCameraPicture(id);
				} else {
					glView.getTextureLoader().failedCameraPicture(id);
				}
				break;
			case PhotoPicker.PICK_IMAGE:
				if(result == RESULT_OK) {
					Uri selectedimage = data.getData();
					String[] filepathcolumn = {android.provider.MediaStore.Images.Media.DATA};
					android.database.Cursor cursor = getContentResolver().query(selectedimage, filepathcolumn, null, null, null);
					cursor.moveToFirst();
					int columnindex = cursor.getColumnIndex(filepathcolumn[0]);
					String filepath = cursor.getString(columnindex);
					cursor.close();
					glView.getTextureLoader().saveGalleryPicture(id, BitmapFactory.decodeFile(filepath));
					glView.getTextureLoader().finishGalleryPicture(id);
				} else {
					glView.getTextureLoader().failedGalleryPicture(id);
				}
				break;
		}
	}

	public void reload() {}

	public boolean onKeyDown( int keyCode, KeyEvent event ) {
		if( keyCode == KeyEvent.KEYCODE_BACK &&
			event.getRepeatCount() == 0 ) {

			glView.sendKeyDownBack();
		}

		return super.onKeyDown( keyCode, event );
	}

	public boolean onKeyUp( int keyCode, KeyEvent event ) {
		if( keyCode == KeyEvent.KEYCODE_BACK ) {
			glView.sendKeyUpBack();
		}

		return super.onKeyUp( keyCode, event );
	}

	static {
		System.loadLibrary("tealeaf");
	}
}
