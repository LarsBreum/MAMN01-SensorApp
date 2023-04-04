package com.example.sensorsapp;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Vibrator;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class AccActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accMeter;
    private TextView accView;
    private TextView commandView;

    private Button toggleButton;
    private NavController navController;
    private Vibrator vibrator;
    private MediaPlayer mediaPlayer;
    private boolean accOn;

    private Instant startTime;
    private double speed; //speed of the car

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acc);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accMeter = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.accOn = true;
        //Gets a list of all sensors
        List<Sensor> deviceSensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        this.vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        this.mediaPlayer = MediaPlayer.create(getApplicationContext(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));


        this.startTime = Instant.now();

        this.accView = (TextView) findViewById(R.id.acc_view);
        this.commandView = (TextView) findViewById(R.id.commandView);

        this.toggleButton = (Button) findViewById(R.id.button_toggle);

        this.speed = 0.0;

        toggleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("accOn", Boolean.toString(accOn));
                if(accOn) {
                    onPause();
                    accOn = false;
                    commandView.setText("Hey - start driving :)");
                } else {
                    onResume();
                    accOn = true;
                }
            }
        });

    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void driver(float xAcc, float yacc, float zAcc) {
        Instant currentTime = Instant.now();

        long timeElapsed = Duration.between(startTime, currentTime).toMillis();
        double acceleration = zAcc*(timeElapsed/10);

        Log.d("acc", Double.toString(acceleration));

        speed = speed+acceleration; // should be (zAcc*(timeElapsed)/1000) to be m/s
        speed = speed/3.6; //km/t - kind off. Se prev comment
        commandView.setText(String.format("%.2f",speed) + " km/t");
        startTime = currentTime; //reset the time to the latest used time

        if(speed > 40+2) {
            Toast.makeText(this, "You're driving too fast", Toast.LENGTH_SHORT).show();
            vibrator.vibrate(500);
            commandView.setTextColor(Color.parseColor("#FFCC1717")); //red
            mediaPlayer.start();
        } else if(speed > 40) {
            commandView.setTextColor(Color.parseColor("#ffa500")); //orange
           // Toast.makeText(this, "Careful now", Toast.LENGTH_SHORT).show();
        } else {
            commandView.setTextColor(Color.parseColor("#00ff00")); //green
        }

        /*
        If (0,0,9) max acc forward
        If (0,9,0) no acc
        If (0,-9,0) max break/backing
        If (9,0,0) max left turn
        If (-9,0,0) max right turn
         */

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Float xAcc = sensorEvent.values[0];
        Float yAcc = sensorEvent.values[1];
        Float zAcc = sensorEvent.values[2];

        this.accView.setText("(" + String.format("%.2f", xAcc) + "; " + String.format("%.2f", yAcc) + "; " + " + " + String.format("%.2f", zAcc) + ")");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if(zAcc > 9) { //remove large movements
                zAcc = Float.valueOf(9);
            }
            if(zAcc < -4) {
                zAcc = Float.valueOf(-4);
            }
            driver(xAcc, yAcc, zAcc);
        }
        //Log.d("AccMeter", xAcc + " ; " + yAcc + " ; " + zAcc);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accMeter, SensorManager.SENSOR_DELAY_NORMAL);
    }

}