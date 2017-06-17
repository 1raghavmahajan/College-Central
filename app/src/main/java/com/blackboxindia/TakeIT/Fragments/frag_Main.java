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

import com.blackboxindia.TakeIT.Network.CloudStorageMethods;
import com.blackboxindia.TakeIT.Network.Interfaces.getAllAdsListener;
import com.blackboxindia.TakeIT.Network.NetworkMethods;
import com.blackboxindia.TakeIT.R;
import com.blackboxindia.TakeIT.activities.MainActivity;
import com.blackboxindia.TakeIT.adapters.adViewTransition;
import com.blackboxindia.TakeIT.adapters.mainAdapter;
import com.blackboxindia.TakeIT.dataModels.AdData;
import com.blackboxindia.TakeIT.dataModels.UserInfo;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import static com.blackboxindia.TakeIT.activities.MainActivity.VIEW_AD_TAG;

public class frag_Main extends Fragment {

    //region variables
    private static String TAG = frag_Main.class.getSimpleName() + " YOYO";
    View view;
    Context context;
    NetworkMethods networkMethods;
    UserInfo userInfo;
    FirebaseAuth mAuth;
    SwipeRefreshLayout swipeRefreshLayout;

    CloudStorageMethods cloudStorageMethods;

    RecyclerView recyclerView;
    Bitmap current;
    ArrayList<AdData> allAds;
    //endregion

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_main, container, false);
        context = view.getContext();
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        cloudStorageMethods = new CloudStorageMethods(context);

        refresh(true);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        return view;
    }

    public void refresh() {
        refresh(false);
    }

    public void refresh(Boolean firstTime) {

        userInfo = ((MainActivity)context).userInfo;
        mAuth = ((MainActivity)context).mAuth;

        if(swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);
        if(mAuth!=null){
            networkMethods = new NetworkMethods(context, mAuth);
            getAllAds(firstTime);
        }
        else{
        }
    }

    public void filter(String query) {
        query = query.trim().toLowerCase();
        if(!query.equals("")) {
            String[] split = query.split(" ");
            ArrayList<AdData> newList = new ArrayList<>();

            for (int i = 0; i < allAds.size(); i++) {
                for (String aSplit : split) {
                    if (allAds.get(i).getTitle().toLowerCase().contains(aSplit)) {
                        newList.add(allAds.get(i));
                        break;
                    }
                }
            }
            if(newList.isEmpty())
                Toast.makeText(context, "No matches found.", Toast.LENGTH_SHORT).show();
            else
                ((mainAdapter) recyclerView.getAdapter()).change(newList);
        }
        else
            ((mainAdapter) recyclerView.getAdapter()).change(allAds);
        //        Integer[] arr = new Integer[allAds.size()];
//        for( int i=0;i<allAds.size();i++){
//            int p=0;
//            for (String aSplit : split) {
//                if(allAds.get(i).getTitle().contains(aSplit))
//                    p++;
//            }
//            arr[i] = p;
//        }
//        for(int i=0;i<allAds.size();i++){
//
//        }
    }

    private void getAllAds(final Boolean firstTime) {
        final ProgressDialog dialog = ProgressDialog.show(context, "Just a sec", "Getting the good stuff", true, false);

        networkMethods.getAllAds( 30 ,new getAllAdsListener() {
            @Override
            public void onSuccess(ArrayList<AdData> list) {
                allAds = list;
                if(firstTime)
                    setUpRecyclerView();
                else
                    ((mainAdapter) recyclerView.getAdapter()).change(allAds);
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

        recyclerView = (RecyclerView) view.findViewById(R.id.ads_recycler);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));

        mainAdapter adapter = new mainAdapter(context,allAds, new mainAdapter.ImageClickListener() {

            @Override
            public void onClick(mainAdapter.adItemViewHolder holder, int position, AdData currentAd, Bitmap main) {

                frag_ViewAd fragViewAd = new frag_ViewAd();

                fragViewAd.setSharedElementEnterTransition(new adViewTransition());
                fragViewAd.setEnterTransition(new Fade());
                setExitTransition(new Fade());
                fragViewAd.setSharedElementReturnTransition(new adViewTransition());

                Bundle args = new Bundle();

                args.putParcelable("adData",allAds.get(position));
                current = main;

                fragViewAd.setArguments(args);

                ((MainActivity)context).launchOtherFragment(fragViewAd,VIEW_AD_TAG);

//                FragmentManager fragmentManager = getActivity().getFragmentManager();
//                fragmentManager.beginTransaction()
//                                .hide(fragmentManager.findFragmentByTag(MAIN_FRAG_TAG))
//                                .commit();
//
//                fragmentManager.beginTransaction()
//                        //.addSharedElement(holder.getMajorImage(), "adImage0")
//                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//                        .add(R.id.frame_layout, fragViewAd, MainActivity.VIEW_AD_TAG)
//                        .commit();
//                fragmentManager.beginTransaction()
//                        .addSharedElement(holder.getMajorImage(), "adImage0")
//                        .replace(R.id.frame_layout, fragViewAd, MainActivity.VIEW_AD_TAG)
//                        .addToBackStack(null)
//                        .commit();

            }
        });
        recyclerView.setAdapter(adapter);
    }

    public void clearRecycler() {
        recyclerView.swapAdapter(null,true);
    }

}
