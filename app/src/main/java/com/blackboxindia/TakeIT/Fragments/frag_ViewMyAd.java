package com.blackboxindia.TakeIT.Fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blackboxindia.TakeIT.Network.Interfaces.onDeleteListener;
import com.blackboxindia.TakeIT.Network.NetworkMethods;
import com.blackboxindia.TakeIT.R;
import com.blackboxindia.TakeIT.activities.MainActivity;
import com.blackboxindia.TakeIT.adapters.ViewAdImageAdapter;
import com.blackboxindia.TakeIT.dataModels.AdData;
import com.blackboxindia.TakeIT.dataModels.UserInfo;
import com.google.firebase.auth.FirebaseAuth;

import static com.blackboxindia.TakeIT.activities.MainActivity.MY_ADS_TAG;
import static com.blackboxindia.TakeIT.activities.MainActivity.VIEW_MyAD_TAG;

public class frag_ViewMyAd extends Fragment {

    //region Variables
    private static String TAG = frag_ViewMyAd.class.getSimpleName() +" YOYO";
    RecyclerView imgRecyclerView;
    TextView tv_Title, tv_Price, tv_Description;
    View view;
    Context context;

    AdData adData;
    Bitmap main;
    //endregion

    //region Initial Setup
    @Override
    public void onResume() {
        ((MainActivity)getActivity()).hideIT();
        MenuItem item = ((MainActivity) getActivity()).toolbar.getMenu().findItem(R.id.toolbar_delete);
        item.setVisible(true);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.toolbar_delete){

                    final ProgressDialog dialog = ProgressDialog.show(context, "Deleting...", "", true, false);
                    NetworkMethods methods = new NetworkMethods(context, FirebaseAuth.getInstance());
                    methods.deleteAd(((MainActivity) getActivity()).userInfo, adData, new onDeleteListener() {
                        @Override
                        public void onSuccess(UserInfo userInfo) {
                            dialog.cancel();
                            FragmentManager fragmentManager = getFragmentManager();
                            if (fragmentManager.findFragmentByTag(VIEW_MyAD_TAG).isVisible()) {
                                fragmentManager.beginTransaction()
                                        .remove(fragmentManager.findFragmentByTag(VIEW_MyAD_TAG))
                                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                                        .commit();
                                fragmentManager.beginTransaction()
                                        .add(R.id.frame_layout,new frag_myAds(),MY_ADS_TAG)
                                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                        .commit();
                            }
                            ((MainActivity)context).UpdateUI(userInfo,false);
                            ((MainActivity)context).createSnackbar("Ad Deleted Successfully",Snackbar.LENGTH_LONG);
                        }

                        @Override
                        public void onFailure(Exception e) {
                            dialog.cancel();
                            ((MainActivity)context).createSnackbar(e.getMessage(),Snackbar.LENGTH_LONG);
                        }
                    });
                }
                return true;
            }
        });
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.frag_viewmyad, container, false);

        context = view.getContext();

        initVariables();

        setUpViews();
        return view;
    }

    private void initVariables() {

        tv_Title = (TextView) view.findViewById(R.id.Ad_tvTitle);
        tv_Price = (TextView) view.findViewById(R.id.Ad_tvPrice);
        tv_Description = (TextView) view.findViewById(R.id.Ad_tvDescription);
        imgRecyclerView = (RecyclerView) view.findViewById(R.id.Ad_imgRecycler);
    }

    void setUpViews() {

        adData = getArguments().getParcelable("adData");

        if(adData!=null) {
            Log.i(TAG,"AdData not null");

            if (adData.getPrice() == 0)
                tv_Price.setText(getString(R.string.free));
            else
                tv_Price.setText(String.format(getString(R.string.currency), adData.getPrice()));

            tv_Title.setText(adData.getTitle());
            tv_Description.setText(adData.getDescription());

            setUpImgRecycler();
        }
        else
            Log.i("frag_ViewAd YOYO","no adDATA");
    }

    void setUpImgRecycler() {
        main = ((frag_myAds)(getFragmentManager().findFragmentByTag(MY_ADS_TAG))).current;
        ViewAdImageAdapter adapter = new ViewAdImageAdapter(context, adData, main);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        imgRecyclerView.setLayoutManager(linearLayoutManager);
        imgRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        ((MainActivity) getActivity()).toolbar.getMenu().findItem(R.id.toolbar_delete).setVisible(false);
    }

    //endregion

}

