package com.example.home;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BackgroundLocationService extends Service implements LocationListener {

    SharedPreferences sharedPreferences;
    ArrayList<String> dt,db;
    String json1,json2;
    Gson gson;
    LocationManager locationManager = null;

    private class LocationListener implements android.location.LocationListener {
        Location location;
        public LocationListener(String passiveProvider) {
            location=new Location(passiveProvider);
        }

        @Override
        public void onLocationChanged(Location mLocation) {
            System.out.println("in Location CHanged");
            location.set(mLocation);
            gson = new Gson();
            sharedPreferences=getSharedPreferences(MainActivity.email,MODE_PRIVATE);
            json1 = sharedPreferences.getString("dataTitle", null);
            json2 = sharedPreferences.getString("dataBody", null);
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            dt = gson.fromJson(json1, type);
            db = gson.fromJson(json2, type);

            if (dt != null) {
                for (int i = 0; i < dt.size(); i++) {
                    LatLng latLng = getLatLngFromAddress(dt.get(i));
                    Double lat = latLng.latitude;
                    Double lon = latLng.longitude;
                    System.out.println(mLocation.getLatitude() + " " + lat + " " + mLocation.getLongitude() + " " + lon);
                    if (MainActivity.checkDistance(mLocation.getLatitude(), lat, mLocation.getLongitude(), lon) <= 250) {

                        Intent alarmIntent = new Intent(getApplicationContext(), MyBroadcastReceiver.class);

                        String address = dt.get(i), body = db.get(i);

                        alarmIntent.putExtra("body", "You're within "+(int)MainActivity.checkDistance(mLocation.getLatitude(), lat, mLocation.getLongitude(), lon)
                                +"m of your pending task location");
                        alarmIntent.putExtra("address", body + " (" + address + ")");

                        System.out.println("You're within "+ (int)MainActivity.checkDistance(mLocation.getLatitude(), lat, mLocation.getLongitude(), lon)+
                                "m of your pending task location" + " " + body + " (" + address + ")");

                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), (int)SystemClock.elapsedRealtime(), alarmIntent, 0);
                        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), pendingIntent);
                    }
                }
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }

    LocationListener[] locationListeners = new LocationListener[]{
            new LocationListener(LocationManager.PASSIVE_PROVIDER)
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("in background service");
        if (locationManager == null) {
            locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            try {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                System.out.println("Idhar hai");
                locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 300000, 0, locationListeners[0]);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(locationManager!=null){
            for(int i=0;i<locationListeners.length;i++){
                try{
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    locationManager.removeUpdates(locationListeners[i]);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
    public LatLng getLatLngFromAddress(String address) {

        Geocoder geocoder = new Geocoder(getApplicationContext());
        List<Address> addressList;

        try {
            addressList = geocoder.getFromLocationName(address, 1);
            if (addressList != null) {
                Address singleaddress = addressList.get(0);
                LatLng latLng = new LatLng(singleaddress.getLatitude(), singleaddress.getLongitude());
                return latLng;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
