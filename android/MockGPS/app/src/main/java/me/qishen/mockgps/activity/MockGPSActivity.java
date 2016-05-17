package me.qishen.mockgps.activity;

import android.content.Context;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import com.google.android.gms.maps.model.MarkerOptions;


import me.qishen.mockgps.R;
import me.qishen.mockgps.utils.LogHelper;
import me.qishen.mockgps.activity.SettingFragment.OnFragmentInteractionListener;

public class MockGPSActivity extends ToolbarDrawerActivity
        implements OnMapReadyCallback, OnFragmentInteractionListener{

    private static final String TAG = LogHelper.makeLogTag(MockGPSActivity.class);
    LocationManager locationManager;
    GoogleMap mMap;

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

            }
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
    public void onFragmentInteraction(Uri uri){
        return;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mock_gps);
        initializeToolbar();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

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

        // Add a marker in Sydney and move the camera for testing.
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Toast.makeText(getApplicationContext(), latLng.toString(), Toast.LENGTH_SHORT).show();
                LogHelper.i(TAG, latLng.toString());
            }
        });
    }
}
