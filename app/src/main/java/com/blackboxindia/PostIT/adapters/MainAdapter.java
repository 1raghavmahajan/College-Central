package com.blackboxindia.PostIT.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
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

import com.blackboxindia.PostIT.Network.Interfaces.onCompleteListener;
import com.blackboxindia.PostIT.R;
import com.blackboxindia.PostIT.activities.MainActivity;
import com.blackboxindia.PostIT.dataModels.AdData;

import java.util.ArrayList;
import java.util.List;


public class MainAdapter extends RecyclerView.Adapter<MainAdapter.adItemViewHolder> {

    private static String TAG = MainAdapter.class.getSimpleName()+" YOYO";

    private final ImageClickListener mListener;
    private List<AdData> adList;
    private LayoutInflater inflater;

    public MainAdapter(Context context, ArrayList<AdData> allAds, ImageClickListener listener) {
        inflater = LayoutInflater.from(context);
        adList = allAds;
        mListener = listener;
    }

    @Override
    public adItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.card_ad, parent, false);
        return new adItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(adItemViewHolder holder, int position) {
        AdData currentAd = adList.get(position);
        holder.setData(currentAd, position, holder);
    }

    @Override
    public void onViewAttachedToWindow(adItemViewHolder holder) {
        Log.i(TAG, "onViewAttachedToWindow: "+holder.tv_title.getText().toString());
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(adItemViewHolder holder) {
        Log.i(TAG, "onViewDetachedFromWindow: "+holder.tv_title.getText().toString());
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public void onViewRecycled(adItemViewHolder holder) {
        Log.i(TAG, "onViewRecycled: "+holder.tv_title.getText());
        super.onViewRecycled(holder);
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

        public ImageView getMajorImage() {
            return majorImage;
        }

        void setData(final AdData currentAd, final int position, adItemViewHolder holder) {

            setListeners(currentAd, holder, position);

            if(currentAd.getNumberOfImages()>0) {
                ((MainActivity)context).cloudStorageMethods.getMajorImage(currentAd.getAdID(), new onCompleteListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if (majorImage != null){
                            progressBar.setVisibility(View.GONE);
                            new loadBitmap().execute(uri);
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e(TAG, "onFailure #" + position + " ", e);
                    }
                });
            }
            else {
                majorImage.setImageResource(R.drawable.ad_img_placeholder);
                progressBar.setVisibility(View.GONE);
            }

            tv_title.setText(currentAd.getTitle());

            if(currentAd.getPrice()!=null) {
                if (currentAd.getPrice() == 0)
                    tv_Price.setText(R.string.free);
                else
                    tv_Price.setText(String.format(context.getString(R.string.currency), currentAd.getPrice()));
            }
            else
                tv_Price.setVisibility(View.INVISIBLE);
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

        class loadBitmap extends AsyncTask<Uri, Void, Void>{

            @Override
            protected Void doInBackground(Uri... params) {
                main = BitmapFactory.decodeFile(params[0].getPath());
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                majorImage.setImageBitmap(main);
            }
        }

    }

}
