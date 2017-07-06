package com.blackboxindia.TakeIT.Fragments;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.blackboxindia.TakeIT.Network.Interfaces.AdListener;
import com.blackboxindia.TakeIT.Network.NetworkMethods;
import com.blackboxindia.TakeIT.R;
import com.blackboxindia.TakeIT.activities.MainActivity;
import com.blackboxindia.TakeIT.adapters.NewAdImageAdapter;
import com.blackboxindia.TakeIT.cameraIntentHelper.ImageUtils;
import com.blackboxindia.TakeIT.dataModels.AdData;
import com.blackboxindia.TakeIT.dataModels.UserInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class Frag_newEvent extends Fragment {

    //region Variables

    private static String TAG = Frag_newEvent.class.getSimpleName()+" YOYO";
    private static Integer ADD_PHOTO_CODE = 154;

    EditText etTitle, etDate, etTime, etDescription;
    Button btn_newImg, btn_Create;
    RecyclerView recyclerView;
    NewAdImageAdapter adapter;
    View view;
    Context context;

    UserInfo userInfo;
    ImageUtils imageUtils;
    ArrayList<Uri> imgURIs;
    //endregion

    //region Init Setup

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_newevent, container, false);

        initVariables();

        setUpRecycler();

        setUpListeners();

        initCamera();

        return view;
    }

    private void initVariables() {

        etTitle = (EditText) view.findViewById(R.id.newAd_etTitle);
        etDate = (EditText) view.findViewById(R.id.newAd_etDate);
        etTime = (EditText) view.findViewById(R.id.newAd_etTime);

        etDescription = (EditText) view.findViewById(R.id.newAd_etDescription);

        btn_newImg = (Button) view.findViewById(R.id.newAd_btnAddImg);
        btn_Create = (Button) view.findViewById(R.id.newAd_btnCreate);

        context = view.getContext();
        imgURIs = new ArrayList<>();
    }

    private void setUpRecycler() {
        recyclerView = (RecyclerView) view.findViewById(R.id.newAd_imgRecycler);
        adapter = new NewAdImageAdapter(context, new NewAdImageAdapter.onDeleteClickListener() {
            @Override
            public void onDelete(int position) {
                imgURIs.remove(position);
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL, false));
    }

    private void setUpListeners() {

        btn_Create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepareAndCreateAd();
            }
        });
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar myCalendar = Calendar.getInstance();
                final int yy,mm,dd;
                yy = myCalendar.get(Calendar.YEAR);
                mm = myCalendar.get(Calendar.MONTH);
                dd = myCalendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                myCalendar.set(Calendar.YEAR, year);
                                myCalendar.set(Calendar.MONTH, monthOfYear);
                                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                                String myFormat = "dd/MM/yy";
                                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                                etDate.setText(sdf.format(myCalendar.getTime()));
                            }
                        }, yy, mm, dd);
                datePickerDialog.setCancelable(true);
                datePickerDialog.setTitle("Set Date:");

            }
        });

        etTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        etTime.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });
    }
    //endregion

    private void prepareAndCreateAd() {
        userInfo = ((MainActivity)context).userInfo;
        if(userInfo!=null) {

            AdData adData = new AdData();

            adData.setCreatedBy(userInfo.getuID());
            adData.setTitle(etTitle.getText().toString().trim());
            adData.setPrice(Integer.valueOf(etDate.getText().toString()));
            adData.setDescription(etDescription.getText().toString().trim());
            adData.setCollegeName(userInfo.getCollegeName());

            adData.setNumberOfImages(imgURIs.size());

            NetworkMethods networkMethods = new NetworkMethods(context);
            networkMethods.createNewAd(userInfo, adData, imgURIs, adapter.getMajor(), new AdListener() {
                @Override
                public void onSuccess(AdData adData) {
                    //Todo: redirect back and refresh
                    //((MainActivity)context).goToMainFragment(false, true);
                    ((MainActivity)context).createSnackbar("Ad Created Successfully", Snackbar.LENGTH_LONG);
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(context, "Error: "+ e.getMessage() , Toast.LENGTH_SHORT).show();
                }
            });

        }
        else
        {
            Toast.makeText(context, "Not Logged in!", Toast.LENGTH_SHORT).show();
        }
    }

    //region Camera Setup
    private void initCamera() {
        imageUtils = new ImageUtils(getActivity(), this, true, new ImageUtils.ImageAttachmentListener() {
            @Override
            public void image_attachment(int from, String filename, Bitmap file, Uri uri) {
                if(imgURIs.isEmpty())
                    view.findViewById(R.id.newAd_recyclerHint).setVisibility(View.GONE);
                imgURIs.add(uri);
                adapter.addImage(file);
            }
        });

        btn_newImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageUtils.imagepicker(ADD_PHOTO_CODE);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        imageUtils.request_permission_result(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageUtils.onActivityResult(requestCode, resultCode, data);
    }
    //endregion

}
