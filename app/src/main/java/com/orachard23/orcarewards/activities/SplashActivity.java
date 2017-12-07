package com.orachard23.orcarewards.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.orachard23.orcarewards.RewardsApp;
import com.orachard23.orcarewards.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        final String TAG = SplashActivity.class.getName();
        Log.d(TAG, "onCreate: ");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (RewardsApp.getApp(SplashActivity.this).getUser() == null) {
                    Log.d(TAG, "run: going to log in");
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    Log.d(TAG, "run: going home");
                    HomeActivity.goHome(SplashActivity.this);
                }
                finish();
            }
        }, 3000);

    }
}
