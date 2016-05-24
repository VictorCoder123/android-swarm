package me.qishen.mockgps.thread;

import java.util.Date;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import me.qishen.mockgps.service.MockLocationService;

public class UpdateGPSThread extends Thread {
    private long timeInterval = 1000;
    private boolean stopped = false;
    private LocationManager locationManager;
    private MockLocationService mService;
    private Location currentLocation = null;

    public UpdateGPSThread(Context context, MockLocationService service) {
        mService = service;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationManager.addTestProvider("gps", false, false, false, false, false, true, true, 1, 1);
        locationManager.setTestProviderEnabled("gps", true);
    }

    @Override
    public void run() {
        while(!stopped) {
            // Create random location
            double latitude = (Math.random() - 0.5) * 180;
            double longitude = (Math.random() - 0.5) * 360;
            long date = new Date().getTime();
            currentLocation = new Location("gps");
            currentLocation.setLongitude(longitude);
            currentLocation.setLatitude(latitude);
            currentLocation.setAccuracy((float) Math.random());
            currentLocation.setTime(date);
            currentLocation.setElapsedRealtimeNanos(date);

            // Update location and send notification
            locationManager.setTestProviderLocation("gps", currentLocation);
            mService.showNotification(currentLocation.toString());

            // Pause for one second
            try{
                Thread.sleep(timeInterval);
            } catch(Exception e){
                stopMock();
            }
        }

        mService.showNotification("Location mocking is stopped.");
        locationManager.setTestProviderEnabled("gps", false);
        locationManager.removeTestProvider("gps");
    }

    public void stopMock() {
        stopped = true;
    }
}
