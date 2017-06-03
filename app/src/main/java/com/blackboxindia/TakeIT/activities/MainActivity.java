package com.blackboxindia.TakeIT.activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.blackboxindia.TakeIT.Fragments.frag_newAccount;
import com.blackboxindia.TakeIT.Fragments.frag_allAds;
import com.blackboxindia.TakeIT.Fragments.frag_myProfile;
import com.blackboxindia.TakeIT.R;

public class MainActivity extends AppCompatActivity {

    AppBarLayout appBarLayout;
    LinearLayout linearLayout;
    FragmentManager fragmentManager;
    Toolbar toolbar;
    DrawerLayout drawer;

    /////////// Login Page Variables /////////////////
    TextInputLayout inputLayoutID, inputLayoutPassword;
    EditText etID, etPassword;
    //////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpToolbar();

        setUpDrawer();

        linearLayout = (LinearLayout) findViewById(R.id.appbar_extra);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbarLayout);
        fragmentManager = getFragmentManager();

        setUpFragment();

        setUpRecyclerView();

        setUpFab();

    }

    private void setUpFragment() {
        linearLayout.setVisibility(View.VISIBLE);
        frag_allAds mc = new frag_allAds();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, mc);
        fragmentTransaction.commit();
    }

    private void setUpFab() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                linearLayout.setVisibility(View.GONE);
                frag_newAccount fragnewAccount = new frag_newAccount();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, fragnewAccount);
                fragmentTransaction.commit();
            }
        });
    }

    private void setUpDrawer() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle navigation view item clicks here.
                int id = item.getItemId();

                if (id == R.id.nav_allAds) {
                    // Handle the camera action
                    Toast.makeText(MainActivity.this, "Hello", Toast.LENGTH_SHORT).show();
                    setUpFragment();
                }
                else if (id == R.id.nav_manage) {

                }
                else if (id == R.id.nav_profile) {
                    linearLayout.setVisibility(View.GONE);
                    frag_myProfile profile = new frag_myProfile();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout,profile);
                    fragmentTransaction.commit();
                }

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    private void setUpToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.app_name));
    }

    private void setUpRecyclerView() {

    }


////////////////////////////////// Login Page Related //////////////////////////////////////////////

    public void setUpLoginPage() {
        inputLayoutID = (TextInputLayout) findViewById(R.id.login_IDLayout);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.login_layoutPassword);

        etID = (EditText) findViewById(R.id.login_etID);
        etPassword = (EditText) findViewById(R.id.login_etPassword);
    }

    public void validateAndLogin(View view) {
        String id = etID.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        if(isIDValid(id) && isPasswordValid(password))
        {
            //Todo: Login
        }
    }

    private boolean isPasswordValid(String password) {
        if(password.length()<8)
        {
            inputLayoutPassword.setError("Minimum 8 characters required.");
            return false;
        }
        else if (password.contains("\"") || password.contains("\\") || password.contains("\'") || password.contains(";"))
        {
            inputLayoutPassword.setError("Password can\'t contain \", \\, \', or ;");
            return false;
        }
        else
            return true;
    }

    private boolean isIDValid(String id) {
        if (id.length() < 4)
        {
            inputLayoutPassword.setError("Minimum 4 characters required.");
            return false;
        }
        else if (id.contains("\"") || id.contains("\\") || id.contains("\'") || id.contains(";"))
        {
            inputLayoutID.setError("ID can\'t contain \", \\, \', or ;");
            return false;
        }
        else if(!id.contains("@"))
        {
            inputLayoutID.setError("Not a valid email format.");
            return false;
        }
        else
        {
            return true;
        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void addImage(View view) {
        Toast.makeText(this, "Heleoeo", Toast.LENGTH_SHORT).show();
    }
}
