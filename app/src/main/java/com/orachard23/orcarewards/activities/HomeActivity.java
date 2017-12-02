package com.orachard23.orcarewards.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.orachard23.orcarewards.R;
import com.orachard23.orcarewards.RewardsApp;
import com.orachard23.orcarewards.fragments.HomeFragment;
import com.orachard23.orcarewards.fragments.WatchFragment;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, WatchFragment.OnSequenceEndedListener {

    private NavigationView mNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RewardsApp.getApp(this).getAdController().initAdPlatforms();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        TextView emailText = mNavigationView.getHeaderView(0).findViewById(R.id.user_id_tv);
        emailText.setText(RewardsApp.getApp(this).getUser().getEmail());

        switchToHomeFragment();
    }

    public static void goHome(Context context) {
        Intent intent = new Intent(context, HomeActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer =findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (isHome()) {
                super.onBackPressed();
            } else {
                switchToHomeFragment();
            }
        }
    }

    private boolean isHome() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        return fragment instanceof HomeFragment;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_home:
                changeFragment(new HomeFragment(), HomeFragment.TAG);
                break;
            case R.id.nav_watch:
                changeFragment(new WatchFragment(), WatchFragment.TAG);
                break;
            case R.id.nav_logout:
                logout();
                break;
            default:
                throw new IllegalArgumentException("Illegal nav item");
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        RewardsApp.getApp(this).logout();
    }

    private void switchToHomeFragment() {
        if (!isHome()) {
            changeFragment(new HomeFragment(), HomeFragment.TAG);
            mNavigationView.setCheckedItem(R.id.nav_home);
        }
    }

    private void changeFragment(Fragment fragment, String tag) {
        Fragment availableFragment = getSupportFragmentManager().findFragmentByTag(tag);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (availableFragment != null) {
            transaction.replace(R.id.content_frame, availableFragment, tag);
        } else {
            transaction.replace(R.id.content_frame, fragment, tag);
        }
        transaction.commit();
    }

    @Override
    public void onSequenceEnded() {
        Toast.makeText(getApplicationContext(), "Point updated", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSequenceAborted() {
        Toast.makeText(getApplicationContext(), "Sequence aborted", Toast.LENGTH_SHORT).show();
        switchToHomeFragment();
    }
}
