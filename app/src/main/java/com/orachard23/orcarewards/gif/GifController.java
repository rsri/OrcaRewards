package com.orachard23.orcarewards.gif;

import android.content.Context;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by srikaram on 02-Dec-17.
 */

public class GifController implements GifRenderView.OnGifEndedListener, Gif.OnGifLoadedListener {
    
    public static final String TAG = GifController.class.getName();

    private volatile int mTotalGifCount;
    private GifRenderView mGifRenderView;
    private Context mContext;
    private List<GifListener> mGifListeners;
    private int mCounter;
    private Gif mGif;

    private boolean showing;

    public GifController(Context context) {
        Log.d(TAG, "GifController: ");
        this.mContext = context;
        mGifListeners = new ArrayList<>();
    }

    public void setGifRenderView(GifRenderView gifRenderView) {
        Log.d(TAG, "setGifRenderView: ");
        this.mGifRenderView = gifRenderView;
        this.mGifRenderView.setGifEndedListener(this);
        this.mGif = new Gif(mContext);
        this.mGif.setOnGifLoadedListener(this);
        mCounter = 1;
    }

    public void setTotalGifCount(int totalGifCount) {
        this.mTotalGifCount = totalGifCount;
    }

    @Override
    public void onGifEnded() {
        Log.d(TAG, "onGifEnded: ");
        mGifRenderView.setVisibility(View.GONE);
        showing = false;
        for (GifListener gifListener : mGifListeners) {
            gifListener.onGifEnded();
        }
    }

    public void addGifListener(GifListener gifListener) {
        Log.d(TAG, "addGifListener: ");
        mGifListeners.add(gifListener);
    }

    public void removeGifListener(GifListener gifListener) {
        Log.d(TAG, "removeGifListener: ");
        mGifListeners.remove(gifListener);
    }

    public void load() {
        Log.d(TAG, "load: ");
        mGif.load(String.valueOf(mCounter));
    }

    public void loadNext() {
        mCounter++;
        if (mCounter > mTotalGifCount) {
            mCounter = 1;
        }
        mGif.load(String.valueOf(mCounter));
    }

    public boolean isLoaded() {
        Log.d(TAG, "isLoaded: ");
        return mGif.isLoaded();
    }

    public void show() {
        Log.d(TAG, "show: ");
        if (isLoaded()) {
            mGifRenderView.setVisibility(View.VISIBLE);
            mGifRenderView.setImageStream(mGif.getInputStream());
            showing = true;
            for (GifListener gifListener : mGifListeners) {
                gifListener.onBeginGif();
            }
        }
    }

    public void close() {
        mGifRenderView.setVisibility(View.GONE);
    }

    @Override
    public void onGifLoaded() {
        Log.d(TAG, "onGifLoaded: ");
        for (GifListener gifListener : mGifListeners) {
            gifListener.onGifLoaded();
        }
    }

    @Override
    public void onGifLoadError(Exception ex) {
        Log.d(TAG, "onGifLoadError: ");
        ex.printStackTrace();
        showing = false;
        for (GifListener gifListener : mGifListeners) {
            gifListener.onGifFailedToLoad();
        }
    }

    public boolean isShowing() {
        return showing;
    }

    public void reset() {
        mCounter = 1;
        showing = false;
    }
}
