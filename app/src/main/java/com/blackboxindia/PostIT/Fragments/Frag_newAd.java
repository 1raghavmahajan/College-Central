package com.blackboxindia.PostIT.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.blackboxindia.PostIT.Network.Interfaces.onCompleteListener;
import com.blackboxindia.PostIT.Network.NetworkMethods;
import com.blackboxindia.PostIT.R;
import com.blackboxindia.PostIT.activities.MainActivity;
import com.blackboxindia.PostIT.adapters.NewAdImageAdapter;
import com.blackboxindia.PostIT.cameraIntentHelper.ImageUtils;
import com.blackboxindia.PostIT.dataModels.AdData;
import com.blackboxindia.PostIT.dataModels.DateObject;
import com.blackboxindia.PostIT.dataModels.UserInfo;

import java.util.ArrayList;
import java.util.Calendar;

import static com.blackboxindia.PostIT.Fragments.Frag_Ads.ARGS_AdType;
import static com.blackboxindia.PostIT.dataModels.AdTypes.TYPE_LOSTFOUND;
import static com.blackboxindia.PostIT.dataModels.AdTypes.TYPE_SELL;
import static com.blackboxindia.PostIT.dataModels.AdTypes.TYPE_TEACH;

public class Frag_newAd extends Fragment {

    //region Variables

    private static String TAG = Frag_newAd.class.getSimpleName()+" YOYO";
    private static Integer ADD_PHOTO_CODE = 154;

    EditText etTitle,etPrice,etDescription;
    TextView tvPrice;
    Button btn_newImg, btn_Create;
    RecyclerView recyclerView;
    NewAdImageAdapter adapter;
    View view;
    Context context;

    UserInfo userInfo;
    ImageUtils imageUtils;
    ArrayList<Uri> imgURIs;
    String adType;

    int count =0;
    private static final int NUMBER_OF_DUPLICATES = 1;

    NetworkMethods networkMethods;
    //endregion

    //region Initial Setup

    @Override
    public void onResume() {
        ((MainActivity)context).toolbar.setTitle(MainActivity.TITLE_NewAd);
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if(arguments!= null)
            adType = arguments.getString(ARGS_AdType);

        if (adType == null) {
            adType = TYPE_SELL;
            //Log.i(TAG, "onCreateView: adType null");
        }
        else
            //Log.i(TAG, "adType: "+adType);

        view = inflater.inflate(R.layout.frag_newad, container, false);

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

        setUpRecycler();

        setUpListeners();

        customize();

        initCamera();

        return view;
    }

    private void initVariables() {

        etTitle = view.findViewById(R.id.newAd_etTitle);
        tvPrice = view.findViewById(R.id.newAd_tvPrice);
        etPrice = view.findViewById(R.id.newAd_etPrice);
        etDescription = view.findViewById(R.id.newAd_etDescription);

        btn_newImg = view.findViewById(R.id.newAd_btnAddImg);
        btn_Create = view.findViewById(R.id.newAd_btnCreate);

        context = view.getContext();
        imgURIs = new ArrayList<>();
    }

    private void customize() {
        switch (adType){
            case TYPE_SELL:
                etPrice.setText("0");
                break;
            case TYPE_LOSTFOUND:
                etDescription.setHint(R.string.hintDescriptionLostFound);
                tvPrice.setVisibility(View.INVISIBLE);
                etPrice.setVisibility(View.INVISIBLE);
                break;
            case TYPE_TEACH:
                etDescription.setHint(R.string.hintDescriptionTeach);
                tvPrice.setVisibility(View.INVISIBLE);
                etPrice.setVisibility(View.INVISIBLE);
                break;
        }
    }

    private void setUpRecycler() {
        recyclerView = view.findViewById(R.id.newAd_imgRecycler);
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
                if(validateForm())
                    prepareAndCreateAd();
            }
        });
    }
    //endregion

    private boolean validateForm(){
        boolean f = true;

        if(adType.equals(TYPE_SELL)){
            if(etPrice.getText().toString().trim().equals("")){
                etPrice.setError("Required!");
            }
        }

        if(etDescription.getText().toString().trim().equals("")){
            etDescription.setError("Please give some details");
            f = false;
        }
        if(etTitle.getText().toString().equals("") ||  etTitle.getText().toString().trim().equals(getString(R.string.setTitle))){
            etTitle.setError("Please give a suitable title");
            f = false;
        }
        return f;
    }

    private void prepareAndCreateAd() {
        userInfo = ((MainActivity)context).userInfo;
        if(userInfo!=null) {

            AdData adData = new AdData();

            adData.setCreatedBy(userInfo);
            adData.setTitle(etTitle.getText().toString().trim());

            switch (adType){
                case TYPE_LOSTFOUND:
                    adData.setPrice(null);
                    break;
                case TYPE_TEACH:
                    adData.setPrice(null);
//                    if(!etPrice.getText().toString().trim().equals(""))
//                        adData.setPrice(Integer.valueOf(etPrice.getText().toString()));
                    break;
                case TYPE_SELL:
                    adData.setPrice(Integer.valueOf(etPrice.getText().toString()));
                    break;
            }

            adData.setDescription(etDescription.getText().toString().trim());
            adData.setNumberOfImages(imgURIs.size());
            adData.setDateTime(new DateObject(Calendar.getInstance()));
            adData.setType(adType);

            networkMethods = new NetworkMethods(context);

            create(adData);

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

    void create(final AdData mAdData){
        networkMethods.createNewAd(userInfo, mAdData, imgURIs, adapter.getMajor(), new onCompleteListener<AdData>() {
            @Override
            public void onSuccess(AdData adData) {

                count++;
                if(count<NUMBER_OF_DUPLICATES){
                    Toast.makeText(context, "In Progress #"+count, Toast.LENGTH_SHORT).show();
                    if(mAdData.getTitle().contains("#"))
                        mAdData.setTitle(mAdData.getTitle().replace("#"+(count-1),"#"+count));
                    else
                        mAdData.setTitle(mAdData.getTitle()+ " #1");

                    mAdData.setDescription(mAdData.getDescription()+" #"+count);
                    if(mAdData.getPrice()!=null)
                        mAdData.setPrice(mAdData.getPrice()+count);
                    create(mAdData);
                }else{
                    ((MainActivity)context).onBackPressed();
                    ((MainActivity)context).createSnackbar("Ad Created Successfully");
                }

            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(context, "Error: "+ e.getMessage() , Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //Log.i(TAG, "onRequestPermissionsResult: ");
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
