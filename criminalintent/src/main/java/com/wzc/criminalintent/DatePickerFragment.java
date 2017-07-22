package com.wzc.criminalintent;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static android.app.Activity.RESULT_OK;


/**
 * Created by wangzhichao on 2017/7/22.
 *
 */

public class DatePickerFragment extends DialogFragment {
    private static final String ARG_DATE = "date";
    public static final String EXTRA_DATE = "com.wzc.criminalintent.extra_date";
    private DatePicker mDatePicker;
    private Button mOkButton;

    public static DatePickerFragment newInstance(Date date) {
        Bundle arg = new Bundle();
        arg.putSerializable(ARG_DATE, date);
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(arg);
        return fragment;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Date date = (Date) getArguments().getSerializable(ARG_DATE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int minute = calendar.get(Calendar.MINUTE);
        View view = inflater.inflate(R.layout.dialog_date, container, false);
        mDatePicker = (DatePicker) view.findViewById(R.id.dialog_date_date_picker);
        mDatePicker.init(year, month, day, null);
        mOkButton = (Button) view.findViewById(R.id.dialog_date_date_ok_button);
        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = mDatePicker.getYear();
                int month = mDatePicker.getMonth();
                int day = mDatePicker.getDayOfMonth();
                Date date = new GregorianCalendar(year, month, day, hour, minute).getTime();
                if (Util.isPad(getActivity())) {
                    returnResult(date);
                    getActivity().finish();
                } else {
                    sendResult(RESULT_OK, date);
                    dismiss();
                }


            }
        });
        return view;
    }
    public void returnResult(Date date) {
        Intent data = new Intent();
        data.putExtra(EXTRA_DATE, date);
        getActivity().setResult(RESULT_OK, data);
    }
//    @NonNull
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        Date date = (Date) getArguments().getSerializable(ARG_DATE);
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(date);
//        int year = calendar.get(Calendar.YEAR);
//        int month = calendar.get(Calendar.MONTH);
//        int day = calendar.get(Calendar.DAY_OF_MONTH);
//        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
//        final int minute = calendar.get(Calendar.MINUTE);
//
//        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date, null);
//        mDatePicker = (DatePicker) view.findViewById(R.id.dialog_date_date_picker);
//        mDatePicker.init(year, month, day, null);
//        return new AlertDialog.Builder(getActivity())
//                .setTitle(R.string.date_picker_title)
//                .setView(view)
//                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        int year = mDatePicker.getYear();
//                        int month = mDatePicker.getMonth();
//                        int day = mDatePicker.getDayOfMonth();
//                        Date date = new GregorianCalendar(year, month, day, hour, minute).getTime();
//                        sendResult(Activity.RESULT_OK, date);
//                    }
//                })
//                .show();
//    }

    private void sendResult(int resultCode, Date date) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE, date);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
