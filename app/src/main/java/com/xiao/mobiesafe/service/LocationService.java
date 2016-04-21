package com.xiao.mobiesafe.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

public class LocationService extends Service {

    private LocationManager lm;
    private SharedPreferences config;
    private MyLocationListener myLocationListener;

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {

        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        config = getSharedPreferences("config", MODE_PRIVATE);

        Criteria criteria=new Criteria();
        criteria.setCostAllowed(true);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        String bestProvider = lm.getBestProvider(criteria, true);
        myLocationListener = new MyLocationListener();
        lm.requestLocationUpdates(bestProvider,0,0, myLocationListener);
        super.onCreate();
    }


    class MyLocationListener implements LocationListener{
        @Override
        public void onLocationChanged(Location location) {
            config.edit()
                    .putString(
                            "location",
                            "j:" + location.getLongitude() + "; w:"
                                    + location.getLatitude()).apply();

            stopSelf();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {}
    }

    @Override
    public void onDestroy() {
        lm.removeUpdates(myLocationListener);
        super.onDestroy();
    }
}
