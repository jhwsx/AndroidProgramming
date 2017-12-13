package com.wzc.photogallery;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;

/**
 * Created by wzc on 2017/12/13.
 */

public class PhotoPageActivity extends SingleFragmentActivity {

    private PhotoPageFragment mPhotoPageFragment;

    public static Intent newIntent(Context context, Uri photoPageUri) {
        Intent intent = new Intent(context, PhotoPageActivity.class);
        intent.setData(photoPageUri);
        return intent;
    }

    @Override
    public Fragment createFragment() {
        mPhotoPageFragment = PhotoPageFragment.newInstance(getIntent().getData());
        return mPhotoPageFragment;
    }

    @Override
    public void onBackPressed() {

        if (mPhotoPageFragment.getWebView() != null && mPhotoPageFragment.getWebView().canGoBack()) {
            mPhotoPageFragment.getWebView().goBack();
        } else {
            super.onBackPressed();
        }
    }
}
