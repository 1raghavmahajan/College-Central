package com.blackboxindia.PostIT.Fragments;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.blackboxindia.PostIT.Network.Interfaces.onCompleteListener;
import com.blackboxindia.PostIT.Network.NetworkMethods;
import com.blackboxindia.PostIT.R;
import com.blackboxindia.PostIT.activities.MainActivity;
import com.blackboxindia.PostIT.adapters.NewAdImageAdapter;
import com.blackboxindia.PostIT.adapters.ViewAdImageAdapter;
import com.blackboxindia.PostIT.cameraIntentHelper.ImageUtils;
import com.blackboxindia.PostIT.dataModels.AdData;
import com.blackboxindia.PostIT.dataModels.AdTypes;
import com.blackboxindia.PostIT.dataModels.DateObject;
import com.blackboxindia.PostIT.dataModels.UserInfo;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class Frag_EditEvent extends Fragment {

    //region Variables

    private static String TAG = Frag_EditEvent.class.getSimpleName()+" YOYO";
    private static Integer ADD_PHOTO_CODE = 154;

    EditText etTitle, etDate, etTime, etDescription;
    Button btn_newImg, btn_Create;
    RecyclerView recyclerView;
    NewAdImageAdapter adapter;
    View view;
    Context context;
    
    AdData event;

    UserInfo userInfo;
    ImageUtils imageUtils;
    ArrayList<Uri> imgURIs;

    Calendar myCalendar;
    NetworkMethods networkMethods;

    //endregion

    //region Initial Setup

    @Override
    public void onResume() {
        ((MainActivity)context).toolbar.setTitle(MainActivity.TITLE_EditEvent);
        super.onResume();
    }

    public static Frag_EditEvent newInstance(AdData event) {

        Frag_EditEvent fragment = new Frag_EditEvent();
        fragment.event = event;

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_editevent, container, false);

        initVariables();

        etTitle.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void afterTextChanged(Editable s) {

                for(int i = s.length(); i > 0; i--) {
                    if(s.subSequence(i-1, i).toString().equals("\n"))
                        s.replace(i-1, i, " ");
                }
            }
        });
        etTitle.requestFocus();

        PopulateViews();

        setUpListeners();

        return view;
    }

    private void initVariables() {
        myCalendar = Calendar.getInstance();

        etTitle = view.findViewById(R.id.AdTitle);
        etDate = view.findViewById(R.id.newAd_etDate);
        etTime = view.findViewById(R.id.newAd_etTime);

        etDescription = view.findViewById(R.id.newAd_etDescription);

        btn_newImg = view.findViewById(R.id.newAd_btnAddImg);
        btn_Create = view.findViewById(R.id.newAd_btnCreate);

        context = view.getContext();
        imgURIs = new ArrayList<>();
    }

    void PopulateViews() {

        if(event!=null) {

            etTitle.setText(event.getTitle());
            etDescription.setText(event.getDescription());

            String myFormat = "dd/MM/yy";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            etDate.setText(sdf.format(event.getDateTime().toCalender().getTime()));
            String timeFormat = "hh:mm a";
            SimpleDateFormat tf = new SimpleDateFormat(timeFormat, Locale.US);
            etTime.setText(tf.format(event.getDateTime().toCalender().getTime()));

            setUpRecycler();
        }

    }

    private void setUpRecycler() {
        recyclerView = view.findViewById(R.id.ImageRecycler);
        if(event.getNumberOfImages()>0) {
            view.findViewById(R.id.ImgRecyclerHint).setVisibility(View.GONE);
            ViewAdImageAdapter adapter = new ViewAdImageAdapter(context, event, null, view);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(adapter);
        }else {
            initCamera();
            adapter = new NewAdImageAdapter(context, new NewAdImageAdapter.onDeleteClickListener() {
                @Override
                public void onDelete(int position) {
                    imgURIs.remove(position);
                }
            });
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL, false));
        }
    }

    private void setUpListeners() {

        btn_Create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateForm())
                    prepareAndCreateAd();
            }
        });
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((EditText)v).setError(null);
                Calendar currentDate= Calendar.getInstance();
                int
                    yy = currentDate.get(Calendar.YEAR),
                    mm = currentDate.get(Calendar.MONTH),
                    dd = currentDate.get(Calendar.DAY_OF_MONTH);

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
                datePickerDialog.show();

            }
        });

        etTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((EditText)v).setError(null);
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                        myCalendar.set(Calendar.HOUR_OF_DAY,selectedHour);
                        myCalendar.set(Calendar.MINUTE,selectedMinute);

                        String myFormat = "hh:mm a";
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                        etTime.setText( sdf.format(myCalendar.getTime()));
                    }
                }, hour, minute, false);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });
    }
    //endregion

    private boolean validateForm(){
        boolean f = true;
        if(etDate.getText().toString().trim().equals("")){
            etDate.setError("Required!");
            f = false;
        }
        if(etTime.getText().toString().trim().equals("")){
            etTime.setError("Required!");
            f = false;
        }
        if(etDescription.getText().toString().trim().equals("")){
            etDescription.setError("Please give some details about the event");
            f = false;
        }
        if(etTitle.getText().toString().equals("") ||  etTitle.getText().toString().toLowerCase().contains("title")){
            etTitle.setError("Please give a suitable title");
            f = false;
        }
        return f;
    }

    private void prepareAndCreateAd() {
        userInfo = ((MainActivity)context).userInfo;
        if(userInfo!=null) {

            AdData nEvent = event;
            
            nEvent.setTitle(etTitle.getText().toString().trim());
            nEvent.setPrice(null);
            nEvent.setDescription(etDescription.getText().toString().trim());

            if(imgURIs!=null) {
                if (imgURIs.size() > 0) {
                    nEvent.setNumberOfImages(imgURIs.size());
                }
            }
            
            nEvent.setDateTime(new DateObject(myCalendar));

            nEvent.setType(AdTypes.TYPE_EVENT);

            networkMethods = new NetworkMethods(context);

            updateEvent(nEvent);

        }
        else
        {
            Toast.makeText(context, "Not Logged in!", Toast.LENGTH_SHORT).show();
        }
    }

    void updateEvent(AdData event){
        boolean ff = true;
        if(imgURIs!=null){
            if(imgURIs.size()>0){
                ff = false;
                networkMethods.editAd(event, new onCompleteListener<AdData>() {
                    @Override
                    public void onSuccess(AdData data) {
                        ((MainActivity)context).onBackPressed();
                        ((MainActivity)context).createSnackbar("Ad Updated Successfully");
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(context, "Error: "+ e.getMessage() , Toast.LENGTH_SHORT).show();
                    }
                }, true, ((NewAdImageAdapter)recyclerView.getAdapter()).getMajor(), imgURIs );
            }
        }
        if(ff){
            networkMethods.editAd(event, new onCompleteListener<AdData>() {
                @Override
                public void onSuccess(AdData data) {
                    ((MainActivity)context).onBackPressed();
                    ((MainActivity)context).createSnackbar("Ad Updated Successfully");
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(context, "Error: "+ e.getMessage() , Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    //region Camera Setup
    private void initCamera() {
        imageUtils = new ImageUtils(getActivity(), this, true, new ImageUtils.ImageAttachmentListener() {
            @Override
            public void image_attachment(int from, String filename, Bitmap file, Uri uri) {
                if(imgURIs.isEmpty())
                    view.findViewById(R.id.ImgRecyclerHint).setVisibility(View.GONE);
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
