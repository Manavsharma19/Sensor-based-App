package com.find.me.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener2;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.find.me.R;
import com.find.me.appCheck.MyApplication;
import com.find.me.receiver.MyBroadcastReceiver;
import com.find.me.ui.dashboard.MainActivity;

import java.text.DecimalFormat;


public class LocationService extends Service implements  SensorEventListener2 {
    SensorManager sensor ;
    private MyBroadcastReceiver mBroadcastReceiver;
    IntentFilter intentFilter = new IntentFilter();

    @Override
    public void onCreate() {
        super.onCreate();
        sensor = (SensorManager) getSystemService(SENSOR_SERVICE);
        mBroadcastReceiver = new MyBroadcastReceiver();
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        // set the custom action
        intentFilter.addAction("do_something");
        registerReceiver(mBroadcastReceiver, new IntentFilter("do_something"));

//        registerReceiver(mBroadcastReceiver, intentFilter);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
            startMyOwnForeground();
        else{
            startForeground(2, new Notification());
        }

    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sensor.registerListener(this,sensor.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_GAME);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        sensor.unregisterListener(this);
//        unregisterReceiver(mBroadcastReceiver, intentFilter);


    }

    @Override
    public void onFlushCompleted(Sensor sensor) {

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {


            double loX = sensorEvent.values[0];
            double loY = sensorEvent.values[1];
            double loZ = sensorEvent.values[2];

            double loAccelerationReader = Math.sqrt(Math.pow(loX, 2)
                    + Math.pow(loY, 2)
                    + Math.pow(loZ, 2));

            DecimalFormat precision = new DecimalFormat("0.00");
            double ldAccRound = Double.parseDouble(precision.format(loAccelerationReader));
//        Log.i("imam", "onSensorChanged: check"+ldAccRound);

            if (ldAccRound > 0.3d && ldAccRound < 0.4d) {
                if (MyApplication.isInForeground()){
                    Log.i("imam", "onSensorChanged: generated foreground"+ldAccRound);
                    //Do your stuff
//                    showNotification("Fall Alert", "Are you in danger? Press to generate alert.");
                }
                else {
                    Log.i("imam", "onSensorChanged: generated  background"+ldAccRound);
                    //Do your stuff
                    showNotification("Fall Alert", "Are you in danger? If not, Press to stop alert.");
                }

            }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
    @RequiresApi(Build.VERSION_CODES.O)
    private void startMyOwnForeground() {

        String NOTIFICATION_CHANNEL_ID = "LocAte.alert";
        String channelName = "LocAteMe Alert";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setAutoCancel(true)
                .build();
        startForeground(2, notification);
        manager.cancelAll();
    }
    private void showNotification(String title, String message){
//        Intent intent = new Intent("do_something");
        Intent intent = new Intent(LocationService.this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT |
                PendingIntent.FLAG_IMMUTABLE );
     /*   LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.sendBroadcast(intent);*/
//        Uri sound = Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.charming_bell);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder channelBuilder = new NotificationCompat.Builder(this, "alert_chanel");
        channelBuilder.setSmallIcon(R.drawable.lam2)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.lam2))
                .setColor(getResources().getColor(R.color.blue_btn_bg_color))
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingIntent)

                .setAutoCancel(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "findMe";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("alert_chanel", name, importance);

            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notificationManager.createNotificationChannel(channel);

            notificationManager.notify(1, channelBuilder.build());

        }
        else {
            channelBuilder.setSmallIcon(R.drawable.lam2)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.lam2))
                    .setColor(getResources().getColor(R.color.blue_btn_bg_color))
                    .setContentTitle(title)
                    .setContentText(message)
                    .setContentIntent(pendingIntent)

                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setPriority(Notification.PRIORITY_HIGH);
            notificationManager.notify(0, builder.build());
        }

    }

}

