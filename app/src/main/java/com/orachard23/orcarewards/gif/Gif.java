package com.orachard23.orcarewards.gif;

import android.content.Context;
import android.util.Log;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.orachard23.orcarewards.util.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by srikaram on 02-Dec-17.
 */

class Gif {
    
    public static final String TAG = Gif.class.getName();

    private Context mContext;

    private OnGifLoadedListener mListener;

    private boolean loaded;

    private InputStream mInputStream;

    Gif(Context mContext) {
        Log.d(TAG, "Gif: ");
        this.mContext = mContext;
    }

    void load(String counter) {
        Log.d(TAG, "load: " + counter);
        loaded = false;
        File resultFile = buildFile(counter);
        if (resultFile.exists()) {
            if (mListener != null) {
                try {
                    FileInputStream inputStream = new FileInputStream(resultFile);
                    loaded = true;
                    mInputStream = inputStream;
                    Log.d(TAG, "load: fetched from local directory");
                    mListener.onGifLoaded();
                } catch (IOException e) {
                    e.printStackTrace();
                    mListener.onGifLoadError(e);
                }
            }
            return;
        }
        if (createFile(resultFile)) {
            Ion.with(mContext)
                    .load(String.format(Constants.URL_GIF, counter))
                    .write(resultFile)
                    .withResponse()
                    .setCallback(fileCallback);
        }
    }

    private File buildFile(String counter) {
        Log.d(TAG, "buildFile: ");
        File localFilesDir = mContext.getFilesDir();
        return new File(localFilesDir, counter+".gif");
    }

    private FutureCallback<Response<File>> fileCallback = new FutureCallback<Response<File>>() {
        @Override
        public void onCompleted(Exception e, Response<File> response) {
            Log.d(TAG, "onCompleted: ");
            if (e != null) {
                e.printStackTrace();
                if (mListener != null) {
                    mListener.onGifLoadError(e);
                }
                return;
            }
            if (mListener != null) {
                if (response.getHeaders().code() != 200) {
                    mListener.onGifLoadError(new RuntimeException(Constants.ERROR_RESPONSE + response.getHeaders().code()));
                    return;
                }
                try {
                    FileInputStream inputStream = new FileInputStream(response.getResult());
                    loaded = true;
                    mInputStream = inputStream;
                    Log.d(TAG, "onCompleted: fetched from server and cached");
                    mListener.onGifLoaded();
                } catch (IOException ioe) {
                    mListener.onGifLoadError(ioe);
                }
            }
        }
    };

    void setOnGifLoadedListener(OnGifLoadedListener listener) {
        Log.d(TAG, "setOnGifLoadedListener: ");
        this.mListener = listener;
    }

    boolean isLoaded() {
        Log.d(TAG, "isLoaded: ");
        return loaded;
    }

    InputStream getInputStream() {
        Log.d(TAG, "getInputStream: ");
        return mInputStream;
    }

    private boolean createFile(File thisImage) {
        Log.d(TAG, "createFile: ");
        try {
            return thisImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            if (mListener != null) {
                mListener.onGifLoadError(e);
            }
            return false;
        }
    }

    public interface OnGifLoadedListener {
        void onGifLoaded();

        void onGifLoadError(Exception ex);
    }
}
