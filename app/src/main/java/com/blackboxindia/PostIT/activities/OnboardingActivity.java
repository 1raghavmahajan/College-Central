package com.blackboxindia.PostIT.activities;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.blackboxindia.PostIT.HelperClasses.GlideApp;
import com.blackboxindia.PostIT.R;
import com.blackboxindia.PostIT.cameraIntentHelper.ImageUtils;

public class OnboardingActivity extends AppCompatActivity {

    //region Variables
    static final String TAG = OnboardingActivity.class.getSimpleName()+" YOYO";
    public static final String PREFERENCES_FILE = "TakeIT_Settings";

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    CoordinatorLayout mCoordinator;

    ImageButton mNextBtn;
    Button mSkipBtn, mFinishBtn;
    ImageView zero, one, two, three;
    ImageView[] indicators;

    int page =0;
    //endregion


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_onboarding);

        initVariables();

        setUpViewPager();
    }

    void setUpViewPager(){
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setCurrentItem(page);
        updateIndicators(page);

        final int color1 = ContextCompat.getColor(this, R.color.orange);
        final int color2 = ContextCompat.getColor(this, R.color.cyan);
        final int color3 = ContextCompat.getColor(this, R.color.BlueGrey500);
        final int color4 = ContextCompat.getColor(this, R.color.green);

        final int[] colorList = new int[]{color1, color2, color3, color4};

        final ArgbEvaluator evaluator = new ArgbEvaluator();

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                int colorUpdate = (Integer) evaluator.evaluate(positionOffset, colorList[position], colorList[position == 3 ? position : position + 1]);
                mCoordinator.setBackgroundColor(colorUpdate);
                mViewPager.setBackgroundColor(colorUpdate);
            }

            @Override
            public void onPageSelected(int position) {
                page = position;
                updateIndicators(page);

                switch (position) {
                    case 0:
                        mViewPager.setBackgroundColor(color1);
                        break;
                    case 1:
                        mViewPager.setBackgroundColor(color2);
                        break;
                    case 2:
                        mViewPager.setBackgroundColor(color3);
                        break;
                    case 3:
                        mViewPager.setBackgroundColor(color4);
                        break;
                }

                mNextBtn.setVisibility(position == 3 ? View.GONE : View.VISIBLE);
                mFinishBtn.setVisibility(position == 3 ? View.VISIBLE : View.GONE);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page += 1;
                mViewPager.setCurrentItem(page, true);
            }
        });

        mSkipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                saveSharedSetting(OnboardingActivity.this, MainActivity.PREF_USER_FIRST_TIME, "false");
            }
        });

        mFinishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                saveSharedSetting(OnboardingActivity.this, MainActivity.PREF_USER_FIRST_TIME, "false");
            }
        });
    }

    void initVariables(){
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mNextBtn = (ImageButton) findViewById(R.id.intro_btn_next);
        mNextBtn.setImageDrawable(
                ImageUtils.tintMyDrawable(ContextCompat.getDrawable(this, R.drawable.ic_right), Color.WHITE));

        mSkipBtn = (Button) findViewById(R.id.intro_btn_skip);
        mFinishBtn = (Button) findViewById(R.id.intro_btn_finish);

        zero = (ImageView) findViewById(R.id.intro_indicator_0);
        one = (ImageView) findViewById(R.id.intro_indicator_1);
        two = (ImageView) findViewById(R.id.intro_indicator_2);
        three = (ImageView) findViewById(R.id.intro_indicator_3);

        mCoordinator = (CoordinatorLayout) findViewById(R.id.main_content);

        indicators = new ImageView[]{zero, one, two, three};

    }

    void updateIndicators(int position) {
        for (int i = 0; i < indicators.length; i++) {
            indicators[i].setBackgroundResource(
                    i == position ? R.drawable.indicator_selected : R.drawable.indicator_unselected
            );
        }
    }

    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        ImageView img;

        int[] bgs = new int[]{R.drawable.page1, R.drawable.page2, R.drawable.page3, R.drawable.page4};

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.frag_onboarding, container, false);


            TextView tv_Title = (TextView) rootView.findViewById(R.id.section_Title);
            String title ="";
            switch (getArguments().getInt(ARG_SECTION_NUMBER)-1) {
                case 0:
                    title = "Sell it";
                    break;
                case 1:
                    title = "Spread it";
                    break;
                case 2:
                    title = "Put it";
                    break;
                case 3:
                    title = "Take it";
            }
            tv_Title.setText(title);


            TextView tv_Subtitle = (TextView) rootView.findViewById(R.id.section_Subtitle);
            String subtitle ="";
            switch (getArguments().getInt(ARG_SECTION_NUMBER) -1){
                case 0:
                    subtitle = getString(R.string.page0);
                    break;
                case 1:
                    subtitle = getString(R.string.page1);
                    break;
                case 2:
                    subtitle = getString(R.string.page2);
                    break;
                case 3:
                    subtitle = getString(R.string.page3);
                    break;
            }
            tv_Subtitle.setText(subtitle);

            img = (ImageView) rootView.findViewById(R.id.section_img);
            GlideApp.with(container.getContext()).load(bgs[getArguments().getInt(ARG_SECTION_NUMBER) - 1]).into(img);
            //img.setBackgroundResource(bgs[getArguments().getInt(ARG_SECTION_NUMBER) - 1]);

            return rootView;
        }
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {


        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Sell it";
                case 1:
                    return "Spread it";
                case 2:
                    return "Put it";
                case 3:
                    return "Take it";
            }
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    public static void saveSharedSetting(Context ctx, String settingName, String settingValue) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(settingName, settingValue);
        editor.apply();
    }

}
