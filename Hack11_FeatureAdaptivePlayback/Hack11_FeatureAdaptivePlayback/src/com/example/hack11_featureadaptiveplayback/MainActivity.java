
package com.example.hack11_featureadaptiveplayback;

import android.app.Activity;
import android.app.Fragment;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

    @Override
    protected void onResume() {    
        super.onResume();

        // コーデックリスト数を取得し、1つずつコーデック情報を確認する
        int codecCount = MediaCodecList.getCodecCount();
        for(int j = 0; j < codecCount; j++) {
            MediaCodecInfo mediaCodecInfo = MediaCodecList.getCodecInfoAt(j);

            // エンコードのコーデックは無視する
            if(mediaCodecInfo.isEncoder()) {
                continue;
            }

            // コーデックが対応しているMimeType一覧を取得する
            String[] supportedTypes = mediaCodecInfo.getSupportedTypes();
            for(int i = 0; i < supportedTypes.length; i++) {
                // MimeTypeで提供されている情報を取得
                MediaCodecInfo.CodecCapabilities capa = mediaCodecInfo.
                        getCapabilitiesForType(supportedTypes[i]);
                
                // Adaptive Playbackが対応しているか確認
                boolean isSupported = capa.isFeatureSupported(
                        MediaCodecInfo.CodecCapabilities.FEATURE_AdaptivePlayback);
                
                Log.i("TEST", "AdaptivePlayback Supported:" + isSupported + 
                        " Codec:" + mediaCodecInfo.getName() + " MimeType:" + supportedTypes[i]);
            }
        }
    }
}
