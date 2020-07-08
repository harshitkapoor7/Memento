package com.example.home;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.allyants.notifyme.NotifyMe;

import java.io.Serializable;
import java.util.Calendar;

public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
            Intent i = new Intent(context, OrderReminderNotificationService.class);
            i.putExtra("address",intent.getStringExtra("address"));
            context.startService(i);
    }
}
