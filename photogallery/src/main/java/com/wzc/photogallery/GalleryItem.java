package com.wzc.photogallery;

/**
 * Created by wzc on 2017/8/18.
 */

public class GalleryItem {

    private String mCaption;

    public String getId() {
        return mId;
    }

    private String mId;
    private String mUrl;

    @Override
    public String toString() {
        return mCaption;
    }

    public void setCaption(String caption) {
        mCaption = caption;
    }

    public void setId(String id) {
        mId = id;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getUrl() {
        return mUrl;
    }
}
