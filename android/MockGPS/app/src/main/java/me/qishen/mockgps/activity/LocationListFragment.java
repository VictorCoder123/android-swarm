package me.qishen.mockgps.activity;

import java.util.Date;
import java.util.Queue;
import java.util.List;
import java.util.LinkedList;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ImageView;
import android.widget.TextView;

import me.qishen.mockgps.R;
import me.qishen.mockgps.utils.LogHelper;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LocationListFragment.OnLLFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LocationListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocationListFragment extends ListFragment {
    private OnLLFragmentInteractionListener mListener;
    private LocationManager locationManager;
    private MultiLineLocationArrayAdapter adapter;
    private List<Location> locations = new LinkedList<>();

    public LocationListFragment() {
        // Required empty public constructor
    }

    // Define a listener that responds to location updates
    LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            // Called when a new location is found by the network location provider.
            makeToast(location.toString());
            updateLocationList(location);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {}

        public void onProviderEnabled(String provider) {}

        public void onProviderDisabled(String provider) {}
    };


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LocationListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LocationListFragment newInstance() {
        LocationListFragment fragment = new LocationListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Display debug information in Toast with short length and bind
     * context with current activity.
     * @param str String to be displayed in Toast.
     */
    public void makeToast(String str){
        Toast.makeText(getActivity().getApplicationContext(), str, Toast.LENGTH_SHORT).show();
    }

    /**
     * Add new location to list and notify its adapter to update
     * in corresponding ListView.
     * @param location
     */
    public void updateLocationList(Location location){
        locations.add(location);
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up ListView Adapter for location.
        adapter = new MultiLineLocationArrayAdapter(getActivity(), locations);
        setListAdapter(adapter);

        locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);

        // Getting the name of the provider that meets the criteria
        Criteria criteria = new Criteria();
        // Default Provider is set to "gps" in MockLocationService
        String provider = locationManager.getBestProvider(criteria, false);

        if(provider == null || provider.equals("")){
            makeToast("No available Provider Found.");
        }else{
            String permission = Manifest.permission.ACCESS_FINE_LOCATION;
            if(ContextCompat.checkSelfPermission(getActivity(), permission) == PackageManager.PERMISSION_GRANTED){
                // Get the last known location from the given provider
                Location location = locationManager.getLastKnownLocation(provider);

                locationManager.requestLocationUpdates(provider, 5000, 0, locationListener);

                if(location != null) updateLocationList(location);
                else makeToast("Location can't be retrieved.");
            }
            else {
                // Ask for location permission at runtime
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_location_list, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onLLFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLLFragmentInteractionListener) {
            mListener = (OnLLFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        // Remove location listener after detachment
        String permission = Manifest.permission.ACCESS_FINE_LOCATION;
        if(ContextCompat.checkSelfPermission(getActivity(), permission) == PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates(locationListener);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        String item = getListAdapter().getItem(position).toString();
        Toast.makeText(getActivity().getApplicationContext(),
                item + " selected", Toast.LENGTH_LONG).show();
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnLLFragmentInteractionListener {
        // TODO: Update argument type and name
        void onLLFragmentInteraction(Uri uri);
    }


    public class MultiLineLocationArrayAdapter extends ArrayAdapter<Location> {
        private final Context context;
        private final List<Location> locations;

        public MultiLineLocationArrayAdapter(Context context, List<Location> list) {
            super(context, -1, list);
            this.context = context;
            this.locations = list;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.row_layout, parent, false);
            TextView firstLineView = (TextView) rowView.findViewById(R.id.firstLine);
            TextView secondLineView = (TextView) rowView.findViewById(R.id.secondLine);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);

            imageView.setImageResource(R.mipmap.ic_launcher);
            // Show location information on first line.
            firstLineView.setText(locations.get(position).toString());
            // Show obtained time of current location.
            Date date = new Date(locations.get(position).getTime());
            secondLineView.setText(date.toString());

            return rowView;
        }
    }

}
