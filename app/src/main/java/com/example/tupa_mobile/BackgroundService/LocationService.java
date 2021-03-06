package com.example.tupa_mobile.BackgroundService;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.tupa_mobile.Activities.MainActivity;
import com.example.tupa_mobile.Connections.Connection;
import com.example.tupa_mobile.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;

import static com.example.tupa_mobile.Fragments.MapFragment.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;

public class LocationService extends Service{

    private int mInterval = 5000; // 5 seconds by default, can be changed later
    private Handler mHandler;

    private static final int MAIN_ID = 1;
    private static final String MAIN_CHANNEL_ID = "Channel_Id";
    private static final String MAIN_CHANNEL_NAME = "Channel_Name";
    private static final String MAIN_CHANNEL_DESC = "Channel_Desc";

    private static final int DYNAMIC_ID = 2;
    private static final String DYNAMIC_CHANNEL_ID = "DYNAMIC_Id";
    private static final String DYNAMIC_CHANNEL_NAME = "DYNAMIC_Name";
    private static final String DYNAMIC_CHANNEL_DESC = "DYNAMIC_Desc";

    private static final int ALERT_ID = 3;
    private static final String ALERT_CHANNEL_ID = "ALERT_Id";
    private static final String ALERT_CHANNEL_NAME = "ALERT_Name";
    private static final String ALERT_CHANNEL_DESC = "ALERT_Desc";

    private static final String TAG = LocationService.class.getSimpleName();
    private FusedLocationProviderClient fusedLocationProviderClient;
    private boolean locationPermissionGranted;

    private NotificationManager notificationManager;
    private boolean isRiskNotificationActive = false;
    private boolean isAlertNotificationActive = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Do service here
        mHandler = new Handler();
        startForeground();
        startRepeatingTask();

        return super.onStartCommand(intent, flags, startId);
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                //compare location
                getLocationPermission();
                try {
                    if (locationPermissionGranted) {
                        Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                        locationResult.addOnSuccessListener(new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if(location == null){
                                    return;
                                }
                                checkRiskArea(location);
                                checkAlertArea(location);
                            }
                        });
                    }
                } catch (SecurityException e)  {
                    Log.e("Exception: %s", e.getMessage(), e);
                }

                //this function can change value of mInterval.
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };

    private void checkAlertArea(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        Connection connection = new Connection();
        connection.getAlertsList(getBaseContext(), 2021, 9, 21, longitude, latitude, ALERT_ID, ALERT_CHANNEL_ID, notificationManager);

        if (getInsideFlood(getBaseContext(), "CONTAINS")) {
            if (!getInsideFlood(getBaseContext(), "NOTIFICATION")){
                NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext(), ALERT_CHANNEL_ID)
                        .setSmallIcon(R.drawable.nibolas)
                        .setContentTitle("Cuidado!")
                        .setContentText("Essa ??rea est?? alagada!")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText("A avenida pipipi popopo est?? na lista de ??reas com risco de alagamento com base na previs??o de hoje."))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                notificationManager.notify(ALERT_ID, builder.build());
                Log.e(TAG, "??rea Alagada");
                saveInsideFlood(getBaseContext(), "NOTIFICATION", true);
            }
        }
        if (!getInsideFlood(getBaseContext(), "CONTAINS")){
            saveInsideFlood(getBaseContext(), "NOTIFICATION", false);
        }
    }

    private void checkRiskArea(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        ArrayList<LatLng> latLngs = new ArrayList<>();
        latLngs.add(new LatLng(-23.61667113533345, -46.648964070576255));
        latLngs.add(new LatLng(-23.613538121785556, -46.63819563711089));
        latLngs.add(new LatLng(-23.61926633610226, -46.63926737693445));
        latLngs.add(new LatLng(-23.620482088151544, -46.64738197845575));

        if(PolyUtil.containsLocation(new LatLng(latitude, longitude), latLngs, true)){
                //send notification
            if (!isRiskNotificationActive) {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext(), DYNAMIC_CHANNEL_ID)
                        .setSmallIcon(R.drawable.nibolas)
                        .setContentTitle("Cuidado!")
                        .setContentText("Voc?? est?? entrando numa ??rea de risco de alagamento!")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText("A avenida pipipi popopo est?? na lista de ??reas com risco de alagamento com base na previs??o de hoje."))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                notificationManager.notify(DYNAMIC_ID, builder.build());
                Log.e(TAG, "Voc?? est?? dentro da ??rea");
                isRiskNotificationActive = true;
            }
        }
        else {
            isRiskNotificationActive = false;
        }
    }



    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions((Activity) getApplicationContext(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    void startRepeatingTask() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getBaseContext());
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }

    private void startForeground() {
        Intent notificationIntent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        createNotificationChannels();

        startForeground(MAIN_ID, new NotificationCompat.Builder(this,
                MAIN_CHANNEL_ID) // don't forget create a notification channel first
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Service is running background")
                .setContentIntent(pendingIntent)
                .build());
    }

    private void createNotificationChannels() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //Main notification channel
            NotificationChannel channel = new NotificationChannel(MAIN_CHANNEL_ID, MAIN_CHANNEL_NAME, NotificationManager.IMPORTANCE_MIN);
            channel.setDescription(MAIN_CHANNEL_DESC);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            //Secondary channel
            NotificationChannel channel1 = new NotificationChannel(DYNAMIC_CHANNEL_ID, DYNAMIC_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription(DYNAMIC_CHANNEL_DESC);
            notificationManager.createNotificationChannel(channel1);

            //Alert channel
            NotificationChannel channel2 = new NotificationChannel(ALERT_CHANNEL_ID, ALERT_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel2.setDescription(ALERT_CHANNEL_DESC);
            notificationManager.createNotificationChannel(channel2);

        }
    }

    public void saveInsideFlood(Context context, String key, boolean value){

        SharedPreferences sp = context.getSharedPreferences("INSIDE_FLOOD", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value).apply();

    }

    public boolean getInsideFlood(Context context, String key){

        SharedPreferences sp = context.getSharedPreferences("INSIDE_FLOOD", Context.MODE_PRIVATE);
        return sp.getBoolean(key, false);

    }
}
