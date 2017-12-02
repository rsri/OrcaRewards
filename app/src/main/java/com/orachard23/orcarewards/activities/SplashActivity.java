package com.orachard23.orcarewards.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.orachard23.orcarewards.RewardsApp;
import com.orachard23.orcarewards.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (RewardsApp.getApp(SplashActivity.this).getUser() == null) {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    HomeActivity.goHome(SplashActivity.this);
                }
                finish();
            }
        }, 3000);

    }
}
