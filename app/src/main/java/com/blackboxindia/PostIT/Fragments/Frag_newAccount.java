package com.blackboxindia.PostIT.Fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
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
import com.blackboxindia.PostIT.Network.Interfaces.onCompleteListener;
import com.blackboxindia.PostIT.Network.Interfaces.onLoginListener;
import com.blackboxindia.PostIT.Network.NetworkMethods;
import com.blackboxindia.PostIT.R;
import com.blackboxindia.PostIT.activities.MainActivity;
import com.blackboxindia.PostIT.cameraIntentHelper.ImageUtils;
import com.blackboxindia.PostIT.dataModels.UserInfo;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;


public class Frag_newAccount extends Fragment {

    //region Variables
    private static final int PICK_PHOTO_CODE = 169;
    private static final String TAG = Frag_newAccount.class.getSimpleName()+" YOYO";

    TextInputEditText etName, etPhone, etAddress, etEmail, etPassword, etConfirmPass;
    Spinner collegeSpinner, hostelSpinner;
    Button btnCreate, btn_image;
    View ParentView;

    Context context;
    ImageView imageView;
    ImageUtils imageUtils;

    UserInfo userInfo;
    Bitmap profileImage;

    ArrayList<String> collegeList;
    ArrayList<String> hostelList;
    CustomDialog.ClickListener collegeListener;
    CustomDialog.ClickListener hostelListener;
    NetworkMethods networkMethods;

    //endregion

    //region Initial Setup

