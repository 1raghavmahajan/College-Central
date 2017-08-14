package com.blackboxindia.PostIT.activities;

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
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.blackboxindia.PostIT.Fragments.Frag_Ads;
import com.blackboxindia.PostIT.Fragments.Frag_LoginPage;
import com.blackboxindia.PostIT.Fragments.Frag_Main;
import com.blackboxindia.PostIT.Fragments.Frag_Manage;
import com.blackboxindia.PostIT.Fragments.Frag_myAds;
import com.blackboxindia.PostIT.Fragments.Frag_myProfile;
import com.blackboxindia.PostIT.Fragments.Frag_newAccount;
import com.blackboxindia.PostIT.Fragments.Frag_newAd;
import com.blackboxindia.PostIT.Fragments.Frag_newEvent;
import com.blackboxindia.PostIT.HelperClasses.GlideApp;
import com.blackboxindia.PostIT.Network.CloudStorageMethods;
import com.blackboxindia.PostIT.Network.ConnectionDetector;
import com.blackboxindia.PostIT.Network.Interfaces.onCompleteListener;
import com.blackboxindia.PostIT.Network.Interfaces.onLoginListener;
import com.blackboxindia.PostIT.Network.NetworkMethods;
import com.blackboxindia.PostIT.R;
import com.blackboxindia.PostIT.dataModels.UserCred;
import com.blackboxindia.PostIT.dataModels.UserInfo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.blackboxindia.PostIT.activities.OnboardingActivity.PREFERENCES_FILE;
import static com.blackboxindia.PostIT.dataModels.AdTypes.TYPE_EVENT;
import static com.blackboxindia.PostIT.dataModels.AdTypes.TYPE_INFO;
import static com.blackboxindia.PostIT.dataModels.AdTypes.TYPE_LOSTFOUND;
import static com.blackboxindia.PostIT.dataModels.AdTypes.TYPE_SELL;
import static com.blackboxindia.PostIT.dataModels.AdTypes.TYPE_TEACH;

public class MainActivity extends AppCompatActivity {

    //region Static Variables
    public final static String ALL_FRAG_TAG = "ALL_ADS";
    public final static String MAIN_SCREEN_TAG = "MAIN_SCREEN";
    public final static String LOGIN_PAGE_TAG = "LOGIN_PAGE";
    public final static String DOCS_TAG = "DOCS_PAGE";
    public final static String MY_PROFILE_TAG = "MY_PROFILE";
    public final static String MANAGE_FRAG_TAG = "MANAGE_ACCOUNT";
    public final static String NEW_ACCOUNT_TAG = "NEW_ACCOUNT";
    public final static String VERIFY_EMAIL_TAG = "VERIFY_EMAIL";
    public final static String MY_ADS_TAG = "MY_ADS";
    public final static String NEW_AD_TAG = "NEW_AD";
    public final static String NEW_EVENT_TAG = "NEW_EVENT";
    public final static String VIEW_AD_TAG = "VIEW_AD";
    public final static String VIEW_EVENT_TAG = "VIEW_EVENT";
    public final static String VIEW_MyAD_TAG = "VIEW_MyAD";
    public final static String VIEW_MyEVENT_TAG = "VIEW_MyEVENT";
    public final static String EDIT_AD_TAG = "EDIT_AD";
    public final static String EDIT_EVENT_TAG = "EDIT_EVENT";

    public final static String TITLE_AllAds = "All Ads";
    public final static String TITLE_MainScreen = "College Central";
    public final static String TITLE_LoginPage = "Login";
    public final static String TITLE_Documents = "Documents";
    public final static String TITLE_MyProfile = "My Profile";
    public final static String TITLE_ManageProfile = "Manage Account";
    public final static String TITLE_NewAccount = "New Account";
    public final static String TITLE_VerifyEmail = "Verify Email";
    public final static String TITLE_MyAds = "My Ads";
    public final static String TITLE_NewAd = "Create New Ad";
    public final static String TITLE_NewEvent = "New Event";
    public final static String TITLE_ViewAd = "College Central";
    public final static String TITLE_ViewEvent = "Event";
    public final static String TITLE_EditAd = "Edit Ad";
    public final static String TITLE_EditEvent = "Edit Event";

