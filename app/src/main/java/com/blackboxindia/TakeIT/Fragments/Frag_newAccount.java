package com.blackboxindia.TakeIT.Fragments;

import android.app.Dialog;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.blackboxindia.TakeIT.Network.Interfaces.addCollegeDataListener;
import com.blackboxindia.TakeIT.Network.Interfaces.getCollegeDataListener;
import com.blackboxindia.TakeIT.Network.NetworkMethods;
import com.blackboxindia.TakeIT.R;
import com.blackboxindia.TakeIT.cameraIntentHelper.ImageUtils;
import com.blackboxindia.TakeIT.dataModels.UserInfo;

import java.util.ArrayList;


public class Frag_newAccount extends Fragment {

    //region Variables
    private static final int PICK_PHOTO_CODE = 169;
    private static final String TAG = Frag_newAccount.class.getSimpleName()+" YOYO";

    TextInputEditText etName, etPhone, etAddress, etEmail, etPassword, etConfirmPass;
    Spinner collegeSpinner, hostelSpinner;
    Button btnCreate, btn_image;
    View view;

    Context context;
    ImageView imageView;
    ImageUtils imageUtils;

    UserInfo userInfo;

    ArrayList<String> collegeList;
    ArrayList<String> hostelList;

    ClickListener collegeListener;
    ClickListener hostelListener;

    NetworkMethods networkMethods;

    //endregion

    //region Initial Setup

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.frag_newaccount, container, false);
        context = view.getContext();
        networkMethods = new NetworkMethods(context);

        initVariables();

        populateSpinners();

        initCamera();

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userInfo.setData(
                        etName.getText().toString().trim(),
                        etEmail.getText().toString().trim(),
                        etAddress.getText().toString().trim(),
                        etPhone.getText().toString().trim()
                        );

                if (validateDetails(userInfo)) {
                    userInfo.setHostel(hostelList.get(hostelSpinner.getSelectedItemPosition()-1));
                    userInfo.setCollegeName(collegeList.get(collegeSpinner.getSelectedItemPosition()-1));
                    if (isPasswordValid()) {
                        userInfo.newUser(etPassword.getText().toString().trim(), v.getContext());
                    }
                }
            }
        });

        return view;
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


        networkMethods.getCollegeOptions(new getCollegeDataListener() {
            @Override
            public void onSuccess(ArrayList<String> data) {
                view.findViewById(R.id.create_collegeProgress).setVisibility(View.INVISIBLE);
                if(data!=null)
                    collegeList = data;
                else
                    collegeList = new ArrayList<>();

                collegeListener = new ClickListener() {

                    @Override
                    public void onItemSelect(String name) {
                        view.findViewById(R.id.create_hostelProgress).setVisibility(View.VISIBLE);
                        networkMethods.getHostelOptions(name, new getCollegeDataListener() {
                            @Override
                            public void onSuccess(ArrayList<String> data) {
                                view.findViewById(R.id.create_hostelProgress).setVisibility(View.INVISIBLE);
                                if(data!=null)
                                    hostelList = data;
                                else
                                    hostelList = new ArrayList<>();

                                hostelListener = new ClickListener() {
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
                                                    new addCollegeDataListener() {
                                                        @Override
                                                        public void onSuccess() {
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
                        networkMethods.addNewCollege(collegeList, new addCollegeDataListener() {
                            @Override
                            public void onSuccess() {
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

    private void configureCollegeSpinner(final ClickListener listener){

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
                    createCustomDialog("College Name:", listener);
                }
                else if(position != 0) {
                    view.findViewById(R.id.create_collegeError).setVisibility(View.INVISIBLE);
                    listener.onItemSelect(parent.getItemAtPosition(position).toString());
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void configureHostelSpinner(final ClickListener listener){

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

                Log.i(TAG, "onItemSelected of " + id + " " + position );
                if(hostelList!=null)
                    Log.i(TAG, "hostel no: "+hostelList.size());
                int size=0;
                if (hostelList != null) {
                    size = hostelList.size();
                }
                if (position == size+1) {
                    createCustomDialog("Hostel Name:",listener);
                }
                else if(position != 0) {
                    view.findViewById(R.id.create_hostelError).setVisibility(View.INVISIBLE);
                    listener.onItemSelect(parent.getItemAtPosition(position).toString());
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    void createCustomDialog(String title, final ClickListener listener){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_text);
        dialog.findViewById(R.id.dialog_Submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = ((EditText) v).getText().toString().trim();
                if (s.equals(""))
                    Toast.makeText(context, "Invalid name", Toast.LENGTH_SHORT).show();
                else if(s.contains(".") || s.contains("#") || s.contains("$") || s.contains("[") || s.contains("]"))
                    ((EditText) v).setError("'.', '#', '$', '[', ']' not allowed");
                else {
                    dialog.cancel();
                    listener.onNewItem(s);
                }
            }
        });
        dialog.findViewById(R.id.dialog_Cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        ((TextView)dialog.findViewById(R.id.dialog_Title)).setText(title);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    private void initVariables() {

        userInfo = new UserInfo();

        etName = (TextInputEditText) view.findViewById(R.id.create_etName);
        etPhone = (TextInputEditText) view.findViewById(R.id.create_etPhone);
        etAddress = (TextInputEditText) view.findViewById(R.id.create_etAddress);
        etEmail = (TextInputEditText) view.findViewById(R.id.create_etEmail);
        etPassword = (TextInputEditText) view.findViewById(R.id.create_etPassword);
        etConfirmPass= (TextInputEditText) view.findViewById(R.id.create_etPasswordConfirm);

        imageView = (ImageView) view.findViewById(R.id.create_img);

        btnCreate = (Button) view.findViewById(R.id.create_btnCreate);
        btn_image = (Button) view.findViewById(R.id.create_btnImageChange);

        collegeSpinner = (Spinner) view.findViewById(R.id.create_etCollege);
        hostelSpinner = (Spinner) view.findViewById(R.id.create_etHostels);
    }

    //endregion

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
            view.findViewById(R.id.create_collegeError).setVisibility(View.VISIBLE);
            f = false;
        }

        if(hostelSpinner.getSelectedItemPosition()==0 || hostelSpinner.getSelectedItemPosition()==hostelList.size()+1){
            view.findViewById(R.id.create_hostelError).setVisibility(View.VISIBLE);
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
        else if (password.contains("\"") || password.contains("\\") || password.contains("\'") || password.contains(";"))
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
                    userInfo.setProfileIMG(ImageUtils.BitMapToString(file, 75));
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

    interface ClickListener {
        void onItemSelect(String name);
        void onNewItem(String name);
    }

}

