package com.orachard23.orcarewards.gif;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.SystemClock;
import android.support.annotation.RawRes;
import android.util.AttributeSet;
import android.view.View;

import java.io.InputStream;

/**
 * Created by srikaram on 30-Nov-17.
 */

public class GifRenderView extends View {

    private long startOfAnimation;

    private Movie movie;

    private OnGifEndedListener listener;

    public GifRenderView(Context context) {
        super(context);
    }

    public GifRenderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GifRenderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setImageStream(InputStream inputStream) {
        movie = Movie.decodeStream(inputStream);
        if (movie == null) {
            setLayerType(LAYER_TYPE_HARDWARE, null);
        } else {
            setLayerType(LAYER_TYPE_SOFTWARE, null);
        }
        try {
            inputStream.close();
        } catch (java.io.IOException ioe) {
        }
        startOfAnimation = 0;
        invalidate();
    }

    public void setGifEndedListener(OnGifEndedListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (movie == null) {
            super.onDraw(canvas);
            return;
        }

        if (startOfAnimation == 0) {
            startOfAnimation = SystemClock.uptimeMillis();
        }

        if (movie != null) {
            int currentTime = (int) (SystemClock.uptimeMillis() - startOfAnimation);
            if (currentTime > movie.duration()) {
                if (listener != null) {
                    listener.onGifEnded();
                }
                return;
            }
            movie.setTime(currentTime);
            float scaleX = (float) (this.getWidth() - getPaddingLeft() - getPaddingRight()) / movie.width();
            float scaleY = (float) (this.getHeight() - getPaddingTop() - getPaddingBottom()) / movie.height();
            canvas.scale(scaleX, scaleY);
            movie.draw(canvas, getPaddingLeft() * 2, getPaddingRight() * 2);
            invalidate();
        }
    }

    public interface OnGifEndedListener {
        void onGifEnded();
    }
}