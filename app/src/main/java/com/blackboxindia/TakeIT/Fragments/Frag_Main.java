package com.blackboxindia.TakeIT.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blackboxindia.TakeIT.R;
import com.blackboxindia.TakeIT.activities.MainActivity;

public class Frag_Main extends Fragment {

    private static final String TAG = Frag_Main.class.getSimpleName()+" YOYO";
    
    View view;
    Context context;
    
    CardView[] cardView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_main,container,false);
        context = view.getContext();    
        
        initVariables();
        
        setListeners();
        
        return view;
    }

    private void setListeners() {
        cardView[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"onClick: card0");
                ((MainActivity)context).launchOtherFragment(new Frag_Ads(),MainActivity.ALL_FRAG_TAG);
            }
        });
        cardView[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"onClick: card1");
//                ((MainActivity)context).goToMainFragment(false,false);
            }
        });
        cardView[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"onClick: card2");
//                ((MainActivity)context).goToMainFragment(false,false);
            }
        });
        cardView[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"onClick: card3");
//                ((MainActivity)context).goToMainFragment(false,false);
            }
        });
        cardView[4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"onClick: card4");
//                ((MainActivity)context).goToMainFragment(false,false);
            }
        });
    }

    private void initVariables() {

        cardView = new CardView[5];
        cardView[0] = (CardView) view.findViewById(R.id.mainCard1);
        cardView[1] = (CardView) view.findViewById(R.id.mainCard2);
        cardView[2] = (CardView) view.findViewById(R.id.mainCard3);
        cardView[3] = (CardView) view.findViewById(R.id.mainCard4);
        cardView[4] = (CardView) view.findViewById(R.id.mainCard5);
    }
    
    
}
