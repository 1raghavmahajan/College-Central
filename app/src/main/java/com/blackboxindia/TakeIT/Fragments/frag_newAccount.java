package com.blackboxindia.TakeIT.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.blackboxindia.TakeIT.R;
import com.blackboxindia.TakeIT.activities.MainActivity;
import com.blackboxindia.TakeIT.cameraIntentHelper.ImageUtils;
import com.blackboxindia.TakeIT.dataModels.UserInfo;


public class frag_newAccount extends Fragment {

    //region Variables
    private static final int PICK_PHOTO_CODE = 169;
    EditText etName, etPhone, etAddress, etEmail, etPassword, etConfirmPass;
    TextInputLayout nameFrame, phoneFrame, mailFrame, passFrame, cPassFrame;
    Button btnCreate, btn_image;
    View view;

    Context context;
    ImageView imageView;
    ImageUtils imageUtils;

    UserInfo userInfo;
    //endregion

    //region Initial Setup
    @Override
    public void onResume() {
        ((MainActivity)getActivity()).hideIT();
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.frag_newaccount, container, false);
        context = view.getContext();

        initVariables();

        initCamera();

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userInfo.setData(etName.getText().toString().trim(),etEmail.getText().toString().trim(),etAddress.getText().toString().trim(),etPhone.getText().toString().trim());
                if (validateDetails(userInfo))
                    if (isPasswordValid()) {
                        userInfo.newUser(etPassword.getText().toString().trim(), v.getContext());
                    }
            }
        });

        return view;
    }

    private void initVariables() {

        userInfo = new UserInfo();

        etName = (EditText) view.findViewById(R.id.create_etName);
        etPhone = (EditText) view.findViewById(R.id.create_etPhone);
        etAddress = (EditText) view.findViewById(R.id.create_etAddress);
        etEmail = (EditText) view.findViewById(R.id.create_etEmail);
        etPassword = (EditText) view.findViewById(R.id.create_etPassword);
        etConfirmPass= (EditText) view.findViewById(R.id.create_etPasswordConfirm);

        nameFrame = (TextInputLayout) view.findViewById(R.id.create_etNameFrame);
        phoneFrame = (TextInputLayout) view.findViewById(R.id.create_etPhoneFrame);
        mailFrame = (TextInputLayout) view.findViewById(R.id.create_etEmailFrame);
        passFrame = (TextInputLayout) view.findViewById(R.id.create_etPasswordFrame);
        cPassFrame = (TextInputLayout) view.findViewById(R.id.create_etPasswordConfirmFrame);

        imageView = (ImageView) view.findViewById(R.id.create_img);

        btnCreate = (Button) view.findViewById(R.id.create_btnCreate);
        btn_image = (Button) view.findViewById(R.id.create_btnImageChange);
    }

    //endregion

    boolean validateDetails(UserInfo userInfo) {
        Bundle bundle = userInfo.validateNewAccountDetails();
        return bundle.getBoolean("allGood");
    }

    boolean isPasswordValid() {

        String password = etPassword.getText().toString().trim();
        String cPassword = etConfirmPass.getText().toString().trim();

        if(!password.equals(cPassword))
        {
            cPassFrame.setError(getString(R.string.pass_dont_match));
            return false;
        }
        else if(password.length()<getResources().getInteger(R.integer.Min_Password_Size))
        {
            passFrame.setError(String.format(getString(R.string.pass_min_size),getResources().getInteger(R.integer.Min_Password_Size)));
            return false;
        }
        else if (password.contains("\"") || password.contains("\\") || password.contains("\'") || password.contains(";"))
        {
            passFrame.setError(getString(R.string.pass_illegal_char));
            return false;
        }
        else
            return true;
    }

    //region Camera Setup
    private void initCamera() {
        imageUtils = new ImageUtils(getActivity(), this, true, new ImageUtils.ImageAttachmentListener() {
            @Override
            public void image_attachment(int from, String filename, Bitmap file, Uri uri) {

                if(from == PICK_PHOTO_CODE) {
                    int h = file.getHeight(), w = file.getWidth();
                    if (h > w) {
                        file = Bitmap.createBitmap(file, 0, (h - w) / 2, w, w);
                    } else if (w > h) {
                        file = Bitmap.createBitmap(file, (w - h) / 2, 0, h, h);
                    }
                    userInfo.setProfileIMG(ImageUtils.BitMapToString(file, 75));
//                    if (imageView.getDrawable() != null)
//                        ((BitmapDrawable) imageView.getDrawable()).getBitmap().recycle();
                    imageView.setImageBitmap(file);
                }
                else
                    Toast.makeText(context, "Some error occurred. Request Code mismatch.", Toast.LENGTH_SHORT).show();
            }
        });

        btn_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageUtils.imagepicker(PICK_PHOTO_CODE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageUtils.onActivityResult(requestCode, resultCode, data);
    }
    //endregion


}

