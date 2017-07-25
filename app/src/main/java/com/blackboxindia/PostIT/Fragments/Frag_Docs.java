package com.blackboxindia.PostIT.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blackboxindia.PostIT.Network.Interfaces.onCompleteListener;
import com.blackboxindia.PostIT.Network.NetworkMethods;
import com.blackboxindia.PostIT.R;
import com.blackboxindia.PostIT.activities.MainActivity;
import com.blackboxindia.PostIT.adapters.DocumentAdapter;
import com.blackboxindia.PostIT.dataModels.Directory;
import com.google.firebase.auth.FirebaseAuth;

public class Frag_Docs extends Fragment {

    private static final String TAG = Frag_Docs.class.getSimpleName()+" YOYO";

    View  mainView;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    Context context;
    Directory directory;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.frag_docs, container, false);
        context = mainView.getContext();

        swipeRefreshLayout = (SwipeRefreshLayout) mainView.findViewById(R.id.docs_swipe_refresh_layout);
        recyclerView = (RecyclerView) mainView.findViewById(R.id.docs_recycler);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
                    getData();
                }
            }
        });
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            swipeRefreshLayout.setRefreshing(true);
            getData();
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    getData();
                }
            });
        }else{
            ((MainActivity)context).createSnackbar("Not Logged In!");
        }


        return mainView;
    }

    private void setUpRecycler() {
        recyclerView.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(new DocumentAdapter(context,directory));
    }

    public void getData() {

        new NetworkMethods(context).getAllFiles(((MainActivity)context).userInfo.getCollegeName(), new onCompleteListener<Directory>() {

            @Override
            public void onSuccess(Directory dir) {
                directory = dir;
                if(swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);

                if(recyclerView.getAdapter()!=null)
                    ((DocumentAdapter)recyclerView.getAdapter()).changeRoot(directory);
                else
                    setUpRecycler();
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "onFailure: getDir", e);
            }

        });

    }

    @Override
    public void onDestroy() {
        ((MainActivity)context).onBackPressedListener = null;
        super.onDestroy();
    }

}
