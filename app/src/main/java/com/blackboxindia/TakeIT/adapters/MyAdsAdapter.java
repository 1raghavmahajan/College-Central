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
import android.widget.TextView;

import com.blackboxindia.TakeIT.HelperClasses.GlideApp;
import com.blackboxindia.TakeIT.Network.Interfaces.BitmapDownloadListener;
import com.blackboxindia.TakeIT.R;
import com.blackboxindia.TakeIT.activities.MainActivity;
import com.blackboxindia.TakeIT.dataModels.AdData;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.Collections;

import static com.blackboxindia.TakeIT.dataModels.AdTypes.TYPE_EVENT;
import static com.blackboxindia.TakeIT.dataModels.AdTypes.TYPE_LOSTFOUND;
import static com.blackboxindia.TakeIT.dataModels.AdTypes.TYPE_SELL;
import static com.blackboxindia.TakeIT.dataModels.AdTypes.TYPE_TEACH;


public class MyAdsAdapter extends RecyclerView.Adapter<MyAdsAdapter.adItemViewHolder> {

    private static String TAG = MyAdsAdapter.class.getSimpleName()+" YOYO";

    private final ImageClickListener mListener;
    private ArrayList<AdData> userAds;
    private LayoutInflater inflater;

    public MyAdsAdapter(Context context, ArrayList<AdData> ads, ImageClickListener listener) {
        inflater = LayoutInflater.from(context);
        userAds = ads;
        Collections.reverse(userAds);
        mListener = listener;
    }

    @Override
    public adItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.card_myad, parent, false);
        return new adItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final adItemViewHolder holder, int position) {
        holder.setData(userAds.get(position), holder.getAdapterPosition());
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
        TextView tv_Title, tv_Type;
        Context context;
        CardView cardView;

        adItemViewHolder(View itemView) {
            super(itemView);
            majorImage = (ImageView) itemView.findViewById(R.id.adItem_Image);
            tv_Title = (TextView) itemView.findViewById(R.id.adItem_Title);
            tv_Type = (TextView) itemView.findViewById(R.id.adItem_Type);
            cardView = (CardView) itemView.findViewById(R.id.adItem);
            context = itemView.getContext();
        }

        ImageView getMajorImage() {
            return majorImage;
        }

        void setData(final AdData currentAd, final int position) {
            if(currentAd!=null) {
                setListeners(currentAd, this, position);
                if(currentAd.getNumberOfImages() > 0) {
                    ((MainActivity) context).imageStorageMethods.getMajorImage(currentAd.getAdID(), new BitmapDownloadListener() {
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
                }

                tv_Title.setText(currentAd.getTitle());

                switch(currentAd.getType()){
                    case TYPE_SELL:
//                        cardView.setCardBackgroundColor(cardView.getResources().getColor(R.color.colorBuySell));
                        tv_Type.setTextColor(cardView.getResources().getColor(R.color.colorBuySell));
                        tv_Type.setText("SELL");
                        break;
                    case TYPE_LOSTFOUND:
//                        cardView.setCardBackgroundColor(cardView.getResources().getColor(R.color.colorLostFound));
                        tv_Type.setTextColor(cardView.getResources().getColor(R.color.colorLostFound));
                        tv_Type.setText("LOST");
                        break;
                    case TYPE_EVENT:
//                        cardView.setCardBackgroundColor(cardView.getResources().getColor(R.color.colorEvents));
                        tv_Type.setTextColor(cardView.getResources().getColor(R.color.colorEvents));
                        tv_Type.setText("EVENT");
                        break;
                    case TYPE_TEACH:
//                        cardView.setCardBackgroundColor(cardView.getResources().getColor(R.color.colorTeaching));
                        tv_Type.setTextColor(cardView.getResources().getColor(R.color.colorTeaching));
                        tv_Type.setText("TEACH");
                        break;
                }

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
