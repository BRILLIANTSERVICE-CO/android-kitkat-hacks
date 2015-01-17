/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.geomagneticrotationvectorsample;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Wrapper activity demonstrating the use of the new
 * {@link SensorEvent#values rotation vector sensor}
 * ({@link Sensor#TYPE_ROTATION_VECTOR TYPE_ROTATION_VECTOR}).
 * 
 * @see Sensor
 * @see SensorEvent
 * @see SensorManager
 * 
 */
public class MainActivity extends Activity {
    private SensorManager mSensorManager;
    private Sensor mGeomagneticRotationVectorSensor;
    private TextView[] mTextView = new TextView[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get an instance of the SensorManager
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mGeomagneticRotationVectorSensor = mSensorManager.getDefaultSensor(
                Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR);
        
        mTextView[0] = (TextView) findViewById(R.id.values_0);
        mTextView[1] = (TextView) findViewById(R.id.values_1);
        mTextView[2] = (TextView) findViewById(R.id.values_2);
        mTextView[3] = (TextView) findViewById(R.id.values_3);
        mTextView[4] = (TextView) findViewById(R.id.values_4);

    }

    @Override
    protected void onResume() {
        // Ideally a game should implement onResume() and onPause()
        // to take appropriate action when the activity looses focus
        super.onResume();
		//センサーのリスナーを登録する
		mSensorManager.registerListener(mSensorEventListener, mGeomagneticRotationVectorSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        // Ideally a game should implement onResume() and onPause()
        // to take appropriate action when the activity looses focus
        super.onPause();
    }

    private SensorEventListener mSensorEventListener = new SensorEventListener(){

		@Override
		public void onSensorChanged(SensorEvent event) {
			
			
			mTextView[0].setText("x*sin(θ/2):"+String.valueOf(event.values[0]));
			mTextView[1].setText("y*sin(θ/2):"+String.valueOf(event.values[1]));
			mTextView[2].setText("z*sin(θ/2) :"+String.valueOf(event.values[2]));
			mTextView[3].setText("cos(θ/2):"+String.valueOf(event.values[3]));
			mTextView[4].setText("estimated_accuracy:"+String.valueOf(event.values[4]));
			
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			
		}
    	
    };
    
}
