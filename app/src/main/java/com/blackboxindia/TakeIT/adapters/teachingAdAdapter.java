package com.blackboxindia.TakeIT.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blackboxindia.TakeIT.HelperClasses.GlideApp;
import com.blackboxindia.TakeIT.Network.Interfaces.BitmapDownloadListener;
import com.blackboxindia.TakeIT.R;
import com.blackboxindia.TakeIT.activities.MainActivity;
import com.blackboxindia.TakeIT.dataModels.AdData;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;


public class teachingAdAdapter extends RecyclerView.Adapter<teachingAdAdapter.adItemViewHolder> {

    private static String TAG = teachingAdAdapter.class.getSimpleName()+" YOYO";

    private final ImageClickListener mListener;
    private List<AdData> adList;
    private LayoutInflater inflater;

    public teachingAdAdapter(Context context, ArrayList<AdData> allAds, ImageClickListener listener) {
        inflater = LayoutInflater.from(context);
        adList = allAds;
        mListener = listener;
    }

    @Override
    public adItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.card_ad_teach, parent, false);
        return new adItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(adItemViewHolder holder, int position) {
        AdData currentAd = adList.get(position);
        holder.setData(currentAd, position, holder);
    }

    public void change(ArrayList<AdData> allAds){
        adList = allAds;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return adList.size();
    }

    public interface ImageClickListener {

        void onClick(adItemViewHolder holder, int position, AdData currentAd);
    }

    public class adItemViewHolder extends RecyclerView.ViewHolder{

        TextView tv_title;
        TextView tv_createdBy;
        ImageView creater;
//        TextView tv_Price;
        Context context;
        CardView cardView;

        adItemViewHolder(View itemView) {
            super(itemView);
            tv_title = (TextView) itemView.findViewById(R.id.adItem_Title);
            tv_createdBy = (TextView) itemView.findViewById(R.id.adItem_CreatedBy);
//            tv_Price = (TextView) itemView.findViewById(R.id.adItem_Price);
            cardView = (CardView) itemView.findViewById(R.id.adItem);
            context = itemView.getContext();
            creater = (ImageView) itemView.findViewById(R.id.adItem_CreatedByImage);
        }

        void setData(final AdData currentAd, final int position, adItemViewHolder holder) {

            setListeners(currentAd, holder, position);

            tv_title.setText(currentAd.getTitle());
            tv_createdBy.setText(currentAd.getCreatedBy().getName());

            if(currentAd.getCreatedBy().getHasProfileIMG()){
                creater.setVisibility(View.VISIBLE);
                GlideApp.with(context).load(R.drawable.avatar).into(creater);
                ((MainActivity)context).imageStorageMethods.getProfileImage(currentAd.getCreatedBy().getuID(), new BitmapDownloadListener() {
                    @Override
                    public void onSuccess(Uri uri) {
                        GlideApp.with(context)
                                .load(uri)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .into(creater);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e(TAG, "onFailure: GetCreaterImage", e);
                    }
                });
            }

//            if(currentAd.getPrice()==0)
//                tv_Price.setText(R.string.free);
//            else
//                tv_Price.setText(String.format(context.getString(R.string.currency), currentAd.getPrice()));

        }

        private void setListeners(final AdData currentAd, final adItemViewHolder holder, final int position) {

//            ViewCompat.setTransitionName(holder.getMajorImage(), String.valueOf(position) + "_image");
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onClick(holder, position, currentAd);
                }
            });
        }

    }

}
