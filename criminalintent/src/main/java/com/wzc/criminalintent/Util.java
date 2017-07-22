package com.wzc.criminalintent;

import android.content.Context;
import android.content.res.Configuration;

/**
 * Created by wzc on 2017/7/22.
 *
 */

public class Util {
    public static boolean isPad(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
}
