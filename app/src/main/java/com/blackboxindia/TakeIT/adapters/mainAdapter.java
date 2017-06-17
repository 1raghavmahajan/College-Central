package com.blackboxindia.TakeIT.adapters;

import android.content.Context;
import android.graphics.Bitmap;
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

import com.blackboxindia.TakeIT.Network.Interfaces.BitmapDownloadListener;
import com.blackboxindia.TakeIT.R;
import com.blackboxindia.TakeIT.activities.MainActivity;
import com.blackboxindia.TakeIT.dataModels.AdData;

import java.util.ArrayList;
import java.util.List;


public class mainAdapter extends RecyclerView.Adapter<mainAdapter.adItemViewHolder> {

    private static String TAG = mainAdapter.class.getSimpleName()+" YOYO";

    private final ImageClickListener mListener;
    private List<AdData> adList;
    private LayoutInflater inflater;

    public mainAdapter(Context context, ArrayList<AdData> allAds, ImageClickListener listener) {
        inflater = LayoutInflater.from(context);
        adList = allAds;
        mListener = listener;
    }

    @Override
    public adItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.ad_item, parent, false);
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

        void onClick(adItemViewHolder holder, int position, AdData currentAd, Bitmap main);
    }

    public class adItemViewHolder extends RecyclerView.ViewHolder{

        ImageView majorImage;
        TextView tv_title;
        TextView tv_Price;
        Context context;
        CardView cardView;
        Bitmap main;
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

        void setData(final AdData currentAd, final int position, adItemViewHolder holder) {

            setListeners(currentAd, holder, position);

            if(currentAd.getNumberOfImages()>0) {
                ((MainActivity)context).cloudStorageMethods.getMajorImage(currentAd.getAdID(), new BitmapDownloadListener() {
                    @Override
                    public void onSuccess(Bitmap bitmap) {
                        if (majorImage != null){
                            progressBar.setVisibility(View.GONE);
                            main = bitmap;
                            if(bitmap!=null)
                                majorImage.setImageBitmap(bitmap);
                            else
                                Log.i(TAG,"major img null");
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e(TAG, "onFailure #" + position + " ", e);
                    }
                });
            }

            tv_title.setText(currentAd.getTitle());

            if(currentAd.getPrice()==0)
                tv_Price.setText(R.string.free);
            else
                tv_Price.setText(String.format(context.getString(R.string.currency), currentAd.getPrice()));
        }

        private void setListeners(final AdData currentAd, final adItemViewHolder holder, final int position) {

            ViewCompat.setTransitionName(holder.getMajorImage(), String.valueOf(position) + "_image");
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onClick(holder, position, currentAd, main);
                }
            });
        }
    }

}
