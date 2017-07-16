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

import com.blackboxindia.PostIT.HelperClasses.GlideApp;
import com.blackboxindia.PostIT.Network.Interfaces.onCompleteListener;
import com.blackboxindia.PostIT.R;
import com.blackboxindia.PostIT.activities.MainActivity;
import com.blackboxindia.PostIT.dataModels.AdData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.adItemViewHolder> {

    private static String TAG = EventsAdapter.class.getSimpleName()+" YOYO";

    private final ImageClickListener mListener;
    private List<AdData> adList;
    private LayoutInflater inflater;

    public EventsAdapter(Context context, ArrayList<AdData> allAds, ImageClickListener listener) {
        inflater = LayoutInflater.from(context);
        adList = allAds;
        mListener = listener;
    }

    @Override
    public adItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.card_event, parent, false);
        return new adItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(adItemViewHolder holder, int position) {
        AdData currentAd = adList.get(position);
        holder.setData(currentAd, position, holder);
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
        TextView tv_title,tv_Date;
//        TextView tv_Price;
        Context context;
        CardView cardView;
        Bitmap main;
        ProgressBar progressBar;

        adItemViewHolder(View itemView) {
            super(itemView);
            majorImage = (ImageView) itemView.findViewById(R.id.adItem_Image);
            tv_title = (TextView) itemView.findViewById(R.id.adItem_Title);
//            tv_Price = (TextView) itemView.findViewById(R.id.adItem_Price);
            tv_Date = (TextView) itemView.findViewById(R.id.adItem_Date);
            cardView = (CardView) itemView.findViewById(R.id.adItem);
            progressBar = (ProgressBar) itemView.findViewById(R.id.adItem_progress);
            context = itemView.getContext();
        }

        public ImageView getMajorImage() {
            return majorImage;
        }

        void setData(final AdData currentEvent, final int position, adItemViewHolder holder) {

            setListeners(currentEvent, holder, position);

            if(currentEvent.getNumberOfImages()>0) {
                ((MainActivity)context).imageStorageMethods.getMajorImage(currentEvent.getAdID(), new onCompleteListener<Uri>() {
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
                GlideApp.with(context)
                        .load(R.drawable.ad_img_placeholder)
                        .into(majorImage);
                progressBar.setVisibility(View.GONE);
            }

            tv_title.setText(currentEvent.getTitle());

            String myFormat = "dd/MM/yy";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            tv_Date.setText(sdf.format(currentEvent.getDateTime().toCalender().getTime()));

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
