package com.example.logintest;
import android.content.Intent;
import android.widget.Toast;
import android.view.View;
import android.widget.Button;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ProgressBar;


public class WorkoutActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private TextView stepCounterTextView;
    private Button missionCompleteButton;
    private ProgressBar stepProgressBar;
    private int steps = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        stepCounterTextView = findViewById(R.id.stepCounter);
        missionCompleteButton = findViewById(R.id.missionCompleteButton);
        stepProgressBar = findViewById(R.id.stepProgressBar);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        missionCompleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(WorkoutActivity.this, "미션 완료", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(WorkoutActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (countSensor != null) {
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        steps = (int) event.values[0] + 1000;
        stepCounterTextView.setText("Steps: " + steps);
        stepProgressBar.setProgress(steps);
        if (steps >= 1000) {
            missionCompleteButton.setEnabled(true);

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // 필요한 경우 여기에 코드를 추가합니다.
    }
}
