package com.yeel.drc.fcm;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;


import com.yeel.drc.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class NotificationUtils {
    private Context mContext;
    int pushID;
    int color;
    public NotificationUtils(Context mContext) {
        this.mContext = mContext;
    }
    public void showNotificationMessage(String title, String message,Intent intent,int pushID,String sound) {
        showNotificationMessage(title, message,intent, null,pushID,sound);

    }
    public void showNotificationMessage(final String title, final String message,Intent intent, String imageUrl,int pushID,String sound) {
        this.pushID = pushID;
        color = ContextCompat.getColor(mContext, R.color.colorPrimaryDark);
        // Check for empty push message
        if (TextUtils.isEmpty(message)) {
            return;
        }
        // notification icon
        final int icon = R.mipmap.ic_launcher;
        PendingIntent resultPendingIntent;
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.S){
            resultPendingIntent = PendingIntent.getActivity(mContext, pushID, intent, PendingIntent.FLAG_IMMUTABLE);
        }else{
            resultPendingIntent = PendingIntent.getActivity(mContext, pushID, intent, PendingIntent.FLAG_ONE_SHOT);
        }

        showSmallNotification(icon, title, message,resultPendingIntent,sound);

    }


    private void showSmallNotification(int icon, String title, String message,PendingIntent resultPendingIntent,String sound) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
        String format = simpleDateFormat.format(new Date());
        NotificationCompat.BigTextStyle inboxStyle = new NotificationCompat.BigTextStyle();
        inboxStyle.bigText(message);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext,"default")
                .setTicker(title)
                .setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setStyle(inboxStyle)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_push_small)
                .setColor(color)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                .setContentText(message);

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Uri soundUriNewOrder=null;
            if(sound.equals("on")){
                soundUriNewOrder= Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.vote_donate);
            }else{
                soundUriNewOrder= Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.silence);
            }
            builder.setChannelId("com.yeel.sustenta.transaction");
            NotificationChannel channel = new NotificationChannel(
                    "com.yeel.sustenta.transaction",
                    "Yeel Sustenta Transaction",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build();
            channel.setSound(soundUriNewOrder,audioAttributes);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
        notificationManager.notify(pushID,builder.build());
    }



    // Playing notification sound
    public void playNotificationSound() {
        try {
            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + mContext.getPackageName() + "/raw/vote_donate");
            Ringtone r = RingtoneManager.getRingtone(mContext, alarmSound);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method checks if the app is in background or not
     */
    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    // Clears notification tray messages
    public static void clearNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public static long getTimeMilliSec(String timeStamp) {
        String formattedDate = "";
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss", Locale.ENGLISH);
        try {
            df.setTimeZone(TimeZone.getTimeZone("GMT+4"));
            Date date = df.parse(timeStamp);
            df.setTimeZone(TimeZone.getDefault());
            formattedDate = df.format(date);
        } catch (Exception e) {

        }


       // SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = df.parse(formattedDate);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }




}
