package com.example.sensor2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager mSensorManager;

    private Sensor mSensorAccelerometer;
    private Sensor mSensorMagnetometer;

    private TextView mTextSensorAzimuth;
    private TextView mTextSensorPitch;
    private TextView mTextSensorRoll;
    private ImageView mSpotTop;
    private ImageView mSpotLeft;
    private ImageView mSpotRight;
    private ImageView mSpotBottom;

    private static final float VALUE_DRIFT = 0.05f;
    private float[]  mAccelerometerData = new float[3];
    private float[]  mMagnetometerData = new float[3];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mTextSensorAzimuth = findViewById(R.id.value_azimuth);
        mTextSensorPitch = findViewById(R.id.value_pitch);
        mTextSensorRoll = findViewById(R.id.value_roll);

        mSpotTop = findViewById(R.id.spot_top);
        mSpotLeft = findViewById(R.id.spot_left);
        mSpotRight = findViewById(R.id.spot_right);
        mSpotBottom = findViewById(R.id.spot_bottom);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mSensorAccelerometer!=null){
            mSensorManager.registerListener(this,mSensorAccelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL);

        }
        if(mSensorMagnetometer!=null){
            mSensorManager.registerListener(this,mSensorMagnetometer,
                    SensorManager.SENSOR_DELAY_NORMAL);

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensorType = event.sensor.getType();
        switch (sensorType){
            case Sensor.TYPE_ACCELEROMETER:
                mAccelerometerData = event.values.clone();
                break;

            case Sensor.TYPE_MAGNETIC_FIELD:
                mMagnetometerData = event.values.clone();
                break;
            default:
                return;
        }

        float[] rotationMatrix = new float[9];
        boolean rotationOk = SensorManager.getRotationMatrix(rotationMatrix,null,
                mAccelerometerData,mMagnetometerData);
        float [] orientationValues = new float[3];
        if (rotationOk){
            SensorManager.getOrientation(rotationMatrix,orientationValues);
        }
        float azimuth = orientationValues[0];
        float pitch = orientationValues[1];
        float roll = orientationValues[2];

        mTextSensorAzimuth.setText(getResources().getString(R.string.value_format,roll));
        mTextSensorPitch.setText(getResources().getString(R.string.value_format,azimuth));
        mTextSensorRoll.setText(getResources().getString(R.string.value_format,pitch));

        if(Math.abs(pitch)< VALUE_DRIFT){
            pitch = 0;
        }
        if(Math.abs(roll)< VALUE_DRIFT){
            roll = 0;
        }
        mSpotTop.setAlpha(0f);
        mSpotLeft.setAlpha(0f);
        mSpotBottom.setAlpha(0f);
        mSpotRight.setAlpha(0f);

        if (pitch > 0){
            mSpotBottom.setAlpha(pitch);
        }else{
            mSpotTop.setAlpha(Math.abs(pitch));
        }

        if (roll > 0){
            mSpotLeft.setAlpha(roll);
        }else{
            mSpotRight.setAlpha(Math.abs(roll));
        }



    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}