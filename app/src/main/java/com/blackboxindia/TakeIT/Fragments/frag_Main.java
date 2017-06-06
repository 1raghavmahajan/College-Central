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

import com.blackboxindia.TakeIT.R;
import com.blackboxindia.TakeIT.activities.MainActivity;
import com.blackboxindia.TakeIT.adapters.adViewTransition;
import com.blackboxindia.TakeIT.adapters.mainAdapter;
import com.blackboxindia.TakeIT.dataModels.AdDataMini;

public class frag_Main extends Fragment {

    //region variables
    View view;
    Context context;
    //endregion

    //region Initial Setup

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)getActivity()).linearLayout.setVisibility(View.VISIBLE);
    }

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
                        .replace(R.id.frame_layout, fragViewAd)
                        .addToBackStack(null)
                        .commit();

            }
        });
        recyclerView.setAdapter(adapter);
    }

    //endregion

}
