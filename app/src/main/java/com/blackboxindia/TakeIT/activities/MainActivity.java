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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.blackboxindia.TakeIT.Fragments.frag_Main;
import com.blackboxindia.TakeIT.Fragments.frag_loginPage;
import com.blackboxindia.TakeIT.Fragments.frag_myProfile;
import com.blackboxindia.TakeIT.Fragments.frag_newAd;
import com.blackboxindia.TakeIT.R;

public class MainActivity extends AppCompatActivity {

    public LinearLayout linearLayout;
    Context context;
    AppBarLayout appBarLayout;
    FragmentManager fragmentManager;
    Toolbar toolbar;
    DrawerLayout drawer;
    FloatingActionButton fab;


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
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();

                if (id == R.id.nav_allAds) {
                    Toast.makeText(context, "All ads Clicked", Toast.LENGTH_SHORT).show();
                }
                else if (id == R.id.nav_manage) {
                    Toast.makeText(context, "Settings Clicked", Toast.LENGTH_SHORT).show();
                    launchOtherFragment(new frag_loginPage(), "LOGIN_PAGE");
                }
                else if (id == R.id.nav_profile) {
                    launchOtherFragment(new frag_myProfile(), "MY_PROFILE");
                }

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    private void setUpMainFragment() {
        linearLayout.setVisibility(View.VISIBLE);
        frag_Main mc = new frag_Main();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, mc, "MAIN_FRAG");
        fragmentTransaction.commit();
//        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
//            @Override
//            public void onBackStackChanged() {
//                Log.i("YOYO","onBackStackChanged");
//                Fragment main_frag = fragmentManager.findFragmentByTag("MAIN_FRAG");
//                if(main_frag.isVisible())
//                    linearLayout.setVisibility(View.VISIBLE);
//            }
//        });
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

    public void launchOtherFragment(Fragment frag, String tag) {
        linearLayout.setVisibility(View.GONE);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.replace(R.id.frame_layout, frag).addToBackStack(tag);
        fragmentTransaction.commit();
    }

    /**
     * For closing the Drawer if open onBackPress
     */
    @Override
    public void onBackPressed() {
        Log.i("YOYO", "onBackPressed");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void addImage(View view) {
        Toast.makeText(this, "Heleoeo", Toast.LENGTH_SHORT).show();
    }
}
