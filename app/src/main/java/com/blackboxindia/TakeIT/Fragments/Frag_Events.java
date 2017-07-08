package com.blackboxindia.TakeIT.Fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.blackboxindia.TakeIT.HelperClasses.adViewTransition;
import com.blackboxindia.TakeIT.Network.Interfaces.getAllAdsListener;
import com.blackboxindia.TakeIT.Network.NetworkMethods;
import com.blackboxindia.TakeIT.R;
import com.blackboxindia.TakeIT.activities.MainActivity;
import com.blackboxindia.TakeIT.adapters.MainAdapter;
import com.blackboxindia.TakeIT.dataModels.AdData;
import com.blackboxindia.TakeIT.dataModels.UserInfo;

import java.util.ArrayList;

import static com.blackboxindia.TakeIT.dataModels.AdTypes.TYPE_EVENT;
import static com.blackboxindia.TakeIT.dataModels.AdTypes.TYPE_SELL;

public class Frag_Events extends Fragment {

    //region variables
    private static final String TAG = Frag_Events.class.getSimpleName() + " YOYO";
    public static final String ARGS_AdType = "AdType";


    @SuppressWarnings("FieldCanBeLocal")
    //Todo: load rest after 40
    private static Integer MAX_Ads = 0;
    private String adType;

    View view;
    Context context;
    NetworkMethods networkMethods;
    UserInfo userInfo;
    SwipeRefreshLayout swipeRefreshLayout;

    RecyclerView recyclerView;
    Bitmap current;
    ArrayList<AdData> allAds;
    ArrayList<AdData> everything;

    //endregion


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_ads, container, false);
        context = view.getContext();
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        recyclerView = (RecyclerView) view.findViewById(R.id.ads_recycler);

        Bundle arguments = getArguments();
        if(arguments!= null)
            adType = arguments.getString(ARGS_AdType);

        if (adType == null) {
            adType = TYPE_SELL;
        }

        refresh();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        return view;
    }

    public void refresh() {

        userInfo = ((MainActivity)context).userInfo;

        if(swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);
//        if(mAuth.getCurrentUser()!=null){
        networkMethods = new NetworkMethods(context);
        getAllAds();
//        }

    }

    public void filter(String query) {

        //Todo: implement prioritized search, filter by location

        query = query.trim().toLowerCase();
        if(!query.equals("")) {

            String[] split = query.split(" ");
            ArrayList<AdData> newList = new ArrayList<>();

            for (AdData i:allAds) {
                for (String aSplit : split) {
                    if (i.getTitle().toLowerCase().contains(aSplit)) {
                        newList.add(i);
                        break;
                    }
                }
            }
            if(newList.isEmpty())
                Toast.makeText(context, "No matches found.", Toast.LENGTH_SHORT).show();
            else {
                if(recyclerView.getAdapter() != null)
                    ((MainAdapter) recyclerView.getAdapter()).change(newList);
            }
        }
        else
            if(recyclerView.getAdapter() != null)
                ((MainAdapter) recyclerView.getAdapter()).change(allAds);
    }

    private void getAllAds() {
        final ProgressDialog dialog = ProgressDialog.show(context, "Just a sec", "Getting the good stuff", true, false);

        networkMethods.getAllAds( MAX_Ads ,new getAllAdsListener() {
            @Override
            public void onSuccess(ArrayList<AdData> list) {
                allAds = list;
                if(recyclerView!=null) {
                    if (recyclerView.getAdapter() == null)
                        setUpRecyclerView();
                    else
                        ((MainAdapter) recyclerView.getAdapter()).change(allAds);
                }
                else
                    setUpRecyclerView();
                dialog.cancel();
            }

            @Override
            public void onFailure(Exception e) {
                dialog.cancel();
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUpRecyclerView() {

        switch (adType){
            case TYPE_EVENT:


        }
    }

    private void setUp() {

        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));

        MainAdapter adapter = new MainAdapter(context,allAds, new MainAdapter.ImageClickListener() {
            @Override
            public void onClick(MainAdapter.adItemViewHolder holder, int position, AdData currentAd, Bitmap main) {


                Frag_ViewAd fragViewAd = Frag_ViewAd.newInstance(allAds.get(position));
                current = main;
//                ((MainActivity)context).launchOtherFragment(fragViewAd,VIEW_AD_TAG);

                fragViewAd.setSharedElementEnterTransition(new adViewTransition());
                fragViewAd.setEnterTransition(new Fade());
                setExitTransition(new Fade());
                fragViewAd.setSharedElementReturnTransition(new adViewTransition());

                getActivity().getFragmentManager().beginTransaction()
                        .addSharedElement(holder.getMajorImage(), "adImage0")
                        .replace(R.id.frame_layout, fragViewAd, MainActivity.VIEW_AD_TAG)
                        .addToBackStack(null)
                        .commit();


            }
        });
        recyclerView.setAdapter(adapter);
    }

    public void clearRecycler() {
        if(recyclerView.getAdapter()!=null)
            recyclerView.swapAdapter(null,true);
    }

    @Override
    public void onResume() {
        ((MainActivity)context).showIT();
        super.onResume();
    }
    @Override
    public void onStop() {
        ((MainActivity)context).hideIT();
        super.onStop();
    }

}
