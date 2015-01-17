package com.example.stepcountersample;

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
	private Sensor mStepCounterSensor;
	private TextView mTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mTextView = (TextView) findViewById(R.id.stepcount);
		//センサーマネージャーを取得する
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		//歩数計センサーオブジェクトを取得する
		mStepCounterSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
	}

	@Override
	protected void onResume() {
		super.onResume();
		//センサーのリスナーを登録する
		mSensorManager.registerListener(mSensorEventListener, mStepCounterSensor,
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
	 
	    /**
	     * センサーの状態に変化が生じたときに呼び出される
	     */
	    @Override
	    public void onSensorChanged(SensorEvent event) {
	    	//歩数をテキスト表示する
	        mTextView.setText("step count:"+Float.toString(event.values[0]));
	    }
	};

}
