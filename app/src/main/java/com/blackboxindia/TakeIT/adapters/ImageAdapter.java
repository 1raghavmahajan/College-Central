package com.blackboxindia.TakeIT.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.blackboxindia.TakeIT.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Raghav on 04-Jun-17.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.imgViewHolder> {

    LayoutInflater inflater;
    List<Integer> images;

    public ImageAdapter(Context context, Integer img)
    {
        inflater = LayoutInflater.from(context);
        //Todo:
        //getAllImages

        images = new ArrayList<>();
        images.add(img);
    }

    @Override
    public imgViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.img_card, parent, false);
        imgViewHolder holder = new imgViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(imgViewHolder holder, int position) {
        holder.setData(images.get(position), position);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    class imgViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;

        public imgViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imgCard_img);
        }

        public void setData(Integer resID, Integer position) {
            imageView.setImageResource(resID);
            imageView.setTransitionName("adImage" + position);
        }
    }

}
