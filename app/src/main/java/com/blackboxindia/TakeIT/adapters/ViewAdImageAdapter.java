package com.blackboxindia.TakeIT.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.blackboxindia.TakeIT.Network.CloudStorageMethods;
import com.blackboxindia.TakeIT.Network.Interfaces.ImageDownloadListener;
import com.blackboxindia.TakeIT.R;
import com.blackboxindia.TakeIT.dataModels.AdData;

import java.util.ArrayList;

public class ViewAdImageAdapter extends RecyclerView.Adapter<ViewAdImageAdapter.imgViewHolder> {

    private static final String TAG = ViewAdImageAdapter.class.getSimpleName()+" YOYO";
    private LayoutInflater inflater;
    private ArrayList<Uri> images;
    private AdData adData;
    private Bitmap main;
    private CloudStorageMethods methods;

    public ViewAdImageAdapter(Context context, AdData adData, Bitmap main, CloudStorageMethods methods) {
        inflater = LayoutInflater.from(context);
        this.adData = adData;
        this.main = main;
        this.methods = methods;
        images = new ArrayList<>(adData.getNumberOfImages());
    }

    @Override
    public imgViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.viewad_img_card, parent, false);
        return new imgViewHolder(view);
    }

    @Override
    public void onBindViewHolder(imgViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return adData.getNumberOfImages();
    }

    class imgViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        ImageButton imgButton;
        ProgressBar progressBar;

        imgViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imgCard_img);
            imgButton = (ImageButton) itemView.findViewById(R.id.imgCard_button);
            progressBar = (ProgressBar) itemView.findViewById(R.id.img_progress);
        }

        void setData(final Integer position) {
            imageView.setTransitionName("adImage" + position);
            if(position==0) {
                progressBar.setVisibility(View.INVISIBLE);
                imageView.setImageBitmap(main);
                imageView.setVisibility(View.VISIBLE);
            }
            methods.getBigImage(adData.getAdID(), position, new ImageDownloadListener() {
                @Override
                public void onSuccess(Uri uri) {
                    Bitmap bitmap = BitmapFactory.decodeFile(uri.getPath());
                    progressBar.setVisibility(View.INVISIBLE);
                    imageView.setImageBitmap(bitmap);
                    imageView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onFailure(Exception e) {
                    if(position!=0) {
                        Log.e(TAG,"getImage onFailure #"+position,e);
                        Toast.makeText(imageView.getContext(), "Failed to get image#"+position, Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                        imageView.setImageResource(R.drawable.ic_error);
                        imageView.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

}
