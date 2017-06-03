package com.blackboxindia.TakeIT.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blackboxindia.TakeIT.R;
import com.blackboxindia.TakeIT.dataModels.AdDataMini;

import java.util.List;


public class customAdapter extends RecyclerView.Adapter<customAdapter.CustomViewHolder> {

    private List<AdDataMini> adList;
    private LayoutInflater inflater;

    public customAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        adList = AdDataMini.getList();
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.ad_item, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        AdDataMini currentAd = adList.get(position);
        holder.setData(currentAd);
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

    class CustomViewHolder extends RecyclerView.ViewHolder{

        ImageView majorImage;
        TextView tv_title;
        TextView tv_Price;
        Context context;

        public CustomViewHolder(View itemView) {
            super(itemView);
            majorImage = (ImageView) itemView.findViewById(R.id.adItem_Image);
            tv_title = (TextView) itemView.findViewById(R.id.adItem_Title);
            tv_Price = (TextView) itemView.findViewById(R.id.adItem_Price);
            context = itemView.getContext();
        }

        public void setData(AdDataMini currentAd) {
            majorImage.setImageResource(currentAd.getMajorImage()); //Todo: make the retrieving+setting process aSync
            tv_title.setText(currentAd.getTitle());

            if(currentAd.getPrice()==0)
                tv_Price.setText(R.string.free);
            else
                tv_Price.setText(context.getString(R.string.currency) + currentAd.getPrice());
        }
    }
}
