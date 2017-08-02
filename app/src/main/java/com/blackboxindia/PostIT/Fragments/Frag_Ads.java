package com.blackboxindia.PostIT.Fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.transition.Fade;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.blackboxindia.PostIT.HelperClasses.adViewTransition;
import com.blackboxindia.PostIT.Network.Interfaces.onCompleteListener;
import com.blackboxindia.PostIT.Network.NetworkMethods;
import com.blackboxindia.PostIT.R;
import com.blackboxindia.PostIT.activities.MainActivity;
import com.blackboxindia.PostIT.adapters.EventsAdapter;
import com.blackboxindia.PostIT.adapters.MainAdapter;
import com.blackboxindia.PostIT.adapters.teachingAdAdapter;
import com.blackboxindia.PostIT.dataModels.AdData;
import com.blackboxindia.PostIT.dataModels.UserInfo;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.blackboxindia.PostIT.dataModels.AdTypes.TYPE_EVENT;
import static com.blackboxindia.PostIT.dataModels.AdTypes.TYPE_LOSTFOUND;
import static com.blackboxindia.PostIT.dataModels.AdTypes.TYPE_SELL;
import static com.blackboxindia.PostIT.dataModels.AdTypes.TYPE_TEACH;

public class Frag_Ads extends Fragment {

    //region variables
    private static final String TAG = Frag_Ads.class.getSimpleName() + " YOYO";
    public static final String ARGS_AdType = "AdType";


    @SuppressWarnings("FieldCanBeLocal")
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
    Map<AdData,Integer> priority;
    //endregion

    //region Initial Setup

    @Override
    public void onResume() {
        ((MainActivity)context).showIT();
        String title = MainActivity.TITLE_AllAds;
        switch (adType){
            case TYPE_LOSTFOUND:
                title = "Lost and Found";
                break;
            case TYPE_EVENT:
                title  = "All Events";
                break;
        }
        ((MainActivity)context).toolbar.setTitle(title);
        super.onResume();
    }

    @Override
    public void onStop() {
        ((MainActivity)context).hideIT();
        List<FileDownloadTask> activeDownloadTasks = FirebaseStorage.getInstance().getReference().getActiveDownloadTasks();
        for (FileDownloadTask task :
                activeDownloadTasks) {
            task.cancel();
        }
        super.onStop();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_ads, container, false);
        context = view.getContext();
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        recyclerView = view.findViewById(R.id.ads_recycler);

        Bundle arguments = getArguments();
        if(arguments!= null)
            adType = arguments.getString(ARGS_AdType);

        if (adType == null) {
            adType = TYPE_SELL;
        }

        ((MainActivity)context).setUpFab(adType);

