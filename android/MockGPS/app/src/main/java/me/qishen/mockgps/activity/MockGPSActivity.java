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

import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import me.qishen.mockgps.R;
import me.qishen.mockgps.service.MockLocationService;
import me.qishen.mockgps.utils.LogHelper;
import me.qishen.mockgps.activity.SettingFragment.OnSettingFragmentInteractionListener;
import me.qishen.mockgps.activity.LocationListFragment.OnLLFragmentInteractionListener;

public class MockGPSActivity extends ToolbarDrawerActivity
        implements OnMapReadyCallback, OnSettingFragmentInteractionListener,
        OnLLFragmentInteractionListener, ActivityCompat.OnRequestPermissionsResultCallback{

    private static final String TAG = LogHelper.makeLogTag(MockGPSActivity.class);
    private LocationManager locationManager;
    private GoogleMap mMap;
    public List<Location> locations = new LinkedList<>();
    public Map<Location, Marker> markerMap = new HashMap<>();

    private NavigationView.OnNavigationItemSelectedListener mNavigationListener =
            new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem menuItem) {
            menuItem.setChecked(true);
            int id = menuItem.getItemId();
            if (id == R.id.nav_map) {
                SupportMapFragment mapFragment = SupportMapFragment.newInstance();
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

    // Define a listener that responds to location updates
    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            updateLocationList(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {}
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

    /**
     * Add new location to list and update it on Google Map.
     * Remove the first location if total size exceeds the maximum
     * size limit.
     * @param location
     */
    public void updateLocationList(Location location){
        if(mMap != null){
            // Remove marker from both Google Map and hash map.
            if(locations.size() > 10) {
                Location oldLoc = locations.get(locations.size()-1);
                Marker oldMarker = markerMap.get(oldLoc);
                if(oldMarker != null) oldMarker.remove();
                markerMap.remove(location);
                locations.remove(locations.size()-1);
            }
            locations.add(0, location);
            LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
            Marker marker = mMap.addMarker(new MarkerOptions().position(latlng).title("Marker"));
            markerMap.put(location, marker);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mock_gps);
        initializeToolbar();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Getting the name of the provider that meets the criteria
        Criteria criteria = new Criteria();
        // Default Provider is set to "gps" in MockLocationService
        String provider = locationManager.getBestProvider(criteria, false);

        if(provider == null || provider.equals("")){
            makeToast("No available Provider Found.");
        }else{
            String permission = Manifest.permission.ACCESS_FINE_LOCATION;
            if(ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED){
                // Get the last known location from the given provider
                Location location = locationManager.getLastKnownLocation(provider);

                locationManager.requestLocationUpdates(provider, 100, 0, locationListener);

                if(location != null) updateLocationList(location);
                else makeToast("Location can't be retrieved.");
            }
            else {
                // Ask for location permission at runtime
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
            }
        }

        // Start mock location service
        Intent intent = new Intent(this, MockLocationService.class);
        intent.setAction(MockLocationService.START_MOCK_CMD);
        startService(intent);
        LogHelper.i(TAG, "Start MockLocation Service.");

        // Default map fragment to be rendered in main content container.
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
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

        // Display all existing locations on Google Map.
        for(Location location : locations){
            updateLocationList(location);
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
