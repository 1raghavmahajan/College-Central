package com.blackboxindia.TakeIT.adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.blackboxindia.TakeIT.Network.Interfaces.ImageDownloadListener;
import com.blackboxindia.TakeIT.R;
import com.blackboxindia.TakeIT.activities.MainActivity;
import com.blackboxindia.TakeIT.dataModels.AdData;

public class ViewAdImageAdapter extends RecyclerView.Adapter<ViewAdImageAdapter.imgViewHolder> {

    private static final String TAG = ViewAdImageAdapter.class.getSimpleName()+" YOYO";
    private LayoutInflater inflater;
    private AdData adData;
    private Bitmap main;
    private Context context;
    private View view;

    private Animator mCurrentAnimator;
    private int mShortAnimationDuration;

    private ImageView expandedImageView;
    private Rect startBounds;
    private float startScaleFinal;
    private View imgView;
    private boolean opened;

    public ViewAdImageAdapter(Context context, AdData adData, Bitmap main, View view) {
        inflater = LayoutInflater.from(context);
        this.context  = context;
        this.adData = adData;
        this.main = main;
        this.view = view;

        opened = false;
        ((MainActivity)context).closeImageListener = new MainActivity.closeImageListener() {
            @Override
            public boolean closeImage() {
                if(opened){
                    opened = false;
                    closeAnim();
                    return false;
                } else {
                    return true;
                }
            }
        };
        expandedImageView = (ImageView) view.findViewById(R.id.expanded_image);
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

    private void zoomImageFromThumb(final View thumbView, Uri uri) {

        mShortAnimationDuration = view.getContext().getResources().getInteger(android.R.integer.config_shortAnimTime);

        // If there's an animation in progress, cancel it immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        imgView = thumbView;
        opened = true;

        // Load the high-resolution "zoomed-in" image.
        expandedImageView.setImageURI(uri);

        // Calculate the starting and ending bounds for the zoomed-in image. This step
        // involves lots of math. Yay, math.
        startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail, and the
        // final bounds are the global visible rectangle of the container view. Also
        // set the container view's offset as the origin for the bounds, since that's
        // the origin for the positioning animation properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        view.findViewById(R.id.frame_container).getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);

        Log.i(TAG,"X: "+String.valueOf(-globalOffset.x)+" Y: "+String.valueOf(-globalOffset.y));

        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final bounds using the
        // "center crop" technique. This prevents undesirable stretching during the animation.
        // Also calculate the start scaling factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation begins,
        // it will position the zoomed-in view in the place of the thumbnail.
        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);
        //closeButton.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations to the top-left corner of
        // the zoomed-in view (the default is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and scale properties
        // (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X, startBounds.left,
                        finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.top,
                        finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X, startScale, 1f))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down to the original bounds
        // and show the thumbnail instead of the expanded image.
        startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeAnim();
            }
        });
    }

    private void closeAnim() {

        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        // Animate the four positioning/sizing properties in parallel, back to their
        // original values.
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X, startBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.top))
                .with(ObjectAnimator
                        .ofFloat(expandedImageView, View.SCALE_X, startScaleFinal))
                .with(ObjectAnimator
                        .ofFloat(expandedImageView, View.SCALE_Y, startScaleFinal));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                imgView.setAlpha(1f);
                expandedImageView.setVisibility(View.GONE);
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                imgView.setAlpha(1f);
                expandedImageView.setVisibility(View.GONE);
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;
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
                if(main!=null) {
                    imageView.setImageBitmap(main);
                    imageView.setVisibility(View.VISIBLE);
                }
            }
            ((MainActivity)context).cloudStorageMethods.getBigImage(adData.getAdID(), position, new ImageDownloadListener() {
                @Override
                public void onSuccess(Uri uri) {

                    progressBar.setVisibility(View.INVISIBLE);
                    imageView.setImageURI(uri);
                    imageView.setVisibility(View.VISIBLE);

                    final Uri finalUri = uri;
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            zoomImageFromThumb(imageView,finalUri);
                        }
                    });

                }

                @Override
                public void onFailure(Exception e) {
                    if(position!=0) {
                        Log.e(TAG,"getImage onFailure #"+position,e);
                        Toast.makeText(imageView.getContext(), "Failed to get image#"+position, Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                        imageView.setImageResource(R.drawable.ic_error);
                    }
                }
            });
        }
    }

}
