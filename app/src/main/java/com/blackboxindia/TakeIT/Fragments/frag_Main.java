package com.blackboxindia.TakeIT.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.transition.Fade;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blackboxindia.TakeIT.R;
import com.blackboxindia.TakeIT.activities.MainActivity;
import com.blackboxindia.TakeIT.adapters.adViewTransition;
import com.blackboxindia.TakeIT.adapters.mainAdapter;
import com.blackboxindia.TakeIT.dataModels.AdDataMini;

public class frag_Main extends Fragment {

    private static String TAG = frag_Main.class.getSimpleName() + " YOYO";

    //region variables
    View view;
    Context context;
    //endregion

    //region Initial Setup

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_main, container, false);
        context = view.getContext();
        setUpRecyclerView();
        return view;
    }

    private void setUpRecyclerView() {

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.ads_recycler);
        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);

        mainAdapter adapter = new mainAdapter(context, new mainAdapter.ImageClickListener() {

            @Override
            public void onClick(mainAdapter.adItemViewHolder holder, int position, AdDataMini currentAd) {

                frag_ViewAd fragViewAd = new frag_ViewAd();

                fragViewAd.setSharedElementEnterTransition(new adViewTransition());
                fragViewAd.setEnterTransition(new Fade());
                setExitTransition(new Fade());
                fragViewAd.setSharedElementReturnTransition(new adViewTransition());

                Bundle args = new Bundle();

                //Todo: Send AdID so that the specific ad can be viewed
                //args.putInt("id", currentAd.getAdID());
                args.putString("Title", currentAd.getTitle());
                args.putInt("majorImage", currentAd.getMajorImage());
                args.putInt("Price", currentAd.getPrice());

                fragViewAd.setArguments(args);

                getActivity().getFragmentManager()
                        .beginTransaction()
                        .addSharedElement(holder.getMajorImage(), "adImage0")
                        .replace(R.id.frame_layout, fragViewAd, MainActivity.VIEW_AD_TAG)
                        .addToBackStack(null)
                        .commit();

            }
        });
        recyclerView.setAdapter(adapter);
    }

    //endregion

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG,"onResume");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        Log.i(TAG,"onAttach");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG,"onCreate");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG,"onStart");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG,"onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG,"onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG,"onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(TAG,"onDetach");
    }

}
