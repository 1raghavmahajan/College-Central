package com.blackboxindia.TakeIT.Fragments;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.blackboxindia.TakeIT.Network.NetworkMethods;
import com.blackboxindia.TakeIT.Network.onCreateNewAdListener;
import com.blackboxindia.TakeIT.R;
import com.blackboxindia.TakeIT.activities.MainActivity;
import com.blackboxindia.TakeIT.adapters.NewAdImageAdapter;
import com.blackboxindia.TakeIT.cameraIntentHelper.ImageUtils;
import com.blackboxindia.TakeIT.dataModels.AdData;
import com.blackboxindia.TakeIT.dataModels.UserInfo;

import java.util.ArrayList;

public class frag_newAd extends Fragment {

    private static String TAG = "frag_newAD YOYO";
    private static Integer ADD_PHOTO_CODE = 154;

    EditText etTitle,etPrice,etDescription;
    Button btn_newImg, btn_Create;
    RecyclerView recyclerView;
    NewAdImageAdapter adapter;
    View view;
    Context context;

    UserInfo userInfo;
    ImageUtils imageUtils;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_newad, container, false);

        Log.i(TAG, "onCreateView ");

        initVariables();

        setUpRecycler();

        setUpListeners();

        initCamera();

        return view;
    }

    private void initVariables() {

        etTitle = (EditText) view.findViewById(R.id.newAd_etTitle);
        etPrice = (EditText) view.findViewById(R.id.newAd_etPrice);
        etDescription = (EditText) view.findViewById(R.id.newAd_etDescription);

        btn_newImg = (Button) view.findViewById(R.id.newAd_btnAddImg);
        btn_Create = (Button) view.findViewById(R.id.newAd_btnCreate);

        context = view.getContext();
    }

    private void setUpRecycler() {
        recyclerView = (RecyclerView) view.findViewById(R.id.newAd_imgRecycler);
        adapter = new NewAdImageAdapter(context);
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
    }

    private void prepareAndCreateAd() {
        userInfo = ((MainActivity)context).userInfo;
        if(userInfo!=null) {

            Bundle bundle = new Bundle();
            bundle.putString("createdBy", userInfo.getuID());
            bundle.putString("Title", etTitle.getText().toString().trim());
            bundle.putInt("Price", Integer.valueOf(etPrice.getText().toString()));

            AdData adData = new AdData(bundle);
            ArrayList<Bitmap> images = adapter.getImages();
            adData.setMajorImage(0);
            adData.setMinorImages(images);
            adData.setDescription(etDescription.getText().toString().trim());

//        final ProgressDialog progressDialog = new ProgressDialog(context, ProgressDialog.STYLE_SPINNER);
//        progressDialog.setTitle("Creating ad...");
//        progressDialog.setCancelable(false);
//        progressDialog.show();

            NetworkMethods networkMethods = new NetworkMethods(context, ((MainActivity) context).mAuth);
            networkMethods.createNewAd(userInfo, adData, new onCreateNewAdListener() {
                @Override
                public void onSuccess(AdData adData) {
                    Toast.makeText(context, "onSuccess", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "AdID: " + adData.getAdID());
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(context, "onFailure:", Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "onFailure: ", e);
                }
            });
        }
        else
        {
            Toast.makeText(context, "Not Logged in!", Toast.LENGTH_SHORT).show();
        }
    }

    private void initCamera() {
        imageUtils = new ImageUtils(getActivity(), this, true, new ImageUtils.ImageAttachmentListener() {
            @Override
            public void image_attachment(int from, String filename, Bitmap file, Uri uri) {
                adapter.addImage(file);
            }
        });

        btn_newImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("YOYO", "onClick");
                imageUtils.imagepicker(ADD_PHOTO_CODE);
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

    @Override
    public void onResume() {
        ((MainActivity)getActivity()).hideSearchBar();
        super.onResume();
    }

}
