package com.blackboxindia.TakeIT.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.blackboxindia.TakeIT.R;
import com.blackboxindia.TakeIT.activities.MainActivity;
import com.blackboxindia.TakeIT.cameraIntentHelper.ImageUtils;
import com.blackboxindia.TakeIT.dataModels.UserInfo;

public class frag_myProfile extends Fragment {

    private static String TAG = frag_myProfile.class.getSimpleName() + " YOYO";

    //region Variables

    private static final int PICK_PHOTO_CODE = 120;
    EditText etName, etEmail, etAddress, etPhone, etPassword;
    Button btn_update, btn_ImageChange;
    View view;
    Context context;
    ImageView imageView;
    ImageUtils imageUtils;

    //endregion


    //region Initial Setup
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_myprofile, container, false);
        context = view.getContext();

        Log.i(TAG,"onCreateView");

        initVariables();

        Bundle bundle = getArguments();
        Parcelable parcelable;
        if (bundle != null) {
            parcelable = bundle.getParcelable("UserInfo");
            if (parcelable != null)
                populateViews((UserInfo) parcelable);
        }

        initCamera();


        return view;
    }

    private void initCamera() {
        imageUtils = new ImageUtils(getActivity(), this, true, new ImageUtils.ImageAttachmentListener() {
            @Override
            public void image_attachment(int from, String filename, Bitmap file, Uri uri) {
                Log.i("YOYO", filename);
                int h = file.getHeight(), w = file.getWidth();
                if (h > w) {
                    file = Bitmap.createBitmap(file, 0, (h - w) / 2, w, w);
                } else if (w > h) {
                    file = Bitmap.createBitmap(file, 0, (w - h) / 2, h, h);
                }

                imageView.setImageBitmap(file);
            }
        });

        btn_ImageChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageUtils.imagepicker(PICK_PHOTO_CODE);
            }
        });
    }

    private void initVariables() {
        etName = (EditText) view.findViewById(R.id.profile_etName);
        etEmail = (EditText) view.findViewById(R.id.profile_etEmail);
        etAddress = (EditText) view.findViewById(R.id.profile_etAddress);
        etPhone = (EditText) view.findViewById(R.id.profile_etPhone);
        etPassword = (EditText) view.findViewById(R.id.profile_etPassword);

        btn_update = (Button) view.findViewById(R.id.profile_btnUpdate);
        btn_ImageChange = (Button) view.findViewById(R.id.profile_btnImageChange);

        imageView = (ImageView) view.findViewById(R.id.profile_img);
    }
    //endregion

    void populateViews(UserInfo userInfo) {
        etName.setText(userInfo.getName());
        etEmail.setText(userInfo.getEmail());
        etPhone.setText(userInfo.getPhone());
        etAddress.setText(userInfo.getAddress());
        etPassword.setText("********");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        imageUtils.request_permission_result(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageUtils.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        ((MainActivity)getActivity()).hideIT();
        super.onResume();
    }

}
