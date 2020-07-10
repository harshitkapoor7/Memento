package com.example.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, OrderReminderNotificationService.class);
//        System.out.println(" ds "+intent.getStringExtra("address"));

        i.putExtra("address", intent.getStringExtra("address"));
        i.putExtra("body", intent.getStringExtra("body"));
        context.startService(i);
    }
}