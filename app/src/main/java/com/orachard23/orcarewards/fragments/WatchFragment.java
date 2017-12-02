package com.orachard23.orcarewards.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.orachard23.orcarewards.R;
import com.orachard23.orcarewards.RewardsApp;
import com.orachard23.orcarewards.ads.AdController;
import com.orachard23.orcarewards.ads.AdListener;
import com.orachard23.orcarewards.gif.GifController;
import com.orachard23.orcarewards.gif.GifListener;
import com.orachard23.orcarewards.gif.GifRenderView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnSequenceEndedListener} interface
 * to handle interaction events.
 */
public class WatchFragment extends Fragment implements ProgressDialog.OnCancelListener {

    public static final String TAG = WatchFragment.class.getName();

    private OnSequenceEndedListener mListener;
    private AdController mAdController;
    private GifController mGifController;

    private ProgressDialog mProgressDialog;

    private boolean mAbortRequested;

    public WatchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        return inflater.inflate(R.layout.fragment_watch, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: ");
        GifRenderView gifImageView = view.findViewById(R.id.gif_view);
        mGifController = RewardsApp.getApp(view.getContext()).getGifController();
        mGifController.setGifRenderView(gifImageView);
        mGifController.addGifListener(orcaGifListener);
        // Fetch total image count dynamically. Modify it in future
        mGifController.setTotalGifCount(11);

        mProgressDialog = new ProgressDialog(view.getContext());
        mProgressDialog.setCancelable(true);
        mProgressDialog.setOnCancelListener(this);
        mProgressDialog.setMessage(getString(R.string.loading));

        mProgressDialog.show();
        mGifController.load();
    }

    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "onAttach: ");
        super.onAttach(context);
        if (context instanceof OnSequenceEndedListener) {
            mListener = (OnSequenceEndedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSequenceEndedListener");
        }
        mAdController = RewardsApp.getApp(context).getAdController();
        mAdController.addAdListener(orcaAdListener);
        mAdController.createNewAd(context);

        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach: ");
        mListener = null;
        mAdController.removeAdListener(orcaAdListener);
        mGifController.removeGifListener(orcaGifListener);

        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        super.onDetach();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        Log.d(TAG, "onCancel: ");
        mAbortRequested = true;
        shouldContinue();
    }

    public interface OnSequenceEndedListener {
        void onSequenceEnded();

        void onSequenceAborted();
    }

    private boolean shouldContinue() {
        Log.d(TAG, "shouldContinue: ");
        boolean shouldContinue = mAbortRequested;
        if (mAbortRequested && mListener != null) {
            mListener.onSequenceAborted();
            mAbortRequested = false;
        }
        return shouldContinue;
    }

    private AdListener orcaAdListener = new AdListener() {

        @Override
        public void onAdOpened() {
            Log.d(TAG, "onAdOpened: ");
            mGifController.load();
        }

        @Override
        public void onAdClosed() {
            Log.d(TAG, "onAdClosed: ");
            if (mListener != null) {
                mListener.onSequenceEnded();
            }
            if (!shouldContinue()) {
                if (!mGifController.isLoaded()) {
                    mProgressDialog.show();
                    return;
                }
                mGifController.show();
            }
        }

        @Override
        public void onAdFailedToLoad(int i) {
            Log.d(TAG, "onAdFailedToLoad: ");
            mAbortRequested = true;
        }

        @Override
        public void onAdLoaded() {
            Log.d(TAG, "onAdLoaded: " + (mProgressDialog.isShowing() || !mGifController.isShowing()));
            if (mProgressDialog.isShowing() || !mGifController.isShowing()) {
                mProgressDialog.dismiss();
                mAdController.show();
            }
        }
    };

    private GifListener orcaGifListener = new GifListener() {
        @Override
        public void onBeginGif() {
            Log.d(TAG, "onBeginGif: ");
            mAdController.load();
        }

        @Override
        public void onGifEnded() {
            Log.d(TAG, "onGifEnded: ");
            if (!shouldContinue()) {
                if (!mAdController.isLoaded()) {
                    mProgressDialog.show();
                    return;
                }
                mAdController.show();
            }
        }

        @Override
        public void onGifFailedToLoad() {
            Log.d(TAG, "onGifFailedToLoad: ");
            mAbortRequested = true;
        }

        @Override
        public void onGifLoaded() {
            Log.d(TAG, "onGifLoaded: " + (mProgressDialog.isShowing() && !mAdController.isShowing()));
            if (mProgressDialog.isShowing() || !mAdController.isShowing()) {
                mProgressDialog.dismiss();
                mGifController.show();
            }
        }
    };

}
