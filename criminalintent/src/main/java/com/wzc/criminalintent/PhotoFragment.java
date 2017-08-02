package com.wzc.criminalintent;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by wzc on 2017/8/2.
 * 用于显示缩放版本照片的DialogFrgment
 */

public class PhotoFragment extends DialogFragment {

    private ImageView mDialogPhoto;
    private static final String EXTRA_PHOTO_PATH = "com.wzc.criminalintent.extra_photo_path";
    public static PhotoFragment newInstance(String photoPath){
        Bundle arg = new Bundle();
        arg.putString(EXTRA_PHOTO_PATH, photoPath);

        PhotoFragment fragment = new PhotoFragment();
        fragment.setArguments(arg);

        return fragment;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        final String photoPath = arguments.getString(EXTRA_PHOTO_PATH);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_photo, null);
        mDialogPhoto = (ImageView) view.findViewById(R.id.dialog_photo_pic);
        Bitmap bitmap = PictureUtils.getScaledBitmap(photoPath, getActivity());
        mDialogPhoto.setImageBitmap(bitmap);
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.dialog_photo_title)
                .setView(view)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                })
                .show();
    }
}
