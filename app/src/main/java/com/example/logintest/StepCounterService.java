package com.example.logintest;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StepCounterService extends Service implements SensorEventListener {
    private SensorManager sensorManager;
    private int steps = 0;
    private DatabaseReference databaseReference;

    @Override
    public void onCreate() {
        super.onCreate();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        registerStepCounterSensor();
        startForegroundService();
        loadLastStepCountFromFirebase();


        // Firebase Database 참조 초기화
        databaseReference = FirebaseDatabase.getInstance().getReference("steps");
    }
    private void loadLastStepCountFromFirebase() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        DatabaseReference lastStepRef = FirebaseDatabase.getInstance().getReference("steps")
                .child(userId).child(date);

        lastStepRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    steps = dataSnapshot.getValue(Integer.class);
                } else {
                    steps = 0;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 오류 처리
            }
        });
    }
    private void registerStepCounterSensor() {
        Sensor stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        if (stepDetectorSensor != null) {
            sensorManager.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_FASTEST);
        }
    }


    private void startForegroundService() {
        String channelId = createNotificationChannel();

        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle("걸음 수 추적 중")
                .setContentText("걸음 수를 추적하고 있습니다.")
                .setSmallIcon(R.drawable.ic_workout)
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
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            // 각 걸음마다 steps 변수를 1 증가
            steps++;
            updateNotification(steps);
            updateDatabase(steps);
        }
    }

    private void updateNotification(int steps) {
        Notification notification = new NotificationCompat.Builder(this, "step_counter_service_channel")
                .setContentTitle("걸음 수 추적 중")
                .setContentText("걸음 수: " + steps)
                .setSmallIcon(R.drawable.ic_workout)
                .build();

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1, notification);
    }

    private void updateDatabase(int steps) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        databaseReference.child(userId).child(date).setValue(steps);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // 필요 시 정확도가 변경되면 처리
    }
}
