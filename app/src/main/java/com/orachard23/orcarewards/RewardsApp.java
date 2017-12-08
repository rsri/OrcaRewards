package com.orachard23.orcarewards;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.orachard23.orcarewards.activities.SplashActivity;
import com.orachard23.orcarewards.ads.AdController;
import com.orachard23.orcarewards.controller.FirebaseController;
import com.orachard23.orcarewards.controller.PointsUpdater;
import com.orachard23.orcarewards.gif.GifController;

/**
 * Created by srikaram on 27-Nov-17.
 */

public class RewardsApp extends Application {

    public static final String TAG = RewardsApp.class.getName();

    private FirebaseUser mUser;

    private AdController mAdController;
    private GifController mGifController;

    private FirebaseController mFirebaseController;
    private PointsUpdater mPointsUpdater;

    @Override
    public void onCreate() {
        super.onCreate();
        mAdController = new AdController(this);
        mGifController = new GifController(this);

        mFirebaseController = new FirebaseController(this);
        mPointsUpdater = new PointsUpdater(mFirebaseController);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            setUser(auth.getCurrentUser());
        }
    }

    public static RewardsApp getApp(Context context) {
        return (RewardsApp) context.getApplicationContext();
    }

    public FirebaseUser getUser() {
        return mUser;
    }

    public PointsUpdater getPointsUpdater() {
        return mPointsUpdater;
    }

    public void setUser(FirebaseUser mUser) {
        if (this.mUser != null) {
            throw new IllegalStateException("User already exists.");
        }
        this.mUser = mUser;
        mPointsUpdater.init();
    }

    public AdController getAdController() {
        return mAdController;
    }

    public GifController getGifController() {
        return mGifController;
    }

    public FirebaseController getFirebaseController() {
        return mFirebaseController;
    }

    public void logout(Context activityContext) {
        Log.d(TAG, "logout: ");
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signOut();
        mUser = null;
        mAdController = new AdController(this);
        mGifController = new GifController(this);
        mPointsUpdater.reset();
        Intent clearedIntent = new Intent(RewardsApp.this, SplashActivity.class);
        activityContext.startActivity(clearedIntent);
    }
}


