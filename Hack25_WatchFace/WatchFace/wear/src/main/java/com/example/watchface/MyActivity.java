package com.example.watchface;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.widget.ImageView;

public class MyActivity extends Activity {
    //private LinearLayout layout;

    ImageView mBackground;
    WatchViewStub stub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        stub = (WatchViewStub) findViewById(R.id.watch_view_stub);

    }

    @Override
    protected void onResume(){
        super.onResume();

        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mBackground = (ImageView)stub.findViewById(R.id.image);
                mBackground.setImageResource(R.drawable.android_kk);
            }
        });
    }

    @Override
    protected void onPause(){
        super.onPause();

        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mBackground = (ImageView)stub.findViewById(R.id.image);
                mBackground.setImageDrawable(null);
            }
        });
    }
}
