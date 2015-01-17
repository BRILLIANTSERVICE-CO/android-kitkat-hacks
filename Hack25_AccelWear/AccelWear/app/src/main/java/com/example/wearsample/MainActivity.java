package com.example.wearsample;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener, SensorEventListener {

    private final static String BR = System.getProperty("line.separator");
    private WatchViewStub stub;
    private TextView mTextView;
    private TextView valueText;
    private Button mButton;
    private SensorManager mSensorManager;

    String valueString;

    //加速度センサの値
    private float[] accValues = new float[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mButton = (Button) stub.findViewById(R.id.button1);
                mButton.setOnClickListener(MainActivity.this);
                mButton.setVisibility(View.GONE);
            }
        });

        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);

    }

    public void onClick(View v){
        if(v.getId()==R.id.button1){
            //Toast.makeText(this, "test", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, ListActivity.class);
            intent.setAction(Intent.ACTION_VIEW);
            startActivity(intent);
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        for(Sensor sensor:sensors){
            int type = sensor.getType();

            if(type == Sensor.TYPE_ACCELEROMETER){
                mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
                //Toast.makeText(this,"check AccSensor", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int value){
    }

    @Override
    public void onSensorChanged(SensorEvent event){

        if(event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) return;

        int type = event.sensor.getType();

        if(type == Sensor.TYPE_ACCELEROMETER){
            accValues = event.values.clone();
        }

        StringBuffer sb = new StringBuffer();
        sb.append("Accelerometer Sensor value" + BR);
        sb.append("加速度[x軸]：" + fm(accValues[0]) + BR);
        sb.append("加速度[y軸]：" + fm(accValues[1]) + BR);
        sb.append("加速度[z軸]：" + fm(accValues[2]) + BR);
        valueString = sb.toString();

        valueText = (TextView) stub.findViewById(R.id.value);
        valueText.setText(valueString);
    }

    public String fm(float value){
        return (value<=0)? ""+value : "+" + value;
    }
}
