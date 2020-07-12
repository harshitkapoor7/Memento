package com.example.home;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.os.SystemClock;
import android.widget.Toast;

import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.model.LatLng;

public class MyLocationService extends BroadcastReceiver {

    public static final String ACTION= "com.example.home.UPDATE_LOCATION";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent!=null){
            System.out.println("In Location Service");

            final String act=intent.getAction();
            if(ACTION.equals(act)){
                LocationResult result=LocationResult.extractResult(intent);
                System.out.println("Coordinates : ");
                if(result!=null){
                    Location location=result.getLastLocation();
                    System.out.println("Coordinates : "+location.getLatitude()+"  "+location.getLongitude());
                    try {
                        Double lt=intent.getDoubleExtra("lat",0);
                        Double ln=intent.getDoubleExtra("lon",0);
                        int req=intent.getIntExtra("req",0);
                        if(MainActivity.checkDistance(location.getLatitude(),lt,location.getLongitude(),ln)<=250){
                            Intent alarmIntent = new Intent(context, MyBroadcastReceiver.class);
                            alarmIntent.putExtra("address", intent.getStringExtra("address"));
                            alarmIntent.putExtra("body", intent.getStringExtra("body"));
                            PendingIntent pendingIntent=PendingIntent.getBroadcast(context,req,alarmIntent,0);
                            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), pendingIntent);

                            System.out.println("Distance Alert");
                        }
                    }catch (Exception e){
                        Toast.makeText(context,"Exception Occured",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

    }
}
