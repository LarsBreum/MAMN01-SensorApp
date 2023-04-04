package com.example.sensorsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.List;

public class SensorActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor rotationSensor;
    private String[] commands;
    private TextView commandView;

    private float[] rotationMatrix;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        //Load in rotation sensor
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        List<Sensor> deviceSensors = sensorManager.getSensorList(Sensor.TYPE_ALL);

        this.commands = new String[]{"Turn right!", "Turn left!", "Go Straight"};
        this.commandView = (TextView) findViewById(R.id.view);
    }

    /**
     * Method to change the command of the textView
     */
    void setCommandView() {
        this.commandView.setText("Turn Right!");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Float xAcc = event.values[0];
        Float yAcc = event.values[1];
        Float zAcc = event.values[2];

        //should have low pass filter here
        Log.d("AccMeter", xAcc + " ; " + yAcc + " ; " + zAcc);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}