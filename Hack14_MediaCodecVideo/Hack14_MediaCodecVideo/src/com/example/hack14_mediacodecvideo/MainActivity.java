
package com.example.hack14_mediacodecvideo;

import java.io.IOException;
import java.nio.ByteBuffer;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTimestamp;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class MainActivity extends Activity {

    private MediaExtractor mMediaExtractorVideo = null;
    private MediaExtractor mMediaExtractorAudio = null;

    private MediaCodec mMediaCodecVideo = null;
    private MediaCodec mMediaCodecAudio = null;
    private MediaFormat mMediaFormatVideo = null;
    private MediaFormat mMediaFormatAudio = null;

    private ByteBuffer[] mInputBuffersVideo = null;
    private ByteBuffer[] mInputBuffersAudio = null;
    private ByteBuffer[] mOutputBuffersAudio = null;
    
    private CustomSurfaceView mCustomSurfaceView = null;
    
    private Thread mInputThreadVideo = null;
    private Thread mOutputThreadVideo = null;
    private Thread mInputThreadAudio = null;
    private Thread mOutputThreadAudio = null;

    private boolean mInputThreadVideoRunnning = true;
    private boolean mOutputThreadVideoRunning = true;
    private boolean mInputThreadAudioRunning = true;
    private boolean mOutputThreadAudioRunning = true;

    private AudioTrack mAudioTrack = null;
    long mAudioStartTimeNs = 0; // オーディオ再生した時間(ナノ秒)

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
    protected void onResume() {
        super.onResume();

        // コンテンツ解析クラス生成
        mMediaExtractorVideo = new MediaExtractor();
        mMediaExtractorAudio = new MediaExtractor();

        // コンテンツ解析
        // (分かりやすくするため、ハードコーディングしています)
        try {
            mMediaExtractorVideo.setDataSource("mnt/sdcard/test.mp4");
            mMediaExtractorAudio.setDataSource("mnt/sdcard/test.mp4");
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // 映像トラック(0)のMediaFormatを取得
        mMediaFormatVideo = mMediaExtractorVideo.getTrackFormat(0);
        
        // 映像データのフォマートMimeTypeから、MediaCodecを取得
        String mimeTypeVideo = mMediaFormatVideo.getString(MediaFormat.KEY_MIME);
        mMediaCodecVideo = MediaCodec.createDecoderByType(mimeTypeVideo);

        FrameLayout frameLayout = (FrameLayout)findViewById(R.id.container);
        
        mCustomSurfaceView = new CustomSurfaceView(this);

        frameLayout.addView(mCustomSurfaceView, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
               
        // 音声トラック(1)のMediaFormatを取得
        mMediaFormatAudio = mMediaExtractorAudio.getTrackFormat(1);

        // 音声データのフォマートMimeTypeから、MediaCodecを取得
        String mimeTypeAudio = mMediaFormatAudio.getString(MediaFormat.KEY_MIME);
        mMediaCodecAudio = MediaCodec.createDecoderByType(mimeTypeAudio);

        int samplingRate = mMediaFormatAudio.getInteger(MediaFormat.KEY_SAMPLE_RATE);
        int buffsize = AudioTrack.getMinBufferSize(
                           samplingRate, 
                           AudioFormat.CHANNEL_OUT_STEREO, 
                           AudioFormat.ENCODING_PCM_16BIT);

        mAudioTrack = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                samplingRate,
                AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT,
                buffsize,
                AudioTrack.MODE_STREAM
            );

        // 映像インプットスレッド生成
        mInputThreadVideo = new Thread(new Runnable() {
            @Override
            public void run() {
                while(mInputThreadVideoRunnning) {
                    // MediaCodecのデコード用インプットバッファの中で
                    // キュー可能なバッファのインデック番号を取得する
                    int inputBufferIndex = 
                        mMediaCodecVideo.dequeueInputBuffer(1000000);

                    if (inputBufferIndex >= 0) {
                        ByteBuffer inputBuffer = 
                                mInputBuffersVideo[inputBufferIndex];
                        
                        // バッファを読み込む
                        int readSize = mMediaExtractorVideo.readSampleData(inputBuffer, 0);
                        long presentationTimeUs = 0;
                        int mask = 0; 

                        // 終端(End Of Stream)のチェック
                        if(readSize < 0){
                            mask = MediaCodec.BUFFER_FLAG_END_OF_STREAM;
                        }

                        // 再生時間を取得
                        presentationTimeUs = mMediaExtractorVideo.getSampleTime();

                        // デコーダーバッファにキュー
                        mMediaCodecVideo.queueInputBuffer(
                            inputBufferIndex,
                            0,
                            readSize, 
                            presentationTimeUs,
                            mask);

                        // 次のデータに進む
                        mMediaExtractorVideo.advance();
                    }

                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        });

        // 映像アウトプットスレッド生成
        mOutputThreadVideo = new Thread(new Runnable(){
            @Override
            public void run() {
                long threadSleepTime = 10;
                boolean flgVideoDisp = false;
                
                while(mOutputThreadVideoRunning) {
                    MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();

                    // MediaCodecのデコード用アウトプットバッファの中で
                    // デコードが完了している
                    // デキュー可能なバッファのインデック番号を取得する
                    // 一度取得したら同じデータの再取得は不可能
                    int outputBufferIndex = 
                        mMediaCodecVideo.dequeueOutputBuffer(
                            bufferInfo,
                            1000000);

                    if(outputBufferIndex >= 0) {
                        // AV同期を行う
                        while(true) {
                            // このフレームを出すべき時間に出す
                            AudioTimestamp audioTimestamp = new AudioTimestamp();
                            mAudioTrack.getTimestamp(audioTimestamp);

                            // 鳴動開始時点との差を引いて、現在の再生時間を求める(ナノ秒→マイクロ秒)
                            long playingTimeUs = (audioTimestamp.nanoTime - mAudioStartTimeNs) / 1000;
                            if(playingTimeUs >= 0) {
                                // ビデオのタイムスタンプとの差分はいくらか
                                long videoDiffTimeUs = 
                                     playingTimeUs - bufferInfo.presentationTimeUs;

                                if(videoDiffTimeUs < 0) {
                                    Log.i("AV Sync","AV Sync:Too Fast");
                                    try {
                                        Thread.sleep(10);
                                        continue;
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                        break;
                                    }
                                }
                                else if(videoDiffTimeUs > 100000) {
                                    Log.i("AV Sync","AV Sync:Too Late");
                                    flgVideoDisp = false;
                                    threadSleepTime = 0;
                                    break;
                                }
                                else {
                                    Log.i("AV Sync","AV Sync:OK");
                                    flgVideoDisp = true;
                                    threadSleepTime = 10;
                                    break;
                                }
                            }
                        }

                        // 出力する
                        if ((bufferInfo.size > 0)) {
                            // Surfaceへの書き込みが終わったので
                            // デコード完了バッファを解放する
                            mMediaCodecVideo.releaseOutputBuffer(outputBufferIndex, flgVideoDisp);
                        }

                        try {
                            Thread.sleep(threadSleepTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            break;
                        }
                    }
                }
            }
        });

        // 音声インプットスレッド生成
        mInputThreadAudio = new Thread(new Runnable() {
            @Override
            public void run() {
                while(mInputThreadAudioRunning) {
                    // MediaCodecのデコード用インプットバッファの中で
                    // キュー可能なバッファのインデック番号を取得する
                    int inputBufferIndex = 
                        mMediaCodecAudio.dequeueInputBuffer(1000000);

                    if (inputBufferIndex >= 0) {
                        ByteBuffer inputBuffer = 
                            mInputBuffersAudio[inputBufferIndex];

                        // バッファを読み込む
                        int readSize = mMediaExtractorAudio.readSampleData(inputBuffer, 0);
                        long presentationTimeUs = 0;
                        int mask = 0; 

                        // 終端(End Of Stream)のチェック
                        if(readSize < 0){
                            mask = MediaCodec.BUFFER_FLAG_END_OF_STREAM;
                        }

                        // 再生時間を取得
                        presentationTimeUs = mMediaExtractorAudio.getSampleTime();

                        // デコーダーバッファにキュー
                        mMediaCodecAudio.queueInputBuffer(
                            inputBufferIndex,
                            0,
                            readSize, 
                            presentationTimeUs,
                            mask);

                        // 次のデータに進む
                        mMediaExtractorAudio.advance();
                    }

                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        });

        // 音声アウトプットスレッド生成
        mOutputThreadAudio = new Thread(new Runnable(){
            boolean mFlgSetStartTime = false;
            @Override
            public void run() {
                while(mOutputThreadAudioRunning) {
                    MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();

                    // MediaCodecのデコード用アウトプットバッファの中で
                    // デコードが完了している
                    // デキュー可能なバッファのインデック番号を取得する
                    int outputBufferIndex = 
                        mMediaCodecAudio.dequeueOutputBuffer(
                            bufferInfo,
                            1000000);

                    if(outputBufferIndex >= 0) {
                        ByteBuffer outputBuffer = mOutputBuffersAudio[outputBufferIndex];

                        final byte[] audioData = new byte[bufferInfo.size];
                        outputBuffer.get(audioData);
                        outputBuffer.clear();

                        // AudioTrackに書き込む
                        if (audioData.length > 0) { 
                            mAudioTrack.write(audioData, 0, audioData.length);
                        }

                        // 初回のみ開始時間を保持する
                        if(mFlgSetStartTime == false) {
                            mAudioStartTimeNs = System.nanoTime();
                            mFlgSetStartTime = true;
                        }
                        
                        // AudioTrackへの書き込みが終わったので
                        // デコード完了バッファを解放する
                        mMediaCodecAudio.releaseOutputBuffer(outputBufferIndex, false);
                    }

                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        
        // 映像と音声スレッド停止
        mInputThreadVideoRunnning = false;
        mOutputThreadVideoRunning = false;
        mInputThreadAudioRunning = false;
        mOutputThreadAudioRunning = false;
    }

    @Override
    protected void onStop() {
        super.onStop();

        mAudioTrack.stop();
        mAudioTrack.release();
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
            View rootView = inflater.inflate(R.layout.fragment_main, container,
                    false);
            return rootView;
        }
    }
    
    class CustomSurfaceView extends SurfaceView implements SurfaceHolder.Callback{

        public CustomSurfaceView(Context context) {
            super(context);
            getHolder().addCallback(this);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            // MediaCodecのコンフィグ設定
            mMediaCodecVideo.configure(
                mMediaFormatVideo,
                mCustomSurfaceView.getHolder().getSurface(), 
                null, 
                0);

            mMediaCodecAudio.configure(mMediaFormatAudio, null, null, 0);
            
            // それぞれのデコーダーを開始
            mMediaCodecVideo.start();
            mMediaCodecAudio.start();
            
            // MediaCodec用のバッファを取得
            mInputBuffersVideo  = mMediaCodecVideo.getInputBuffers();
            mInputBuffersAudio  = mMediaCodecAudio.getInputBuffers();
            mOutputBuffersAudio = mMediaCodecAudio.getOutputBuffers();

            mMediaExtractorVideo.selectTrack(0); // 映像トラックを選択
            mMediaExtractorAudio.selectTrack(1); // 音声トラックを選択

            // AudioTrackを開始
            mAudioTrack.play();
            
            // 映像と音声スレッド開始
            mInputThreadVideo.start();
            mOutputThreadVideo.start();
            mInputThreadAudio.start();
            mOutputThreadAudio.start();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
        }
    }
}
