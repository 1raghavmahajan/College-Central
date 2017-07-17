package com.blackboxindia.PostIT.Fragments;

        import android.app.Fragment;
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

import com.blackboxindia.PostIT.Network.Interfaces.onDeleteListener;
import com.blackboxindia.PostIT.Network.NetworkMethods;
import com.blackboxindia.PostIT.R;
import com.blackboxindia.PostIT.activities.MainActivity;
import com.blackboxindia.PostIT.adapters.ViewAdImageAdapter;
import com.blackboxindia.PostIT.dataModels.AdData;
import com.blackboxindia.PostIT.dataModels.UserInfo;

import static com.blackboxindia.PostIT.activities.MainActivity.MY_ADS_TAG;

public class Frag_ViewMyAd extends Fragment {

    //region Variables
    private static String TAG = Frag_ViewMyAd.class.getSimpleName() +" YOYO";
    RecyclerView imgRecyclerView;
    TextView tv_Title, tv_Price, tv_Description;
    View view;
    Context context;

    AdData adData;
    Bitmap main;
    //endregion

    //region Initial Setup
    public static Frag_ViewMyAd newInstance(AdData adData) {
        Frag_ViewMyAd fragment = new Frag_ViewMyAd();
        fragment.adData = adData;
        return fragment;
    }

    @Override
    public void onResume() {
        MenuItem item = ((MainActivity) getActivity()).toolbar.getMenu().findItem(R.id.toolbar_delete);
        item.setVisible(true);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.toolbar_delete){

                    final ProgressDialog dialog = ProgressDialog.show(context, "Deleting...", "", true, false);
                    NetworkMethods methods = new NetworkMethods(context);
                    methods.deleteAd(((MainActivity) getActivity()).userInfo, adData, new onDeleteListener() {
                        @Override
                        public void onSuccess(UserInfo userInfo) {
                            dialog.cancel();
                            ((MainActivity)context).onBackPressed();
                            ((MainActivity)context).UpdateUI(userInfo,false,false);
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

        populateViews();
        return view;
    }

    private void initVariables() {

        tv_Title = (TextView) view.findViewById(R.id.Ad_tvTitle);
        tv_Price = (TextView) view.findViewById(R.id.Ad_tvPrice);
        tv_Description = (TextView) view.findViewById(R.id.Ad_tvDescription);
        imgRecyclerView = (RecyclerView) view.findViewById(R.id.Ad_imgRecycler);
    }

    @Override
    public void onStop() {
        super.onStop();
        ((MainActivity) getActivity()).toolbar.getMenu().findItem(R.id.toolbar_delete).setVisible(false);
        ((MainActivity) getActivity()).onBackPressedListener = null;
    }

    //endregion

    void populateViews() {

        if(adData!=null) {
            Log.i(TAG,"AdData not null");

            if(adData.getPrice()!=null) {
                if (adData.getPrice() == 0)
                    tv_Price.setText(getString(R.string.free));
                else
                    tv_Price.setText(String.format(getString(R.string.currency), adData.getPrice()));
            }else {
                tv_Price.setVisibility(View.GONE);
            }

            tv_Title.setText(adData.getTitle());
            tv_Description.setText(adData.getDescription());

            setUpImgRecycler();
        }
        else
            Log.i("Frag_ViewAd YOYO","no adDATA");
    }

    void setUpImgRecycler() {
        main = ((Frag_myAds)(getFragmentManager().findFragmentByTag(MY_ADS_TAG))).current;
        ViewAdImageAdapter adapter = new ViewAdImageAdapter(context, adData, main, view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        imgRecyclerView.setLayoutManager(linearLayoutManager);
        imgRecyclerView.setAdapter(adapter);
    }

}

