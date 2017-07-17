package com.wzc.criminalintent;

import android.support.v4.app.Fragment;

/**
 * Created by wzc on 2017/7/17.
 * Crime列表页面
 */

public class CrimeListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }
}
