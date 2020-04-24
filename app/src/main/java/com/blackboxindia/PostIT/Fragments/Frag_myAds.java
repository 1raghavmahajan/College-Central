package com.blackboxindia.PostIT.Fragments;

import static com.blackboxindia.PostIT.dataModels.AdTypes.TYPE_EVENT;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
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

    @Override
    public void onResume() {
        ((MainActivity)context).toolbar.setTitle(MainActivity.TITLE_MyAds);
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_myads, container, false);
        context = view.getContext();
        ads_default = view.findViewById(R.id.ads_default);
        swipe = view.findViewById(R.id.swipe_refresh_layout);
        recyclerView = view.findViewById(R.id.myads_recycler);

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
                                    //Log.i(TAG, "onSuccess: updated user");
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    //Log.e(TAG, "onFailure: update user ", e);
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
                                            //Log.i(TAG, "onSuccess: updated user");
                                        }

                                        @Override
                                        public void onFailure(Exception e) {
                                            //Log.e(TAG, "onFailure: update user ", e);
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
                                //Log.e(TAG, "onFailure: getAd", e);
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
                    ((MainActivity)context).launchOtherFragment(frag_viewMyEvent, MainActivity.VIEW_MyEVENT_TAG, true);
                } else {
                    Frag_ViewMyAd fragViewMyAd = Frag_ViewMyAd.newInstance(currentAd);
                    ((MainActivity)context).launchOtherFragment(fragViewMyAd, MainActivity.VIEW_MyAD_TAG,true);
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
