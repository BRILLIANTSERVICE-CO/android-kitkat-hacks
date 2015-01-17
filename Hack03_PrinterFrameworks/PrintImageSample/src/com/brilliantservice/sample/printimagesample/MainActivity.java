
package com.brilliantservice.sample.printimagesample;

import com.brilliantservice.sample.printsample.R;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.print.PrintHelper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_print).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        startPrint();
                    }
                });
    }

    private void startPrint() {

        // プリントマネージャ取得
        PrintHelper printHelper = new PrintHelper(this);

        // カラーモード設定
        printHelper.setColorMode(PrintHelper.COLOR_MODE_COLOR);
        // 印刷向き設定
        printHelper.setOrientation(PrintHelper.ORIENTATION_LANDSCAPE);
        // スケールモード設定
        printHelper.setScaleMode(PrintHelper.SCALE_MODE_FIT);

        // 画像データ取得
        ImageView imageView = (ImageView) findViewById(R.id.iv_kklogo);
        BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();

        // 印刷開始
        printHelper.printBitmap("kk_logo.png - test print", bitmapDrawable.getBitmap());
    }
}
