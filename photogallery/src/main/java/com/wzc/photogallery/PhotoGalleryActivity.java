package com.wzc.photogallery;

import android.support.v4.app.Fragment;
import android.view.Menu;

public class PhotoGalleryActivity extends SingleFragmentActivity {

    @Override
    public Fragment createFragment() {
        return PhotoGalleryFragment.newInstance();
    }


}
