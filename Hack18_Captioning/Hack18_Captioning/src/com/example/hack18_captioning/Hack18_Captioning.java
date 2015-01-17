
package com.example.hack18_captioning;

import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.media.MediaFormat;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.accessibility.CaptioningManager;
import android.view.accessibility.CaptioningManager.CaptionStyle;
import android.widget.VideoView;

public class Hack18_Captioning extends Activity {

    private static final String TAG = "CaptioningSample";
    private VideoView mVideoView = null;

    private CaptioningManager mCaptioningManager = null;
    private boolean mCaptioningEnable;
    private Locale mCaptioningLocale;
    private float mCaptioningSize;
    private CaptioningManager.CaptionStyle mCaptionStyle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hack18__captioning);

        mVideoView = (VideoView) findViewById(R.id.video);

        mCaptioningManager = (CaptioningManager) getSystemService(Context.CAPTIONING_SERVICE);

        mCaptioningEnable = mCaptioningManager.isEnabled();
        mCaptioningLocale = mCaptioningManager.getLocale();
        mCaptioningSize = mCaptioningManager.getFontScale();
        mCaptionStyle = mCaptioningManager.getUserStyle();

        mCaptioningManager.addCaptioningChangeListener(mCaptioningListener);

        mVideoView.setVideoURI(Uri.parse("android.resource://" + this.getPackageName() + "/"
                + R.raw.sample_movie));

        mVideoView.addSubtitleSource(
                getResources().openRawResource(R.raw.sample_vtt),
                MediaFormat.createSubtitleFormat("text/vtt",
                        Locale.ENGLISH.getLanguage()));

        mVideoView.start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mCaptioningManager.removeCaptioningChangeListener(mCaptioningListener);
    }

    private CaptioningManager.CaptioningChangeListener mCaptioningListener = new CaptioningManager.CaptioningChangeListener() {
        @Override
        public void onEnabledChanged(boolean enabled) {
            super.onEnabledChanged(enabled);
            mCaptioningEnable = enabled;
            Log.i(TAG, "onEnabledChanged enable = " + mCaptioningEnable);
        }

        @Override
        public void onLocaleChanged(Locale locale) {
            super.onLocaleChanged(locale);
            mCaptioningLocale = locale;
            Log.i(TAG, "onLocaleChanged locale = " + mCaptioningLocale);
        }

        @Override
        public void onFontScaleChanged(float fontScale) {
            super.onFontScaleChanged(fontScale);
            mCaptioningSize = fontScale;
            Log.i(TAG, "onFontScaleChanged fontScale = " + mCaptioningSize);
        }

        @Override
        public void onUserStyleChanged(CaptionStyle userStyle) {
            super.onUserStyleChanged(userStyle);
            mCaptionStyle = userStyle;
            Log.i(TAG, "onUserStyleChanged userStyle = " + userStyle);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.hack18__captioning, menu);
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
}
