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
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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

    public LinearLayout linearLayout;
    Context context;
    AppBarLayout appBarLayout;
    FragmentManager fragmentManager;
    Toolbar toolbar;
    CollapsingToolbarLayout cTLayout;
    DrawerLayout drawer;
    FloatingActionButton fab;
    //View headerView;

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
                launchOtherFragment(new frag_loginPage(), "LOGIN_PAGE");
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
                            launchOtherFragment(new frag_myProfile(), "MY_PROFILE");
                            //Toast.makeText(context, "Please login First", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.nav_newAccount:
                        launchOtherFragment(new frag_newAccount(), "NEW_ACCOUNT");
                        break;
                }
                return true;
            }
        });
    }

    private void setUpMainFragment() {

        Log.i("YOYO", "setUpMainFragment ");
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

        fab.setVisibility(View.VISIBLE);

        linearLayout.setVisibility(View.VISIBLE);
        AppBarLayout.LayoutParams params =
                (AppBarLayout.LayoutParams) cTLayout.getLayoutParams();
        params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
        cTLayout.setLayoutParams(params);

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

        if (fragmentManager.findFragmentByTag("MAIN_FRAG").isVisible()) {
            fragmentManager.beginTransaction()
                    .hide(fragmentManager.findFragmentByTag("MAIN_FRAG"))
                    .commit();


            fragmentManager.beginTransaction()
                    .add(R.id.frame_layout, frag, tag)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                    .commit();

        } else {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.frame_layout, frag);
            transaction.commit();
        }
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

    public void hideSearchBar(){
        fab.setVisibility(View.GONE);

        linearLayout.setVisibility(View.GONE);
        appBarLayout.setExpanded(false, true);

        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) cTLayout.getLayoutParams();
        params.setScrollFlags(0);
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

    //This works apparently
    public void addImage(View view) {
        Toast.makeText(this, "Heleoeo", Toast.LENGTH_SHORT).show();
    }
}
