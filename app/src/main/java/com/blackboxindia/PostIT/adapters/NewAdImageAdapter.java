package com.blackboxindia.PostIT.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.recyclerview.widget.RecyclerView;
import com.blackboxindia.PostIT.R;
import java.util.ArrayList;

public class NewAdImageAdapter extends RecyclerView.Adapter<NewAdImageAdapter.imgViewHolder> {

    private LayoutInflater inflater;
    private ArrayList<Bitmap> images;
    private onDeleteClickListener listener;

    public NewAdImageAdapter(Context context, onDeleteClickListener listener) {
        inflater = LayoutInflater.from(context);
        images = new ArrayList<>();
        this.listener = listener;
    }

    @Override
    public imgViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.card_newad_img, parent, false);
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

    private void removeImage(int position){
        images.remove(position);
        notifyItemRemoved(position);
        //notifyItemRangeChanged(position,images.size());
    }

    public Bitmap getMajor(){
        if(images!=null) {
            if (images.size() > 0) {
                return images.get(0);
            }
        }
        return null;
    }

    public interface onDeleteClickListener {
        void onDelete(int position);
    }

    class imgViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        ImageButton btn_delete;

        imgViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgCard_img);
            btn_delete = itemView.findViewById(R.id.imgCard_delete);
        }

        void setData(final Integer position) {
//            if(imageView.getDrawable() !=null)
//                ((BitmapDrawable)imageView.getDrawable()).getBitmap().recycle();
            imageView.setImageBitmap(images.get(position));
            imageView.setTransitionName("adImage" + position);

            btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeImage(position);
                    listener.onDelete(position);
                }
            });
        }
    }

}
