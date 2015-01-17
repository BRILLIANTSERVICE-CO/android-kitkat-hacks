
package com.example.hack11_setmaxvideosize;

import java.io.IOException;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

public class MainActivity extends Activity {
    private MediaExtractor mMediaExtractorVideo = null;
    private MediaFormat mMediaFormatVideo = null;
    
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

        // コンテンツ解析クラス生成
        mMediaExtractorVideo = new MediaExtractor();

        // コンテンツ解析
        // (分かりやすくするため、ハードコーディングしています)
        try {
            mMediaExtractorVideo.setDataSource("mnt/sdcard/test.mp4");
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // 映像トラック(0)のMediaFormatを取得
        mMediaFormatVideo = mMediaExtractorVideo.getTrackFormat(0);

        // 最大画像サイズの幅と高さを設定
        // (分かりやすくするため、ハードコーディングしています)
        mMediaFormatVideo.setInteger(MediaFormat.KEY_MAX_WIDTH, 1920);
        mMediaFormatVideo.setInteger(MediaFormat.KEY_MAX_HEIGHT, 1080);
    }    
}
