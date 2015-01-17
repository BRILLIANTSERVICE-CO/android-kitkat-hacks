
package com.brilliantservice.sample.printhtmlsample;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;

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

    private WebView mWebView = null;

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (mWebView != null) {
            mWebView.destroy();
        }
    }

    private void startPrint() {

        if (mWebView != null) {
            mWebView.destroy();
        }

        // WebView生成
        mWebView = new WebView(this);

        // WebView Clientを登録し、ページ読み込み完了時のメソッドをオーバーライドする
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                // ページ読み込み完了で印刷処理実行
                doPrint(mWebView);
            }
        });

        // Googleページをロード
        mWebView.loadUrl("http://www.google.co.jp/");
    }

    private void doPrint(WebView webView) {
        // PrintManager取得
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);

        // PrintDocumentAdapter生成
        PrintDocumentAdapter printDocumentAdapter = webView.createPrintDocumentAdapter();

        // PrintAttributes生成
        PrintAttributes printAttributes = new PrintAttributes.Builder()
                //
                .setColorMode(PrintAttributes.COLOR_MODE_COLOR)
                //
                .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                //
                .setResolution(new PrintAttributes.Resolution("Brilliant", "Service", 600, 600))
                //
                .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                //
                .build();

        // 印刷実行
        printManager.print("Google page - test print", printDocumentAdapter, printAttributes);
    }
}
