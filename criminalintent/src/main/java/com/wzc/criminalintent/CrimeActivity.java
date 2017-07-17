package com.wzc.criminalintent;

import android.support.v4.app.Fragment;

/**
 * Created by wzc on 2017/7/17
 * Crime详情页面
 */
public class CrimeActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new CrimeFragment();
    }
}
