package com.blackboxindia.TakeIT.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.blackboxindia.TakeIT.Fragments.frag_Main;
import com.blackboxindia.TakeIT.Fragments.frag_loginPage;
import com.blackboxindia.TakeIT.Fragments.frag_myProfile;
import com.blackboxindia.TakeIT.Fragments.frag_newAccount;
import com.blackboxindia.TakeIT.Fragments.frag_newAd;
import com.blackboxindia.TakeIT.R;
import com.blackboxindia.TakeIT.dataModels.UserInfo;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends Activity {

    //region Variables
    public final static String MAIN_FRAG_TAG = "MAIN_FRAG";
    public final static String LOGIN_PAGE_TAG = "LOGIN_PAGE";
    public final static String MY_PROFILE_TAG = "MY_PROFILE";
    public final static String NEW_ACCOUNT_TAG = "NEW_ACCOUNT";
    public final static String MY_ADS_TAG = "MY_ADS";
    public final static String NEW_AD_TAG = "NEW_AD";
    public final static String VIEW_AD_TAG = "VIEW_AD";


    public LinearLayout linearLayout;
    public ProgressBar progressBar;
    Context context;
    AppBarLayout appBarLayout;
    FragmentManager fragmentManager;
    Toolbar toolbar;
    CollapsingToolbarLayout cTLayout;
    DrawerLayout drawer;
    FloatingActionButton fab;

    public FirebaseAuth mAuth;
    public UserInfo userInfo;

    //endregion

    //region Initial Setup

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initVariables();

        setUpToolbar();

        setUpDrawer();

        setUpFab();

        setUpMainFragment();
    }

    private void initVariables() {
        linearLayout = (LinearLayout) findViewById(R.id.appbar_extra);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbarLayout);
        progressBar = (ProgressBar) findViewById(R.id.progressBarTop);
        fragmentManager = getFragmentManager();
        context = getApplicationContext();
    }

    private void setUpToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_add);
        setActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!drawer.isDrawerOpen(Gravity.START))
                    drawer.openDrawer(Gravity.START);
                else
                    drawer.closeDrawer(Gravity.START);
            }
        });
        cTLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        cTLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        cTLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
        cTLayout.setTitle(getString(R.string.app_name));
    }

    private void setUpDrawer() {

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, R.string.navDrawer_open, R.string.navDrawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        Button btn_nav = (Button) navigationView.getHeaderView(0).findViewById(R.id.nav_btnLogin);
        btn_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchOtherFragment(new frag_loginPage(), LOGIN_PAGE_TAG);
                if(drawer.isDrawerOpen(Gravity.START))
                    drawer.closeDrawer(Gravity.START);
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                drawer.closeDrawer(GravityCompat.START);
                switch (item.getItemId()) {
                    case R.id.nav_allAds:
                        goToMainFragment();
                        break;
                    case R.id.nav_manage:
                        Toast.makeText(context, "Settings Clicked", Toast.LENGTH_SHORT).show();
                        launchOtherFragment(new frag_loginPage(), LOGIN_PAGE_TAG);
                        break;
                    case R.id.nav_profile:
                        if (userInfo != null) {
                            frag_myProfile fragMyProfile = new frag_myProfile();
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("UserInfo", userInfo);
                            fragMyProfile.setArguments(bundle);
                            launchOtherFragment(fragMyProfile, MY_PROFILE_TAG);
                        } else {
                            launchOtherFragment(new frag_myProfile(), MY_PROFILE_TAG);
                            //Toast.makeText(context, "Please login First", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.nav_newAccount:
                        launchOtherFragment(new frag_newAccount(), NEW_ACCOUNT_TAG);
                        break;
                }
                return true;
            }
        });
    }

    private void setUpMainFragment() {

        showIT();

        frag_Main mc = new frag_Main();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, mc, MAIN_FRAG_TAG);
        fragmentTransaction.commit();
    }

    private void setUpFab() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchOtherFragment(new frag_newAd(), NEW_AD_TAG);
            }
        });
    }

    //endregion

    //region Movement

    void goToMainFragment() {

        showIT();
        if(fragmentManager.findFragmentByTag(MAIN_FRAG_TAG)!=null) {

            if (!fragmentManager.findFragmentByTag(MAIN_FRAG_TAG).isVisible()) {
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_layout,fragmentManager.findFragmentByTag(MAIN_FRAG_TAG), MAIN_FRAG_TAG)
                        //.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                        .commit();
                fragmentManager.beginTransaction()
                        .show(fragmentManager.findFragmentByTag(MAIN_FRAG_TAG))
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
            }
        }
        else
            setUpMainFragment();

    }

    public void launchOtherFragment(Fragment frag, String tag) {
        
        if(fragmentManager.findFragmentByTag(MAIN_FRAG_TAG) != null) {
            if (fragmentManager.findFragmentByTag(MAIN_FRAG_TAG).isVisible()) {
                fragmentManager.beginTransaction()
                        .hide(fragmentManager.findFragmentByTag(MAIN_FRAG_TAG))
                        .commit();


                fragmentManager.beginTransaction()
                        .add(R.id.frame_layout, frag, tag)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                        .commit();

            } else {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                if(fragmentManager.findFragmentByTag(tag)!=null) {
                    if (!fragmentManager.findFragmentByTag(tag).isVisible()) {

                    }
                }
                else {

                    if(fragmentManager.findFragmentByTag(NEW_ACCOUNT_TAG)!=null) {
                        if (fragmentManager.findFragmentByTag(NEW_ACCOUNT_TAG).isVisible())
                            transaction.remove(fragmentManager.findFragmentByTag(NEW_ACCOUNT_TAG));
                    }

                    else if(fragmentManager.findFragmentByTag(LOGIN_PAGE_TAG)!=null) {
                        if (fragmentManager.findFragmentByTag(LOGIN_PAGE_TAG).isVisible())
                            transaction.remove(fragmentManager.findFragmentByTag(LOGIN_PAGE_TAG));
                    }

                    else if(fragmentManager.findFragmentByTag(MY_PROFILE_TAG)!=null) {
                        if (fragmentManager.findFragmentByTag(MY_PROFILE_TAG).isVisible())
                            transaction.remove(fragmentManager.findFragmentByTag(MY_PROFILE_TAG));
                    }

                    else if(fragmentManager.findFragmentByTag(MY_ADS_TAG)!=null) {
                        if (fragmentManager.findFragmentByTag(MY_ADS_TAG).isVisible())
                            transaction.remove(fragmentManager.findFragmentByTag(MY_ADS_TAG));
                    }

                    else if(fragmentManager.findFragmentByTag(NEW_AD_TAG)!=null) {
                        if (fragmentManager.findFragmentByTag(NEW_AD_TAG).isVisible())
                            transaction.remove(fragmentManager.findFragmentByTag(NEW_AD_TAG));
                    }

                    else if(fragmentManager.findFragmentByTag(VIEW_AD_TAG)!=null) {
                        if (fragmentManager.findFragmentByTag(VIEW_AD_TAG).isVisible())
                            transaction.remove(fragmentManager.findFragmentByTag(VIEW_AD_TAG));
                    }
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                    transaction.commit();
                    fragmentManager.beginTransaction()
                            .add(R.id.frame_layout, frag, tag)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit();
                }

            }
        }
    }

    @Override
    public void onBackPressed() {
        // For closing the Drawer if open onBackPress
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            goToMainFragment();
            //super.onBackPressed();
        }
    }

    //endregion

    public void hideIT() {
        fab.setVisibility(View.GONE);

        linearLayout.setVisibility(View.GONE);
        appBarLayout.setExpanded(false, true);

        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) cTLayout.getLayoutParams();
        params.setScrollFlags(0);
        cTLayout.setLayoutParams(params);
    }

    public void showIT() {
        fab.setVisibility(View.VISIBLE);

        linearLayout.setVisibility(View.VISIBLE);
        AppBarLayout.LayoutParams params =
                (AppBarLayout.LayoutParams) cTLayout.getLayoutParams();
        params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
        cTLayout.setLayoutParams(params);
    }

    public void UpdateUIonLogin(UserInfo userInfo, FirebaseAuth auth) {
        mAuth = auth;
        this.userInfo = userInfo;

        //Drawer
        ((TextView) findViewById(R.id.nav_Name)).setText(userInfo.getName());
        ((TextView) findViewById(R.id.nav_email)).setText(userInfo.getEmail());

        (findViewById(R.id.nav_btnLogin)).setVisibility(View.GONE);

        goToMainFragment();
    }

}
