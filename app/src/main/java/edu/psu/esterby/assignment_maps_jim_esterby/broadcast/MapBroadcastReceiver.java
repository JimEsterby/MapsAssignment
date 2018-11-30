package edu.psu.esterby.assignment_maps_jim_esterby.broadcast;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import edu.psu.esterby.assignment_maps_jim_esterby.R;
import edu.psu.esterby.assignment_maps_jim_esterby.model.DataItem;

public class MapBroadcastReceiver extends BroadcastReceiver {

    public static final String NEW_MAP_LOCATION_BROADCAST = "edu.psu.esterby.assignment_maps_jim_esterby.action.NEW_MAP_LOCATION_BROADCAST";
    public static final String CHANNEL_ID = "1";
    public static final String CHANNEL_DESCRIPTION = "BROADCAST MAP CHANNEL";
    public static final String CHANNEL_NAME = "MAPS";

    @Override
    public void onReceive(Context c, Intent intent) {
        // get the data passed in the intent
        DataItem item = (DataItem) intent.getSerializableExtra("MAP_LOCATION");

        // obtain the notification manager from the context
        NotificationManagerCompat nm = NotificationManagerCompat.from(c);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(c, CHANNEL_ID);
        builder.setSmallIcon(R.drawable.broadcast);
        builder.setContentTitle(item.getLocation());

        if (Double.isNaN(item.getLatitude()) || Double.isNaN(item.getLongitude())) {
            builder.setContentText("Location coordinates unknown");
        } else {
            builder.setContentText("Lat = " + item.getLatitude() + ", Long = " + item.getLocation());
        }

        // set up the notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {  // API 26 and later
            NotificationChannel ch = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            ch.setDescription(CHANNEL_DESCRIPTION);
            NotificationManager notificationManager = (NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(ch);
        }

        nm.notify(1, builder.build());
    }
}

