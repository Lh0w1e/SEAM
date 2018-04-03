package com.app.seam.MyServices;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.app.seam.Constant.Constant;
import com.app.seam.R;

/**
 * Created by Colinares on 4/1/2018.
 */

public class NotificationBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        String event_name = intent.getStringExtra(Constant.EXTRA_EVENT_NAME);
        String event_date = intent.getStringExtra(Constant.EXTRA_EVENT_DATE);
        String event_time = intent.getStringExtra(Constant.EXTRA_EVENT_TIME);

        Notification.BigTextStyle bigTextStyle = new Notification.BigTextStyle();
        bigTextStyle.bigText("Event Name : " + event_name + "\n" + "Event Date : " + event_date + "\n" + "Event Time : " + event_time);
        bigTextStyle.setSummaryText("By : SEAM");

        Notification.Builder builder = new Notification.Builder(context)
                .setSmallIcon(R.drawable.seam_icon)
                .setContentTitle("You Have New Event")
                .setContentText("Event : " + event_name)
                .setStyle(bigTextStyle)
                .setAutoCancel(true);

        Notification notification = builder.build();
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notification.defaults |= Notification.DEFAULT_LIGHTS;
        notificationManager.notify(0, notification);

    }
}
