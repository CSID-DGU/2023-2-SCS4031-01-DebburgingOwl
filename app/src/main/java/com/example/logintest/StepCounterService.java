package com.example.logintest;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

public class StepCounterService extends Service implements SensorEventListener {
    private SensorManager sensorManager;
    private int steps = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        registerStepCounterSensor();
        startForegroundService();
    }

    private void registerStepCounterSensor() {
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (countSensor != null) {
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    private void startForegroundService() {
        String channelId = createNotificationChannel();

        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle("걸음 수 추적 중")
                .setContentText("걸음 수를 추적하고 있습니다.")
                .setSmallIcon(R.drawable.ic_home)
                .build();

        startForeground(1, notification);
    }

    private String createNotificationChannel() {

            String channelId = "step_counter_service_channel";
            String channelName = "Step Counter Service Channel";
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
            return channelId;

    }

    @Override
    public IBinder onBind(Intent intent) {
        // This service is started not bound, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        steps = (int) event.values[0];
        // Update the ongoing notification with the new step count
        // This is a simplified example; you'd likely want to move this into a method that gets called periodically, not with every sensor event
        Notification notification = new NotificationCompat.Builder(this, "step_counter_service_channel")
                .setContentTitle("걸음 수 추적 중")
                .setContentText("걸음 수: " + steps)
                .setSmallIcon(R.drawable.ic_home)
                //todo icon 수정해야함.
                .build();

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1, notification);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // 필요 시 정확도가 변경되면 처리
    }
}
