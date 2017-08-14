package com.blackboxindia.PostIT.adapters;

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

import com.blackboxindia.PostIT.HelperClasses.GlideApp;
import com.blackboxindia.PostIT.Network.Interfaces.onCompleteListener;
import com.blackboxindia.PostIT.R;
import com.blackboxindia.PostIT.activities.MainActivity;
import com.blackboxindia.PostIT.dataModels.AdData;
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
        ImageView majorImage;
        Context context;
        CardView cardView;

        adItemViewHolder(View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.adItem_Title);
            tv_createdBy = itemView.findViewById(R.id.adItem_CreatedBy);
            majorImage = itemView.findViewById(R.id.adItem_Image);
            cardView = itemView.findViewById(R.id.adItem);
            context = itemView.getContext();
        }

        void setData(final AdData currentAd, final int position, adItemViewHolder holder) {
            setListeners(currentAd, holder, position);
            tv_title.setText(currentAd.getTitle());
            tv_createdBy.setText(currentAd.getCreatedBy().getName());
            if(currentAd.getNumberOfImages() > 0) {
                majorImage.setVisibility(View.VISIBLE);
                majorImage.setImageResource(R.drawable.placeholder);
                ((MainActivity) context).cloudStorageMethods.getMajorImage(currentAd.getAdID(), new onCompleteListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if (majorImage != null) {
                            GlideApp.with(context)
                                    .load(uri)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .skipMemoryCache(true)
                                    .into(majorImage);
                        }
                    }
                    @Override
                    public void onFailure(Exception e) {
                        Log.e(TAG, "onFailure #" + position + " ", e);
                    }
                });
            }else
                majorImage.setVisibility(View.GONE);
        }

        private void setListeners(final AdData currentAd, final adItemViewHolder holder, final int position) {
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onClick(holder, position, currentAd);
                }
            });
        }

    }

}
