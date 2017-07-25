package com.blackboxindia.PostIT.Fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.blackboxindia.PostIT.HelperClasses.CustomDialog;
import com.blackboxindia.PostIT.HelperClasses.GlideApp;
import com.blackboxindia.PostIT.Network.Interfaces.onCompleteListener;
import com.blackboxindia.PostIT.Network.Interfaces.onUpdateListener;
import com.blackboxindia.PostIT.Network.NetworkMethods;
import com.blackboxindia.PostIT.R;
import com.blackboxindia.PostIT.activities.MainActivity;
import com.blackboxindia.PostIT.cameraIntentHelper.ImageUtils;
import com.blackboxindia.PostIT.dataModels.UserInfo;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class Frag_myProfile extends Fragment {

    //region Variables

    private static String TAG = Frag_myProfile.class.getSimpleName() + " YOYO";
    private static final int PICK_PHOTO_CODE = 120;

    TextInputEditText etName, etEmail, etAddress, etPhone, etCollege;
    Spinner hostelSpinner;
    Button btn_update, btn_ImageChange;
    ImageView ic_notVerified;
    View MainView;
    Context context;
    ImageView imageView;
    ImageUtils imageUtils;

    UserInfo userInfo;
    UserInfo userInfoNew;
    Bitmap newProfileImage;

    ArrayList<String> hostelList;
    CustomDialog.ClickListener hostelListener;

    boolean recentlySentMail = false;
    FirebaseUser currentUser;
    //endregion

    //region Initial Setup

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, Bundle savedInstanceState) {
        MainView = inflater.inflate(R.layout.frag_myprofile, container, false);
        context = MainView.getContext();

        initVariables();

        if(((MainActivity)context).userInfo!=null) {
            userInfo = ((MainActivity) context).userInfo;
            userInfoNew = userInfo.createCopy();
            userInfoNew.setuID(userInfo.getuID());
            populateViews();
        }

        initCamera();

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateCredentials();
            }
        });
        checkVerified();

        return MainView;
    }

    private void initVariables() {
        etName = (TextInputEditText) MainView.findViewById(R.id.profile_etName);
        etEmail = (TextInputEditText) MainView.findViewById(R.id.profile_etEmail);
        etAddress = (TextInputEditText) MainView.findViewById(R.id.profile_etAddress);
        etPhone = (TextInputEditText) MainView.findViewById(R.id.profile_etPhone);
        etCollege = (TextInputEditText) MainView.findViewById(R.id.profile_etCollege);

        hostelSpinner = (Spinner) MainView.findViewById(R.id.profile_hostelSpinner);

        btn_update = (Button) MainView.findViewById(R.id.profile_btnUpdate);
        btn_ImageChange = (Button) MainView.findViewById(R.id.profile_btnImageChange);

        imageView = (ImageView) MainView.findViewById(R.id.profile_img);
        ic_notVerified = (ImageView) MainView.findViewById(R.id.profile_verifiedIcon);
    }
    //endregion

    void populateViews() {
        if(userInfo.getHasProfileIMG())
            ((MainActivity)context).cloudStorageMethods.getProfileImage(userInfo.getuID(), new onCompleteListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    GlideApp.with(context).load(uri)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(imageView);
                }

                @Override
                public void onFailure(Exception e) {
                    GlideApp.with(context).load(R.drawable.avatar).into(imageView);
                }
            });

        etName.setText(userInfo.getName());
        etEmail.setText(userInfo.getEmail());
        etPhone.setText(userInfo.getPhone());
        etAddress.setText(userInfo.getRoomNumber());
        etCollege.setText(userInfo.getCollegeName());

        etEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Sorry, you can't changeRoot this.", Toast.LENGTH_SHORT).show();
            }
        });
        etCollege.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Sorry, you can't changeRoot this.", Toast.LENGTH_SHORT).show();
            }
        });

        ArrayList<String> defHostel = new ArrayList<>();
        defHostel.add("Select Hostel...");
        ArrayAdapter<String> defHostelAdapter = new ArrayAdapter<String>(context,R.layout.spinner_item,defHostel){
            @Override
            public boolean isEnabled(int position){
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {

                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0)
                    tv.setTextColor(Color.GRAY);
                else
                    tv.setTextColor(Color.BLACK);
                return view;
            }
        };
        defHostelAdapter.setDropDownViewResource(R.layout.spinner_item);
        hostelSpinner.setAdapter(defHostelAdapter);

        MainView.findViewById(R.id.profile_hostelProgress).setVisibility(View.VISIBLE);
        final NetworkMethods networkMethods = new NetworkMethods(context);

        networkMethods.getHostelOptions(userInfo.getCollegeName(), new onCompleteListener<ArrayList<String>>() {
            @Override
            public void onSuccess(ArrayList<String> data) {

                MainView.findViewById(R.id.profile_hostelProgress).setVisibility(View.INVISIBLE);
                if (data != null)
                    hostelList = data;
                else
                    hostelList = new ArrayList<>();

                hostelListener = new CustomDialog.ClickListener() {
                    @Override
                    public void onItemSelect(String name) {

                    }

                    @Override
                    public void onNewItem(String name) {
                        final ProgressDialog progressDialog = ProgressDialog.show(context, "Adding new hostel", "Please wait...", true, false);
                        hostelList.add(name);
                        networkMethods.addNewHostel(hostelList,
                                userInfo.getCollegeName(),
                                new onCompleteListener<Void>() {
                                    @Override
                                    public void onSuccess(Void a) {
                                        progressDialog.cancel();
                                        Toast.makeText(context, "Successfully added!", Toast.LENGTH_SHORT).show();
                                        configureHostelSpinner(hostelListener);
                                    }

                                    @Override
                                    public void onFailure(Exception e) {
                                        progressDialog.cancel();
                                        hostelList.remove(hostelList.size() - 1);
                                        Log.e(TAG, "Add hostel error", e);
                                        Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                };

                configureHostelSpinner(hostelListener);

                hostelSpinner.setSelection(hostelList.indexOf(userInfo.getHostel())+1);
            }
            @Override
            public void onFailure(Exception e) {
                Log.e(TAG,"Get hostel list error", e);
                Toast.makeText(context, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressWarnings("ConstantConditions")
    void checkVerified(){
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        currentUser.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (!currentUser.isEmailVerified()) {
                    ic_notVerified.setVisibility(View.VISIBLE);
                    ic_notVerified.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final ProgressDialog dialog = ProgressDialog.show(context, "Checking...", "", true, false);
                            currentUser.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    dialog.cancel();
                                    if(currentUser.isEmailVerified()){
                                        ic_notVerified.setVisibility(View.GONE);
                                    }
                                    else {
                                        new AlertDialog.Builder(context)
                                                .setMessage("Account not verified, check your email account for a verification mail.")
                                                .setPositiveButton("OK", null)
                                                .setCancelable(true)
                                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Log.i(TAG, "onClick: cancel");
                                                        dialog.cancel();
                                                    }
                                                })
                                                .setNeutralButton("Resend Email", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        //noinspection ConstantConditions
                                                        Log.i(TAG, "onClick: resend");
                                                        if (!recentlySentMail) {
                                                            currentUser.sendEmailVerification()
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            Toast.makeText(context, "Email sent!", Toast.LENGTH_SHORT).show();
                                                                            recentlySentMail = true;
                                                                            new Handler().postDelayed(new Runnable() {
                                                                                @Override
                                                                                public void run() {
                                                                                    recentlySentMail = false;
                                                                                }
                                                                            }, 5 * 60 * 1000);
                                                                        }
                                                                    })
                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });
                                                        } else
                                                            Toast.makeText(context, "Wait for 3 minutes before sending the mail again.", Toast.LENGTH_LONG).show();
                                                    }
                                                })
                                                .create()
                                                .show();
                                    }
                                }
                            });
                        }
                    });
                }

            }
        });
    }

    private void configureHostelSpinner(final CustomDialog.ClickListener listener){

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(context,R.layout.spinner_item,addStuff(hostelList)){
            @Override
            public boolean isEnabled(int position){
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {

                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0)
                    tv.setTextColor(Color.GRAY);
                else
                    tv.setTextColor(Color.BLACK);
                return view;
            }
        };

        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        hostelSpinner.setAdapter(spinnerArrayAdapter);
        hostelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Log.i(TAG, "onItemSelected of " + id + " " + position );
                if(hostelList!=null)
                    Log.i(TAG, "hostel no: "+hostelList.size());
                int size=0;
                if (hostelList != null) {
                    size = hostelList.size();
                }
                if (position == size+1) {
                    CustomDialog.using(context).create("Hostel Name:",listener);
                }
                else if(position != 0) {
                    MainView.findViewById(R.id.profile_hostelError).setVisibility(View.INVISIBLE);
                    listener.onItemSelect(parent.getItemAtPosition(position).toString());
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private ArrayList<String> addStuff(ArrayList<String> strings){
        String[] mama = new String[]{
                "Select Hostel...",
                "Add New Hostel"
        };
        ArrayList<String> strings1 = new ArrayList<>();
        strings1.addAll(strings);
        strings1.add(0,mama[0]);
        strings1.add(mama[1]);
        return strings1;
    }

    private void UpdateCredentials() {

        userInfoNew.setData(
                etName.getText().toString().trim(),
                etEmail.getText().toString().trim(),
                etAddress.getText().toString().trim(),
                etPhone.getText().toString().trim());

        userInfoNew.setCollegeName(etCollege.getText().toString().trim());

        if(validateDetails(userInfoNew)) {

            userInfoNew.setHostel(hostelList.get(hostelSpinner.getSelectedItemPosition()-1));

            userInfo = userInfoNew;

            final ProgressDialog show = ProgressDialog.show(context, "Updating...", "", true, false);
            NetworkMethods methods = new NetworkMethods(context);

            if(!userInfoNew.getHasProfileIMG()) {
                newProfileImage = null;
            }

            methods.UpdateUser(userInfo, newProfileImage, new onUpdateListener() {
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
        if(userInfo.getRoomNumber().equals("")){
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

        if(hostelSpinner.getSelectedItemPosition()==0 || hostelSpinner.getSelectedItemPosition()==hostelList.size()+1){
            MainView.findViewById(R.id.profile_hostelError).setVisibility(View.VISIBLE);
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
//                    userInfoNew.setHasProfileIMG(ImageUtils.BitMapToString(file, 75));
                    userInfoNew.setHasProfileIMG(true);
                    newProfileImage = file;

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

}