    private final static String TAG = MainActivity.class.getSimpleName()+" YOYO";
    public static final String PREF_USER_FIRST_TIME = "user_first_time";
    //endregion

    //region Variables
    public Toolbar toolbar;
    public CoordinatorLayout coordinatorLayout;
    public ProgressBar progressBar;

    Context context;
    boolean isUserFirstTime;
    public boolean offlineMode;

    AppBarLayout appBarLayout;
    FragmentManager fragmentManager;
    DrawerLayout drawer;
    NavigationView navigationView;
    Menu navigationViewMenu;
    Button btn_login;
    MenuItem search_icon;
    public FloatingActionButton fab;
    public Snackbar currentSnackbar;
    private boolean specialSnackbar;

    public UserInfo userInfo;
    public NetworkMethods networkMethods;
    public CloudStorageMethods cloudStorageMethods;

    public OnBackPressedListener backPressedListener;

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
        background.setAlpha(10);

        initVariables();

        setUpToolbar();

        setUpDrawer();

        setUpMainScreen();

        setUpUser();

    }

    private void setUpUser(){

        UserInfo userInfo = UserInfo.readCachedUserDetails(context);

        final boolean isCached = userInfo!=null;

        offlineMode = !ConnectionDetector.isNetworkAvailable(context);

        if(isCached){
            UpdateUI(userInfo,false);
        }

        final UserCred userCred = new UserCred();
        if(!offlineMode){
            if(userCred.load_Cred(context)) {
                networkMethods.Login(userCred.getEmail(), userCred.getpwd(),
                        new onLoginListener() {
                            @Override
                            public void onSuccess(UserInfo userInfo) {
                                UpdateUI(userInfo,false);
                                Toast.makeText(context, "Logged In!", Toast.LENGTH_SHORT).show();
                                userInfo.cacheUserDetails(context);
                            }

                            @Override
                            public void onFailure(Exception e) {
                                if(e!=null) {
                                    if (e.getMessage().contains("network")) {
                                        offlineMode = true;
                                        createSnackbar("Network Error. Retry login?", Snackbar.LENGTH_INDEFINITE, true, "Retry", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                @SuppressWarnings("deprecation")
                                                final ProgressDialog dialog = ProgressDialog.show(context, "Logging you in...", "", true, false);
                                                networkMethods.Login(userCred.getEmail(), userCred.getpwd(), new onLoginListener() {
                                                    @Override
                                                    public void onSuccess(UserInfo userInfo) {
                                                        offlineMode = false;
                                                        UpdateUI(userInfo, false);
                                                        userInfo.cacheUserDetails(context);
                                                        dialog.cancel();
                                                        Toast.makeText(context, "Logged In!", Toast.LENGTH_SHORT).show();
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
                                                            if(isCached) {
                                                                UserInfo.clearCache(context);
                                                                UpdateUIonLogout(false, false);
                                                            }
                                                        }
                                                    }
                                                });
                                            }
                                        });
                                    } else {
                                        createSnackbar("Session Expired. Please login again.");
                                        UserCred.clear_cred(context);
                                        if(isCached) {
                                            UserInfo.clearCache(context);
                                            UpdateUIonLogout(false, false);
                                        }
                                    }
                                }
                                else {
                                    createSnackbar("Please Login to continue", Snackbar.LENGTH_INDEFINITE, true, "Login", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            launchOtherFragment(new Frag_LoginPage(), LOGIN_PAGE_TAG, true);
                                        }
                                    });
                                }
                            }
                        });
            } else {

                if(isCached) {
                    UserInfo.clearCache(context);
                    Log.i(TAG, "setUpUser: problem");
                    UpdateUIonLogout(false, false);
                }

                createSnackbar("Please login to get started", Snackbar.LENGTH_INDEFINITE, true, "Login", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        launchOtherFragment(new Frag_LoginPage(),LOGIN_PAGE_TAG, true);
                    }
                });

            }
        }else {
            if(userCred.load_Cred(context))
            createSnackbar("No Network!", Snackbar.LENGTH_INDEFINITE, true, "Retry?", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   setUpUser();
                }
            });
        }

    }

    public void goOnline(final boolean prompt) {
        final UserCred userCred = new UserCred();
        if(userCred.load_Cred(context)) {
            networkMethods.Login(userCred.getEmail(), userCred.getpwd(),
                    new onLoginListener() {
                        @Override
                        public void onSuccess(UserInfo userInfo) {
                            offlineMode = false;
                            UpdateUI(userInfo,false);
                            Toast.makeText(context, "Logged In!", Toast.LENGTH_SHORT).show();
                            userInfo.cacheUserDetails(context);
                        }

                        @Override
                        public void onFailure(Exception e) {
                            if(e!=null) {
                                if (e.getMessage().contains("network")) {
                                    offlineMode = true;
                                    createSnackbar("Network Error. Retry login?", Snackbar.LENGTH_INDEFINITE, true, "Retry", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            final ProgressDialog dialog = ProgressDialog.show(context, "Logging you in...", "", true, false);
                                            networkMethods.Login(userCred.getEmail(), userCred.getpwd(), new onLoginListener() {
                                                @Override
                                                public void onSuccess(UserInfo userInfo) {
                                                    offlineMode = false;
                                                    UpdateUI(userInfo, false);
                                                    userInfo.cacheUserDetails(context);
                                                    dialog.cancel();
                                                    Toast.makeText(context, "Logged In!", Toast.LENGTH_SHORT).show();
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
                                                }
                                            });
                                        }
                                    });
                                } else {
                                    createSnackbar("Session Expired, please login again.");
                                    UserCred.clear_cred(context);
                                }
                            }
                            else {
                                if(prompt) {
                                    createSnackbar("Please Login to continue", Snackbar.LENGTH_INDEFINITE, true, "Login", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            launchOtherFragment(new Frag_LoginPage(), LOGIN_PAGE_TAG, true);
                                        }
                                    });
                                }
                            }
                        }
                    });
        } else if (prompt) {
            createSnackbar("Please Login to continue", Snackbar.LENGTH_INDEFINITE, true, "Login", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    launchOtherFragment(new Frag_LoginPage(), LOGIN_PAGE_TAG, true);
                }
            });
        }
    }

    private void initVariables() {
        appBarLayout = findViewById(R.id.appbarLayout);
        progressBar = findViewById(R.id.progressBarTop);
        coordinatorLayout = findViewById(R.id.coordinator_layout);
        fab = findViewById(R.id.fab);
        fragmentManager = getFragmentManager();
        context = this;
        cloudStorageMethods = new CloudStorageMethods(context);
        cloudStorageMethods.getCache();
        networkMethods = new NetworkMethods(context);
    }

    private void setUpToolbar() {

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.inflateMenu(R.menu.toolbar_menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) toolbar.getMenu().findItem(R.id.toolbar_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        search_icon = toolbar.getMenu().findItem(R.id.toolbar_search);
        search_icon.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {

            OnBackPressedListener listener;

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                if(backPressedListener!=null){
                    listener = backPressedListener;
                }
                backPressedListener = new OnBackPressedListener() {
                    @Override
                    public boolean doneSomething() {
                        if(search_icon.isActionViewExpanded()) {
                            search_icon.collapseActionView();
                            return true;
                        }
                        return false;
                    }
                };
                animateSearchToolbar(1, true, true);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                if(listener!=null)
                    backPressedListener = listener;
                ((Frag_Ads)(fragmentManager.findFragmentByTag(ALL_FRAG_TAG))).filter("");
                if (item.isActionViewExpanded())
                    animateSearchToolbar(1, false, false);
                return true;
            }
        });

    }

    private void setUpDrawer() {

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navDrawer_open, R.string.navDrawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationViewMenu = navigationView.getMenu();

        btn_login = navigationView.getHeaderView(0).findViewById(R.id.nav_btnLogin);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchOtherFragment(new Frag_LoginPage(), LOGIN_PAGE_TAG, true);
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
                        launchOtherFragment(new Frag_Main(), MAIN_SCREEN_TAG, true);
                        break;
                    case R.id.nav_manage:
                        launchOtherFragment(new Frag_Manage(), MANAGE_FRAG_TAG, true);
                        break;
                    case R.id.nav_profile:
                        if (userInfo != null) {
                            launchOtherFragment(new Frag_myProfile(), MY_PROFILE_TAG, true);
                        } else {
                            Toast.makeText(context, "Please login First", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.nav_newAccount:
                        launchOtherFragment(new Frag_newAccount(), NEW_ACCOUNT_TAG, true);
                        break;
                    case R.id.nav_myAds:
                        launchOtherFragment(new Frag_myAds(),MY_ADS_TAG, true);
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
//                  if(true){
                    //Todo:

                      if(userInfo!=null)
                         ((TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_email)).setText(userInfo.getEmail());

                      switch (adType) {

                        case TYPE_SELL:
                        case TYPE_LOSTFOUND:
                        case TYPE_TEACH:

                            Frag_newAd frag_newAd = new Frag_newAd();

                            Bundle args = new Bundle();
                            args.putString(Frag_Ads.ARGS_AdType,adType);

                            frag_newAd.setArguments(args);

                            launchOtherFragment(frag_newAd, NEW_AD_TAG, true);

                            break;

                        case TYPE_EVENT:
                            launchOtherFragment(new Frag_newEvent(), NEW_EVENT_TAG, true);
                            break;

                        case TYPE_INFO:

                      }
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        cloudStorageMethods.saveCache();
    }

    @Override
    protected void onResume() {
        offlineMode = !ConnectionDetector.isNetworkAvailable(context);
        super.onResume();
    }

    //endregion

    //region Movement

    public void launchOtherFragment(Fragment frag, String tag) {
        launchOtherFragment(frag, tag, false);
    }

    public void launchOtherFragment(Fragment frag, String tag, boolean addToStack) {

        FragmentTransaction transaction = fragmentManager.beginTransaction()
                .replace(R.id.frame_layout, frag, tag)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

        if(addToStack)
                transaction.addToBackStack(null);
        transaction.commit();

    }

    boolean twiceToExit = false;
    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if(toolbar.getMenu().findItem(R.id.toolbar_search).isActionViewExpanded()) {
            toolbar.getMenu().findItem(R.id.toolbar_search).collapseActionView();
        }
        else {

            boolean f = true;

//             region Viewing Ad
//            if(fragmentManager.findFragmentByTag(VIEW_AD_TAG)!=null)
//            {
//                if(fragmentManager.findFragmentByTag(VIEW_AD_TAG).isVisible()){
//                    f = false;
//                    if(onBackPressedListener!=null) {
//                        //Log.i(TAG, "onBackPressed: onBackPressedListener!=null");
//                        if (onBackPressedListener.doneSomething()) {
//                            //Log.i(TAG, "onBackPressed: Image already closed");
//                            super.onBackPressed();
//                        }
//                    }else
//                        super.onBackPressed();
//                }
//            }
//            if(fragmentManager.findFragmentByTag(VIEW_EVENT_TAG)!=null)
//            {
//                if(fragmentManager.findFragmentByTag(VIEW_EVENT_TAG).isVisible()){
//                    f = false;
//                    if(onBackPressedListener!=null) {
//                        //Log.i(TAG, "onBackPressed: onBackPressedListener!=null");
//                        if (onBackPressedListener.doneSomething()) {
//                            //Log.i(TAG, "onBackPressed: Image already closed");
//                            super.onBackPressed();
//                        }
//                    }else
//                        super.onBackPressed();
//                }
//            }
//            if(fragmentManager.findFragmentByTag(VIEW_MyAD_TAG)!=null)
//            {
//                if(fragmentManager.findFragmentByTag(VIEW_MyAD_TAG).isVisible()){
//                    f = false;
//                    if(onBackPressedListener!=null) {
//                        //Log.i(TAG, "onBackPressed: onBackPressedListener!=null");
//                        if (onBackPressedListener.doneSomething()) {
//                            //Log.i(TAG, "onBackPressed: Image already closed");
//                            super.onBackPressed();
//                        }
//                    }else
//                        super.onBackPressed();
//                }
//            }
//            if(fragmentManager.findFragmentByTag(VIEW_MyEVENT_TAG)!=null)
//            {
//                if(fragmentManager.findFragmentByTag(VIEW_MyEVENT_TAG).isVisible()){
//                    f = false;
//                    if(onBackPressedListener!=null) {
//                        //Log.i(TAG, "onBackPressed: onBackPressedListener!=null");
//                        if (onBackPressedListener.doneSomething()) {
//                            //Log.i(TAG, "onBackPressed: Image already closed");
//                            super.onBackPressed();
//                        }
//                    }else
//                        super.onBackPressed();
//                }
//            }
//

            if(backPressedListener !=null) {
                if (backPressedListener.doneSomething()) {
                    f = false;
                }
            }

            if(fragmentManager.findFragmentByTag(MAIN_SCREEN_TAG)!=null){
                if(fragmentManager.findFragmentByTag(MAIN_SCREEN_TAG).isVisible()) {
                    f = false;

                    if (twiceToExit) {
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

            if(f)
                super.onBackPressed();
        }
    }

    public interface OnBackPressedListener {
        boolean doneSomething();
    }

    //endregion

    //region Search Related

    @Override
    protected void onNewIntent(Intent intent) {
        if(Intent.ACTION_SEARCH.equals(intent.getAction())){
            String query = intent.getStringExtra(SearchManager.QUERY);
            if (fragmentManager.findFragmentByTag(ALL_FRAG_TAG) != null) {
                if(fragmentManager.findFragmentByTag(ALL_FRAG_TAG).isVisible())
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
                if(item.getItemId() == R.id.toolbar_refresh) {
                    if(fragmentManager.findFragmentByTag(ALL_FRAG_TAG)!=null)
                        ((Frag_Ads) fragmentManager.findFragmentByTag(ALL_FRAG_TAG)).refresh();
                }
                return true;
            }
        });
    }

    boolean recentlySentMail=false;
    public boolean checkVerification() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser==null) {
            createSnackbar("Please Login to continue", Snackbar.LENGTH_INDEFINITE,true , "Login", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    launchOtherFragment(new Frag_LoginPage(), LOGIN_PAGE_TAG, true);
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
    public void UpdateUI(UserInfo userInfo, Boolean redirect) {

        this.userInfo = userInfo;

        //Drawer
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_Name)).setText(userInfo.getName());
        String notVerified = " (Not Verified)";
        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
            FirebaseAuth.getInstance().getCurrentUser().reload();
            offlineMode = false;
            if (FirebaseAuth.getInstance().getCurrentUser().isEmailVerified())
                notVerified = "";
        }
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_email)).setText(userInfo.getEmail()+notVerified);
        final ImageView imageView = navigationView.getHeaderView(0).findViewById(R.id.nav_profileImg);
        if(userInfo.getHasProfileIMG()) {
            Log.i(TAG, "UpdateUI: hasImage");
            cloudStorageMethods.getProfileImage(userInfo.getuID(), new onCompleteListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    if(imageView!=null) {
                        imageView.setImageURI(uri);
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    //Log.e(TAG, "onFailure: getProfileImage ", e);
                    GlideApp.with(context).load(R.drawable.avatar).into(imageView);
//                    imageView.setImageResource(R.drawable.avatar);
                }
            });
        }
        else {
            GlideApp.with(context).load(R.drawable.avatar).into(imageView);
        }

        btn_login.setVisibility(View.GONE);

        navigationViewMenu.findItem(R.id.nav_myAds).setVisible(true);
        navigationViewMenu.findItem(R.id.nav_manage).setVisible(true);
        navigationViewMenu.findItem(R.id.nav_profile).setVisible(true);
        navigationViewMenu.findItem(R.id.nav_logout).setVisible(true);
        navigationViewMenu.findItem(R.id.nav_newAccount).setVisible(false);

        if(redirect) {
            clearBackStack();
            launchOtherFragment(new Frag_Main(), MAIN_SCREEN_TAG);
            createSnackbar("Logged in!");
        }
    }

    public void UpdateUIonLogout() {
        UpdateUIonLogout(true, true);
    }

    public void UpdateUIonLogout(boolean createSnack, boolean redirect) {

        this.userInfo = null;

        //Drawer
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_Name)).setText(R.string.sample_ID);
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_email)).setText(R.string.sample_email);
        ImageView imageView = navigationView.getHeaderView(0).findViewById(R.id.nav_profileImg);
        if(imageView.getDrawable() !=null) {
            GlideApp.with(context).load(R.drawable.avatar).into(imageView);
        }

        btn_login.setVisibility(View.VISIBLE);

        navigationViewMenu.findItem(R.id.nav_myAds).setVisible(false);
        navigationViewMenu.findItem(R.id.nav_manage).setVisible(false);
        navigationViewMenu.findItem(R.id.nav_profile).setVisible(false);
        navigationViewMenu.findItem(R.id.nav_logout).setVisible(false);
        navigationViewMenu.findItem(R.id.nav_newAccount).setVisible(true);

        if(redirect) {
            clearBackStack();
            launchOtherFragment(new Frag_Main(), MAIN_SCREEN_TAG);
        }

        if(createSnack)
            createSnackbar("Logged out!");
    }

    public void clearBackStack(){
        while (getFragmentManager().getBackStackEntryCount() > 0){
            getFragmentManager().popBackStackImmediate();
        }
    }

    public void createSnackbar(String msg) {
        createSnackbar(msg,Snackbar.LENGTH_SHORT, false);
    }

    public void createSnackbar(String msg, boolean removeOnTouch) {
        createSnackbar(msg,Snackbar.LENGTH_INDEFINITE, removeOnTouch);
    }

    public void createSnackbar(String msg, int length, boolean removeOnTouch) {
        createSnackbar(msg,length,removeOnTouch, null,null);
    }

    public void createSnackbar(String msg, int length, final boolean removeOnTouch, String actionTitle, View.OnClickListener listener) {
        currentSnackbar = Snackbar.make(coordinatorLayout, msg, length);
        currentSnackbar.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                super.onDismissed(transientBottomBar, event);
                specialSnackbar = false;
                currentSnackbar = null;
            }

            @Override
            public void onShown(Snackbar transientBottomBar) {
                specialSnackbar = removeOnTouch;
                super.onShown(transientBottomBar);
            }
        });

        if(actionTitle!=null){
            currentSnackbar.setAction(actionTitle,listener);
            currentSnackbar.setActionTextColor(getResources().getColor(R.color.colorSearch));
        }
        currentSnackbar.show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Log.i(TAG, "onTouchEvent: ");
        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if(specialSnackbar && currentSnackbar!=null) {
            if (ev.getAction() == MotionEvent.ACTION_UP) {
                if (currentSnackbar.isShown()) {

                    Rect sRect = new Rect();
                    currentSnackbar.getView().getHitRect(sRect);

                    //This way the snackbar will only be dismissed if
                    //the user clicks outside it.
                    if (!sRect.contains((int) ev.getX(), (int) ev.getY())) {
                        currentSnackbar.dismiss();
                        currentSnackbar = null;
                    }
                }
            }
        }

        return super.dispatchTouchEvent(ev);
    }

    //endregion

}