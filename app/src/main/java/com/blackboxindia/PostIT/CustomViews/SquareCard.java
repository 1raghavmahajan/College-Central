package com.blackboxindia.PostIT.CustomViews;

import android.content.Context;
import android.util.AttributeSet;
import androidx.cardview.widget.CardView;


public class SquareCard extends CardView {

    public SquareCard(Context context) {
        super(context);
    }

    public SquareCard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //noinspection SuspiciousNameCombination
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
