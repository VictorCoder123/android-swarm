package me.qishen.mockgps.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import me.qishen.mockgps.R;
import me.qishen.mockgps.service.MockLocationService;
import me.qishen.mockgps.utils.LogHelper;
import me.qishen.mockgps.activity.MapRouteFragment;
import me.qishen.mockgps.activity.SettingFragment.OnSettingFragmentInteractionListener;
import me.qishen.mockgps.activity.LocationListFragment.OnLLFragmentInteractionListener;

public class MockGPSActivity extends ToolbarDrawerActivity
        implements OnMapReadyCallback, OnSettingFragmentInteractionListener,
        OnLLFragmentInteractionListener, ActivityCompat.OnRequestPermissionsResultCallback{

    private static final String TAG = LogHelper.makeLogTag(MockGPSActivity.class);

    public GoogleMap mMap;
    public MapRouteFragment mapFragment;
    public List<Location> locations = new CopyOnWriteArrayList<>();

    private NavigationView.OnNavigationItemSelectedListener mNavigationListener =
            new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem menuItem) {
            menuItem.setChecked(true);
            int id = menuItem.getItemId();
            if (id == R.id.nav_map) {
                mapFragment = MapRouteFragment.newInstance();
                mapFragment.getMapAsync(MockGPSActivity.this);
                switchFragment(mapFragment);
            } else if (id == R.id.nav_tools) {
                SettingFragment settingFragment = SettingFragment.newInstance("Hello", "World");
                switchFragment(settingFragment);
            } else if (id == R.id.nav_show) {
                LocationListFragment locationListFragment = LocationListFragment.newInstance();
                switchFragment(locationListFragment);
            }
            // Close drawer after switching fragment.
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
    };

    /**
     * Replace old fragment in content container with new fragment from params.
     * @param fragment
     */
    public void switchFragment(Fragment fragment){
        // Use FragmentManager to append main fragment on specified container
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_mock_gps, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onSettingFragmentInteraction(Uri uri){
        return;
    }

    @Override
    public void onLLFragmentInteraction(Uri uri){

    }

    /**
     * Display debug information in Toast with short length and bind
     * context with current activity.
     * @param str String to be displayed in Toast.
     */
    public void makeToast(String str){
        Toast.makeText(this.getApplicationContext(), str, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mock_gps);
        initializeToolbar();

        // Start mock location service
        Intent intent = new Intent(this, MockLocationService.class);
        intent.setAction(MockLocationService.START_MOCK_CMD);
        startService(intent);
        LogHelper.i(TAG, "Start MockLocation Service.");

        // Default map fragment to be rendered in main content container.
        mapFragment = MapRouteFragment.newInstance();
        mapFragment.getMapAsync(this);
        switchFragment(mapFragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mock_gps, menu);
        return true;
    }

    @Override
    protected void populateDrawerItems(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(mNavigationListener);
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

    @Override
    public void onMapReady(GoogleMap googleMap){
        mMap = googleMap;
        mapFragment.mMap = googleMap;

        // Display all existing locations on Google Map by calling method in MapRouteFragment
        for(Location location : locations){
            mapFragment.updateLocationList(location);
        }

        // Add listener on click event on Google Map.
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Toast.makeText(getApplicationContext(), latLng.toString(), Toast.LENGTH_SHORT).show();
                LogHelper.i(TAG, latLng.toString());
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 200: {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LogHelper.i(TAG, "Location Permission Granted.");
                }
            }
        }
    }
}
