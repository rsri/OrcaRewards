package com.orachard23.orcarewards.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import com.orachard23.orcarewards.R;
import com.orachard23.orcarewards.RewardsApp;
import com.orachard23.orcarewards.ads.AdController;
import com.orachard23.orcarewards.ads.AdListener;
import com.orachard23.orcarewards.gif.GifController;
import com.orachard23.orcarewards.gif.GifListener;
import com.orachard23.orcarewards.gif.GifRenderView;
import com.orachard23.orcarewards.util.Constants;

import java.util.concurrent.TimeUnit;

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
    private Handler mHandler;
    private boolean mFinishShowingGif;
    private long mGifRunTime;

    public WatchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        return inflater.inflate(R.layout.fragment_watch, container, false);
    }

    private Runnable mHideAdRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "run: mHideAdRunnable");
            mAdController.close();
        }
    };

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: ");
        GifRenderView gifImageView = view.findViewById(R.id.gif_view);
        mGifController = RewardsApp.getApp(view.getContext()).getGifController();
        mGifController.setGifRenderView(gifImageView);
        mGifController.addGifListener(orcaGifListener);

        AdView adView = view.findViewById(R.id.ad_view);
        mAdController = RewardsApp.getApp(view.getContext()).getAdController();
        mAdController.addAdListener(orcaAdListener);
        mAdController.createNewAd(view.getContext());
        mAdController.setView(adView);

        // Fetch total image count dynamically. Modify it in future
        mGifController.setTotalGifCount(11);

        mProgressDialog = new ProgressDialog(view.getContext());
        mProgressDialog.setCancelable(true);
        mProgressDialog.setOnCancelListener(this);
        mProgressDialog.setMessage(getString(R.string.loading));

        mProgressDialog.show();

        mHandler = new Handler();
        mGifController.load();
        mAdController.load();

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

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.watch);
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach: ");
        mListener = null;
        mAdController.removeAdListener(orcaAdListener);
        mGifController.removeGifListener(orcaGifListener);

        mGifController.resetCounter();
        super.onDetach();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        Log.d(TAG, "onCancel: ");
        mAbortRequested = true;
        shouldQuit();
    }

    public interface OnSequenceEndedListener {
        void onSequenceEnded();

        void onSequenceAborted();
    }

    private boolean shouldQuit() {
        Log.d(TAG, "shouldQuit: ");
        boolean shouldQuit = mAbortRequested;
        if (mAbortRequested && mListener != null) {
            mListener.onSequenceAborted();
            mAbortRequested = false;
        }
        return shouldQuit;
    }

    private AdListener orcaAdListener = new AdListener() {

        @Override
        public void onAdOpened() {
            Log.d(TAG, "onAdOpened: ");
            mFinishShowingGif = false;
            mGifController.loadNext();
            mGifRunTime = 0;
            Toast.makeText(getContext(), "Ad should be shown here.", Toast.LENGTH_SHORT).show();
            mHandler.postDelayed(mHideAdRunnable, Constants.AD_TIMEOUT);
        }

        @Override
        public void onAdEnded() {
            Log.d(TAG, "onAdEnded: ");
            if (mListener != null) {
                mListener.onSequenceEnded();
            }
            if (!shouldQuit()) {
                if (mGifController.isLoaded()) {
                    showGif();
                } else {
                    mProgressDialog.show();
                }
            }
        }

        @Override
        public void onAdFailedToLoad(int i) {
            Log.d(TAG, "onAdFailedToLoad: ");
            mAbortRequested = true;
            mProgressDialog.dismiss();
            shouldQuit();
        }

        @Override
        public void onAdLoaded() {
            Log.d(TAG, "onAdLoaded: " + (mProgressDialog.isShowing() || !mGifController.isShowing()));
            mProgressDialog.dismiss();
            if (!shouldQuit() && !mGifController.isShowing() && mFinishShowingGif) {
                showAd();
            }
        }

        @Override
        public void onAdLeftApplication() {
            Log.d(TAG, "onAdLeftApplication: ");
//            mHandler.removeCallbacks(mHideAdRunnable);
        }

        @Override
        public void onAdClicked() {
            Log.d(TAG, "onAdClicked: ");
        }
    };
    
    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    private GifListener orcaGifListener = new GifListener() {

        private long gifStartTime;

        @Override
        public void onBeginGif() {
            Log.d(TAG, "onBeginGif: ");
            if (!mFinishShowingGif) {
                mGifController.load();
            }
            gifStartTime = SystemClock.uptimeMillis();
        }

        @Override
        public void onGifEnded() {
            Log.d(TAG, "onGifEnded: " + mFinishShowingGif);
            if (!shouldQuit()) {
                long runTime = SystemClock.uptimeMillis() - gifStartTime;
                mGifRunTime += runTime;
                if (mGifRunTime > Constants.GIF_TIMEOUT) {
                    mFinishShowingGif = true;
                    showAd();
                    return;
                }
                if (!mFinishShowingGif && mGifController.isLoaded()) {
                    showGif();
                } else if (mFinishShowingGif && mAdController.isLoaded()) {
                    showAd();
                } else {
                    mProgressDialog.show();
                }
            }
        }

        @Override
        public void onGifFailedToLoad() {
            Log.d(TAG, "onGifFailedToLoad: ");
            mAbortRequested = true;
            mProgressDialog.dismiss();
            shouldQuit();
        }

        @Override
        public void onGifLoaded() {
            Log.d(TAG, "onGifLoaded: " + (mProgressDialog.isShowing() && !mAdController.isShowing()));
            mProgressDialog.dismiss();
            if (!shouldQuit()) {
                if (!mFinishShowingGif && !mGifController.isShowing() && !mAdController.isShowing()) {
                    showGif();
                } else if (mFinishShowingGif && mAdController.isLoaded()) {
                    showAd();
                }
            }
        }

    };

    private void showGif() {
        mAdController.close();
        mGifController.show();
    }

    private void showAd() {
        mGifController.close();
        mAdController.show();
    }

}
