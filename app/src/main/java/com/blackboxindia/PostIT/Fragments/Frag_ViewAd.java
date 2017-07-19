package com.blackboxindia.PostIT.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blackboxindia.PostIT.HelperClasses.GlideApp;
import com.blackboxindia.PostIT.Network.Interfaces.onCompleteListener;
import com.blackboxindia.PostIT.R;
import com.blackboxindia.PostIT.activities.MainActivity;
import com.blackboxindia.PostIT.adapters.ViewAdImageAdapter;
import com.blackboxindia.PostIT.dataModels.AdData;
import com.blackboxindia.PostIT.dataModels.UserInfo;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

public class Frag_ViewAd extends Fragment {

    //region Variables
    private static String TAG = Frag_ViewAd.class.getSimpleName() +" YOYO";
    RecyclerView imgRecyclerView;
    TextView tv_Title, tv_Price, tv_Description;
    TextView tv_Name, tv_Address, tv_Phone, tv_College;
    ImageView imageView;
    View view;
    Context context;

    AdData adData;
    Bitmap main;

    //endregion

    //region Initial Setup

    public static Frag_ViewAd newInstance(AdData adData) {

        Frag_ViewAd fragment = new Frag_ViewAd();
        fragment.adData = adData;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.frag_viewad, container, false);
        context = view.getContext();

        initVariables();

        PopulateViews();
        return view;
    }

    private void initVariables() {

        tv_Title = (TextView) view.findViewById(R.id.Ad_tvTitle);
        tv_Price = (TextView) view.findViewById(R.id.Ad_tvPrice);
        tv_Description = (TextView) view.findViewById(R.id.Ad_tvDescription);
        imgRecyclerView = (RecyclerView) view.findViewById(R.id.Ad_imgRecycler);
        tv_Name = (TextView) view.findViewById(R.id.Ad_tvName);
        tv_Address = (TextView) view.findViewById(R.id.Ad_tvRoomNumber);
        tv_Phone = (TextView) view.findViewById(R.id.Ad_tvPhone);
        tv_College = (TextView) view.findViewById(R.id.Ad_tvCollege);
        imageView = (ImageView) view.findViewById(R.id.Ad_Profile);

    }

    @Override
    public void onStop() {
        super.onStop();
        List<FileDownloadTask> activeDownloadTasks = FirebaseStorage.getInstance().getReference().getActiveDownloadTasks();
        for (FileDownloadTask task :
                activeDownloadTasks) {
            task.cancel();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((MainActivity) getActivity()).onBackPressedListener = null;
    }
    //endregion

    void PopulateViews() {

        if(adData!=null) {

            if(adData.getPrice()!=null) {
                if (adData.getPrice() == 0)
                    tv_Price.setText(getString(R.string.free));
                else
                    tv_Price.setText(String.format(getString(R.string.currency), adData.getPrice()));
            }
            else
                tv_Price.setVisibility(View.INVISIBLE);

            tv_Title.setText(adData.getTitle());
            tv_Description.setText(adData.getDescription());

            UserInfo userInfo = adData.getCreatedBy();
            tv_Name.setText(userInfo.getName());
            tv_Address.setText(userInfo.getRoomNumber());
            tv_Phone.setText(userInfo.getPhone());
            tv_College.setText(userInfo.getCollegeName());
            tv_Phone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:"+tv_Phone.getText()));
                    startActivity(intent);
                }
            });
            if(userInfo.getHasProfileIMG()) {

                ((MainActivity)context).cloudStorageMethods.getProfileImage(userInfo.getuID(), new onCompleteListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        GlideApp.with(context)
                                .load(uri)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .into(imageView);
                    }

                    @Override
                    public void onFailure(Exception e) {

                    }
                });
            }
            setUpImgRecycler();
        }
        else
            Log.i("Frag_ViewAd YOYO","no adDATA");
    }

    void setUpImgRecycler() {
        if(adData.getNumberOfImages()>0) {
            main = ((Frag_Ads) (getFragmentManager().findFragmentByTag(MainActivity.ALL_FRAG_TAG))).current;
            ViewAdImageAdapter adapter = new ViewAdImageAdapter(context, adData, main, view);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            imgRecyclerView.setLayoutManager(linearLayoutManager);
            imgRecyclerView.setAdapter(adapter);
        }
        else
            imgRecyclerView.setVisibility(View.GONE);
    }

}

