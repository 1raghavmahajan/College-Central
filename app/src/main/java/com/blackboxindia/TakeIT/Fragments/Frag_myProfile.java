package com.blackboxindia.TakeIT.Fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.blackboxindia.TakeIT.Network.Interfaces.onUpdateListener;
import com.blackboxindia.TakeIT.Network.NetworkMethods;
import com.blackboxindia.TakeIT.R;
import com.blackboxindia.TakeIT.activities.MainActivity;
import com.blackboxindia.TakeIT.cameraIntentHelper.ImageUtils;
import com.blackboxindia.TakeIT.dataModels.UserInfo;
import com.google.firebase.auth.FirebaseAuth;

public class Frag_myProfile extends Fragment {

    //region Variables

    private static String TAG = Frag_myProfile.class.getSimpleName() + " YOYO";
    private static final int PICK_PHOTO_CODE = 120;
    TextInputEditText etName, etEmail, etAddress, etPhone, etCollege;
    Button btn_update, btn_ImageChange;
    View view;
    Context context;
    ImageView imageView;
    ImageUtils imageUtils;

    UserInfo userInfo;
    UserInfo userInfo1;

    //endregion

    //region Initial Setup
    @Override
    public void onResume() {
        ((MainActivity)getActivity()).hideIT();
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_myprofile, container, false);
        context = view.getContext();

        initVariables();

        if(((MainActivity)context).userInfo!=null) {
            userInfo = ((MainActivity) context).userInfo;
            userInfo1 = userInfo.createCopy();
            userInfo1.setuID(userInfo.getuID());
            populateViews();
        }

        initCamera();

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateCredentials();
            }
        });

        return view;
    }

    private void initVariables() {
        etName = (TextInputEditText) view.findViewById(R.id.profile_etName);
        etEmail = (TextInputEditText) view.findViewById(R.id.profile_etEmail);
        etAddress = (TextInputEditText) view.findViewById(R.id.profile_etAddress);
        etPhone = (TextInputEditText) view.findViewById(R.id.profile_etPhone);
        etCollege = (TextInputEditText) view.findViewById(R.id.profile_etCollege);

        btn_update = (Button) view.findViewById(R.id.profile_btnUpdate);
        btn_ImageChange = (Button) view.findViewById(R.id.profile_btnImageChange);

        imageView = (ImageView) view.findViewById(R.id.profile_img);
    }
    //endregion

    void populateViews() {
        if(userInfo.getProfileIMG()!=null)
            if(!userInfo.getProfileIMG().equals("null"))
                new loadPic().execute();
        etName.setText(userInfo.getName());
        etEmail.setText(userInfo.getEmail());
        etPhone.setText(userInfo.getPhone());
        etAddress.setText(userInfo.getAddress());
        etCollege.setText(userInfo.getCollegeName());
    }

    private void UpdateCredentials() {

        userInfo1.setData(
                etName.getText().toString().trim(),
                etEmail.getText().toString().trim(),
                etAddress.getText().toString().trim(),
                etPhone.getText().toString().trim());

        userInfo1.setCollegeName(etCollege.getText().toString().trim());

        if(validateDetails(userInfo1)) {

            userInfo = userInfo1;

            final ProgressDialog show = ProgressDialog.show(context, "Updating...", "", true, false);
            NetworkMethods methods = new NetworkMethods(context, FirebaseAuth.getInstance());
            methods.UpdateUser(userInfo, new onUpdateListener() {
                @Override
                public void onSuccess(UserInfo userInfo) {
                    show.cancel();
                    ((MainActivity) context).UpdateUI(userInfo, false, false);
                    ((MainActivity) context).createSnackbar("Successfully Updated.", Snackbar.LENGTH_SHORT);
                }

                @Override
                public void onFailure(Exception e) {
                    show.cancel();
                    ((MainActivity) context).createSnackbar(e.getMessage(), Snackbar.LENGTH_SHORT);
                }
            });

        }
    }

    boolean validateDetails(UserInfo userInfo) {

        Boolean f = true;
        if(userInfo.getName().equals("")) {
            etName.setError("Field Required");
            f = false;
        }
        if(userInfo.getAddress().equals("")){
            etAddress.setError("Field Required");
            f = false;
        }
        if(!Patterns.PHONE.matcher(userInfo.getPhone()).matches()) {
            etPhone.setError("Invalid phone number");
            f = false;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(userInfo.getEmail()).matches()) {
            etEmail.setError("Invalid EmailID");
            f = false;
        }
        return f;
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
                    userInfo1.setProfileIMG(ImageUtils.BitMapToString(file, 75));
//                    if (imageView.getDrawable() != null)
//                        ((BitmapDrawable) imageView.getDrawable()).getBitmap().recycle();
                    imageView.setImageBitmap(file);
                }
                else
                    Toast.makeText(context, "Some error occurred. Request Code mismatch.", Toast.LENGTH_SHORT).show();
            }
        });

        btn_ImageChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageUtils.imagepicker(PICK_PHOTO_CODE);
            }
        });
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

    //endregion

    private class loadPic extends AsyncTask<Void,Void,Bitmap>{

        @Override
        protected Bitmap doInBackground(Void... params) {
            return (ImageUtils.StringToBitMap(userInfo.getProfileIMG()));
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            imageView.setImageBitmap(bitmap);
        }
    }

}
