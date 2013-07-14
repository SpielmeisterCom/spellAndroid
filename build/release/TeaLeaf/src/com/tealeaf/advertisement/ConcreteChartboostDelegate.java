package com.tealeaf.advertisement;


import com.google.ads.mediation.customevent.CustomEventInterstitialListener;

import com.chartboost.sdk.ChartboostDelegate;

import com.tealeaf.*;


public class ConcreteChartboostDelegate implements ChartboostDelegate {
	private CustomEventInterstitialListener listener;

	public ConcreteChartboostDelegate( CustomEventInterstitialListener listener ) {
		this.listener = listener;
	}

	/**
	 * shouldDisplayInterstitial( String location )
	 *
	 * This is used to control when an interstitial should or should not be displayed
	 * If you should not display an interstitial, return FALSE
	 *
	 * For example: during gameplay, return FALSE.
	 *
	 * Is fired on:
	 * - showInterstitial()
	 * - Interstitial is loaded & ready to display
	 */
	@Override
	public boolean shouldDisplayInterstitial( String location ) {
//		logger.log( "ConcreteChartboostDelegate::shouldDisplayInterstitial()" );

		return true;
	}

	/**
	 * shouldRequestInterstitial( String location )
	 *
	 * This is used to control when an interstitial should or should not be requested
	 * If you should not request an interstitial from the server, return FALSE
	 *
	 * For example: user should not see interstitials for some reason, return FALSE.
	 *
	 * Is fired on:
	 * - cacheInterstitial()
	 * - showInterstitial() if no interstitial is cached
	 *
	 * Notes:
	 * - We do not recommend excluding purchasers with this delegate method
	 * - Instead, use an exclusion list on your campaign so you can control it on the fly
	 */
	@Override
	public boolean shouldRequestInterstitial( String location ) {
//		logger.log( "ConcreteChartboostDelegate::shouldRequestInterstitial()" );

		return true;
	}

	/**
	 * didCacheInterstitial( String location )
	 *
	 * Passes in the location name that has successfully been cached
	 *
	 * Is fired on:
	 * - cacheInterstitial() success
	 * - All assets are loaded
	 *
	 * Notes:
	 * - Similar to this is: cb.hasCachedInterstitial( String location )
	 * Which will return true if a cached interstitial exists for that location
	 */
	@Override
	public void didCacheInterstitial( String location ) {
//		logger.log( "ConcreteChartboostDelegate::didCacheInterstitial()" );
		// Save which location is ready to display immediately

		listener.onReceivedAd();
	}

	/**
	 * didFailToLoadInterstitial( String location )
	 *
	 * This is called when an interstitial has failed to load for any reason
	 *
	 * Is fired on:
	 * - cacheInterstitial() failure
	 * - showInterstitial() failure if no interstitial was cached
	 *
	 * Possible reasons:
	 * - No network connection
	 * - No publishing campaign matches for this user (go make a new one in the dashboard)
	 */
	@Override
	public void didFailToLoadInterstitial( String location ) {
//		logger.log( "ConcreteChartboostDelegate::didFailToLoadInterstitial()" );
		// Show a house ad or do something else when a chartboost interstitial fails to load

//		this.view.resumeRuntime();

		listener.onFailedToReceiveAd();
	}

	/**
	 * didDismissInterstitial( String location )
	 *
	 * This is called when an interstitial is dismissed
	 *
	 * Is fired on:
	 * - Interstitial click
	 * - Interstitial close
	 *
	 * #Pro Tip: Use the code below to immediately re-cache interstitials
	 */
	@Override
	public void didDismissInterstitial( String location ) {
//		logger.log( "ConcreteChartboostDelegate::didDismissInterstitial()" );
		// Immediately re-caches an interstitial
//		cb.cacheInterstitial(location);

//		this.view.resumeRuntime();
	}

	/**
	 * didCloseInterstitial( String location )
	 *
	 * This is called when an interstitial is closed
	 *
	 * Is fired on:
	 * - Interstitial close
	 */
	@Override
	public void didCloseInterstitial( String location ) {
//		logger.log( "ConcreteChartboostDelegate::didCloseInterstitial()" );
		// Know that the user has closed the interstitial

		listener.onDismissScreen();
	}

	/**
	 * didClickInterstitial( String location )
	 *
	 * This is called when an interstitial is clicked
	 *
	 * Is fired on:
	 * - Interstitial click
	 */
	@Override
	public void didClickInterstitial( String location ) {
//		logger.log( "ConcreteChartboostDelegate::didClickInterstitial()" );
		// Know that the user has clicked the interstitial

		listener.onLeaveApplication();
	}

