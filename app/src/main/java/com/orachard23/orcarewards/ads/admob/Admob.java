package com.orachard23.orcarewards.ads.admob;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.orachard23.orcarewards.BuildConfig;
import com.orachard23.orcarewards.ads.Ad;
import com.orachard23.orcarewards.util.Constants;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by srikaram on 30-Nov-17.
 */

public class Admob extends Ad {
    
    public static final String TAG = Admob.class.getName();

    private String mDeviceId;

    private AdView mAdView;
    private boolean loaded;
    private boolean mResumed;

    public Admob(Context context) {
        super(context);
        Log.d(TAG, "Admob: ");
        constructDeviceId();
    }

    private void constructDeviceId() {
        String androidId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        mDeviceId = md5(androidId).toUpperCase();
    }

    private String md5(String androidId) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.update(androidId.getBytes(Charset.forName("US-ASCII")),0,androidId.length());
            byte[] magnitude = digest.digest();
            BigInteger bi = new BigInteger(1, magnitude);
            return String.format("%0" + (magnitude.length << 1) + "x", bi);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public void init() {
        Log.d(TAG, "init: ");
        mAdView.setAdListener(new AdmobAdListener());
    }

    @Override
    public void load() {
        Log.d(TAG, "load: " + mDeviceId);
        loaded = false;
        if (BuildConfig.USE_ORIGINAL_AD_ID) {
            mAdView.loadAd(new AdRequest.Builder().build());
        } else {
            AdRequest request = new AdRequest.Builder().addTestDevice(mDeviceId)
                    .build();
            mAdView.loadAd(request);
        }
    }

    @Override
    public boolean isLoaded() {
        Log.d(TAG, "isLoaded: ");
        return loaded;
    }

    @Override
    public void show() {
        Log.d(TAG, "show: ");
        if (!mResumed) {
            mAdView.setVisibility(View.VISIBLE);
            mAdView.resume();
            mResumed = true;
            mAdView.getAdListener().onAdOpened();
        }
    }

    @Override
    public void close() {
        if (mResumed) {
            mAdView.pause();
            mAdView.setVisibility(View.GONE);
            mResumed = false;
            mAdView.getAdListener().onAdClosed();
        }
    }

    @Override
    public void setView(View adView) {
        if (!(adView instanceof AdView)) {
            throw new IllegalArgumentException("Adview expected, but found " + adView.getClass().getSimpleName());
        }
        mAdView = (AdView) adView;
//        mAdView = new AdView(getContext());
//        mAdView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        ((ViewGroup) adView.getParent()).addView(mAdView);
//        mAdView.setVisibility(View.GONE);
    }

    private class AdmobAdListener extends AdListener {

        @Override
        public void onAdFailedToLoad(int i) {
            if (getAdListener() != null) {
                getAdListener().onAdFailedToLoad(i);
            }
        }

        @Override
        public void onAdClosed() {
            if (getAdListener() != null) {
                getAdListener().onAdEnded();
            }
        }

        @Override
        public void onAdOpened() {
            if (getAdListener() != null) {
                getAdListener().onAdOpened();
            }
        }

        @Override
        public void onAdLoaded() {
            loaded = true;
            if (getAdListener() != null) {
                getAdListener().onAdLoaded();
            }
        }

        @Override
        public void onAdLeftApplication() {
            if (getAdListener() != null) {
                getAdListener().onAdLeftApplication();
            }
        }

        @Override
        public void onAdClicked() {
            if (getAdListener() != null) {
                getAdListener().onAdClicked();
            }
        }
    }

}
