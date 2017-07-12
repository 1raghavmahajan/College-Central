package com.blackboxindia.TakeIT.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.blackboxindia.TakeIT.Fragments.Frag_Ads;
import com.blackboxindia.TakeIT.Fragments.Frag_LoginPage;
import com.blackboxindia.TakeIT.Fragments.Frag_Main;
import com.blackboxindia.TakeIT.Fragments.Frag_Manage;
import com.blackboxindia.TakeIT.Fragments.Frag_myAds;
import com.blackboxindia.TakeIT.Fragments.Frag_myProfile;
import com.blackboxindia.TakeIT.Fragments.Frag_newAccount;
import com.blackboxindia.TakeIT.Fragments.Frag_newAd;
import com.blackboxindia.TakeIT.Fragments.Frag_newEvent;
import com.blackboxindia.TakeIT.HelperClasses.GlideApp;
import com.blackboxindia.TakeIT.Network.ImageStorageMethods;
import com.blackboxindia.TakeIT.Network.Interfaces.BitmapDownloadListener;
import com.blackboxindia.TakeIT.Network.Interfaces.onLoginListener;
import com.blackboxindia.TakeIT.Network.NetworkMethods;
import com.blackboxindia.TakeIT.R;
import com.blackboxindia.TakeIT.dataModels.UserCred;
import com.blackboxindia.TakeIT.dataModels.UserInfo;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.blackboxindia.TakeIT.activities.OnboardingActivity.PREFERENCES_FILE;
import static com.blackboxindia.TakeIT.dataModels.AdTypes.TYPE_EVENT;
import static com.blackboxindia.TakeIT.dataModels.AdTypes.TYPE_LOSTFOUND;
import static com.blackboxindia.TakeIT.dataModels.AdTypes.TYPE_SELL;
import static com.blackboxindia.TakeIT.dataModels.AdTypes.TYPE_TEACH;

public class MainActivity extends AppCompatActivity {

    //region Static Variables
    public final static String ALL_FRAG_TAG = "ALL_ADS";
    public final static String MAIN_SCREEN_TAG = "MAIN_SCREEN";
    public final static String LOGIN_PAGE_TAG = "LOGIN_PAGE";
    public final static String MY_PROFILE_TAG = "MY_PROFILE";
    public final static String MANAGE_FRAG_TAG = "MANAGE_ACCOUNT";
    public final static String NEW_ACCOUNT_TAG = "NEW_ACCOUNT";
    public final static String MY_ADS_TAG = "MY_ADS";
    public final static String NEW_AD_TAG = "NEW_AD";
    public final static String NEW_EVENT_TAG = "NEW_EVENT";
    public final static String VIEW_AD_TAG = "VIEW_AD";
    public final static String VIEW_EVENT_TAG = "VIEW_EVENT";
    public final static String VIEW_MyAD_TAG = "VIEW_MyAD";
    public final static String VERIFY_EMAIL_TAG = "VERIFY_EMAIL";

    public final static String TAG = MainActivity.class.getSimpleName()+" YOYO";
    public static final String PREF_USER_FIRST_TIME = "user_first_time";
    //endregion

    //region Variables
    public Toolbar toolbar;
    public CoordinatorLayout coordinatorLayout;
    public ProgressBar progressBar;

    Context context;
    boolean isUserFirstTime;
    public String currentFragTag;

    AppBarLayout appBarLayout;
    FragmentManager fragmentManager;
    DrawerLayout drawer;
    public FloatingActionButton fab;
    NavigationView navigationView;
    Menu navigationViewMenu;

    public UserInfo userInfo;
    public ImageStorageMethods imageStorageMethods;

    public closeImageListener closeImageListener;

    //endregion

    //region Initial Setup

    public static String readSharedSetting(Context ctx, String settingName, String defaultValue) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        return sharedPref.getString(settingName, defaultValue);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isUserFirstTime = Boolean.valueOf(readSharedSetting(MainActivity.this, PREF_USER_FIRST_TIME, "true"));

        Intent introIntent = new Intent(MainActivity.this, OnboardingActivity.class);
        introIntent.putExtra(PREF_USER_FIRST_TIME, isUserFirstTime);

        if (isUserFirstTime)
            startActivity(introIntent);

        setContentView(R.layout.activity_main);

