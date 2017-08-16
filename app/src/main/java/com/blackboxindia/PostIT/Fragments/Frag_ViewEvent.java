package com.blackboxindia.PostIT.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class Frag_ViewEvent extends Fragment {

    //region Variables
    private static String TAG = Frag_ViewEvent.class.getSimpleName() +" YOYO";
    RecyclerView imgRecyclerView;
    TextView tv_Title, tv_Description;
    TextView tv_Date, tv_Time;
    TextView tv_Name, tv_Address, tv_Phone, tv_Email, tv_Hostel;
    ImageView imageView;
    View view;
    Context context;

    AdData event;
    Bitmap main;

    //endregion

    //region Initial Setup

    public static Frag_ViewEvent newInstance(AdData event) {

        Frag_ViewEvent fragment = new Frag_ViewEvent();
        fragment.event = event;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.frag_viewevent, container, false);
        context = view.getContext();

        initVariables();

        PopulateViews();
        return view;
    }

    private void initVariables() {

        tv_Title = view.findViewById(R.id.Ad_tvTitle);
        tv_Description = view.findViewById(R.id.Ad_tvDescription);
        imgRecyclerView = view.findViewById(R.id.Ad_imgRecycler);

        tv_Date = view.findViewById(R.id.Ad_etDate);
        tv_Time = view.findViewById(R.id.Ad_etTime);

        tv_Name = view.findViewById(R.id.Ad_tvName);
        tv_Address = view.findViewById(R.id.Ad_tvRoomNumber);
        tv_Phone = view.findViewById(R.id.Ad_tvPhone);
        tv_Hostel = view.findViewById(R.id.Ad_tvHostel);
        tv_Email = view.findViewById(R.id.Ad_tvEmail);
        imageView = view.findViewById(R.id.Ad_Profile);

    }

    @Override
    public void onResume() {
        ((MainActivity)context).toolbar.setTitle(MainActivity.TITLE_ViewEvent);
        super.onResume();
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
        ((MainActivity) getActivity()).backPressedListener = null;
    }

    //endregion

    void PopulateViews() {

        if(event!=null) {

            tv_Title.setText(event.getTitle());
            tv_Description.setText(event.getDescription());

            UserInfo userInfo = event.getCreatedBy();
            tv_Name.setText(userInfo.getName());
            tv_Address.setText(userInfo.getRoomNumber());
            tv_Phone.setText(userInfo.getPhone());
            tv_Hostel.setText(userInfo.getHostel());
            tv_Email.setText(userInfo.getEmail());

            String dateFormat = "dd/MM/yy";
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
            tv_Date.setText(sdf.format(event.getDateTime().toCalender().getTime()));

            String timeFormat = "hh:mm a";
            SimpleDateFormat tf = new SimpleDateFormat(timeFormat, Locale.US);
            tv_Time.setText(tf.format(event.getDateTime().toCalender().getTime()));

            if(userInfo.getHasProfileIMG()) {
                ((MainActivity)context).cloudStorageMethods.getProfileImage(userInfo.getuID(), new onCompleteListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if(imageView!=null) {
                            GlideApp.with(context).load(uri)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .skipMemoryCache(true)
                                    .into(imageView);
                        }
//                        imageView.setImageURI(uri);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        imageView.setVisibility(View.INVISIBLE);
                    }
                });
            }else {
                imageView.setVisibility(View.INVISIBLE);
            }
            setUpImgRecycler();
        }
//        else
            //Log.i("Frag_ViewAd YOYO","no adDATA");
    }

    void setUpImgRecycler() {
        //Todo: Correct this
        if(event.getNumberOfImages()>0) {
            main = ((Frag_Ads)(getFragmentManager().findFragmentByTag(MainActivity.ALL_FRAG_TAG))).current;
            ViewAdImageAdapter adapter = new ViewAdImageAdapter(context, event, main, view);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            imgRecyclerView.setLayoutManager(linearLayoutManager);
            imgRecyclerView.setAdapter(adapter);
        }
        else
            imgRecyclerView.setVisibility(View.GONE);
    }

}