        swipeRefreshLayout.setRefreshing(true);
        refresh();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        return view;
    }

    private void setUpRecyclerView() {

        if(allAds.size()!=0)
            view.findViewById(R.id.ads_default).setVisibility(View.GONE);

        switch (adType){
            case TYPE_SELL:
                setUp1();
                break;
            case TYPE_LOSTFOUND:
                setUp1();
                break;
            case TYPE_TEACH:
                setUp2();
                break;
            case TYPE_EVENT:
                setUp3();
                break;
        }
    }

    private void setUp1() {

        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));

        MainAdapter adapter = new MainAdapter(context,allAds, new MainAdapter.ImageClickListener() {
            @Override
            public void onClick(MainAdapter.adItemViewHolder holder, int position, AdData currentAd, Bitmap main) {

                Frag_ViewAd fragViewAd = Frag_ViewAd.newInstance(allAds.get(position));
                current = main;

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

    private void setUp2() {

        recyclerView.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));

        teachingAdAdapter adapter = new teachingAdAdapter(context,allAds, new teachingAdAdapter.ImageClickListener() {

            @Override
            public void onClick(teachingAdAdapter.adItemViewHolder holder, int position, AdData currentAd) {


                Frag_ViewAd fragViewAd = Frag_ViewAd.newInstance(allAds.get(position));

//                ((MainActivity)context).launchOtherFragment(fragViewAd,VIEW_AD_TAG);
                Bundle args = new Bundle();
                args.putString(ARGS_AdType, TYPE_TEACH);
                fragViewAd.setArguments(args);

                getActivity().getFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout, fragViewAd, MainActivity.VIEW_AD_TAG)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .addToBackStack(null)
                        .commit();


            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void setUp3() {
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));

        EventsAdapter adapter = new EventsAdapter(context,allAds, new EventsAdapter.ImageClickListener() {
            @Override
            public void onClick(EventsAdapter.adItemViewHolder holder, int position, AdData currentAd, Bitmap main) {

                Frag_ViewEvent fragViewAd = Frag_ViewEvent.newInstance(allAds.get(position));

                fragViewAd.setSharedElementEnterTransition(new adViewTransition());
                fragViewAd.setEnterTransition(new Fade());
                setExitTransition(new Fade());
                fragViewAd.setSharedElementReturnTransition(new adViewTransition());

                getActivity().getFragmentManager().beginTransaction()
                        .addSharedElement(holder.getMajorImage(), "adImage0")
                        .replace(R.id.frame_layout, fragViewAd, MainActivity.VIEW_EVENT_TAG)
                        .addToBackStack(null)
                        .commit();

            }
        });
        recyclerView.setAdapter(adapter);
    }

    //endregion

    public void refresh() {

        userInfo = ((MainActivity)context).userInfo;

        networkMethods = new NetworkMethods(context);
        getAllAds();

    }

    public void filter(String query) {

        Log.i(TAG, "filter: "+query);

        priority = new HashMap<>();
        query = query.trim().toLowerCase();
        if(!query.equals("") && query.length()>2) {

            String[] split = query.split(" ");

            for (AdData i:allAds) {
                for (String aSplit : split) {
                    if(aSplit.length()>2) {
                        if (i.getTitle().toLowerCase().contains(aSplit)) {
                            if (priority.containsKey(i))
                                priority.put(i, priority.get(i) + 1);
                            else
                                priority.put(i, 1);
                        }
                        if (i.getDescription().toLowerCase().contains(aSplit)) {
                            if (priority.containsKey(i))
                                priority.put(i, priority.get(i) + 1);
                            else
                                priority.put(i, 1);
                        }
                    }
                }
            }
            ArrayList<AdData> newList = new ArrayList<>(priority.keySet());
            Collections.sort(newList, new Comparator<AdData>() {
                @Override
                public int compare(AdData o1, AdData o2) {
                    if(priority.get(o1)>priority.get(o2))
                        return 1;
                    else if(priority.get(o1)<priority.get(o2))
                        return -1;
                    else
                        return 0;
                }
            });
            if(newList.isEmpty()) {
                view.findViewById(R.id.ads_default).setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.INVISIBLE);
                Toast.makeText(context, "No matches found.", Toast.LENGTH_SHORT).show();
            }
            else {
                if(recyclerView.getAdapter() != null) {
                    view.findViewById(R.id.ads_default).setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);

                    switch (adType) {
                        case TYPE_EVENT:
                            ((EventsAdapter) recyclerView.getAdapter()).change(newList);
                            break;
                        case TYPE_TEACH:
                            ((teachingAdAdapter) recyclerView.getAdapter()).change(newList);
                            break;
                        default:
                            ((MainAdapter) recyclerView.getAdapter()).change(newList);
                            break;
                    }
                }
            }
        }
        else
        if(recyclerView.getAdapter() != null) {
            if(allAds.size()==0) {
                view.findViewById(R.id.ads_default).setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.INVISIBLE);
            }else
                recyclerView.setVisibility(View.VISIBLE);
            switch (adType) {
                case TYPE_EVENT:
                    ((EventsAdapter) recyclerView.getAdapter()).change(allAds);
                    break;
                case TYPE_TEACH:
                    ((teachingAdAdapter) recyclerView.getAdapter()).change(allAds);
                    break;
                default:
                    ((MainAdapter) recyclerView.getAdapter()).change(allAds);
                    break;
            }
        }

    }

    private void getAllAds() {

        networkMethods.getAllAds( MAX_Ads ,new onCompleteListener<ArrayList<AdData>>() {
            @Override
            public void onSuccess(ArrayList<AdData> list) {
                everything = list;
                filterList();
                if(recyclerView!=null) {
                    if (recyclerView.getAdapter() == null) {
                        checkDeleteOrderEvents();
                        setUpRecyclerView();
                    }
                    else {

                        if(allAds.size()!=0)
                            view.findViewById(R.id.ads_default).setVisibility(View.GONE);

                        switch (adType) {
                            case TYPE_TEACH:
                                ((teachingAdAdapter) recyclerView.getAdapter()).change(allAds);
                                break;
                            case TYPE_EVENT:
                                checkDeleteOrderEvents();
                                ((EventsAdapter) recyclerView.getAdapter()).change(allAds);
                                break;
                            default:
                                ((MainAdapter) recyclerView.getAdapter()).change(allAds);
                                break;
                        }
                    }
                }
                else
                    setUpRecyclerView();
                if(swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Exception e) {
                if(swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterList() {

        allAds = new ArrayList<>();
        if(everything!=null){
            if(everything.size()!=0){
                for (AdData ad : everything) {
                    if(ad.getType().equals(adType))
                        allAds.add(ad);
                }
            }
        }

    }

    private void checkDeleteOrderEvents() {

        Log.i(TAG, "checkDeleteOrderEvents: ");

        for (int i=0; i<allAds.size();i++) {
            Calendar calender = allAds.get(i).getDateTime().toCalender();
            calender.add(Calendar.HOUR_OF_DAY,12);
            if(calender.before(Calendar.getInstance())) {
                Log.i(TAG, "checkDeleteOrderEvents: to delete: "+allAds.get(i).getTitle());
                networkMethods.deleteEvent(userInfo,allAds.get(i));
                allAds.remove(i);
            }
        }

        Collections.sort(allAds, new Comparator<AdData>() {
            @Override
            public int compare(AdData o1, AdData o2) {
                Calendar c1 = o1.getDateTime().toCalender();
                Calendar c2 = o2.getDateTime().toCalender();
                if(c1.after(c2))
                    return 1;
                else if(c1.before(c2))
                    return -1;
                else
                    return 0;
            }
        });

    }

}
