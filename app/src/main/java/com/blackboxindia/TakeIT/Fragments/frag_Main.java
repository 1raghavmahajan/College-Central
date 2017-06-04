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
import com.blackboxindia.TakeIT.adapters.adViewTransiton;
import com.blackboxindia.TakeIT.adapters.mainAdapter;
import com.blackboxindia.TakeIT.dataModels.AdDataMini;

public class frag_Main extends Fragment implements mainAdapter.ImageClickListener {

    RecyclerView recyclerView;
    View view;
    Context context;

    @Override
    public void onAttach(Context context) {
        Log.i("YOYO", "onAttach");
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("YOYO", "onResume");
        ((MainActivity)getActivity()).linearLayout.setVisibility(View.VISIBLE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.i("YOYO", "onCreateView");
        view = inflater.inflate(R.layout.frag_main,container,false);;
        context = view.getContext();
        setUpRecyclerView();
        return view;
    }

    private void setUpRecyclerView() {
        recyclerView = (RecyclerView) view.findViewById(R.id.ads_recycler);
        //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);
        mainAdapter adapter = new mainAdapter(context, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onKittenClicked(mainAdapter.adItemViewHolder holder, int position, AdDataMini currentAd) {

        Log.i("YOYO", "MainActvity.onKittenClicked");

        frag_ViewAd fragViewAd = new frag_ViewAd();

        fragViewAd.setSharedElementEnterTransition(new adViewTransiton());
        fragViewAd.setEnterTransition(new Fade());
        setExitTransition(new Fade());
        fragViewAd.setSharedElementReturnTransition(new adViewTransiton());

        Bundle args = new Bundle();
        /**
         * Todo:
         * Send AdID so that the specific ad can be viewed
         */
        //args.putInt("id", currentAd.getAdID());
        args.putString("Title",currentAd.getTitle());
        args.putInt("majorImage", currentAd.getMajorImage());
        args.putInt("Price", currentAd.getPrice());

        fragViewAd.setArguments(args);

        getActivity().getFragmentManager()
                .beginTransaction()
                .addSharedElement(holder.getMajorImage(), "adImage0")
                .replace(R.id.frame_layout, fragViewAd)
                .addToBackStack(null)
                .commit();
    }
}
