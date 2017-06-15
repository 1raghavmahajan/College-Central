package com.blackboxindia.TakeIT.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blackboxindia.TakeIT.Network.CloudStorageMethods;
import com.blackboxindia.TakeIT.Network.Interfaces.onLoginListener;
import com.blackboxindia.TakeIT.Network.NetworkMethods;
import com.blackboxindia.TakeIT.R;
import com.blackboxindia.TakeIT.activities.MainActivity;
import com.blackboxindia.TakeIT.adapters.ViewAdImageAdapter;
import com.blackboxindia.TakeIT.dataModels.AdData;
import com.blackboxindia.TakeIT.dataModels.UserInfo;
import com.google.firebase.auth.FirebaseAuth;

public class frag_ViewAd extends Fragment {

    //region Variables
    private static String TAG = frag_ViewAd.class.getSimpleName() +" YOYO";
    RecyclerView imgRecyclerView;
    TextView tv_Title, tv_Price, tv_Description;
    TextView tv_Name, tv_Address, tv_Phone;
    View view;
    Context context;

    AdData adData;
    Bitmap main;
    CloudStorageMethods cloudStorageMethods;
    //endregion

    //region Initial Setup
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.frag_viewad, container, false);
        context = view.getContext();

        initVariables();

        setUpViews();
        return view;
    }

    private void initVariables() {

        tv_Title = (TextView) view.findViewById(R.id.Ad_tvTitle);
        tv_Price = (TextView) view.findViewById(R.id.Ad_tvPrice);
        tv_Description = (TextView) view.findViewById(R.id.Ad_tvDescription);
        imgRecyclerView = (RecyclerView) view.findViewById(R.id.Ad_imgRecycler);
        tv_Name = (TextView) view.findViewById(R.id.Ad_tvName);
        tv_Address = (TextView) view.findViewById(R.id.Ad_tvAddress);
        tv_Phone = (TextView) view.findViewById(R.id.Ad_tvPhone);

        cloudStorageMethods = new CloudStorageMethods(context);
    }

    void setUpViews() {

        adData = getArguments().getParcelable("adData");

        if(adData!=null) {
            if (adData.getPrice() == 0)
                tv_Price.setText(getString(R.string.free));
            else
                tv_Price.setText(String.format(getString(R.string.currency), adData.getPrice()));

            tv_Title.setText(adData.getTitle());
            tv_Description.setText(adData.getDescription());

            NetworkMethods networkMethods = new NetworkMethods(context, FirebaseAuth.getInstance());
            networkMethods.getUserDetails(adData.getCreatedBy(), new onLoginListener() {
                @Override
                public void onSuccess(FirebaseAuth Auth, UserInfo userInfo) {
                    tv_Name.setText(userInfo.getName());
                    tv_Address.setText(userInfo.getAddress());
                    tv_Phone.setText(userInfo.getPhone());
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e(TAG,"getUserDetails failed",e);
                }
            });

            setUpImgRecycler();
        }
        else
            Log.i("frag_ViewAd YOYO","no adDATA");
    }

    void setUpImgRecycler() {
        main = ((frag_Main)(getFragmentManager().findFragmentByTag(MainActivity.MAIN_FRAG_TAG))).current;
        ViewAdImageAdapter adapter = new ViewAdImageAdapter(context, adData, main, cloudStorageMethods);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        imgRecyclerView.setLayoutManager(linearLayoutManager);
        imgRecyclerView.setAdapter(adapter);
    }

    //endregion

    @Override
    public void onResume() {
        ((MainActivity)getActivity()).hideIT();
        super.onResume();
    }
}

