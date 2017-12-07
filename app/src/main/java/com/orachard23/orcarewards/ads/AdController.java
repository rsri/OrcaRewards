package com.orachard23.orcarewards.ads;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.orachard23.orcarewards.ads.admob.Admob;
import com.orachard23.orcarewards.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by srikaram on 01-Dec-17.
 */

public class AdController {

    public static final String TAG = AdController.class.getName();

    private boolean isInited;
    private Context mContext;
    private Ad mCurrentAd;
    private List<AdListener> mAdListeners;
    private boolean showing;

    public AdController(Context context) {
        Log.d(TAG, "AdController: ");
        this.mContext = context;
        mAdListeners = new ArrayList<>();
    }

    public void addAdListener(AdListener listener) {
        Log.d(TAG, "addAdListener: ");
        mAdListeners.add(listener);
    }

    public void removeAdListener(AdListener listener) {
        Log.d(TAG, "removeAdListener: ");
        mAdListeners.remove(listener);
    }

    public void initAdPlatforms() {
        Log.d(TAG, "initAdPlatforms: ");
        if (!isInited) {
            // Init other ad platforms if required here
            MobileAds.initialize(mContext, Constants.getAdmobAdId());
            isInited = true;
        }
    }

    public void createNewAd(Context context) {
        Log.d(TAG, "createNewAd: ");
        // Modify logic here to introduce new Ad platforms
        mCurrentAd = new Admob(context);
        mCurrentAd.setAdListener(new AdControllerListener());
    }

    public void load() {
        Log.d(TAG, "load: ");
        mCurrentAd.load();
    }

    public boolean isLoaded() {
        Log.d(TAG, "isLoaded: ");
        return mCurrentAd.isLoaded();
    }

    public void close() {
        Log.d(TAG, "close: ");
        mCurrentAd.close();
    }

    public void show() {
        Log.d(TAG, "show: ");
        if (isLoaded()) {
            mCurrentAd.show();
        }
    }

    public boolean isShowing() {
        return showing;
    }

    public void setView(View adView) {
        mCurrentAd.setView(adView);
        mCurrentAd.init();
    }

    private class AdControllerListener extends AdListener {
        @Override
        public void onAdOpened() {
            Log.d(TAG, "onAdOpened: ");
            showing = true;
            for (AdListener listener : mAdListeners) {
                listener.onAdOpened();
            }
        }

        @Override
        public void onAdEnded() {
            Log.d(TAG, "onAdEnded: ");
            showing = false;
            for (AdListener listener : mAdListeners) {
                listener.onAdEnded();
            }
        }

        @Override
        public void onAdFailedToLoad(int i) {
            Log.d(TAG, "onAdFailedToLoad: ");
            showing = false;
            for (AdListener listener : mAdListeners) {
                listener.onAdFailedToLoad(i);
            }
        }

        @Override
        public void onAdLoaded() {
            Log.d(TAG, "onAdLoaded: ");
            for (AdListener listener : mAdListeners) {
                listener.onAdLoaded();
            }
        }

        @Override
        public void onAdLeftApplication() {
            Log.d(TAG, "onAdLeftApplication: ");
            for (AdListener adListener : mAdListeners) {
                adListener.onAdLeftApplication();
            }
        }

        @Override
        public void onAdClicked() {
            Log.d(TAG, "onAdClicked: ");
            for (AdListener adListener : mAdListeners) {
                adListener.onAdClicked();
            }
        }
    }
}
