
package com.brilliantservice.sample.storageaccessframeworksample;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private final int OPEN_DOCUMENT_REQUEST = 1;
    private final int CREATE_DOCUMENT_REQUEST = 2;
    private final int DELETE_DOCUMENT_REQUEST = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.item1) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            // intent.setType("text/plain");
            intent.setType("image/*");
            // intent.setType("audio/*");
            // intent.setType("video/*");
            // intent.setType("*/*");
            startActivityForResult(intent, OPEN_DOCUMENT_REQUEST);
            return true;
        }
        else if (id == R.id.item2) {
            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.setType("image/png");
            intent.putExtra(Intent.EXTRA_TITLE, "無題.png");
            startActivityForResult(intent, CREATE_DOCUMENT_REQUEST);
        }
        else if (id == R.id.item3) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("image/*");
            startActivityForResult(intent, DELETE_DOCUMENT_REQUEST);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case OPEN_DOCUMENT_REQUEST:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    ImageView iv = (ImageView) findViewById(R.id.iv_load);
                    InputStream is;
                    try {
                        is = getContentResolver().openInputStream(uri);
                        iv.setImageBitmap(BitmapFactory.decodeStream(is));
                        is.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                break;

            case CREATE_DOCUMENT_REQUEST:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    ImageView iv = (ImageView) findViewById(R.id.iv_load);
                    OutputStream os;
                    try {
                        os = getContentResolver().openOutputStream(uri);
                        BitmapDrawable bitmapDrawable = (BitmapDrawable) iv.getDrawable();
                        bitmapDrawable.getBitmap().compress(CompressFormat.PNG, 100, os);
                        os.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case DELETE_DOCUMENT_REQUEST:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    if (false == DocumentsContract.deleteDocument(getContentResolver(), uri)) {
                        Toast.makeText(getApplicationContext(), "ファイルの削除に失敗しました。",
                                Toast.LENGTH_LONG).show();
                    }
                }
        }
    }
}
