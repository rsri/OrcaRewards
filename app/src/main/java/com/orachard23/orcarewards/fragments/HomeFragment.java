package com.orachard23.orcarewards.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.orachard23.orcarewards.R;
import com.orachard23.orcarewards.RewardsApp;
import com.orachard23.orcarewards.controller.PointsUpdater;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements PointsUpdater.OnPointUpdatedListener {

    public static final String TAG = HomeFragment.class.getName();

    public HomeFragment() {
        // Required empty public constructor
    }

    private TextView pointsView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pointsView = view.findViewById(R.id.points_tv);
        int points = RewardsApp.getApp(view.getContext()).getPointsUpdater().getPoints();
        Log.d(TAG, "onViewCreated: " + points);
        pointsView.setText(String.valueOf(points));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        RewardsApp.getApp(context).getPointsUpdater().addListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.app_name);
    }

    @Override
    public void onDetach() {
        RewardsApp.getApp(getContext()).getPointsUpdater().removeListener(this);
        super.onDetach();
    }

    @Override
    public void onPointUpdated(int newPoint) {
        Log.d(TAG, "onPointUpdated: " + newPoint);
        pointsView.setText(String.valueOf(newPoint));
    }
}
