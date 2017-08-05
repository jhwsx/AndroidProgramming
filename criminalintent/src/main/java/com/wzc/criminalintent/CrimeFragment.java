package com.wzc.criminalintent;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
    private static final String DIALOG_PHOTO = "DialogPhoto";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_CONTACT = 2;
    private static final int REQUEST_CALLPHONE_READCONTACTS_PERMISSION = 3;
    private static final int REQUEST_PHOTO = 4;
    private static final String DIALOG_TIME = "DialogTime";
    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private Button mTimeButton;
    private Button mReportButton;
    private Button mSuspectButton;
    private Button mCallSuspectButton;
    private ImageView mPhotoView;
    private ImageButton mPhotoButton;
    private File mPhotoFile;
    private Callbacks mCallbacks;

    public interface Callbacks {
        // 修改在平板中,修改CrimeFragment中的内容时,左边列表并不会立即刷新的问题
        void onCrimeUpdated(Crime crime);
    }
    public static CrimeFragment newInstance(UUID crimeId) {
        CrimeFragment fragment = new CrimeFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (Callbacks) activity;
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
        // 获取保存照片文件的存储位置
        mPhotoFile = CrimeLab.getInstance(getActivity()).getPhotoFile(mCrime);

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
                updateCrime();
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
                updateCrime();
            }
        });

        mReportButton = (Button) view.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 使用ShareCompat类的静态内部类来发送信息
                ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText(getCrimeReport())
                        .setSubject(getString(R.string.crime_report_subject))
                        .setChooserTitle(R.string.send_report)
                        .startChooser();

//                Intent intent = new Intent(Intent.ACTION_SEND);
//                intent.setType("text/plain");
//                intent.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
//                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
//                intent = Intent.createChooser(intent, getString(R.string.send_report)); // 用作选择器的标题
//                startActivity(intent);
            }
        });

        // 获取联系人信息
        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        mSuspectButton = (Button) view.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
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

        mCallSuspectButton = (Button) view.findViewById(R.id.call_suspect);
        mCallSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> permissionList = new ArrayList<String>();
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED) {
                    permissionList.add(Manifest.permission.CALL_PHONE);
                }
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED) {
                    permissionList.add(Manifest.permission.READ_CONTACTS);
                }
                if (!permissionList.isEmpty()) {
                    requestPermissions(permissionList.toArray(new String[permissionList.size()]), REQUEST_CALLPHONE_READCONTACTS_PERMISSION);
                } else {
                    dialPhone();
                }

            }
        });
        mPhotoButton = (ImageButton) view.findViewById(R.id.crime_camera);
        // 检查一下是否可以拍照(有相机应用并且有存储目录存在)
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = mPhotoFile != null
                && captureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);
        if (canTakePhoto) {
            // 把指向存储路径的uri,传给intent
            Uri uri = Uri.fromFile(mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });
        mPhotoView = (ImageView) view.findViewById(R.id.crime_photo);

        updatePhotoView();
        // 点击mPhotoView可以查看缩略图
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                PhotoFragment dialog = PhotoFragment.newInstance(mPhotoFile.getPath());
                dialog.show(fragmentManager, DIALOG_PHOTO);
            }
        });
        return view;

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    private void dialPhone() {
        if (mCrime.getSuspectId() == null) {
            Toast.makeText(getActivity(),"please choose suspect first.",Toast.LENGTH_SHORT).show();
        } else {
            Cursor cursor = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? ",
                    new String[]{mCrime.getSuspectId()}, null);
            try {
                if (cursor.getCount() == 0) {
                    return;
                }
                cursor.moveToFirst();
                String phoneNumber = cursor.getString(0);
                Uri numberUri = Uri.parse("tel:" + phoneNumber);
                Intent intent = new Intent(Intent.ACTION_DIAL, numberUri);
                startActivity(intent);
            } finally {
                cursor.close();
            }
        }
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
                updateCrime();
                break;
            case REQUEST_TIME:
                Time time = (Time) data.getSerializableExtra(TimerPickerFragment.EXTRA_TIME);
                updateTime(time);
                updateDate();
                updateCrime();
                break;
            case REQUEST_CONTACT:
                if (data != null) {
                    Uri contactUri = data.getData();
                    // 查询字段
                    String[] queryFields = {ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts._ID};
                    Cursor cursor = getActivity().getContentResolver().query(contactUri, queryFields, null, null, null);
                    try {
                        // 执行查询

                        if (cursor.getCount() == 0) { // 没有查询到数据
                            return;
                        }

                        cursor.moveToFirst();
                        // 取出第一条数据即可
                        String suspect = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        String suspectId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                        mCrime.setSuspect(suspect);
                        mCrime.setSuspectId(suspectId);
                        mSuspectButton.setText(mCrime.getSuspect());
                        updateCrime();
                    } finally {
                        cursor.close();
                    }

                }
                break;
            case REQUEST_PHOTO:
//                updatePhotoView();
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
            suspect = getString(R.string.crime_report_suspect, suspect);
        }

        String report = getString(R.string.crime_report,
                mCrime.getTitle(), dateString, solvedString, suspect);

        return report;
    }

    private void updatePhotoView() {
        // 设置这个监听,在布局切换时,就会更新照片
        mPhotoView.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (mPhotoFile == null || !mPhotoFile.exists()) {
                            mPhotoView.setImageBitmap(null);
                            mPhotoView.setEnabled(false);
                        } else {
                            int destWidth = mPhotoView.getWidth();
                            int destHeight = mPhotoView.getHeight();
                            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), destWidth, destHeight);
                            mPhotoView.setImageBitmap(bitmap);
                            mPhotoView.setEnabled(true);
                            updateCrime();
                        }
//                        mPhotoView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        int count = 0;
        if (requestCode == REQUEST_CALLPHONE_READCONTACTS_PERMISSION && permissions.length > 0) {
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.READ_CONTACTS) && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), "you has allowed the app to read contacts", Toast.LENGTH_SHORT).show();
                    count++;
                }
                if (permissions[i].equals(Manifest.permission.CALL_PHONE) && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), "you has allowed the app to call phone", Toast.LENGTH_SHORT).show();
                    count++;
                }
                if (count == 2) {
                    dialPhone();
                }
            }
        }
//        Log.e("tagtag", "requestCode = " + requestCode);
//        for (int i = 0; i < permissions.length; i++) {
//            Log.e("tagtag", permissions[i]);
//        }
//        for (int i = 0; i < grantResults.length; i++) {
//            Log.e("tagtag", grantResults[i] + "");
//        }
    }

    private void updateCrime() {
        CrimeLab.getInstance(getActivity()).updateCrime(mCrime);
        mCallbacks.onCrimeUpdated(mCrime);
    }
}
