package com.blackboxindia.TakeIT.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.support.design.widget.Snackbar;
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
import com.blackboxindia.TakeIT.Network.Interfaces.onDeleteListener;
import com.blackboxindia.TakeIT.Network.NetworkMethods;
import com.blackboxindia.TakeIT.R;
import com.blackboxindia.TakeIT.activities.MainActivity;
import com.blackboxindia.TakeIT.dataModels.AdData;
import com.blackboxindia.TakeIT.dataModels.UserInfo;
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

        ImageView majorImage, btn_Delete;
        TextView tv_Title, tv_Type;
        Context context;
        CardView cardView;

        adItemViewHolder(View itemView) {
            super(itemView);
            majorImage = (ImageView) itemView.findViewById(R.id.adItem_Image);
            tv_Title = (TextView) itemView.findViewById(R.id.adItem_Title);
            tv_Type = (TextView) itemView.findViewById(R.id.adItem_Type);
            cardView = (CardView) itemView.findViewById(R.id.adItem);
            btn_Delete = (ImageView) itemView.findViewById(R.id.adItem_Delete);
            context = itemView.getContext();
        }

        public ImageView getMajorImage() {
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
            btn_Delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final ProgressDialog dialog = ProgressDialog.show(context, "Deleting...", "", true, false);
                    NetworkMethods methods = new NetworkMethods(context);
                    methods.deleteAd(((MainActivity)context).userInfo, currentAd, new onDeleteListener() {
                        @Override
                        public void onSuccess(UserInfo userInfo) {
                            dialog.cancel();
                            userAds.remove(position);
                            notifyItemRemoved(position);
                            ((MainActivity)context).UpdateUI(userInfo,false,false);
                            ((MainActivity)context).createSnackbar("Ad Deleted Successfully", Snackbar.LENGTH_LONG);
                        }

                        @Override
                        public void onFailure(Exception e) {
                            dialog.cancel();
                            ((MainActivity)context).createSnackbar(e.getMessage(),Snackbar.LENGTH_LONG);
                        }
                    });
                }
            });
        }
    }
}