	/**
	 * didShowInterstitial( String location )
	 *
	 * This is called when an interstitial has been successfully shown
	 *
	 * Is fired on:
	 * - showInterstitial() success
	 */
	@Override
	public void didShowInterstitial( String location ) {
//		logger.log( "ConcreteChartboostDelegate::didShowInterstitial()" );
		// Know that the user has seen the interstitial

		listener.onPresentScreen();
	}

	/**
	 * More Apps delegate methods
	 */

	/**
	 * shouldDisplayLoadingViewForMoreApps()
	 *
	 * Return FALSE to prevent the pretty More-Apps loading screen
	 *
	 * Is fired on:
	 * - showMoreApps()
	 */
	@Override
	public boolean shouldDisplayLoadingViewForMoreApps() {
//		logger.log( "ConcreteChartboostDelegate::shouldDisplayLoadingViewForMoreApps()" );
		return false;
	}

	/**
	 * shouldRequestMoreApps()
	 *
	 * Return FALSE to prevent a More-Apps page request
	 *
	 * Is fired on:
	 * - cacheMoreApps()
	 * - showMoreApps() if no More-Apps page is cached
	 */
	@Override
	public boolean shouldRequestMoreApps() {
//		logger.log( "ConcreteChartboostDelegate::shouldRequestMoreApps()" );
		return false;
	}

	/**
	 * shouldDisplayMoreApps()
	 *
	 * Return FALSE to prevent the More-Apps page from displaying
	 *
	 * Is fired on:
	 * - showMoreApps()
	 * - More-Apps page is loaded & ready to display
	 */
	@Override
	public boolean shouldDisplayMoreApps() {
//		logger.log( "ConcreteChartboostDelegate::shouldDisplayMoreApps()" );
		return false;
	}

	/**
	 * didFailToLoadMoreApps()
	 *
	 * This is called when the More-Apps page has failed to load for any reason
	 *
	 * Is fired on:
	 * - cacheMoreApps() failure
	 * - showMoreApps() failure if no More-Apps page was cached
	 *
	 * Possible reasons:
	 * - No network connection
	 * - No publishing campaign matches for this user (go make a new one in the dashboard)
	 */
	@Override
	public void didFailToLoadMoreApps() {
//		logger.log( "ConcreteChartboostDelegate::didFailToLoadMoreApps()" );
		// Do something else when the More-Apps page fails to load
	}

	/**
	 * didCacheMoreApps()
	 *
	 * Is fired on:
	 * - cacheMoreApps() success
	 * - All assets are loaded
	 */
	@Override
	public void didCacheMoreApps() {
//		logger.log( "ConcreteChartboostDelegate::didCacheMoreApps()" );
		// Know that the More-Apps page is cached and ready to display
	}

	/**
	 * didDismissMoreApps()
	 *
	 * This is called when the More-Apps page is dismissed
	 *
	 * Is fired on:
	 * - More-Apps click
	 * - More-Apps close
	 */
	@Override
	public void didDismissMoreApps() {
//		logger.log( "ConcreteChartboostDelegate::didDismissMoreApps()" );
		// Know that the More-Apps page has been dismissed
	}

	/**
	 * didCloseMoreApps()
	 *
	 * This is called when the More-Apps page is closed
	 *
	 * Is fired on:
	 * - More-Apps close
	 */
	@Override
	public void didCloseMoreApps() {
//		logger.log( "ConcreteChartboostDelegate::didCloseMoreApps()" );
		// Know that the More-Apps page has been closed
	}

	/**
	 * didClickMoreApps()
	 *
	 * This is called when the More-Apps page is clicked
	 *
	 * Is fired on:
	 * - More-Apps click
	 */
	@Override
	public void didClickMoreApps() {
//		logger.log( "ConcreteChartboostDelegate::didClickMoreApps()" );
		// Know that the More-Apps page has been clicked

	}

	/*
	 * didShowMoreApps()
	 *
	 * This is called when the More-Apps page has been successfully shown
	 *
	 * Is fired on:
	 * - showMoreApps() success
	 */
	@Override
	public void didShowMoreApps() {
//		logger.log( "ConcreteChartboostDelegate::didShowMoreApps()" );
		// Know that the More-Apps page has been presented on the screen
	}

	/**
	 * shouldRequestInterstitialsInFirstSession()
	 *
	 * Return FALSE if the user should not request interstitials until the 2nd startSession()
	 *
	 */
	@Override
	public boolean shouldRequestInterstitialsInFirstSession() {
//		logger.log( "ConcreteChartboostDelegate::shouldRequestInterstitialsInFirstSession()" );
		return true;
	}
}
