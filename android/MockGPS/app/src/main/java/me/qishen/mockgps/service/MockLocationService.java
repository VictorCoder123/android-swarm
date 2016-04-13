package me.qishen.mockgps.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Binder;
import android.widget.Toast;
import android.util.Log;

import me.qishen.mockgps.R;
import me.qishen.mockgps.thread.UpdateGPSThread;

public class MockLocationService extends Service {

    public static final String START_MOCK_CMD = "start_mock_cmd";
    public static final String STOP_MOCK_CMD = "stop_mock_cmd";
    private int NOTIFICATION = R.string.local_service_started;
    private static final String TAG = "MockLocationService";

    private NotificationManager mNM;
    private UpdateGPSThread mockThread = null;

    public MockLocationService() {

    }

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
        public MockLocationService getService() {
            return MockLocationService.this;
        }
    }

    private final IBinder mBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate(){
        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        showNotification("Start mocking location.");
    }

    @Override
    public void onDestroy(){
        // Cancel persistent notification
        mNM.cancel(NOTIFICATION);
        // Tell user we are stopped
        Toast.makeText(this, R.string.local_service_stopped, Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        if(intent == null || intent.getAction() == null) return START_STICKY;
        else if(intent.getAction().equals(START_MOCK_CMD)){
            Log.d(TAG, "Start command");
            startMock();
        }
        else if(intent.getAction().equals(STOP_MOCK_CMD)){
            Log.d(TAG, "Stop command");
            stopMock();
        }

        return START_STICKY;
    }

    public boolean isMocking() {
        return mockThread!=null && mockThread.isAlive();
    }

    private void startMock() {
        stopMock();
        mockThread = new UpdateGPSThread(getApplicationContext(), this);
        mockThread.start();
    }

    private void stopMock() {
        if (null != mockThread && mockThread.isAlive()) {
            mockThread.stopMock();
            mockThread.interrupt();
        }
        mockThread = null;
    }

    public void showNotification(String message) {
        CharSequence title = getText(NOTIFICATION);
        CharSequence content = message;
        Notification notification = new Notification.Builder(this)
                .setTicker(title)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(title)
                .setContentText(message)
                .build();
        mNM.notify(NOTIFICATION, notification);
    }
}
