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
import com.blackboxindia.PostIT.adapters.ViewAdImageAdapter;
import com.blackboxindia.PostIT.cameraIntentHelper.ImageUtils;
import com.blackboxindia.PostIT.dataModels.AdData;
import com.blackboxindia.PostIT.dataModels.DateObject;
import com.blackboxindia.PostIT.dataModels.UserInfo;

import java.util.ArrayList;
import java.util.Calendar;

import static com.blackboxindia.PostIT.dataModels.AdTypes.TYPE_LOSTFOUND;
import static com.blackboxindia.PostIT.dataModels.AdTypes.TYPE_SELL;
import static com.blackboxindia.PostIT.dataModels.AdTypes.TYPE_TEACH;

public class Frag_EditAd extends Fragment {

    //region Variables

    private static String TAG = Frag_EditAd.class.getSimpleName()+" YOYO";
    private static Integer ADD_PHOTO_CODE = 154;

    EditText etTitle,etPrice,etDescription;
    TextView tvPrice;
    Button btn_newImg, btn_UpdateAd;
    RecyclerView recyclerView;
    NewAdImageAdapter adapter;
    View view;
    Context context;

    AdData ad;

    UserInfo userInfo;
    ImageUtils imageUtils;
    ArrayList<Uri> imgURIs;
    String adType;

    NetworkMethods networkMethods;
    //endregion

    //region Initial Setup

    @Override
    public void onResume() {
        ((MainActivity)context).toolbar.setTitle(MainActivity.TITLE_EditAd);
        super.onResume();
    }

    public static Frag_EditAd newInstance(AdData ad) {
        Frag_EditAd fragment = new Frag_EditAd();
        fragment.ad = ad;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        if(ad!= null)
            adType = ad.getType();

        if (adType == null) {
            adType = TYPE_SELL;
        }

        view = inflater.inflate(R.layout.frag_editad, container, false);

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

        customize();

        return view;
    }

    private void initVariables() {

        etTitle = view.findViewById(R.id.AdTitle);
        tvPrice = view.findViewById(R.id.newAd_tvPrice);
        etPrice = view.findViewById(R.id.newAd_etPrice);
        etDescription = view.findViewById(R.id.newAd_etDescription);

        btn_newImg = view.findViewById(R.id.newAd_btnAddImg);
        btn_UpdateAd = view.findViewById(R.id.newAd_btnCreate);

        context = view.getContext();
        imgURIs = new ArrayList<>();
    }

    void PopulateViews() {
        if(ad!=null) {
            if(ad.getPrice()!=null) {
                if (ad.getPrice() == 0)
                    etPrice.setText(getString(R.string.free));
                else
                    etPrice.setText(String.format(getString(R.string.currency), ad.getPrice()));
            }
            else
                etPrice.setVisibility(View.INVISIBLE);

            etTitle.setText(ad.getTitle());
            etDescription.setText(ad.getDescription());
            setUpImgRecycler();
        }
    }

    void setUpImgRecycler() {
        recyclerView = view.findViewById(R.id.ImageRecycler);
        if(ad.getNumberOfImages()>0) {
            view.findViewById(R.id.ImgRecyclerHint).setVisibility(View.GONE);
            ViewAdImageAdapter adapter = new ViewAdImageAdapter(context, ad, null, view);
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

    private void customize() {
        if(ad.getNumberOfImages()>0){
            btn_newImg.setVisibility(View.GONE);
        }
        switch (adType){
            case TYPE_SELL:
                etPrice.setText("0");
                break;
            case TYPE_LOSTFOUND:
                etDescription.setHint(R.string.hintDescriptionLostFound);
                tvPrice.setVisibility(View.GONE);
                etPrice.setVisibility(View.GONE);
                break;
            case TYPE_TEACH:
                etDescription.setHint(R.string.hintDescriptionTeach);
                tvPrice.setVisibility(View.GONE);
                etPrice.setVisibility(View.GONE);
                break;
        }
    }

    private void setUpListeners() {
        btn_UpdateAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateForm())
                    prepareAndUpdateAd();
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
        if(etTitle.getText().toString().equals("") ||  etTitle.getText().toString().toLowerCase().contains("title")){
            etTitle.setError("Please give a suitable title");
            f = false;
        }
        return f;
    }

    private void prepareAndUpdateAd() {
        userInfo = ((MainActivity)context).userInfo;
        if(userInfo!=null) {

            AdData adData = ad;

            adData.setCreatedBy(userInfo);
            adData.setTitle(etTitle.getText().toString().trim());

            switch (adType){
                case TYPE_LOSTFOUND:
                    adData.setPrice(null);
                    break;
                case TYPE_TEACH:
                    adData.setPrice(null);
                    break;
                case TYPE_SELL:
                    adData.setPrice(Integer.valueOf(etPrice.getText().toString()));
                    break;
            }

            adData.setDescription(etDescription.getText().toString().trim());

            if(imgURIs!=null) {
                if (imgURIs.size() > 0) {
                    adData.setNumberOfImages(imgURIs.size());
                }
            }

            adData.setDateTime(new DateObject(Calendar.getInstance()));

            networkMethods = new NetworkMethods(context);

            Update(adData);

        }
        else
        {
            Toast.makeText(context, "Not Logged in!", Toast.LENGTH_SHORT).show();
        }
    }

    void Update(final AdData mAdData){
        boolean ff = true;
        if(imgURIs!=null){
            if(imgURIs.size()>0){
                ff = false;
                networkMethods.editAd(mAdData, new onCompleteListener<AdData>() {
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
            networkMethods.editAd(mAdData, new onCompleteListener<AdData>() {
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
