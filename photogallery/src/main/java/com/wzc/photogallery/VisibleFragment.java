package com.wzc.photogallery;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;

/**
 * Created by wzc on 2017/12/11.
 * 隐藏前台通知的通用型Fragment
 */

public abstract class VisibleFragment extends Fragment {
    private static final String TAG = VisibleFragment.class.getSimpleName();

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(PollService.ACTION_SHOW_NOTIFICATION);
        getActivity().registerReceiver(mOnShowNotification, intentFilter,PollService.PERM_PRIVATE,null);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(mOnShowNotification);
    }

    private BroadcastReceiver mOnShowNotification = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            Toast.makeText(context, "Got a broadcast: " + intent.getAction(),
//                    Toast.LENGTH_SHORT)
//                    .show();
            // If we receive this, we're visible, so cancel the notificaitoin
            setResultCode(Activity.RESULT_CANCELED);
        }
    };
}
