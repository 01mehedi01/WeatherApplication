package com.example.zakir.weather;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.support.v7.widget.SearchView;

public class MainActivity extends AppCompatActivity {
    public static final String BASE_URL = "http://api.openweathermap.org/data/2.5/";
    public static double latitude, longitude;
    public static String units = "metric";
    public static String tempSign = "°C";

    private CurrentWeatherResponse currentWeatherResponse;

    private CurrentWeatherService service;
    private FusedLocationProviderClient client;
    private LocationCallback callback;
    private LocationRequest request;
    private Geocoder giocoder;
    private List<Address> addresses;

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private TabPagerAdapter tabPagerAdapter;
    private Calendar calendar;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calendar = Calendar.getInstance();
        giocoder = new Geocoder(this);

        Intent intent = getIntent();
        if(Intent.ACTION_SEARCH.equals(intent.getAction()) && !Intent.ACTION_SEARCH.equals(null)){
            String query = intent.getStringExtra(SearchManager.QUERY);

            try {
                List<Address> myLoc = giocoder.getFromLocationName(query, 1);
                latitude = myLoc.get(0).getLatitude();
                longitude = myLoc.get(0).getLongitude();
            } catch (IOException e) {
                e.printStackTrace();
            }

            SearchRecentSuggestions searchRecentSuggestions = new SearchRecentSuggestions(this,SearchSuggestions.AUTHORITY,SearchSuggestions.MODE);
            searchRecentSuggestions.saveRecentQuery(query,null);
        }else {
            ////////////////////////////////////////// Location /////////////////////////////////////////////////
            client = LocationServices.getFusedLocationProviderClient(this);
            callback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    for (Location location : locationResult.getLocations()) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    try {
                        addresses = giocoder.getFromLocation(latitude,longitude,1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    }
                }
            };
            createLocationRequest();
        }
        ////////////////////////////////////////////////// Tab Layout //////////////////////////////////////////
        mViewPager = findViewById(R.id.mViewPager);
        mTabLayout = findViewById(R.id.tabLayout);

        mTabLayout.addTab(mTabLayout.newTab().setText("current weather").setIcon(R.drawable.icon));
        mTabLayout.addTab(mTabLayout.newTab().setText("forecust weather").setIcon(R.drawable.icon));

        tabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager(),mTabLayout.getTabCount());
        mViewPager.setAdapter(tabPagerAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
    ////////////////////////////////// End onCreate Method ///////////////////////////////////////////

    /////////////////////////////////// MenuItem Override Method ////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        SearchManager manager = (SearchManager) getSystemService(SEARCH_SERVICE);
        android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.celsius).setChecked(true);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.celsius:
                units = "metric";
                tempSign = "°C";
                finish();
                startActivity(getIntent());
                break;

            case R.id.fahrenheit:
                units = "imperial";
                tempSign = "°F";
                finish();
                startActivity(getIntent());
                break;
            case R.id.exit:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("Exit Application?");
                alertDialogBuilder
                        .setMessage("Do you want to exit!")
                        .setCancelable(false)
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        moveTaskToBack(true);
                                        android.os.Process.killProcess(android.os.Process.myPid());
                                        System.exit(1);
                                    }
                                })

                        .setNegativeButton("No",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.cancel();
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();


        }

        return super.onOptionsItemSelected(item);
    }

    ////////////////////////////////// Inner class for pageAdapter ///////////////////////////////////
    public class TabPagerAdapter extends FragmentPagerAdapter {
        private int tabCount;

        public TabPagerAdapter(FragmentManager fm, int tabCount) {
            super(fm);
            this.tabCount = tabCount;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return new TabOneFragment();
                case 1:
                    return new TabTwoFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return tabCount;
        }

    }
    //////////////////////////////////////////////// Method for Location Request ///////////////////////////////////////////////
    private void createLocationRequest() {
        request = new LocationRequest()
                .setInterval(5000)
                .setFastestInterval(2500)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},1);
            return;
        }
        client.requestLocationUpdates(request, callback, null);
    }
}
