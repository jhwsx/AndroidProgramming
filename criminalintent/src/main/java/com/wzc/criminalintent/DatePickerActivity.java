package com.wzc.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.Date;


/**
 * Created by wzc on 2017/7/22.
 * 用来托管DatePickerFragment的类
 */

public class DatePickerActivity extends SingleFragmentActivity {
    private static final String EXTRA_DATE = "com.wzc.criminalintent.extra_date";
    public static Intent newIntent(Context context, Date date){
        Intent intent = new Intent(context, DatePickerActivity.class);
        intent.putExtra(EXTRA_DATE, date);
        return intent;
    }
    @Override
    protected Fragment createFragment() {
        Date date = (Date) getIntent().getSerializableExtra(EXTRA_DATE);
        return DatePickerFragment.newInstance(date);
    }
}
