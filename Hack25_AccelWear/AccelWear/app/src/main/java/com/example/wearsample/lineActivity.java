package com.example.wearsample;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

/**
 * Created by mitak_000 on 2014/08/28.
 */
public class lineActivity extends Activity {

    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(new TouchLineView(this));
    }
}
