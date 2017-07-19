package com.blackboxindia.PostIT.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blackboxindia.PostIT.R;
import com.blackboxindia.PostIT.activities.MainActivity;
import com.blackboxindia.PostIT.dataModels.AdTypes;

public class Frag_Main extends Fragment {

    private static final String TAG = Frag_Main.class.getSimpleName()+" YOYO";
    
    View view;
    Context context;
    MainActivity mainActivity;
    
    CardView[] cardView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_main,container,false);
        context = view.getContext();
        mainActivity = (MainActivity) context;

        initVariables();

        setListeners();

        return view;
    }

    private void initVariables() {
        cardView = new CardView[6];
        cardView[0] = (CardView) view.findViewById(R.id.mainCard0);
        cardView[1] = (CardView) view.findViewById(R.id.mainCard1);
        cardView[2] = (CardView) view.findViewById(R.id.mainCard2);
        cardView[3] = (CardView) view.findViewById(R.id.mainCard3);
        cardView[4] = (CardView) view.findViewById(R.id.mainCard4);
        cardView[5] = (CardView) view.findViewById(R.id.mainCard5);
    }

    private void setListeners() {
        cardView[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"onClick: sell");

                Frag_Ads frag_ads = new Frag_Ads();

                Bundle args = new Bundle();
                args.putString(Frag_Ads.ARGS_AdType, AdTypes.TYPE_SELL);
                frag_ads.setArguments(args);

                mainActivity.launchOtherFragment(frag_ads,MainActivity.ALL_FRAG_TAG);
            }
        });
        cardView[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"onClick: lost");
                Frag_Ads frag_ads = new Frag_Ads();

                Bundle args = new Bundle();
                args.putString(Frag_Ads.ARGS_AdType, AdTypes.TYPE_LOSTFOUND);
                frag_ads.setArguments(args);

                mainActivity.launchOtherFragment(frag_ads,MainActivity.ALL_FRAG_TAG);
            }
        });
        cardView[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"onClick: event");
                Frag_Ads frag_ads = new Frag_Ads();

                Bundle args = new Bundle();
                args.putString(Frag_Ads.ARGS_AdType, AdTypes.TYPE_EVENT);
                frag_ads.setArguments(args);

                mainActivity.launchOtherFragment(frag_ads,MainActivity.ALL_FRAG_TAG);
            }
        });
        cardView[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"onClick: teach");
                Frag_Ads frag_ads = new Frag_Ads();

                Bundle args = new Bundle();
                args.putString(Frag_Ads.ARGS_AdType, AdTypes.TYPE_TEACH);
                frag_ads.setArguments(args);

                mainActivity.launchOtherFragment(frag_ads,MainActivity.ALL_FRAG_TAG);


            }
        });
        cardView[4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"onClick: data");
                
                if(mainActivity.userInfo != null){
                    mainActivity.launchOtherFragment(new Frag_Docs(),MainActivity.DOCS_TAG);
                }
                else {
                    mainActivity.createSnackbar("Please Login to view Documents", Snackbar.LENGTH_LONG);
                }

            }
        });
    }

}
