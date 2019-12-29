package com.example.android_hw;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;


public class OrientationData implements SensorEventListener {
    Context myContext;
    private SensorManager manager;
    private Sensor accelerometer;
    private Sensor magnometer;

    private float[] accelOutput;
    private float[] magOutput;


    private float[] orientation = new float[3];
    private float[] startOrientation = null;

    private OrientationData orientationData;
    private long frameTime;

    public float[] getOrientation() {
        return orientation;
    }

    public float[] getStartOrientation() {
        return startOrientation;
    }

    public void newGame() {
        startOrientation = null;
    }

    public OrientationData(Context myContext) {
        this.myContext = myContext;
        manager = (SensorManager) myContext.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnometer = manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    public void register() {
        // SENSOR_DELAY_GAME tells to system how often it should listen to changes in accelerometer (prevents overloading)
        manager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        manager.registerListener(this, magnometer, SensorManager.SENSOR_DELAY_GAME);
    }

    public void pause() {
        manager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelOutput = event.values;
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            magOutput = event.values;
        }
        if (accelOutput != null && magOutput != null) {
            float[] R = new float[9]; // Rotation
            float[] I = new float[9]; // Inclination
            boolean success = SensorManager.getRotationMatrix(R, I, accelOutput, magOutput);
            if (success == true) {
                SensorManager.getOrientation(R, orientation);
                if (startOrientation == null) {
                    startOrientation = new float[orientation.length];
                    System.arraycopy(orientation, 0, startOrientation, 0, orientation.length);
                }
            }

        }
    }
}