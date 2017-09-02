package com.wzc.photogallery;

import android.os.HandlerThread;
import android.util.Log;

/**
 * Created by wzc on 2017/8/23.
 */

public class ThumbnailDownloader<T> extends HandlerThread {
    private static final String TAG = "ThumbnailDownloader";

    private Boolean mHasQuit = false;

    public ThumbnailDownloader() {
        super(TAG);
    }

    @Override
    public boolean quit() {
        mHasQuit = true;
        return super.quit();
    }

    public void queueThumbnail(T target, String url) {
        Log.i(TAG,"Got a url: " + url);
    }
}
