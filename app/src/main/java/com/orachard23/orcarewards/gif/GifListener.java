package com.orachard23.orcarewards.gif;

/**
 * Created by srikaram on 02-Dec-17.
 */

public interface GifListener {

    void onBeginGif();

    void onGifEnded();

    void onGifFailedToLoad();

    void onGifLoaded();
}
