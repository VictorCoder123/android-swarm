package me.qishen.mockgps.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import me.qishen.mockgps.R;
import me.qishen.mockgps.utils.LogHelper;

/**
 * A simple {@link Fragment} subclass.
 * to handle interaction events.
 * Use the {@link MapRouteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapRouteFragment extends SupportMapFragment {

    private static final String TAG = LogHelper.makeLogTag(MapRouteFragment.class);
    private LocationManager locationManager;
    private MockGPSActivity mActivity;

    public GoogleMap mMap;
    // A reference to locations in parent activity
    public List<Location> locations;
    // Mapping from location to Marker on Google Map
    public Map<Location, Marker> markerMap = new HashMap<>();

    // Define a listener that responds to location updates
    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            //mActivity.makeToast(location.toString());
            updateLocationList(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {}
    };

    public MapRouteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MapRouteFragment.
     */
    public static MapRouteFragment newInstance() {
        MapRouteFragment fragment = new MapRouteFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Add new location to list and update it on Google Map.
     * Remove the first location if total size exceeds the maximum size limit.
     * @param location
     */
    public void updateLocationList(Location location){
        if(mMap != null){
            // Remove marker from both Google Map and Hash Map.
            if(locations.size() > 10) {
                Location oldLoc = locations.get(locations.size()-1);
                // Remove marker
                Marker oldMarker = markerMap.get(oldLoc);
                if(oldMarker != null) oldMarker.remove();
                markerMap.remove(location);
                locations.remove(locations.size()-1);
            }
            // Add new location into both Google Map and Hash Map with Marker.
            locations.add(0, location);
            LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
            Marker marker = mMap.addMarker(new MarkerOptions().position(latlng).title("Marker"));
            markerMap.put(location, marker);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mActivity = (MockGPSActivity) getActivity();
        //mMap = mActivity.mMap;
        locations = mActivity.locations;
        locationManager = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);

        // Getting the name of the provider that meets the criteria
        Criteria criteria = new Criteria();
        // Default Provider is set to "gps" in MockLocationService
        String provider = locationManager.getBestProvider(criteria, false);

        if(provider == null || provider.equals("")){
            mActivity.makeToast("No available Provider Found.");
        }else{
            String permission = Manifest.permission.ACCESS_FINE_LOCATION;
            if(ContextCompat.checkSelfPermission(mActivity, permission) == PackageManager.PERMISSION_GRANTED){
                // Get the last known location from the given provider
                Location location = locationManager.getLastKnownLocation(provider);

                locationManager.requestLocationUpdates(provider, 100, 0, locationListener);

                if(location != null) updateLocationList(location);
                else mActivity.makeToast("Location can't be retrieved.");
            }
            else {
                // Ask for location permission at runtime
                ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // Remove location listener after detachment
        String permission = Manifest.permission.ACCESS_FINE_LOCATION;
        if(ContextCompat.checkSelfPermission(getActivity(), permission) == PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates(locationListener);
        }
    }

}
