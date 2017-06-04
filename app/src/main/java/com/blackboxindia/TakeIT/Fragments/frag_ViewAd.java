package com.blackboxindia.TakeIT.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blackboxindia.TakeIT.R;
import com.blackboxindia.TakeIT.adapters.ImageAdapter;

/**
 * Created by Raghav on 04-Jun-17.
 */

public class frag_ViewAd extends Fragment {

    RecyclerView imgRecyclerView;
    TextView tv_Title, tv_Price;
    View mainView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_viewad,container,false);

        Log.i("YOYO", "Fragment creation..");

        tv_Title = (TextView) view.findViewById(R.id.Ad_tvTitle);
        tv_Price = (TextView) view.findViewById(R.id.Ad_tvPrice);
        imgRecyclerView = (RecyclerView) view.findViewById(R.id.Ad_imgRecycler);

        mainView = view;

        setUpViews();

        return view;
    }

    void setUpViews()
    {
        Bundle bundle = getArguments();
        String title = bundle.getString("Title");
        Integer price  = bundle.getInt("Price");
        Integer img  = bundle.getInt("majorImage");

        if(price==0)
            tv_Price.setText("Free");
        else
            tv_Price.setText(mainView.getContext().getString(R.string.currency) + String.valueOf(price));

        tv_Title.setText(title);
        setUpImgRecycler(img);

    }

    void setUpImgRecycler(Integer img)
    {
        ImageAdapter adapter = new ImageAdapter(mainView.getContext(), img);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mainView.getContext(),LinearLayoutManager.HORIZONTAL, false);
        imgRecyclerView.setLayoutManager(linearLayoutManager);
        imgRecyclerView.setAdapter(adapter);
    }
}

