package com.blackboxindia.TakeIT.Fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blackboxindia.TakeIT.Network.NetworkMethods;
import com.blackboxindia.TakeIT.R;
import com.blackboxindia.TakeIT.activities.MainActivity;
import com.blackboxindia.TakeIT.adapters.MyAdsAdapter;
import com.blackboxindia.TakeIT.dataModels.AdData;
import com.blackboxindia.TakeIT.dataModels.UserInfo;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import static com.blackboxindia.TakeIT.activities.MainActivity.MY_ADS_TAG;
import static com.blackboxindia.TakeIT.activities.MainActivity.VIEW_MyAD_TAG;


public class frag_myAds extends Fragment {

    private static String TAG = frag_myAds.class.getSimpleName() + " YOYO";

    //region variables
    View view;
    Context context;
    NetworkMethods networkMethods;
    UserInfo userInfo;
    FirebaseAuth mAuth;

    ArrayList<String> userAdKeys;

    public Bitmap current;

    //endregion


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_myads, container, false);
        context = view.getContext();

        if(((MainActivity)context).userInfo!=null) {

            userInfo = ((MainActivity)context).userInfo;
            userAdKeys = userInfo.getUserAdKeys();
            mAuth = ((MainActivity)context).mAuth;
            networkMethods = new NetworkMethods(context,mAuth);
            setUpRecycler();
        }

        return view;
    }

    private void setUpRecycler() {
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.myads_recycler);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        MyAdsAdapter myAdsAdapter = new MyAdsAdapter(context, userAdKeys, new MyAdsAdapter.ImageClickListener() {
            @Override
            public void onClick(MyAdsAdapter.adItemViewHolder holder, int position, AdData currentAd) {
                frag_ViewMyAd fragViewMyAd = new frag_ViewMyAd();

                Log.i(TAG,"onClick");

                Bundle args = new Bundle();
                args.putParcelable("adData",currentAd);
                fragViewMyAd.setArguments(args);

                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .add(R.id.frame_layout,fragViewMyAd,VIEW_MyAD_TAG)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
                fragmentManager.beginTransaction()
                        .remove(fragmentManager.findFragmentByTag(MY_ADS_TAG))
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                        .commit();
            }
        });
        recyclerView.setAdapter(myAdsAdapter);
    }

    @Override
    public void onResume() {
        ((MainActivity)getActivity()).hideIT();
        super.onResume();
    }
}
