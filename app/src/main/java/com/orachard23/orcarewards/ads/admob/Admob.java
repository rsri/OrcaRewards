package com.orachard23.orcarewards.ads.admob;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
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

    private InterstitialAd mInterstitialAd;
    private String mDeviceId;

    public Admob(Context context) {
        super(context);
        Log.d(TAG, "Admob: ");
        constructDeviceId();
    }

    private void constructDeviceId() {
        String androidId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        mDeviceId = md5(androidId);
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
        mInterstitialAd = new InterstitialAd(getContext());
        mInterstitialAd.setAdUnitId(Constants.getAdmobAdUnitId());
        mInterstitialAd.setAdListener(new AdmobAdListener());
    }

    @Override
    public void load() {
        Log.d(TAG, "load: ");
        if (BuildConfig.USE_ORIGINAL_AD_ID) {
            mInterstitialAd.loadAd(new AdRequest.Builder().build());
        } else {
            AdRequest request = new AdRequest.Builder().addTestDevice(mDeviceId)
                    .build();
            mInterstitialAd.loadAd(request);
        }
    }

    @Override
    public boolean isLoaded() {
        Log.d(TAG, "isLoaded: ");
        return mInterstitialAd.isLoaded();
    }

    @Override
    public void show() {
        Log.d(TAG, "show: ");
        mInterstitialAd.show();
    }

    private class AdmobAdListener extends AdListener {

        @Override
        public void onAdOpened() {
            Log.d(TAG, "onAdOpened: ");
            if (getAdListener() != null) {
                getAdListener().onAdOpened();
            }
        }

        @Override
        public void onAdClosed() {
            Log.d(TAG, "onAdClosed: ");
            if (getAdListener() != null) {
                getAdListener().onAdClosed();
            }
        }

        @Override
        public void onAdFailedToLoad(int i) {
            Log.d(TAG, "onAdFailedToLoad: ");
            if (getAdListener() != null) {
                getAdListener().onAdFailedToLoad(i);
            }
        }

        @Override
        public void onAdLoaded() {
            Log.d(TAG, "onAdLoaded: ");
            if (getAdListener() != null) {
                getAdListener().onAdLoaded();
            }
        }
    }
}
