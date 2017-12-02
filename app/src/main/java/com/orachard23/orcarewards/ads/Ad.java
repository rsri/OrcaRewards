package com.orachard23.orcarewards.ads;

import android.content.Context;

/**
 * Created by srikaram on 30-Nov-17.
 */

public abstract class Ad {

    private Context mContext;

    private AdListener mAdListener;

    public Ad(Context context) {
        this.mContext = context;
    }

    public abstract void init();

    public abstract void load();

    public abstract boolean isLoaded();

    public abstract void show();

    protected Context getContext() {
        return mContext;
    }

    public AdListener getAdListener() {
        return mAdListener;
    }

    public void setAdListener(AdListener adListener) {
        this.mAdListener = adListener;
    }
}
