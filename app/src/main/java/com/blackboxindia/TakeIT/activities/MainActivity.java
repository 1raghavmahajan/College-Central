package com.blackboxindia.TakeIT.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.blackboxindia.TakeIT.Fragments.frag_Main;
import com.blackboxindia.TakeIT.Fragments.frag_loginPage;
import com.blackboxindia.TakeIT.Fragments.frag_myAds;
import com.blackboxindia.TakeIT.Fragments.frag_myProfile;
import com.blackboxindia.TakeIT.Fragments.frag_newAccount;
import com.blackboxindia.TakeIT.Fragments.frag_newAd;
import com.blackboxindia.TakeIT.Network.CloudStorageMethods;
import com.blackboxindia.TakeIT.Network.Interfaces.onLoginListener;
import com.blackboxindia.TakeIT.Network.NetworkMethods;
import com.blackboxindia.TakeIT.R;
import com.blackboxindia.TakeIT.Utils;
import com.blackboxindia.TakeIT.cameraIntentHelper.ImageUtils;
import com.blackboxindia.TakeIT.dataModels.UserCred;
import com.blackboxindia.TakeIT.dataModels.UserInfo;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    //region Static Variables
    public final static String MAIN_FRAG_TAG = "MAIN_FRAG";
    public final static String LOGIN_PAGE_TAG = "LOGIN_PAGE";
    public final static String MY_PROFILE_TAG = "MY_PROFILE";
    public final static String NEW_ACCOUNT_TAG = "NEW_ACCOUNT";
    public final static String MY_ADS_TAG = "MY_ADS";
    public final static String NEW_AD_TAG = "NEW_AD";
    public final static String VIEW_AD_TAG = "VIEW_AD";
    public final static String VIEW_MyAD_TAG = "VIEW_MyAD";
    public final static String TAG = MainActivity.class.getSimpleName()+" YOYO";
    //endregion

    //region Variables
    public ProgressBar progressBar;
    Context context;
    AppBarLayout appBarLayout;
    FragmentManager fragmentManager;
    public Toolbar toolbar;
    public CoordinatorLayout coordinatorLayout;
    DrawerLayout drawer;
    FloatingActionButton fab;
    NavigationView navigationView;
    Menu navigationViewMenu;

    public FirebaseAuth mAuth;
    public UserInfo userInfo;

    public CloudStorageMethods cloudStorageMethods;

    public static final String PREF_USER_FIRST_TIME = "user_first_time";
    boolean isUserFirstTime;

    //endregion

    //region Initial Setup

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isUserFirstTime = Boolean.valueOf(Utils.readSharedSetting(MainActivity.this, PREF_USER_FIRST_TIME, "true"));

        Intent introIntent = new Intent(MainActivity.this, OnboardingActivity.class);
        introIntent.putExtra(PREF_USER_FIRST_TIME, isUserFirstTime);

        if (isUserFirstTime)
            startActivity(introIntent);

        setContentView(R.layout.activity_main);

        initVariables();

        setUpToolbar();

        setUpDrawer();

        setUpFab();

        loadData();

    }

    private void loadData() {
        UserCred userCred = new UserCred();
        if(userCred.load_Cred(context)) {

            final ProgressDialog dialog = ProgressDialog.show(context, "Logging you in...", "", true, false);
            NetworkMethods methods = new NetworkMethods(context);
            methods.Login(userCred.getEmail(), userCred.getpwd(), new onLoginListener() {
                @Override
                public void onSuccess(FirebaseAuth Auth, UserInfo userInfo) {
                    UpdateUI(userInfo, Auth, false);
                    dialog.cancel();
                    Toast.makeText(context, "Logged In!", Toast.LENGTH_SHORT).show();
                    setUpMainFragment();
                }

                @Override
                public void onFailure(Exception e) {
                    if (e.getMessage().contains("network")) {
                        dialog.cancel();
                        Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show();
                    } else {
                        dialog.cancel();
                        Toast.makeText(context, "Session Expired. Please login again.", Toast.LENGTH_SHORT).show();
                        UserCred.clear_cred(context);
                    }
                    setUpMainFragment();
                }
            });
        }
        else {
            Snackbar.make(coordinatorLayout, "Please Login to continue", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Login", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            launchOtherFragment(new frag_loginPage(), LOGIN_PAGE_TAG);
                        }
                    }).show();
            setUpMainFragment();
        }
    }

    private void initVariables() {
        appBarLayout = (AppBarLayout) findViewById(R.id.appbarLayout);
        progressBar = (ProgressBar) findViewById(R.id.progressBarTop);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
        fragmentManager = getFragmentManager();
        context = this;
        cloudStorageMethods = new CloudStorageMethods(context);
        cloudStorageMethods.getCache();
    }

    private void setUpToolbar() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.inflateMenu(R.menu.toolbar_menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) toolbar.getMenu().findItem(R.id.toolbar_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        MenuItem item = toolbar.getMenu().findItem(R.id.toolbar_search);
        MenuItemCompat.setOnActionExpandListener(item, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                animateSearchToolbar(1, true, true);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                ((frag_Main)(fragmentManager.findFragmentByTag(MAIN_FRAG_TAG))).filter("");
                if (item.isActionViewExpanded())
                    animateSearchToolbar(1, false, false);
                return true;
            }
        });
    }

    public void animateSearchToolbar(int numberOfMenuIcon, boolean containsOverflow, boolean show) {

        toolbar.setBackgroundColor(getResources().getColor(R.color.colorSearch));
        //drawer.setStatusBarBackgroundColor(ContextCompat.getColor(this, R.color.quantum_grey_600));

        if (show) {
            int width = toolbar.getWidth() -
                    (containsOverflow ? getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_overflow_material) : 0) -
                    ((getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material) * numberOfMenuIcon) / 2);
            Animator createCircularReveal = ViewAnimationUtils.createCircularReveal(toolbar,
                    isRtl(getResources()) ? toolbar.getWidth() - width : width, toolbar.getHeight() / 2, 0.0f, (float) width);
            createCircularReveal.setDuration(400);
            createCircularReveal.start();
        }
        else {

            int width = toolbar.getWidth() -
                    (containsOverflow ? getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_overflow_material) : 0) -
                    ((getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material) * numberOfMenuIcon) / 2);
            Animator createCircularReveal = ViewAnimationUtils.createCircularReveal(toolbar,
                    isRtl(getResources()) ? toolbar.getWidth() - width : width, toolbar.getHeight() / 2, (float) width, 0.0f);
            createCircularReveal.setDuration(300);
            createCircularReveal.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    toolbar.setBackgroundColor(getThemeColor(MainActivity.this, R.attr.colorPrimary));
                    drawer.setStatusBarBackgroundColor(getThemeColor(MainActivity.this, R.attr.colorPrimaryDark));
                }
            });
            createCircularReveal.start();
            drawer.setStatusBarBackgroundColor(getThemeColor(MainActivity.this, R.attr.colorPrimaryDark));
        }
    }

    private boolean isRtl(Resources resources) {
        return resources.getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
    }

    private static int getThemeColor(Context context, int id) {
        Resources.Theme theme = context.getTheme();
        TypedArray a = theme.obtainStyledAttributes(new int[]{id});
        int result = a.getColor(0, 0);
        a.recycle();
        return result;
    }

    private void setUpDrawer() {

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navDrawer_open, R.string.navDrawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationViewMenu = navigationView.getMenu();

        Button btn_nav = (Button) navigationView.getHeaderView(0).findViewById(R.id.nav_btnLogin);
        btn_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchOtherFragment(new frag_loginPage(), LOGIN_PAGE_TAG);
                if(drawer.isDrawerOpen(Gravity.START))
                    drawer.closeDrawer(Gravity.START);
            }
        });

