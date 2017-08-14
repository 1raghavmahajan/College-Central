package com.blackboxindia.PostIT.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blackboxindia.PostIT.Fragments.Frag_EditAd;
import com.blackboxindia.PostIT.Fragments.Frag_EditEvent;
import com.blackboxindia.PostIT.HelperClasses.GlideApp;
import com.blackboxindia.PostIT.Network.Interfaces.onCompleteListener;
import com.blackboxindia.PostIT.Network.Interfaces.onDeleteListener;
import com.blackboxindia.PostIT.Network.NetworkMethods;
import com.blackboxindia.PostIT.R;
import com.blackboxindia.PostIT.activities.MainActivity;
import com.blackboxindia.PostIT.dataModels.AdData;
import com.blackboxindia.PostIT.dataModels.UserInfo;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.Collections;

import static com.blackboxindia.PostIT.dataModels.AdTypes.TYPE_EVENT;
import static com.blackboxindia.PostIT.dataModels.AdTypes.TYPE_LOSTFOUND;
import static com.blackboxindia.PostIT.dataModels.AdTypes.TYPE_SELL;
import static com.blackboxindia.PostIT.dataModels.AdTypes.TYPE_TEACH;


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

    public void change(ArrayList<AdData> ll){
        userAds = ll;
        notifyDataSetChanged();
    }

    public interface ImageClickListener {
        void onClick(adItemViewHolder holder, int position, AdData currentAd);
    }

    public class adItemViewHolder extends RecyclerView.ViewHolder{

        ImageView majorImage, btn_Delete, btn_Edit;
        TextView tv_Title, tv_Type, tv_Price;
        Context context;
        CardView cardView;

        adItemViewHolder(View itemView) {
            super(itemView);
            majorImage = itemView.findViewById(R.id.adItem_Image);
            tv_Title = itemView.findViewById(R.id.adItem_Title);
            tv_Type = itemView.findViewById(R.id.adItem_Type);
            tv_Price = itemView.findViewById(R.id.adItem_Price);
            cardView = itemView.findViewById(R.id.adItem);
            btn_Delete = itemView.findViewById(R.id.adItem_Delete);
            btn_Edit = itemView.findViewById(R.id.adItem_Edit);
            context = itemView.getContext();
        }

        public ImageView getMajorImage() {
            return majorImage;
        }

        @SuppressWarnings("deprecation")
        void setData(final AdData currentAd, final int position) {
            if(currentAd!=null) {
                setListeners(currentAd, this, position);
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
                            //Log.e(TAG, "onFailure #" + position + " ", e);
                        }
                    });
                }else
                    majorImage.setVisibility(View.GONE);

                tv_Title.setText(currentAd.getTitle());

                switch(currentAd.getType()){
                    case TYPE_SELL:
//                        cardView.setCardBackgroundColor(cardView.getResources().getColor(R.color.colorBuySell));
                        tv_Type.setTextColor(cardView.getResources().getColor(R.color.colorBuySell));
                        tv_Type.setText(R.string.txt_sell);
                        tv_Price.setVisibility(View.VISIBLE);
                        tv_Price.setText(String.format(context.getString(R.string.currency), currentAd.getPrice()));
                        break;
                    case TYPE_LOSTFOUND:
//                        cardView.setCardBackgroundColor(cardView.getResources().getColor(R.color.colorLostFound));
                        tv_Type.setTextColor(cardView.getResources().getColor(R.color.colorLostFound));
                        tv_Type.setText(R.string.txt_lost);
                        tv_Price.setVisibility(View.GONE);
                        break;
                    case TYPE_EVENT:
//                        cardView.setCardBackgroundColor(cardView.getResources().getColor(R.color.colorEvents));
                        tv_Type.setTextColor(cardView.getResources().getColor(R.color.colorEvents));
                        tv_Type.setText(R.string.txt_event);
                        tv_Price.setVisibility(View.GONE);
                        break;
                    case TYPE_TEACH:
//                        cardView.setCardBackgroundColor(cardView.getResources().getColor(R.color.colorTeaching));
                        tv_Type.setTextColor(cardView.getResources().getColor(R.color.colorTeaching));
                        tv_Type.setText(R.string.txt_teach);
                        tv_Price.setVisibility(View.GONE);
                        break;
                }

            }
        }

        private void setListeners(final AdData currentAd, final adItemViewHolder holder, final int position) {

//            ViewCompat.setTransitionName(holder.getMajorImage(), String.valueOf(position) + "_image");
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
                            notifyItemRangeChanged(position, getItemCount());
                            ((MainActivity)context).UpdateUI(userInfo,false);
                            ((MainActivity)context).createSnackbar("Ad Deleted Successfully");
                        }

                        @Override
                        public void onFailure(Exception e) {
                            dialog.cancel();
                            ((MainActivity)context).createSnackbar(e.getMessage(),Snackbar.LENGTH_LONG, true);
                        }
                    });
                }
            });
            btn_Edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(currentAd.getType().equals(TYPE_EVENT)){
                        Frag_EditEvent frag_editAd = Frag_EditEvent.newInstance(currentAd);
                        ((MainActivity) context).launchOtherFragment(frag_editAd, MainActivity.EDIT_EVENT_TAG, true);
                    }else {
                        Frag_EditAd frag_editAd = Frag_EditAd.newInstance(currentAd);
                        ((MainActivity) context).launchOtherFragment(frag_editAd, MainActivity.EDIT_AD_TAG, true);
                    }
                }
            });
        }
    }
}
