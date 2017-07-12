package com.blackboxindia.TakeIT.Fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.blackboxindia.TakeIT.HelperClasses.adViewTransition;
import com.blackboxindia.TakeIT.Network.Interfaces.newAdListener;
import com.blackboxindia.TakeIT.Network.NetworkMethods;
import com.blackboxindia.TakeIT.R;
import com.blackboxindia.TakeIT.activities.MainActivity;
import com.blackboxindia.TakeIT.adapters.MyAdsAdapter;
import com.blackboxindia.TakeIT.dataModels.AdData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.blackboxindia.TakeIT.dataModels.AdTypes.TYPE_EVENT;


public class Frag_myAds extends Fragment {

    //region variables

    private static String TAG = Frag_myAds.class.getSimpleName() + " YOYO";
    View view;
    Context context;
    NetworkMethods networkMethods;

    ArrayList<String> userAdKeys;
    ArrayList<AdData> ads;
    Map<String,Integer> positions;

    public Bitmap current;

    //endregion

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_myads, container, false);
        context = view.getContext();

        if(((MainActivity)context).userInfo!=null) {

            userAdKeys = ((MainActivity)context).userInfo.getUserAdKeys();
            networkMethods = new NetworkMethods(context);
            getAds();
            setUpRecycler();
        }

        return view;
    }

    private void getAds() {
        final ProgressDialog dialog = ProgressDialog.show(context, "Getting your stuff...", "", true, false);
        ads = new ArrayList<>();
        positions = new HashMap<>();

        for (String k :
                userAdKeys) {
            final String k2=k;
            networkMethods.getAd(k2, new newAdListener() {
                @Override
                public void onSuccess(AdData adData) {
                    ads.add(adData);
                    if(ads.size()==userAdKeys.size()){
                        dialog.cancel();
                        setUpRecycler();
                    }
                }
                @Override
                public void onFailure(Exception e) {
                    networkMethods.getAd(k2, new newAdListener() {
                        @Override
                        public void onSuccess(AdData adData) {
                            if(!ads.contains(adData))
                                ads.add(adData);
                            if(ads.size()==userAdKeys.size()){
                                dialog.cancel();
                                setUpRecycler();
                            }
                        }
                        @Override
                        public void onFailure(Exception e) {
                            dialog.cancel();
                            Toast.makeText(context, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onFailure: getAd", e);
                        }
                    });
                }
            });
        }
    }

    private void setUpRecycler() {
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.myads_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));
        MyAdsAdapter myAdsAdapter = new MyAdsAdapter(context, ads, new MyAdsAdapter.ImageClickListener() {
            @Override
            public void onClick(MyAdsAdapter.adItemViewHolder holder, int position, AdData currentAd) {

                if(currentAd.getType().equals(TYPE_EVENT)){

                    Frag_ViewMyEvent frag_viewMyEvent = Frag_ViewMyEvent.newInstance(currentAd);

                    frag_viewMyEvent.setSharedElementEnterTransition(new adViewTransition());
                    frag_viewMyEvent.setEnterTransition(new Fade());
                    setExitTransition(new Fade());
                    frag_viewMyEvent.setSharedElementReturnTransition(new adViewTransition());

                    getActivity().getFragmentManager().beginTransaction()
                            .addSharedElement(holder.getMajorImage(), "adImage0")
                            .replace(R.id.frame_layout, frag_viewMyEvent, MainActivity.VIEW_MyEVENT_TAG)
                            .addToBackStack(null)
                            .commit();


                } else {

                    Frag_ViewMyAd fragViewMyAd = Frag_ViewMyAd.newInstance(currentAd);

//                current = main;

                    fragViewMyAd.setSharedElementEnterTransition(new adViewTransition());
                    fragViewMyAd.setEnterTransition(new Fade());
                    setExitTransition(new Fade());
                    fragViewMyAd.setSharedElementReturnTransition(new adViewTransition());

                    getActivity().getFragmentManager().beginTransaction()
                            .addSharedElement(holder.getMajorImage(), "adImage0")
                            .replace(R.id.frame_layout, fragViewMyAd, MainActivity.VIEW_MyAD_TAG)
                            .addToBackStack(null)
                            .commit();

                }
            }
        });
        recyclerView.setAdapter(myAdsAdapter);
    }

}
