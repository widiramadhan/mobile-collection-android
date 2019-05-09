package sfi.mobile.collection.services;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import sfi.mobile.collection.app.AppController;

public class ServiceLoc extends Service {
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    public LocationManager locationManager;
    public LocationService.MyLocationListener listener;
    public Location previousBestLocation = null;
    Intent intent;

    public void onCreate() {
        super.onCreate();
    }

    public void onStart(Intent intent, int startId) {

        super.onStart(intent, startId);
        addLocationListener();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Thread addLocationListener() {
        final Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    Looper.prepare();//Initialise the current thread as a looper.
                    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                    Criteria c = new Criteria();
                    c.setAccuracy(Criteria.ACCURACY_COARSE);

                    final String PROVIDER = locationManager.getBestProvider(c, true);

                    MyLocationListener myLocationListener = new MyLocationListener();
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    locationManager.requestLocationUpdates(PROVIDER, 600000, 0, myLocationListener);
                    Log.d("LOC_SERVICE", "Service RUNNING!");
                    Looper.loop();
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
        }, "LocationThread");
        t.start();
        return t;
    }

    public static void updateLocation(Location location)
    {
        Context appCtx = AppController.getAppContext();

        double latitude, longitude;

        latitude = location.getLatitude();
        longitude = location.getLongitude();

        Intent filterRes = new Intent();
        filterRes.setAction("xxx.yyy.intent.action.LOCATION");
        filterRes.putExtra("latitude", latitude);
        filterRes.putExtra("longitude", longitude);
        appCtx.sendBroadcast(filterRes);
    }


    class MyLocationListener implements LocationListener
    {

        @Override
        public void onLocationChanged(Location location)
        {
            updateLocation(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

}