        Window window = getWindow();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.TRANSPARENT);

        View backgroundImage = findViewById(R.id.frame_layout);
        Drawable background = backgroundImage.getBackground();
        background.setAlpha(20);

        initVariables();

        setUpToolbar();

        setUpDrawer();

        setUpMainScreen();

        loadData();

    }

    private void loadData() {
        final UserCred userCred = new UserCred();
        if(userCred.load_Cred(context)) {

            final ProgressDialog dialog = ProgressDialog.show(context, "Logging you in...", "", true, false);
            final NetworkMethods methods = new NetworkMethods(context);

            final onLoginListener listener = new onLoginListener() {
                @Override
                public void onSuccess(UserInfo userInfo) {
                    UpdateUI(userInfo,false, false);
                    dialog.cancel();
                    createSnackbar("Logged In!");
//                    Todo:
//                    setUpMainFragment();
                }

                @Override
                public void onFailure(Exception e) {
                    if (e.getMessage().contains("network")) {
                        dialog.cancel();
                        createSnackbar("Network Error. Retry login?", Snackbar.LENGTH_INDEFINITE, "Retry", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        methods.Login(userCred.getEmail(), userCred.getpwd(), new onLoginListener() {
                                            @Override
                                            public void onSuccess(UserInfo userInfo) {
                                                UpdateUI(userInfo,false,false);
                                                dialog.cancel();
                                                createSnackbar("Logged In!");
//                                                Todo:
//                                                setUpMainFragment();
                                            }

                                            @Override
                                            public void onFailure(Exception e) {
                                                if (e.getMessage().contains("network")) {
                                                    dialog.cancel();
                                                    createSnackbar("Network Error, Please try again later.");
                                                } else {
                                                    dialog.cancel();
                                                    createSnackbar("Session Expired. Please login again.");
                                                    UserCred.clear_cred(context);
                                                }
//                                                Todo:
//                                                setUpMainFragment();
                                            }
                                        });
                                    }
                                });
                    } else {
                        dialog.cancel();
                        createSnackbar("Session Expired. Please login again.");
                        UserCred.clear_cred(context);
                    }
                    //Todo:
                    //setUpMainFragment();
                }
            };
            methods.Login(userCred.getEmail(), userCred.getpwd(), listener);
        }
        else {
            createSnackbar("Please Login to continue", Snackbar.LENGTH_INDEFINITE,"Login", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            launchOtherFragment(new Frag_LoginPage(), LOGIN_PAGE_TAG);
                        }
                    });
            //Todo:
            //setUpMainFragment();
        }
    }

    private void initVariables() {
        appBarLayout = (AppBarLayout) findViewById(R.id.appbarLayout);
        progressBar = (ProgressBar) findViewById(R.id.progressBarTop);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fragmentManager = getFragmentManager();
        context = this;
        imageStorageMethods = new ImageStorageMethods(context);
        imageStorageMethods.getCache();
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
                ((Frag_Ads)(fragmentManager.findFragmentByTag(ALL_FRAG_TAG))).filter("");
                if (item.isActionViewExpanded())
                    animateSearchToolbar(1, false, false);
                return true;
            }
        });

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
                launchOtherFragment(new Frag_LoginPage(), LOGIN_PAGE_TAG);
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
                        //Todo:
                        //goToMainFragment(false,false);
                        launchOtherFragment(new Frag_Main(), MAIN_SCREEN_TAG);
                        break;
                    case R.id.nav_manage:
                        launchOtherFragment(new Frag_Manage(), MANAGE_FRAG_TAG);
                        break;
                    case R.id.nav_profile:
                        if (userInfo != null) {
                            launchOtherFragment(new Frag_myProfile(), MY_PROFILE_TAG);
                        } else {
                            Toast.makeText(context, "Please login First", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.nav_newAccount:
                        launchOtherFragment(new Frag_newAccount(), NEW_ACCOUNT_TAG);
                        break;
                    case R.id.nav_myAds:
                        launchOtherFragment(new Frag_myAds(),MY_ADS_TAG);
                        break;
                    case R.id.nav_logout:
                        NetworkMethods.Logout(context);
                        break;
                }
                return true;
            }
        });
    }

    private void setUpMainScreen() {

        hideIT();

        currentFragTag = MAIN_SCREEN_TAG;

        Frag_Main frag_main = new Frag_Main();
        //mc.setRetainInstance(true);

        fragmentManager.beginTransaction()
                .replace(R.id.frame_layout, frag_main, MAIN_SCREEN_TAG)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack(null)
                .commit();

    }

    public void setUpFab(final String adType) {

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkVerification()) {
                    ((TextView) findViewById(R.id.nav_email)).setText(userInfo.getEmail());

                    switch (adType) {

                        case TYPE_SELL:
                        case TYPE_LOSTFOUND:
                        case TYPE_TEACH:

                            Frag_newAd frag_newAd = new Frag_newAd();

                            Bundle args = new Bundle();
                            args.putString(Frag_Ads.ARGS_AdType,adType);

                            frag_newAd.setArguments(args);

                            launchOtherFragment(frag_newAd, NEW_AD_TAG);

                            break;

                        case TYPE_EVENT:
                            launchOtherFragment(new Frag_newEvent(), NEW_EVENT_TAG);
                            break;
                    }
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        imageStorageMethods.saveCache();
    }
    //endregion

    //region Movement

    public void launchOtherFragment(Fragment frag, String tag) {
//        if(!currentFragTag.equals(tag)) {
            currentFragTag = tag;
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_layout, frag, tag)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .addToBackStack(null).commit();
            }

    boolean twiceToExit = false;
    @Override
    public void onBackPressed() {
        // For closing the Drawer if open onBackPress
        //Todo: getCurrentFrag
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if(toolbar.getMenu().findItem(R.id.toolbar_search).isActionViewExpanded()) {
            toolbar.getMenu().findItem(R.id.toolbar_search).collapseActionView();
        }
        else {
            switch (currentFragTag) {
                case VIEW_MyAD_TAG:
                    if(closeImageListener!=null) {
                        if (closeImageListener.closeImage()) {

                            super.onBackPressed();
/*
                            fragmentManager.beginTransaction()
                                    .remove(fragmentManager.findFragmentByTag(VIEW_MyAD_TAG))
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                                    .commit();

                            currentFragTag = MY_ADS_TAG;

                            fragmentManager.beginTransaction()
                                    .add(R.id.frame_layout, new Frag_myAds(), MY_ADS_TAG)
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                    .commit();*/
                        }
                    }
                    break;

                case VERIFY_EMAIL_TAG:
                    //Todo:
                    break;

                case MAIN_SCREEN_TAG:

                    if (twiceToExit) {
                        //imageStorageMethods.saveCache();
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
                    break;

                case VIEW_AD_TAG:
                    if(closeImageListener!=null) {
                        if (closeImageListener.closeImage()) {
                            //goToMainFragment(false,false);
                            super.onBackPressed();
                        }
                    }
                    break;

                default:
                    //goToMainFragment(false, false);
                    super.onBackPressed();
                    break;
            }
        }
    }

    public interface closeImageListener {
        boolean closeImage();
    }

    //endregion

    //region Search Related

    @Override
    protected void onNewIntent(Intent intent) {
        if(Intent.ACTION_SEARCH.equals(intent.getAction())){
            String query = intent.getStringExtra(SearchManager.QUERY);

            if(currentFragTag.equals(ALL_FRAG_TAG)) {
                if (fragmentManager.findFragmentByTag(ALL_FRAG_TAG) != null)
                    ((Frag_Ads) (fragmentManager.findFragmentByTag(ALL_FRAG_TAG))).filter(query);
            }

        }
    }

    public void animateSearchToolbar(int numberOfMenuIcon, boolean containsOverflow, boolean show) {

        //noinspection deprecation
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorSearch));

        if (show) {
            int width = toolbar.getWidth() -
                    (containsOverflow ? getResources().getDimensionPixelSize(R.dimen.action_button_min_width_overflow_material) : 0) -
                    ((getResources().getDimensionPixelSize(R.dimen.action_button_min_width_material) * numberOfMenuIcon) / 2);
            Animator createCircularReveal = ViewAnimationUtils.createCircularReveal(toolbar,
                    isRtl(getResources()) ? toolbar.getWidth() - width : width, toolbar.getHeight() / 2, 0.0f, (float) width);
            createCircularReveal.setDuration(400);
            createCircularReveal.start();
        }
        else {

            int width = toolbar.getWidth() -
                    (containsOverflow ? getResources().getDimensionPixelSize(R.dimen.action_button_min_width_overflow_material) : 0) -
                    ((getResources().getDimensionPixelSize(R.dimen.action_button_min_width_material) * numberOfMenuIcon) / 2);
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
                    ((Frag_Ads)fragmentManager.findFragmentByTag(ALL_FRAG_TAG)).refresh();
                return true;
            }
        });
    }

    boolean recentlySentMail=false;
    public boolean checkVerification() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser==null) {
            createSnackbar("Please Login to continue", Snackbar.LENGTH_LONG, "Login", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    launchOtherFragment(new Frag_LoginPage(), LOGIN_PAGE_TAG);
                }
            });
            return false;
        }
        else {
            currentUser.reload();
            if(recentlySentMail){
                if(currentUser.isEmailVerified()) {
                    return true;
                }
                else {
                    Toast.makeText(context, "Please try again in a second.", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            else {

                if (currentUser.isEmailVerified()) {
                    return true;
                }
                else {
                    new AlertDialog.Builder(MainActivity.this)
                            .setMessage("You need to verify email before posting an Ad.")
                            .setPositiveButton("OK", null)
                            .setNeutralButton("Resend Email", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //noinspection ConstantConditions
                                    FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(context, "Email sent!", Toast.LENGTH_SHORT).show();
                                                    recentlySentMail = true;
                                                    new Handler().postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            recentlySentMail = false;
                                                        }
                                                    }, 5 * 60 * 1000);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            })
                            .create()
                            .show();
                    return false;
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    @SuppressWarnings("ConstantConditions")
    public void UpdateUI(UserInfo userInfo, Boolean redirect, Boolean toRefresh) {

        this.userInfo = userInfo;

        //Drawer
        ((TextView) findViewById(R.id.nav_Name)).setText(userInfo.getName());
        FirebaseAuth.getInstance().getCurrentUser().reload();
        String notVerified = " (Not Verified)";
        if(FirebaseAuth.getInstance().getCurrentUser().isEmailVerified())
            notVerified = "";
        ((TextView) findViewById(R.id.nav_email)).setText(userInfo.getEmail()+notVerified);
        final ImageView imageView = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.nav_profileImg);
        if(userInfo.getHasProfileIMG()) {
            imageStorageMethods.getProfileImage(userInfo.getuID(), new BitmapDownloadListener() {
                @Override
                public void onSuccess(Uri uri) {
//                    imageView.setImageURI(uri);
                    GlideApp.with(context)
                            .load(uri)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(imageView);
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e(TAG, "onFailure: getProfileImage ", e);
                    GlideApp.with(context).load(R.drawable.avatar).into(imageView);
//                    imageView.setImageResource(R.drawable.avatar);
                }
            });
        }
        else {
            GlideApp.with(context).load(R.drawable.avatar).into(imageView);
        }

        (findViewById(R.id.nav_btnLogin)).setVisibility(View.GONE);

        navigationViewMenu.findItem(R.id.nav_myAds).setVisible(true);
        navigationViewMenu.findItem(R.id.nav_manage).setVisible(true);
        navigationViewMenu.findItem(R.id.nav_profile).setVisible(true);
        navigationViewMenu.findItem(R.id.nav_logout).setVisible(true);
        navigationViewMenu.findItem(R.id.nav_newAccount).setVisible(false);

        if(redirect)
            launchOtherFragment(new Frag_Main(),MAIN_SCREEN_TAG);
            //Todo:
            //goToMainFragment(false,toRefresh);
        else if (toRefresh){
            if(fragmentManager.findFragmentByTag(ALL_FRAG_TAG)!=null)
                ((Frag_Ads) (fragmentManager.findFragmentByTag(ALL_FRAG_TAG))).refresh();
            else
                Log.i(TAG,"Main frag null");
        }

    }

    public void UpdateUIonLogout() {

        this.userInfo = null;

        //Drawer
        ((TextView) findViewById(R.id.nav_Name)).setText(R.string.sample_ID);
        ((TextView) findViewById(R.id.nav_email)).setText(R.string.sample_email);
        ImageView imageView = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.nav_profileImg);
        if(imageView.getDrawable() !=null) {
//            ((BitmapDrawable) imageView.getDrawable()).getBitmap().recycle();
            GlideApp.with(context).load(R.drawable.avatar).into(imageView);
        }

        (findViewById(R.id.nav_btnLogin)).setVisibility(View.VISIBLE);

        navigationViewMenu.findItem(R.id.nav_myAds).setVisible(false);
        navigationViewMenu.findItem(R.id.nav_manage).setVisible(false);
        navigationViewMenu.findItem(R.id.nav_profile).setVisible(false);
        navigationViewMenu.findItem(R.id.nav_logout).setVisible(false);
        navigationViewMenu.findItem(R.id.nav_newAccount).setVisible(true);

        //Todo:
        //goToMainFragment(true, false);
        launchOtherFragment(new Frag_Main(),MAIN_SCREEN_TAG);
        createSnackbar("Logged out!");
    }

    public void createSnackbar(String msg) {
        createSnackbar(msg,Snackbar.LENGTH_SHORT);
    }

    public void createSnackbar(String msg, int length) {
        createSnackbar(msg,length,null,null);
    }

    @SuppressWarnings("deprecation")
    public void createSnackbar(String msg, int length, String actionTitle, View.OnClickListener listener) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, msg, length);
        if(actionTitle!=null){
            snackbar.setAction(actionTitle,listener);
            snackbar.setActionTextColor(getResources().getColor(R.color.colorSearch));
        }
        snackbar.show();
    }

    //endregion

}