package com.blackboxindia.TakeIT.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blackboxindia.TakeIT.R;
import com.blackboxindia.TakeIT.adapters.ImageAdapter;
import com.blackboxindia.TakeIT.dataModels.AdDataMini;

public class frag_ViewAd extends Fragment {

    //region Variables
    RecyclerView imgRecyclerView;
    TextView tv_Title, tv_Price;
    View view;
    Context context;
    //endregion

    //region Initial Setup
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.frag_viewad, container, false);
        context = view.getContext();

        tv_Title = (TextView) view.findViewById(R.id.Ad_tvTitle);
        tv_Price = (TextView) view.findViewById(R.id.Ad_tvPrice);
        imgRecyclerView = (RecyclerView) view.findViewById(R.id.Ad_imgRecycler);

        setUpViews();

        return view;
    }

    void setUpViews() {

        AdDataMini dataMini = new AdDataMini(getArguments());

        if (dataMini.getPrice() == 0)
            tv_Price.setText(getString(R.string.free));
        else
            tv_Price.setText(String.format(getString(R.string.currency), dataMini.getPrice()));

        tv_Title.setText(dataMini.getTitle());
        setUpImgRecycler(dataMini.getMajorImage());

    }

    void setUpImgRecycler(Integer img) {
        ImageAdapter adapter = new ImageAdapter(context, img);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        imgRecyclerView.setLayoutManager(linearLayoutManager);
        imgRecyclerView.setAdapter(adapter);
    }

    //endregion
}

