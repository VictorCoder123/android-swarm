package me.qishen.mockgps.activity;

import android.app.Activity;
import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;

import me.qishen.mockgps.BuildConfig;
import me.qishen.mockgps.R;
import me.qishen.mockgps.service.MockLocationService;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final String TAG = "MainFragment";

    MockLocationService mService;
    boolean mBound;

    OnFragmentInteractionListener mListener;
    TextView textView = null;
    Context mContext = null;
    LocationManager locationManager;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MockLocationService.LocalBinder binder = (MockLocationService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(Activity activity) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.mContext =  activity;
        fragment.locationManager = (LocationManager) fragment.mContext.getSystemService(Context.LOCATION_SERVICE);
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Start MockLocationService only if Location Mock is enabled.
        if(!isMockLocationEnabled()) {
            textView.setText("Mock location is disabled.");
        }
        else {
            Intent intent = new Intent(mContext, MockLocationService.class);
            intent.setAction(MockLocationService.START_MOCK_CMD);
            mContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            //Log.d(TAG, "Service is started :)");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mContext.unbindService(mConnection);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        textView = (TextView) view.findViewById(R.id.textView_result);

        Button button = (Button) view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int res = mContext.checkCallingOrSelfPermission("android.permission.ACCESS_COARSE_LOCATION");
                Location loc = locationManager.getLastKnownLocation("gps");
                if (res == PackageManager.PERMISSION_GRANTED && loc != null) {
                    textView.setText(loc.toString());
                }
                else{
                    textView.setText("No location information available now.");
                }
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /*@Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }*/

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    /**
     * Check if location mocking is enabled in application level
     * @return boolean
     */
    public boolean isMockLocationEnabled() {
        boolean isMockLocation = false;
        try {
            //if marshmallow
            if(Build.VERSION.SDK_INT >= 23) {
                AppOpsManager opsManager = (AppOpsManager) mContext.getSystemService(Context.APP_OPS_SERVICE);
                isMockLocation = (opsManager.checkOp("android:mock_location",
                        android.os.Process.myUid(), BuildConfig.APPLICATION_ID) == AppOpsManager.MODE_ALLOWED);
            }
            else {
                // in marshmallow this will always return true
                isMockLocation = !android.provider.Settings.Secure.getString(mContext.getContentResolver(),
                        "mock_location").equals("0");
            }
        }
        catch (Exception e) {
            return isMockLocation;
        }
        return isMockLocation;
    }
}
