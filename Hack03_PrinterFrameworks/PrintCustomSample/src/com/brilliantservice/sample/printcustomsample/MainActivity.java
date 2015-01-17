
package com.brilliantservice.sample.printcustomsample;

import java.io.FileOutputStream;
import java.io.IOException;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.print.pdf.PrintedPdfDocument;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_print).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doPrint();
                    }
                });
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private class MyPrintDocumentAdapter extends PrintDocumentAdapter {

        private PrintedPdfDocument mPdfDocument;
        private PrintDocumentInfo mDocumentInfo = null;
        private int mRenderPageWidth;
        private int mRenderPageHeight;

        private Context mContext;

        public MyPrintDocumentAdapter(Context context) {
            mContext = context;
        }

        @Override
        public void onLayout(PrintAttributes oldAttributes,
                PrintAttributes newAttributes,
                CancellationSignal cancellationSignal,
                LayoutResultCallback callback, Bundle extras) {

            // 渡された新しいページ属性のPDFドキュメントを作成します。
            mPdfDocument = new PrintedPdfDocument(mContext, newAttributes);

            // 既に印刷キャンセルされているかどうか確認
            if (cancellationSignal.isCanceled()) {
                // キャンセルされていた場合はonLayoutCancelled()をコールして終了
                callback.onLayoutCancelled();
                return;
            }

            boolean layoutChanged = false;

            // レイアウトの変更があったかどうかを確認します。
            layoutChanged = checkAttributes(newAttributes);

            // ページ数の計算処理
            int page = calculatePageCount(newAttributes);

            // 計算処理結果不正の場合はonLayoutFailed()をコール
            if (page <= 0) {
                callback.onLayoutFailed("Page Count Error!!");
                return;
            }
            else {
                // 印刷フレームワークへ印刷情報を渡します。
                PrintDocumentInfo info = new PrintDocumentInfo
                        .Builder("test.pdf")
                                .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                                .setPageCount(page)
                                .build();
                callback.onLayoutFinished(info, layoutChanged);
                mDocumentInfo = info;
            }
        }

        private int calculatePageCount(PrintAttributes printAttributes) {

            return 1;

        }

        private final int MILS_IN_INCH = 1000;

        private boolean checkAttributes(PrintAttributes printAttributes) {

            boolean ret = false;

            final int density = Math.max(printAttributes.getResolution().getHorizontalDpi(),
                    printAttributes.getResolution().getVerticalDpi());

            final int marginLeft = (int) (density * (float) printAttributes.getMinMargins()
                    .getLeftMils() / MILS_IN_INCH);
            final int marginRight = (int) (density * (float) printAttributes.getMinMargins()
                    .getRightMils() / MILS_IN_INCH);
            final int contentWidth = (int) (density * (float) printAttributes.getMediaSize()
                    .getWidthMils() / MILS_IN_INCH) - marginLeft - marginRight;
            if (mRenderPageWidth != contentWidth) {
                mRenderPageWidth = contentWidth;
                ret = true;
            }

            final int marginTop = (int) (density * (float) printAttributes.getMinMargins()
                    .getTopMils() / MILS_IN_INCH);
            final int marginBottom = (int) (density * (float) printAttributes.getMinMargins()
                    .getBottomMils() / MILS_IN_INCH);
            final int contentHeight = (int) (density * (float) printAttributes.getMediaSize()
                    .getHeightMils() / MILS_IN_INCH) - marginTop - marginBottom;
            if (mRenderPageHeight != contentHeight) {
                mRenderPageHeight = contentHeight;
                ret = true;
            }

            return ret;
        }

        @Override
        public void onWrite(PageRange[] pages,
                ParcelFileDescriptor destination,
                CancellationSignal cancellationSignal,
                WriteResultCallback callback) {

            // 既に印刷キャンセルされているかどうか確認
            if (cancellationSignal.isCanceled()) {
                // キャンセルされていた場合はonWriteCancelled()をコールして終了
                callback.onWriteCancelled();
                return;
            }

            if (mPdfDocument == null) {
                // エラーが発生していた場合はonWriteFailed()をコールして終了
                callback.onWriteFailed("Internal Error Occurred!!");
                return;
            }

            for (int i = 0; i < mDocumentInfo.getPageCount(); i++) {

                //
                PdfDocument.Page page = mPdfDocument.startPage(i);
                // ページ毎に描画を行います。
                drawPdfDocument(page.getCanvas());
                // ページの描画処理終了
                mPdfDocument.finishPage(page);
            }

            try {
                // ファイルへの書き込み
                mPdfDocument.writeTo(new FileOutputStream(destination.getFileDescriptor()));
            } catch (IOException e) {
                callback.onWriteFailed(e.toString());
                return;
            } finally {
                mPdfDocument.close();
                mPdfDocument = null;
            }

            callback.onWriteFinished(pages);
        }

        private void drawPdfDocument(Canvas canvas) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pic001);
            canvas.drawBitmap(bitmap, canvas.getWidth() / 2 - bitmap.getWidth() / 2,
                    canvas.getHeight() / 2 - bitmap.getHeight() / 2, new Paint());
        }

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void doPrint() {
        // PrintManager取得
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
        // PrintDocumentAdapter生成
        PrintDocumentAdapter printDocumentAdapter = new MyPrintDocumentAdapter(this);
        // 印刷実行
        printManager.print("test print", printDocumentAdapter, null);

    }
}
