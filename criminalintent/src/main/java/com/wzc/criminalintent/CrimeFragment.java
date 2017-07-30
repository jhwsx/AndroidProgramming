package com.wzc.criminalintent;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

/**
 * Created by wzc on 2017/7/16.
 * Crime片段
 */

public class CrimeFragment extends Fragment {
    public static final String EXTRA_RETURN_RESULT = "return_result";
    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_CONTACT = 2;
    private static final String DIALOG_TIME = "DialogTime";
    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private Button mTimeButton;
    private Button mReportButton;
    private Button mSuspectButton;

    public static CrimeFragment newInstance(UUID crimeId) {
        CrimeFragment fragment = new CrimeFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
//        mCrime = new Crime();
//        // 从片段的托管CrimeActivity获取intent
//        UUID crimeId = (UUID) getActivity().getIntent().getSerializableExtra(CrimeActivity.EXTRA_CRIME_ID);
        Bundle arguments = getArguments();
        UUID crimeId = (UUID) arguments.getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.getInstance(getActivity()).getCrime(crimeId);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime, container, false);
        mTitleField = (EditText) view.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDateButton = (Button) view.findViewById(R.id.crime_date);
        updateDate();
//        mDateButton.setEnabled(false);
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Util.isPad(getActivity())) {
                    Intent intent = DatePickerActivity.newIntent(getActivity(), mCrime.getDate());
                    startActivityForResult(intent, REQUEST_DATE);
                } else {
                    FragmentManager fragmentManager = getFragmentManager();
//                DatePickerFragment dialog = new DatePickerFragment();
                    DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                    dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                    dialog.show(fragmentManager, DIALOG_DATE);
                }

            }
        });

        mTimeButton = (Button) view.findViewById(R.id.crime_time);
        updateTime();
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                TimerPickerFragment fragment = TimerPickerFragment.newInstance(getTime());
                fragment.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
                fragment.show(fragmentManager, DIALOG_TIME);
            }
        });
        mSolvedCheckBox = (CheckBox) view.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mSolvedCheckBox.setChecked(isChecked);
                mCrime.setSolved(isChecked);
            }
        });

        mReportButton = (Button) view.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
                intent = Intent.createChooser(intent, getString(R.string.send_report)); // 用作选择器的标题
                startActivity(intent);
            }
        });

        // 获取联系人信息
        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        mSuspectButton = (Button) view.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(pickContact,REQUEST_CONTACT);
            }
        });

        if (mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
        }
        // 检查可响应获取联系人信息的activity是否存在
        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);
        }
        return view;

    }

    @Override
    public void onPause() {
        super.onPause();
        // 在这个方法里进行用户编辑完后的数据刷新
        CrimeLab.getInstance(getActivity()).updateCrime(mCrime);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_delete_crime:
                CrimeLab.getInstance(getActivity()).deleteCrime(mCrime);
                startActivity(new Intent(getActivity(), CrimeListActivity.class));
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_DATE:
                Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
                mCrime.setDate(date);
                updateDate();
                updateTime();
                break;
            case REQUEST_TIME:
                Time time = (Time) data.getSerializableExtra(TimerPickerFragment.EXTRA_TIME);
                updateTime(time);
                updateDate();
                break;
            case REQUEST_CONTACT:
                if (data != null) {
                    Uri contactUri = data.getData();
                    // 查询字段
                    String[] queryFields = {ContactsContract.Contacts.DISPLAY_NAME};
                    Cursor cursor = getActivity().getContentResolver().query(contactUri, queryFields, null,  null, null);
                    try {
                        // 执行查询

                        if (cursor.getCount() == 0) { // 没有查询到数据
                            return;
                        }

                        cursor.moveToFirst();
                        // 取出第一条数据即可
                        String suspect = cursor.getString(0);
                        mCrime.setSuspect(suspect);
                        mSuspectButton.setText(mCrime.getSuspect());
                    } finally {
                        cursor.close();
                    }

                }
                break;
        }
    }

    private void updateTime(Time time) {
        Date date = mCrime.getDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(year, month, day, time.getHour(), time.getMinute());
        mCrime.setDate(calendar.getTime());
        updateTime();
    }

    private void updateTime() {
        mTimeButton.setText(getTime().toString());
    }

    @NonNull
    private Time getTime() {
        Date date = mCrime.getDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        return new Time(hour, minute);
    }

    private String formatDate(Date date) {
        return new SimpleDateFormat("EEE, yyyy/MM/dd HH:mm:ss", Locale.US).format(date);
    }

    public void returnResult() {
        Intent data = new Intent();
        data.putExtra(EXTRA_RETURN_RESULT, "我是从CrimeFragment返回的信息");
        getActivity().setResult(RESULT_OK, data);
    }

    private void updateDate() {
        mDateButton.setText(mCrime.getDate().toString());
    }

    private String getCrimeReport() {
        String solvedString = null;
        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }
        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();

        String suspect = mCrime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect);
        }

        String report = getString(R.string.crime_report,
                mCrime.getTitle(), dateString, solvedString, suspect);

        return report;
    }
}
