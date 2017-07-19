package com.wzc.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.UUID;

/**
 * Created by wzc on 2017/7/17
 * Crime详情页面
 */
public class CrimeActivity extends SingleFragmentActivity {
    private static final String EXTRA_CRIME_ID = "com.wzc.criminalintent.crime_id";

    public static Intent newIntent(Context context, UUID crimeId) {
        Intent intent = new Intent(context, CrimeActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
//        return new CrimeFragment();
        return CrimeFragment.newInstance(crimeId);
    }
}
