package com.wzc.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

/**
 * Created by wzc on 2017/7/22.
 *
 */

public class TimerPickerFragment extends DialogFragment {
    private static final String ARG_TIME = "time";
    public  static final String EXTRA_TIME = "com.wzc.criminalintent.extra_time";
    private TimePicker mTimePicker;

    public static TimerPickerFragment newInstance(Time time) {
        Bundle arg = new Bundle();
        arg.putSerializable(ARG_TIME, time);
        TimerPickerFragment fragment = new TimerPickerFragment();
        fragment.setArguments(arg);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Time time = (Time) getArguments().getSerializable(ARG_TIME);

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_time, null);
        mTimePicker = (TimePicker) view.findViewById(R.id.dialog_time_time_picker);
        mTimePicker.setCurrentHour(time.getHour());
        mTimePicker.setCurrentMinute(time.getMinute());
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.time_picker_title)
                .setView(view)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Integer hour = mTimePicker.getCurrentHour();
                        Integer minute = mTimePicker.getCurrentMinute();
                        Time time = new Time(hour, minute);
                        sendResult(Activity.RESULT_OK,time);
                    }
                })
                .show();
    }

    private void sendResult(int resultCode, Time time) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_TIME, time);

        getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode,intent);
    }
}
