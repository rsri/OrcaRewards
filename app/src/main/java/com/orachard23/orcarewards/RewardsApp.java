package com.orachard23.orcarewards;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.orachard23.orcarewards.activities.SplashActivity;
import com.orachard23.orcarewards.ads.AdController;
import com.orachard23.orcarewards.gif.GifController;

/**
 * Created by srikaram on 27-Nov-17.
 */

public class RewardsApp extends Application {

    private FirebaseUser mUser;

    private AdController mAdController;
    private GifController mGifController;

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        mUser = auth.getCurrentUser();

        mAdController = new AdController(this);
        mGifController = new GifController(this);
    }

    public static RewardsApp getApp(Context context) {
        return (RewardsApp) context.getApplicationContext();
    }

    public FirebaseUser getUser() {
        return mUser;
    }

    public void setUser(FirebaseUser mUser) {
        if (this.mUser != null) {
            throw new IllegalStateException("User already exists.");
        }
        this.mUser = mUser;
    }

    public AdController getAdController() {
        return mAdController;
    }

    public GifController getGifController() {
        return mGifController;
    }

    public void logout() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signOut();
        mUser = null;
        Intent clearedIntent = new Intent(RewardsApp.this, SplashActivity.class);
        startActivity(clearedIntent);
    }
}


