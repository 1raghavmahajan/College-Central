package com.blackboxindia.TakeIT.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.blackboxindia.TakeIT.Network.GlideApp;
import com.blackboxindia.TakeIT.Network.Interfaces.AdListener;
import com.blackboxindia.TakeIT.Network.Interfaces.BitmapDownloadListener;
import com.blackboxindia.TakeIT.Network.NetworkMethods;
import com.blackboxindia.TakeIT.R;
import com.blackboxindia.TakeIT.activities.MainActivity;
import com.blackboxindia.TakeIT.dataModels.AdData;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Collections;


public class MyAdsAdapter extends RecyclerView.Adapter<MyAdsAdapter.adItemViewHolder> {

    private static String TAG = MyAdsAdapter.class.getSimpleName()+" YOYO";

    private final ImageClickListener mListener;
    private ArrayList<String> userAds;
    private LayoutInflater inflater;

    private NetworkMethods networkMethods;

    public MyAdsAdapter(Context context, ArrayList<String> keys, ImageClickListener listener) {
        inflater = LayoutInflater.from(context);
        networkMethods = new NetworkMethods(context,FirebaseAuth.getInstance());
        userAds = keys;
        Collections.reverse(userAds);
        mListener = listener;
    }

    @Override
    public adItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.ad_item, parent, false);
        return new adItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final adItemViewHolder holder, int position) {

        networkMethods.getAd(userAds.get(position), new AdListener() {
            @Override
            public void onSuccess(AdData adData) {
                if(holder!=null){
                    holder.setData(adData, holder.getAdapterPosition());
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG,"getAd #"+holder.getAdapterPosition()+" ",e);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userAds.size();
    }

    public interface ImageClickListener {

        void onClick(adItemViewHolder holder, int position, AdData currentAd);
    }

    public class adItemViewHolder extends RecyclerView.ViewHolder{

        ImageView majorImage;
        TextView tv_title;
        TextView tv_Price;
        Context context;
        CardView cardView;
        ProgressBar progressBar;

        adItemViewHolder(View itemView) {
            super(itemView);
            majorImage = (ImageView) itemView.findViewById(R.id.adItem_Image);
            tv_title = (TextView) itemView.findViewById(R.id.adItem_Title);
            tv_Price = (TextView) itemView.findViewById(R.id.adItem_Price);
            cardView = (CardView) itemView.findViewById(R.id.adItem);
            progressBar = (ProgressBar) itemView.findViewById(R.id.adItem_progress);
            context = itemView.getContext();
        }

        ImageView getMajorImage() {
            return majorImage;
        }

        void setData(final AdData currentAd, final int position) {
            if(currentAd!=null) {
                setListeners(currentAd, this, position);
                ((MainActivity)context).cloudStorageMethods.getMajorImage(currentAd.getAdID(), new BitmapDownloadListener() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if (majorImage != null && currentAd.getNumberOfImages() > 0) {
                            GlideApp.with(context).load(uri).into(majorImage);
                            progressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e(TAG, "onFailure #" + position + " ", e);
                    }
                });
                tv_title.setText(currentAd.getTitle());
                if (currentAd.getPrice() == 0)
                    tv_Price.setText(R.string.free);
                else
                    tv_Price.setText(String.format(context.getString(R.string.currency), currentAd.getPrice()));
            }
            else
                Log.i(TAG,"CurrentAd null");
        }

        private void setListeners(final AdData currentAd, final adItemViewHolder holder, final int position) {

            ViewCompat.setTransitionName(holder.getMajorImage(), String.valueOf(position) + "_image");
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onClick(holder, position, currentAd);
                }
            });
        }
    }
}
