package com.blackboxindia.TakeIT.activities;

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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blackboxindia.TakeIT.Fragments.frag_Main;
import com.blackboxindia.TakeIT.Fragments.frag_loginPage;
import com.blackboxindia.TakeIT.Fragments.frag_myProfile;
import com.blackboxindia.TakeIT.Fragments.frag_newAccount;
import com.blackboxindia.TakeIT.Fragments.frag_newAd;
import com.blackboxindia.TakeIT.R;
import com.blackboxindia.TakeIT.dataModels.UserInfo;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    //region Variables

    public LinearLayout linearLayout;
    Context context;
    AppBarLayout appBarLayout;
    FragmentManager fragmentManager;
    Toolbar toolbar;
    DrawerLayout drawer;
    FloatingActionButton fab;
    //View headerView;

    FirebaseAuth mAuth;
    UserInfo userInfo;

    //endregion

    //region Initial Setup

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initVariables();

        setUpToolbar();

        setUpDrawer();

        setUpMainFragment();

        setUpFab();

    }

    private void initVariables() {
        linearLayout = (LinearLayout) findViewById(R.id.appbar_extra);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbarLayout);
        fragmentManager = getFragmentManager();
        context = getApplicationContext();
    }

    private void setUpToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        CollapsingToolbarLayout cTLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        cTLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        cTLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
        cTLayout.setTitle(getString(R.string.app_name));
    }

    private void setUpDrawer() {

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navDrawer_open, R.string.navDrawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        //headerView =  navigationView.getHeaderView(0);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.nav_allAds:
                        goToMainFragment();
                        break;
                    case R.id.nav_manage:
                        Toast.makeText(context, "Settings Clicked", Toast.LENGTH_SHORT).show();
                        launchOtherFragment(new frag_loginPage(), "LOGIN_PAGE");
                        break;
                    case R.id.nav_profile:
                        if (userInfo != null) {
                            frag_myProfile fragMyProfile = new frag_myProfile();
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("UserInfo", userInfo);
                            fragMyProfile.setArguments(bundle);
                            launchOtherFragment(fragMyProfile, "MY_PROFILE");
                        } else {
                            if (drawer.isDrawerOpen(GravityCompat.START)) {
                                drawer.closeDrawer(GravityCompat.START);
                            }
                            Toast.makeText(context, "Please login First", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.nav_newAccount:
                        launchOtherFragment(new frag_newAccount(), "NEW_ACCOUNT");
                        break;
                }
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

//        Button btnLogin = (Button) navigationView.findViewById(R.id.nav_btnLogin);
//        btnLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                launchOtherFragment(new frag_loginPage(),"LOGIN_PAGE");
//            }
//        });
    }

    private void setUpMainFragment() {
        linearLayout.setVisibility(View.VISIBLE);
        frag_Main mc = new frag_Main();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, mc, "MAIN_FRAG");
        fragmentTransaction.commit();
    }

    private void setUpFab() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchOtherFragment(new frag_newAd(), "NEW_AD");
            }
        });
    }

    //endregion

    //region Movement

    void goToMainFragment() {
        linearLayout.setVisibility(View.VISIBLE);
        if (fragmentManager.findFragmentByTag("MAIN_FRAG") != null) {
            if (!fragmentManager.findFragmentByTag("MAIN_FRAG").isVisible()) {

                //Todo:Handle frag already exists

                frag_Main mc = new frag_Main();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, mc, "MAIN_FRAG");
                fragmentTransaction.commit();
            }
        } else {
            linearLayout.setVisibility(View.VISIBLE);
            frag_Main mc = new frag_Main();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, mc, "MAIN_FRAG");
            fragmentTransaction.commit();
        }
    }

    public void launchOtherFragment(Fragment frag, String tag) {
        linearLayout.setVisibility(View.GONE);
        if (fragmentManager.findFragmentByTag(tag) != null) {

            //noinspection StatementWithEmptyBody
            if (!fragmentManager.findFragmentByTag(tag).isVisible()) {

                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                fragmentTransaction.replace(R.id.frame_layout, frag).addToBackStack(tag);
                fragmentTransaction.commit();
            } else {

                //Todo: handle if fragment already in back stack but not visible

                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                fragmentTransaction.replace(R.id.frame_layout, frag).addToBackStack(tag);
                fragmentTransaction.commit();

            }
        } else {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragmentTransaction.replace(R.id.frame_layout, frag).addToBackStack(tag);
            fragmentTransaction.commit();
        }
    }

    public void NavLoginButtonClicked(View view) {
        launchOtherFragment(new frag_loginPage(), "LOGIN_PAGE");
    }

    @Override
    public void onBackPressed() {
        // For closing the Drawer if open onBackPress
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //endregion

    public void UpdateUIonLogin(UserInfo userInfo, FirebaseAuth auth) {
        mAuth = auth;
        this.userInfo = userInfo;

        //Drawer
        ((TextView) findViewById(R.id.nav_Name)).setText(userInfo.getName());
        ((TextView) findViewById(R.id.nav_email)).setText(userInfo.getEmail());

        (findViewById(R.id.nav_btnLogin)).setVisibility(View.GONE);

        goToMainFragment();
    }

    //This works apparently
    public void addImage(View view) {
        Toast.makeText(this, "Heleoeo", Toast.LENGTH_SHORT).show();
    }

}
