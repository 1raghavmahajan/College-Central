package com.blackboxindia.TakeIT.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.blackboxindia.TakeIT.R;
import com.blackboxindia.TakeIT.activities.MainActivity;
import com.blackboxindia.TakeIT.adapters.mainAdapter;

public class frag_Main extends Fragment {

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
        mainAdapter adapter = new mainAdapter(context);
        recyclerView.setAdapter(adapter);
    }
}
