package com.orachard23.orcarewards.controller;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orachard23.orcarewards.RewardsApp;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by srikaram on 04-Dec-17.
 */

public class FirebaseController {

    private final Context mContext;

    public FirebaseController(Context context) {
        this.mContext = context;
    }

    public void signUpUser(String uid) {
        DatabaseReference usersRef = getUsersRef();
        Map<String, Object> accountInclusionData = new HashMap<>();
        accountInclusionData.put(uid, 0);
        usersRef.updateChildren(accountInclusionData);
    }

    int incrementPoint(int point) {
        int updatedPoint = point+1;
        DatabaseReference userRef = getUserRef();
        userRef.setValue(updatedPoint);
        return updatedPoint;
    }

    void updatePointAfterRedemption(int updatedPoint) {
        DatabaseReference userRef = getUserRef();
        userRef.setValue(updatedPoint);
    }

    public void getPoint(ValueEventListener listener) {
        DatabaseReference userRef = getUserRef();
        userRef.addListenerForSingleValueEvent(listener);
    }

    private DatabaseReference getUsersRef() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        return database.getReference().child("users");
    }

    private DatabaseReference getUserRef() {
        String uid = RewardsApp.getApp(mContext).getUser().getUid();
        DatabaseReference usersRef = getUsersRef();
        return usersRef.child(uid);
    }

    public void getNumberOfGifs(ValueEventListener listener) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference numRef = database.getReference().child("num_of_pics");
        numRef.addListenerForSingleValueEvent(listener);
    }

    public void getEmailInfo(ValueEventListener listener) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference numRef = database.getReference().child("email_data");
        numRef.addListenerForSingleValueEvent(listener);
    }
}
