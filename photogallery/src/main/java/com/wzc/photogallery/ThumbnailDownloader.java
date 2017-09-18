package com.wzc.photogallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by wzc on 2017/8/23.
 */

public class ThumbnailDownloader<T> extends HandlerThread {
    private static final String TAG = "ThumbnailDownloader";
    private static final int MESSAGE_DOWNLOAD = 0;
    private Boolean mHasQuit = false;
    private Handler mRequestHandler;
    // 线程安全的HashMap,使用标记下载请求的T类型对象作为key,使用和请求关联的url下载链接作为value,
    // 这样的目的是为了将下载结果和需要显示图片的UI元素关联起来,也就是说,可以清楚地知道将下载结果
    // 发送给哪个显示图片的UI元素.
    private ConcurrentMap<T, String> mRequestMap = new ConcurrentHashMap<>();
    // 存放来自主线程的Handler
    private Handler mResponseHandler;

    public void setThumbnailDownloadListener(ThumbnailDownloadListener<T> listener) {
        mThumbnailDownloadListener = listener;
    }

    private ThumbnailDownloadListener<T> mThumbnailDownloadListener;

    public interface ThumbnailDownloadListener<T> {
        void onThumbnailDownloaded(T target, Bitmap thumbnail);
    }

    public ThumbnailDownloader(Handler responseHandler) {
        super(TAG);
        mResponseHandler = responseHandler;
    }
    /**
     * Call back method that can be explicitly overridden if needed to execute some
     * setup before Looper loops.
     * 在Looper首次检查消息队列之前调用,这里是创建Handler的好地方
     */
    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
        mRequestHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == MESSAGE_DOWNLOAD) {
                    T target = (T) msg.obj;
                    Log.i(TAG, "Got a request for URL: " + mRequestMap.get(target));
                    handleRequest(target);
                }
            }
        };
    }

    public void clearQueue() {
        mRequestHandler.removeMessages(MESSAGE_DOWNLOAD);
    }

    private void handleRequest(final T target) {
        final String url = mRequestMap.get(target);

        if (url == null) {
            return;
        }

        try {
            byte[] bitmapBytes = new FlickrFetchr().getUrlBytes(url);
            final Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
            Log.i(TAG, "Bitmap created.");

            mResponseHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mRequestMap.get(target) != url || mHasQuit) {
                        return;
                    }
                    mRequestMap.remove(target);
                    mThumbnailDownloadListener.onThumbnailDownloaded(target, bitmap);
                }
            });
        } catch (IOException e) {
            Log.e(TAG, "Error downloading img.", e);
        }
    }

    @Override
    public boolean quit() {
        mHasQuit = true;
        return super.quit();
    }

    public void queueThumbnail(T target, String url) {
        Log.i(TAG, "Got a url: " + url);

        if (url == null) {
            mRequestMap.remove(target);
        } else {
            mRequestMap.put(target, url);
            // 新消息代表的是什么?是T target(RecyclerView中的PhotoHolder的下载请求)
            // 注意,消息自身是不带url信息的.
            // 把PhotoHolder和url的对应关系更新到mRequestMap里面,随后,从mRequestMap中取出图片url,以保证总是使用了匹配
            // PhotoHolder实例的最新下载请求url(因为RecyclerView中的PhotoHolder是会不断回收复用的)
            // TODO 思考,如果不这样做,还有别的办法吗?
            mRequestHandler.obtainMessage(MESSAGE_DOWNLOAD, target).sendToTarget();
        }
    }
}
