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
import android.support.design.widget.Snackbar;
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

import com.blackboxindia.TakeIT.HelperClasses.GlideApp;
import com.blackboxindia.TakeIT.Network.Interfaces.BitmapDownloadListener;
import com.blackboxindia.TakeIT.Network.Interfaces.addCollegeDataListener;
import com.blackboxindia.TakeIT.Network.Interfaces.getCollegeDataListener;
import com.blackboxindia.TakeIT.Network.Interfaces.onUpdateListener;
import com.blackboxindia.TakeIT.Network.NetworkMethods;
import com.blackboxindia.TakeIT.R;
import com.blackboxindia.TakeIT.activities.MainActivity;
import com.blackboxindia.TakeIT.cameraIntentHelper.ImageUtils;
import com.blackboxindia.TakeIT.dataModels.UserInfo;

import java.util.ArrayList;

public class Frag_myProfile extends Fragment {

    //region Variables

    private static String TAG = Frag_myProfile.class.getSimpleName() + " YOYO";
    private static final int PICK_PHOTO_CODE = 120;

    TextInputEditText etName, etEmail, etAddress, etPhone, etCollege;
    Spinner hostelSpinner;
    Button btn_update, btn_ImageChange;
    View MainView;
    Context context;
    ImageView imageView;
    ImageUtils imageUtils;

    UserInfo userInfo;
    UserInfo userInfoNew;
    Bitmap newProfileImage;

    ArrayList<String> hostelList;
    Frag_newAccount.ClickListener hostelListener;
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
    }
    //endregion

    void populateViews() {
        if(userInfo.getHasProfileIMG())
            ((MainActivity)context).imageStorageMethods.getProfileImage(userInfo.getuID(), new BitmapDownloadListener() {
                @Override
                public void onSuccess(Uri uri) {
                    GlideApp.with(context).load(uri).into(imageView);
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
                Toast.makeText(context, "Sorry, you can't change this.", Toast.LENGTH_SHORT).show();
            }
        });
        etCollege.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Sorry, you can't change this.", Toast.LENGTH_SHORT).show();
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

        networkMethods.getHostelOptions(userInfo.getCollegeName(), new getCollegeDataListener() {
            @Override
            public void onSuccess(ArrayList<String> data) {

                MainView.findViewById(R.id.profile_hostelProgress).setVisibility(View.INVISIBLE);
                if (data != null)
                    hostelList = data;
                else
                    hostelList = new ArrayList<>();

                hostelListener = new Frag_newAccount.ClickListener() {
                    @Override
                    public void onItemSelect(String name) {

                    }

                    @Override
                    public void onNewItem(String name) {
                        final ProgressDialog progressDialog = ProgressDialog.show(context, "Adding new hostel", "Please wait...", true, false);
                        hostelList.add(name);
                        networkMethods.addNewHostel(hostelList,
                                userInfo.getCollegeName(),
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

    private void configureHostelSpinner(final Frag_newAccount.ClickListener listener){

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
                    createCustomDialog("Hostel Name:",listener);
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

    void createCustomDialog(String title, final Frag_newAccount.ClickListener listener){
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

            if(userInfoNew.getHasProfileIMG())
                newProfileImage = null;

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
