package com.blackboxindia.TakeIT.adapters;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blackboxindia.TakeIT.R;
import com.blackboxindia.TakeIT.dataModels.AdDataMini;

import java.util.List;


public class mainAdapter extends RecyclerView.Adapter<mainAdapter.adItemViewHolder> {

    private final ImageClickListener mListener;
    private List<AdDataMini> adList;
    private LayoutInflater inflater;

    public mainAdapter(Context context, ImageClickListener listener) {
        inflater = LayoutInflater.from(context);
        adList = AdDataMini.getList();
        mListener = listener;
    }

    @Override
    public adItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.ad_item, parent, false);
        return new adItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(adItemViewHolder holder, int position) {
        AdDataMini currentAd = adList.get(position);
        holder.setData(currentAd, position, holder);
        /**
         * Todo:
         * to not put strain on the network
         * Images only start loading onBind
         * setData to find the image of the currentAd
         */
    }

    @Override
    public int getItemCount() {
        return adList.size();
    }

    public interface ImageClickListener {

        void onClick(adItemViewHolder holder, int position, AdDataMini currentAd);
    }

    public class adItemViewHolder extends RecyclerView.ViewHolder{

        ImageView majorImage;
        TextView tv_title;
        TextView tv_Price;
        Context context;
        CardView cardView;

        adItemViewHolder(View itemView) {
            super(itemView);
            majorImage = (ImageView) itemView.findViewById(R.id.adItem_Image);
            tv_title = (TextView) itemView.findViewById(R.id.adItem_Title);
            tv_Price = (TextView) itemView.findViewById(R.id.adItem_Price);
            cardView = (CardView) itemView.findViewById(R.id.adItem);
            context = itemView.getContext();
        }

        public ImageView getMajorImage() {
            return majorImage;
        }

        void setData(AdDataMini currentAd, int position, adItemViewHolder holder) {

            setListeners(currentAd, holder, position);
            majorImage.setImageResource(currentAd.getMajorImage()); //Todo: make the retrieving+setting process aSync

            tv_title.setText(currentAd.getTitle());

            if(currentAd.getPrice()==0)
                tv_Price.setText(R.string.free);
            else
                tv_Price.setText(String.format(context.getString(R.string.currency), currentAd.getPrice()));
        }

        private void setListeners(final AdDataMini currentAd, final adItemViewHolder holder, final int position) {

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