//        GlideApp.with(this).load(R.drawable.header_image).into(new SimpleTarget<Drawable>() {
//            @Override
//            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
//                navigationView.getHeaderView(0).setBackground(resource);
//            }
//        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                drawer.closeDrawer(GravityCompat.START);
                switch (item.getItemId()) {
                    case R.id.nav_allAds:
                        goToMainFragment();
                        break;
                    case R.id.nav_manage:
                        break;
                    case R.id.nav_profile:
                        if (userInfo != null) {
                            launchOtherFragment(new frag_myProfile(), MY_PROFILE_TAG);
                        } else {
                            Toast.makeText(context, "Please login First", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.nav_newAccount:
                        launchOtherFragment(new frag_newAccount(), NEW_ACCOUNT_TAG);
                        break;
                    case R.id.nav_myAds:
                        launchOtherFragment(new frag_myAds(),MY_ADS_TAG);
                        break;
                    case R.id.nav_logout:
                        NetworkMethods.Logout(context);
                        break;
                }
                return true;
            }
        });
    }

    private void setUpMainFragment() {

        showIT();

        frag_Main mc = new frag_Main();
        mc.setRetainInstance(true);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, mc, MAIN_FRAG_TAG);
        fragmentTransaction.commit();
    }

    private void setUpFab() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mAuth==null) {
                    Snackbar.make(coordinatorLayout, "Please Login to continue", Snackbar.LENGTH_LONG)
                            .setAction("Login", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    launchOtherFragment(new frag_loginPage(), LOGIN_PAGE_TAG);
                                }
                            }).show();
                }
                else
                    launchOtherFragment(new frag_newAd(), NEW_AD_TAG);
            }
        });
    }
    //endregion

    //region Movement

    boolean goToMainFragment() {
        return goToMainFragment(false, false);
    }

    public boolean goToMainFragment(Boolean clearAll, Boolean toRefresh) {

        if(fragmentManager.findFragmentByTag(MAIN_FRAG_TAG)!=null) {

            showIT();

            if (!fragmentManager.findFragmentByTag(MAIN_FRAG_TAG).isVisible()) {

                fragmentManager.beginTransaction()
                        .replace(R.id.frame_layout,fragmentManager.findFragmentByTag(MAIN_FRAG_TAG), MAIN_FRAG_TAG)
                        //.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                        .commit();
                fragmentManager.beginTransaction()
                        .show(fragmentManager.findFragmentByTag(MAIN_FRAG_TAG))
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
                if (clearAll)
                    ((frag_Main)(fragmentManager.findFragmentByTag(MAIN_FRAG_TAG))).clearRecycler();
                else if (toRefresh)
                    ((frag_Main)(fragmentManager.findFragmentByTag(MAIN_FRAG_TAG))).refresh();
                return false;
            }
            else {
                if (clearAll)
                    ((frag_Main)(fragmentManager.findFragmentByTag(MAIN_FRAG_TAG))).clearRecycler();
                else if (toRefresh)
                    ((frag_Main)(fragmentManager.findFragmentByTag(MAIN_FRAG_TAG))).refresh();
                return true;
            }
        }
        else {
            setUpMainFragment();
            return false;
        }

    }

    public void launchOtherFragment(Fragment frag, String tag) {
        
        if(fragmentManager.findFragmentByTag(MAIN_FRAG_TAG) != null)
        {
            if (fragmentManager.findFragmentByTag(MAIN_FRAG_TAG).isVisible())
            {
                fragmentManager.beginTransaction()
                        .hide(fragmentManager.findFragmentByTag(MAIN_FRAG_TAG))
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                        .commit();

                fragmentManager.beginTransaction()
                        .add(R.id.frame_layout, frag, tag)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
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

    boolean twiceToExit = false;
    @Override
    public void onBackPressed() {
        // For closing the Drawer if open onBackPress
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if(toolbar.getMenu().findItem(R.id.toolbar_search).isActionViewExpanded()) {
            toolbar.getMenu().findItem(R.id.toolbar_search).collapseActionView();
//            animateSearchToolbar(1, false, false);
        }
        else {
            if(fragmentManager.findFragmentByTag(VIEW_MyAD_TAG)!=null) {
                if (fragmentManager.findFragmentByTag(VIEW_MyAD_TAG).isVisible()) {

                    fragmentManager.beginTransaction()
                            .remove(fragmentManager.findFragmentByTag(VIEW_MyAD_TAG))
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                            .commit();
                    fragmentManager.beginTransaction()
                            .add(R.id.frame_layout,new frag_myAds(),MY_ADS_TAG)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit();
//                    fragmentManager.beginTransaction().add(R.id.frame_layout,new frag_myAds(),MY_ADS_TAG)
//                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
                }
            }
            else {
                if (goToMainFragment()) {

                    if (twiceToExit) {
                        //cloudStorageMethods.saveCache();
                        finish();
                    }

                    this.twiceToExit = true;
                    Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            twiceToExit = false;
                        }
                    }, 2000);
                }
            }
//            super.onBackPressed();
        }
    }

    //endregion

    //region UI updating

    public void hideIT() {
        fab.setVisibility(View.GONE);
        toolbar.getMenu().findItem(R.id.toolbar_search).setVisible(false);
        toolbar.getMenu().findItem(R.id.toolbar_refresh).setVisible(false);
    }

    public void showIT() {
        fab.setVisibility(View.VISIBLE);
        toolbar.getMenu().findItem(R.id.toolbar_search).setVisible(true);

        toolbar.getMenu().findItem(R.id.toolbar_refresh).setVisible(true);
        toolbar.getMenu().findItem(R.id.toolbar_refresh).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.toolbar_refresh)
                    ((frag_Main)fragmentManager.findFragmentByTag(MAIN_FRAG_TAG)).refresh();
                return true;
            }
        });
    }

    public void UpdateUI(UserInfo userInfo, FirebaseAuth auth) {
        UpdateUI(userInfo, auth, true);
    }

    public void UpdateUI(UserInfo userInfo, Boolean redirect) {
        UpdateUI(userInfo,null, redirect);
    }

    public void UpdateUI(UserInfo userInfo, FirebaseAuth auth, Boolean redirect) {

        if(auth!=null) {
            mAuth = auth;
        }
        this.userInfo = userInfo;

        //Drawer
        ((TextView) findViewById(R.id.nav_Name)).setText(userInfo.getName());
        ((TextView) findViewById(R.id.nav_email)).setText(userInfo.getEmail());
        ImageView imageView = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.nav_profileImg);
        if(userInfo.getProfileIMG()!= null) {
            if (!userInfo.getProfileIMG().equals("null")) {
                imageView.setImageBitmap(ImageUtils.StringToBitMap(userInfo.getProfileIMG()));
            } else {
                imageView.setImageResource(R.drawable.sample_profile_image);
            }
        }
        else {
            imageView.setImageResource(R.drawable.sample_profile_image);
        }

        (findViewById(R.id.nav_btnLogin)).setVisibility(View.GONE);

        navigationViewMenu.findItem(R.id.nav_myAds).setVisible(true);
        //navigationViewMenu.findItem(R.id.nav_manage).setVisible(true);
        navigationViewMenu.findItem(R.id.nav_profile).setVisible(true);
        navigationViewMenu.findItem(R.id.nav_logout).setVisible(true);
        navigationViewMenu.findItem(R.id.nav_newAccount).setVisible(false);

        if(redirect)
            goToMainFragment();

    }

    public void UpdateUIonLogout() {
        mAuth = null;
        this.userInfo = null;

        //Drawer
        ((TextView) findViewById(R.id.nav_Name)).setText(R.string.sample_ID);
        ((TextView) findViewById(R.id.nav_email)).setText(R.string.sample_email);
        ImageView imageView = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.nav_profileImg);
        if(imageView.getDrawable() !=null) {
            ((BitmapDrawable) imageView.getDrawable()).getBitmap().recycle();
            imageView.setImageResource(R.drawable.sample_profile_image);
        }

        (findViewById(R.id.nav_btnLogin)).setVisibility(View.VISIBLE);

        navigationViewMenu.findItem(R.id.nav_myAds).setVisible(false);
        //navigationViewMenu.findItem(R.id.nav_manage).setVisible(false);
        navigationViewMenu.findItem(R.id.nav_profile).setVisible(false);
        navigationViewMenu.findItem(R.id.nav_logout).setVisible(false);
        navigationViewMenu.findItem(R.id.nav_newAccount).setVisible(true);

        goToMainFragment(true, false);
        Toast.makeText(context, "Logged out!", Toast.LENGTH_SHORT).show();
    }

    //endregion

    @Override
    protected void onNewIntent(Intent intent) {
        if(Intent.ACTION_SEARCH.equals(intent.getAction())){
            String query = intent.getStringExtra(SearchManager.QUERY);
            ((frag_Main)(fragmentManager.findFragmentByTag(MAIN_FRAG_TAG))).filter(query);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        cloudStorageMethods.saveCache();
    }
}