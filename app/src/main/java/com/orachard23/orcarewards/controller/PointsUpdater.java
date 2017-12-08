package com.orachard23.orcarewards.controller;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by srikaram on 05-Dec-17.
 */

public class PointsUpdater {

    public static final String TAG = PointsUpdater.class.getName();

    private List<OnPointUpdatedListener> mListeners;
    private FirebaseController mFirebaseController;

    private int mPoints;

    public PointsUpdater(FirebaseController firebaseController) {
        this.mFirebaseController = firebaseController;
        mListeners = new ArrayList<>();
    }

    public void reset() {
        mPoints = 0;
        mListeners.clear();
    }

    public void addListener(OnPointUpdatedListener pointUpdatedListener) {
        mListeners.add(pointUpdatedListener);
    }

    public void removeListener(OnPointUpdatedListener pointUpdatedListener) {
        mListeners.remove(pointUpdatedListener);
    }

    public void incrementPoint() {
        mPoints = mFirebaseController.incrementPoint(mPoints);
        for (OnPointUpdatedListener listener : mListeners) {
            listener.onPointUpdated(mPoints);
        }
    }

    public void updatePointAfterRedemption(int redeemPoints) {
        mPoints -= redeemPoints;
        mFirebaseController.updatePointAfterRedemption(mPoints);
        for (OnPointUpdatedListener listener : mListeners) {
            listener.onPointUpdated(mPoints);
        }
    }

    private void setPoints(int points) {
        this.mPoints = points;
        for (OnPointUpdatedListener listener : mListeners) {
            listener.onPointUpdated(mPoints);
        }
    }

    public int getPoints() {
        return mPoints;
    }

    public void init() {
        mFirebaseController.getPoint(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Integer value = dataSnapshot.getValue(Integer.class);
                setPoints(value == null ? 0 : value);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: " + databaseError.toString());
                setPoints(0);
            }
        });
    }

    public interface OnPointUpdatedListener {
        void onPointUpdated(int newPoint);
    }
}
