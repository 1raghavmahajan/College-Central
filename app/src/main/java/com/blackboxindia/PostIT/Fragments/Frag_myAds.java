package com.blackboxindia.PostIT.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.blackboxindia.PostIT.HelperClasses.adViewTransition;
import com.blackboxindia.PostIT.Network.Interfaces.onCompleteListener;
import com.blackboxindia.PostIT.Network.Interfaces.onUpdateListener;
import com.blackboxindia.PostIT.Network.NetworkMethods;
import com.blackboxindia.PostIT.R;
import com.blackboxindia.PostIT.activities.MainActivity;
import com.blackboxindia.PostIT.adapters.MyAdsAdapter;
import com.blackboxindia.PostIT.dataModels.AdData;
import com.blackboxindia.PostIT.dataModels.UserInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static com.blackboxindia.PostIT.dataModels.AdTypes.TYPE_EVENT;


public class Frag_myAds extends Fragment {

    //region variables

    private static String TAG = Frag_myAds.class.getSimpleName() + " YOYO";
    View view;
    Context context;
    NetworkMethods networkMethods;

    ImageView ads_default;
    SwipeRefreshLayout swipe;
    RecyclerView recyclerView;

    ArrayList<String> userAdKeys;
    ArrayList<AdData> ads;

    public Bitmap current;

    //endregion

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_myads, container, false);
        context = view.getContext();
        ads_default = (ImageView) view.findViewById(R.id.ads_default);
        swipe = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        recyclerView = (RecyclerView) view.findViewById(R.id.myads_recycler);

        if(((MainActivity)context).userInfo!=null) {
            swipe.setRefreshing(true);
            userAdKeys = ((MainActivity)context).userInfo.getUserAdKeys();
            networkMethods = new NetworkMethods(context);
            getAds();
        }

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAds();
            }
        });

        return view;
    }

    private void getAds() {
        if(userAdKeys.size()>0) {

//            final ProgressDialog dialog = ProgressDialog.show(context, "Getting your stuff...", "", true, false);
            ads = new ArrayList<>();

            for (String k :
                    userAdKeys) {
                final String k2 = k;
                networkMethods.getAd(k2, new onCompleteListener<AdData>() {
                    @Override
                    public void onSuccess(AdData adData) {
                        if(adData!=null)
                            ads.add(adData);
                        else {
                            userAdKeys.remove(k2);
                        }

                        if (ads.size() == userAdKeys.size()) {
                            ((MainActivity)context).userInfo.setUserAdKeys(userAdKeys);
                            new NetworkMethods(context).UpdateUser(((MainActivity) context).userInfo, null, new onUpdateListener() {
                                @Override
                                public void onSuccess(UserInfo userInfo) {
                                    Log.i(TAG, "onSuccess: updated user");
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    Log.e(TAG, "onFailure: update user ", e);
                                }
                            });
                            orderAds();
                            if(swipe.isRefreshing())
                                swipe.setRefreshing(false);
                            if(ads.size()>0)
                                ads_default.setVisibility(View.INVISIBLE);
                            if(recyclerView.getAdapter()==null)
                                setUpRecycler();
                            else
                                ((MyAdsAdapter)recyclerView.getAdapter()).change(ads);
                        }
                    }

                    @Override
                    public void onFailure(final Exception e) {
                        networkMethods.getAd(k2, new onCompleteListener<AdData>() {
                            @Override
                            public void onSuccess(AdData adData) {
                                if(adData!=null) {
                                    if (!ads.contains(adData))
                                        ads.add(adData);
                                }
                                else{
                                    userAdKeys.remove(k2);
                                }
                                if (ads.size() == userAdKeys.size()) {
                                    ((MainActivity)context).userInfo.setUserAdKeys(userAdKeys);
                                    new NetworkMethods(context).UpdateUser(((MainActivity) context).userInfo, null, new onUpdateListener() {
                                        @Override
                                        public void onSuccess(UserInfo userInfo) {
                                            Log.i(TAG, "onSuccess: updated user");
                                        }

                                        @Override
                                        public void onFailure(Exception e) {
                                            Log.e(TAG, "onFailure: update user ", e);
                                        }
                                    });
                                    if(swipe.isRefreshing())
                                        swipe.setRefreshing(false);
                                    orderAds();
                                    if(ads.size()>0)
                                        ads_default.setVisibility(View.INVISIBLE);
                                    if(recyclerView.getAdapter()==null)
                                        setUpRecycler();
                                    else
                                        ((MyAdsAdapter)recyclerView.getAdapter()).change(ads);
                                }
                            }

                            @Override
                            public void onFailure(Exception e) {
                                if(swipe.isRefreshing())
                                    swipe.setRefreshing(false);
                                Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "onFailure: getAd", e);
                            }
                        });
                    }
                });
            }
        }
    }

    private void setUpRecycler() {
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

    private void orderAds() {
        Collections.sort(ads, new Comparator<AdData>() {
            @Override
            public int compare(AdData o1, AdData o2) {
                return o1.getType().compareTo(o2.getType());
            }
        });
    }

}