    @Override
    public void onResume() {
        ((MainActivity)context).toolbar.setTitle(MainActivity.TITLE_NewAccount);
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        ParentView = inflater.inflate(R.layout.frag_newaccount, container, false);
        context = ParentView.getContext();
        networkMethods = new NetworkMethods(context);

        initVariables();

        populateSpinners();

        initCamera();

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepareAndCreateAccount();
            }
        });

        return ParentView;
    }

    private void initVariables() {

        userInfo = new UserInfo();

        etName = ParentView.findViewById(R.id.create_etName);
        etPhone = ParentView.findViewById(R.id.create_etPhone);
        etAddress = ParentView.findViewById(R.id.create_etAddress);
        etEmail = ParentView.findViewById(R.id.create_etEmail);
        etPassword = ParentView.findViewById(R.id.create_etPassword);
        etConfirmPass= ParentView.findViewById(R.id.create_etPasswordConfirm);

        imageView = ParentView.findViewById(R.id.create_img);

        btnCreate = ParentView.findViewById(R.id.create_btnCreate);
        btn_image = ParentView.findViewById(R.id.create_btnImageChange);

        collegeSpinner = ParentView.findViewById(R.id.create_etCollege);
        hostelSpinner = ParentView.findViewById(R.id.create_etHostels);

    }

    private void populateSpinners() {

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

        ArrayList<String> defCollege = new ArrayList<>();
        defCollege.add("Select College...");
        ArrayAdapter<String> defCollegeAdapter = new ArrayAdapter<String>(context,R.layout.spinner_item,defCollege){
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
        defCollegeAdapter.setDropDownViewResource(R.layout.spinner_item);
        collegeSpinner.setAdapter(defCollegeAdapter);


        networkMethods.getCollegeOptions(new onCompleteListener<ArrayList<String>>() {
            @Override
            public void onSuccess(ArrayList<String> data) {
                Log.i(TAG, "onSuccess: getCollegeDetails");
                ParentView.findViewById(R.id.create_collegeProgress).setVisibility(View.INVISIBLE);
                if(data!=null)
                    collegeList = data;
                else
                    collegeList = new ArrayList<>();

                collegeListener = new CustomDialog.ClickListener() {

                    @Override
                    public void onItemSelect(String name) {
                        ParentView.findViewById(R.id.create_hostelProgress).setVisibility(View.VISIBLE);
                        networkMethods.getHostelOptions(name, new onCompleteListener<ArrayList<String>>() {
                            @Override
                            public void onSuccess(ArrayList<String> data) {
                                ParentView.findViewById(R.id.create_hostelProgress).setVisibility(View.INVISIBLE);
                                if(data!=null)
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
                                        int position = collegeSpinner.getSelectedItemPosition();
                                        if(position!=0 && position!=collegeList.size()+1) {
                                            networkMethods.addNewHostel(hostelList,
                                                    collegeList.get(position-1),
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
                                    }
                                };

                                configureHostelSpinner(hostelListener);
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Log.e(TAG,"Get hostel list error", e);
                                Toast.makeText(context, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onNewItem(String name) {
                        final ProgressDialog progressDialog = ProgressDialog.show(context, "Adding new college", "Please wait...", true, false);

                        collegeList.add(name);
                        networkMethods.addNewCollege(name, new onCompleteListener<Void>() {
                            @Override
                            public void onSuccess(Void a) {
                                progressDialog.cancel();
                                Toast.makeText(context, "Successfully added!", Toast.LENGTH_SHORT).show();
                                configureCollegeSpinner(collegeListener);
                            }

                            @Override
                            public void onFailure(Exception e) {
                                progressDialog.cancel();
                                collegeList.remove(collegeList.size()-1);
                                Log.e(TAG,"Add college error", e);
                                Toast.makeText(context, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                };
                configureCollegeSpinner(collegeListener);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG,"Get college options error", e);
            }
        });

    }

    //endregion

    //region Spinner related

    //0-collegeSpinner 1-hostelSpinner
    private ArrayList<String> addStuff(ArrayList<String> strings, int i){
        String[] mama = new String[]{
                "Select College...",
                "Select Hostel...",
                "Add New College",
                "Add New Hostel",
        };
        ArrayList<String> strings1 = new ArrayList<>();
        strings1.addAll(strings);
        strings1.add(0,mama[i]);
        strings1.add(mama[i+2]);
        return strings1;
    }

    private void configureCollegeSpinner(final CustomDialog.ClickListener listener){

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(context,R.layout.spinner_item,addStuff(collegeList,0)){
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
        collegeSpinner.setAdapter(spinnerArrayAdapter);
        collegeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int size=0;
                if(collegeList!=null)
                    size = collegeList.size();

                if (position == size+1) {
                    CustomDialog.using(context).create("College Name:", listener);
//                    createCustomDialog("College Name:", listener);
                }
                else if(position != 0) {
                    ParentView.findViewById(R.id.create_collegeError).setVisibility(View.INVISIBLE);
                    listener.onItemSelect(parent.getItemAtPosition(position).toString());
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void configureHostelSpinner(final CustomDialog.ClickListener listener){

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(context,R.layout.spinner_item,addStuff(hostelList,1)){
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
                    ParentView.findViewById(R.id.create_hostelError).setVisibility(View.INVISIBLE);
                    listener.onItemSelect(parent.getItemAtPosition(position).toString());
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    //endregion

    private void prepareAndCreateAccount() {
        userInfo.setData(
                etName.getText().toString().trim(),
                etEmail.getText().toString().trim(),
                etAddress.getText().toString().trim(),
                etPhone.getText().toString().trim()
        );

        if (validateDetails(userInfo)) {
            userInfo.setHostel(hostelList.get(hostelSpinner.getSelectedItemPosition()-1));
            userInfo.setCollegeName(collegeList.get(collegeSpinner.getSelectedItemPosition()-1));
            if(!userInfo.getHasProfileIMG())
                profileImage = null;
            if (isPasswordValid()) {

                String password = etPassword.getText().toString().trim();

                NetworkMethods net = new NetworkMethods(context);
                net.Create_Account(userInfo,password, profileImage, new onLoginListener() {
                    @Override
                    public void onSuccess(UserInfo userInfo) {
                        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                            ((MainActivity) context).UpdateUI(userInfo,true);
                            ((MainActivity) context).createSnackbar("Account Created Successfully");
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        if (e != null) {
                            if (e.getMessage().contains("network"))
                                Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
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
            etEmail.setError("Invalid Email ID");
            f = false;
        }

        if(collegeSpinner.getSelectedItemPosition()==0 || collegeSpinner.getSelectedItemPosition()==collegeList.size()+1){
            ParentView.findViewById(R.id.create_collegeError).setVisibility(View.VISIBLE);
            f = false;
        }

        if(hostelSpinner.getSelectedItemPosition()==0 || hostelSpinner.getSelectedItemPosition()==hostelList.size()+1){
            ParentView.findViewById(R.id.create_hostelError).setVisibility(View.VISIBLE);
            f = false;
        }

        return f;
    }

    boolean isPasswordValid() {

        String password = etPassword.getText().toString().trim();
        String cPassword = etConfirmPass.getText().toString().trim();

        if(!password.equals(cPassword))
        {
            etConfirmPass.setError(getString(R.string.pass_dont_match));
            return false;
        }
        else if(password.length()<getResources().getInteger(R.integer.Min_Password_Size))
        {
            etPassword.setError(String.format(getString(R.string.pass_min_size),getResources().getInteger(R.integer.Min_Password_Size)));
            return false;
        }
        else if (password.contains("\"") || password.contains("\'"))
        {
            etPassword.setError(getString(R.string.pass_illegal_char));
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
                    userInfo.setHasProfileIMG(true);
                    profileImage = file;
                    imageView.setImageBitmap(file);
                }
                else
                    Toast.makeText(context, "Some error occurred. Please try again.", Toast.LENGTH_SHORT).show();
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

