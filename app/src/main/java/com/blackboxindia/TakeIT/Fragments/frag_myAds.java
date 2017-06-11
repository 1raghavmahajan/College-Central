package com.blackboxindia.TakeIT.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blackboxindia.TakeIT.Network.NetworkMethods;
import com.blackboxindia.TakeIT.R;
import com.blackboxindia.TakeIT.activities.MainActivity;
import com.blackboxindia.TakeIT.adapters.MyAdsAdaper;
import com.blackboxindia.TakeIT.adapters.adViewTransition;
import com.blackboxindia.TakeIT.dataModels.AdData;
import com.blackboxindia.TakeIT.dataModels.UserInfo;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;


public class frag_myAds extends Fragment {

    private static String TAG = frag_myAds.class.getSimpleName() + " YOYO";

    //region variables
    View view;
    Context context;
    NetworkMethods networkMethods;
    UserInfo userInfo;
    FirebaseAuth mAuth;

    ArrayList<String> userAdKeys;

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
        MyAdsAdaper myAdsAdaper = new MyAdsAdaper(context, userAdKeys, new MyAdsAdaper.ImageClickListener() {
            @Override
            public void onClick(MyAdsAdaper.adItemViewHolder holder, int position, AdData currentAd) {
                frag_ViewAd fragViewAd = new frag_ViewAd();

                fragViewAd.setSharedElementEnterTransition(new adViewTransition());
                fragViewAd.setEnterTransition(new Fade());
                setExitTransition(new Fade());
                fragViewAd.setSharedElementReturnTransition(new adViewTransition());

                Bundle args = new Bundle();

                args.putParcelable("adData",currentAd);

                fragViewAd.setArguments(args);

                getActivity().getFragmentManager()
                        .beginTransaction()
                        .addSharedElement(holder.getMajorImage(), "adImage0")
                        .replace(R.id.frame_layout, fragViewAd, MainActivity.VIEW_AD_TAG)
                        .addToBackStack(null)
                        .commit();
            }
        });
        recyclerView.setAdapter(myAdsAdaper);
    }

}
