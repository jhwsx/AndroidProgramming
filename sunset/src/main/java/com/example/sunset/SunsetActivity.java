package com.example.sunset;

import android.support.v4.app.Fragment;

public class SunsetActivity extends SingleFragmentActivity {

    @Override
    public Fragment createFragment() {
        return SunsetFragment.newInstance();
    }
}
