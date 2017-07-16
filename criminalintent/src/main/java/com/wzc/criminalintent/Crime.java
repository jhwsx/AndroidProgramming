package com.wzc.criminalintent;

import java.util.UUID;

/**
 * Created by wzc on 2017/7/16.
 * Crime类,模型
 */

public class Crime {
    private UUID mId;
    private String mTitle;

    public Crime() {
        mId = UUID.randomUUID();
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }
}
