package com.tealeaf.advertisement;


import android.support.v4.app.FragmentActivity;

import com.google.ads.*;
import com.chartboost.sdk.*;

import com.tealeaf.*;


/**
 * Handles advertisment
 */
public class Ads implements AdListener {
	private Chartboost chartboost;
	private FragmentActivity activity;
	private InterstitialAd interstitial;
	private TeaLeafGLSurfaceView view;
	private String admobApiKey;

	public Ads( FragmentActivity activity, String admobApiKey ) {
		this.activity = activity;
		this.admobApiKey = admobApiKey;
	}

	public void onCreate( TeaLeafGLSurfaceView view ) {
		this.view = view;

		interstitial = new InterstitialAd( this.activity, this.admobApiKey );

		interstitial.setAdListener( this );
	}

	public void loadInterstitial() {
		interstitial.loadAd( new AdRequest() );
	}

	public void showInterstitial() {
		interstitial.show();
	}

	@Override
	public void onReceiveAd( Ad ad ) {
//		logger.log( "Ads::onReceiveAd()" );
	}

	@Override
	public void onFailedToReceiveAd( Ad ad, AdRequest.ErrorCode error ) {
//		logger.log( "Ads::onFailedToReceiveAd(): " + error );
	}

	@Override
	public void onPresentScreen( Ad ad ) {
//		logger.log( "Ads::onPresentScreen()" );

		view.pauseRuntime();
	}

	@Override
	public void onDismissScreen(Ad ad) {
//		logger.log( "Ads::onDismissScreen()" );

		view.resumeRuntime();
	}

	@Override
	public void onLeaveApplication( Ad ad ) {
//		logger.log( "Ads::onLeaveApplication()" );

		view.resumeRuntime();
	}
}
