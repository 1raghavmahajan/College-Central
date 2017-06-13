package com.blackboxindia.TakeIT.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.blackboxindia.TakeIT.R;

import java.util.ArrayList;

public class NewAdImageAdapter extends RecyclerView.Adapter<NewAdImageAdapter.imgViewHolder> {

    private LayoutInflater inflater;
    private ArrayList<Bitmap> images;

    public NewAdImageAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        images = new ArrayList<>();
    }

    @Override
    public imgViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.img_card, parent, false);
        return new imgViewHolder(view);
    }

    @Override
    public void onBindViewHolder(imgViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public void addImage(Bitmap bm) {
        images.add(bm);
        notifyItemInserted(images.size()-1);
    }

    public ArrayList<Bitmap> getImages() {
        return images;
    }
    public Bitmap getMajor(){return images.get(0);}

    class imgViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        imgViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imgCard_img);
        }

        void setData(Integer position) {
            if(imageView.getDrawable() !=null)
                ((BitmapDrawable)imageView.getDrawable()).getBitmap().recycle();
            imageView.setImageBitmap(images.get(position));
            //imageView.setTransitionName("adImage" + position);
        }
    }

}
