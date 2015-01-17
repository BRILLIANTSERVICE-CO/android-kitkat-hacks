package com.example.stepdetectorsample;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {
	private SensorManager mSensorManager;
	private Sensor mStepDetectorSensor;
	private TextView StepText;
	private TextView TimestampText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		StepText= (TextView) findViewById(R.id.step);
		TimestampText = (TextView) findViewById(R.id.timestamp);
		//センサーマネージャーを取得する
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		//歩行検知センサーオブジェクトを取得する
		mStepDetectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
	}

	@Override
	protected void onResume() {
		super.onResume();
		//センサーのリスナーを登録する
		mSensorManager.registerListener(mSensorEventListener, mStepDetectorSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	protected void onStop() {
		super.onStop();
		//センサーのリスナー登録を解除する
		mSensorManager.unregisterListener(mSensorEventListener);
	}


	private SensorEventListener mSensorEventListener = new SensorEventListener() {
	 
	    @Override
	    public void onAccuracyChanged(Sensor sensor, int accuracy) {
	 
	    }
	 
	    int count = 0;
	    /**
	     * 歩行を検知すると呼び出される
	     */
	    @Override
	    public void onSensorChanged(SensorEvent event) {
	    	//歩行検知時は常に1.0を返す
	    	if(event.values[0] == 1.0f){
	    		count ++;
	    		StepText.setText("step detect count:"+Integer.toString(count));
	    		//タイムスタンプを返す
	    		TimestampText.setText("timestamp:"+Long.toString(event.timestamp));
	    	}
	    }
	};

}
