package com.find.me.ui.dashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.find.me.R;
import com.find.me.fragments.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.internal.NavigationMenuView;
import com.google.android.material.navigation.NavigationView;

import com.find.me.adapter.ViewPagerAdapter;
import com.find.me.fragments.ContactsFragment;
import com.find.me.fragments.ProfileFragment;
import com.find.me.service.LocationService;
import com.find.me.utils.Preferences;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    BottomNavigationView bottomNavigationView;
    MenuItem prevMenuItem;
    ImageView btntogle;

    //This is our viewPager
    private ViewPager viewPager;
    //Fragments
    ContactsFragment contactsFragment;
    HomeFragment homeFragment;
    ProfileFragment profileFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ImageView btnDrawerToggle = (ImageView) findViewById(R.id.btnToggle);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        NavigationMenuView navMenuView = (NavigationMenuView) navigationView.getChildAt(0);
        navMenuView.addItemDecoration(new DividerItemDecoration(MainActivity.this, DividerItemDecoration.VERTICAL));
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);
        Intent serviceIntent = new Intent(MainActivity.this, LocationService.class);
        startService(serviceIntent);
        btntogle = findViewById(R.id.btnToggle);
        btntogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.LEFT);

            }
        });
        //Initializing the bottomNavigationView
        bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottom_navigation);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_home:
                                viewPager.setCurrentItem(0);
                                break;

                            case R.id.action_contacts:
                                viewPager.setCurrentItem(1);
                                break;
                            case R.id.action_Profile:
                                viewPager.setCurrentItem(2);
                                break;
                        }
                        return false;
                    }
                });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                }
                else
                {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                Log.d("page", "onPageSelected: "+position);
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigationView.getMenu().getItem(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setupViewPager(viewPager);
    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        homeFragment =new HomeFragment();
        contactsFragment=new ContactsFragment();
        profileFragment=new ProfileFragment();
        adapter.addFragment(homeFragment);
        adapter.addFragment(contactsFragment);
        adapter.addFragment(profileFragment);
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END);
        } else {
            finish();
            System.exit(0);
        }
    }
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }

   /*     if (id == R.id.nav_privacy) {
            Intent intent = new Intent(this, PolicyPage.class);
            startActivity(intent);
        }*/
/*        if (id == R.id.nav_settings) {
  *//*          Intent intent = new Intent(this, Settings.class);
            startActivity(intent);*//*
        }*/
        if (id == R.id.nav_signout) {
            Preferences.writeString(this,"username","dkajbfbv");
            Preferences.writeString(this,"password","masbfmansbva");
            finish();

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}