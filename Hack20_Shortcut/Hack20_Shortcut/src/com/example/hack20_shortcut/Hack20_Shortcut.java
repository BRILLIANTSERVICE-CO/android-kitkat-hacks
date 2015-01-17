
package com.example.hack20_shortcut;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class Hack20_Shortcut extends Activity {

    private static final String ACTION_INSTALL = "com.android.launcher.action.INSTALL_SHORTCUT";
    private static final String ACTION_UNINSTALL = "com.android.launcher.action.UNINSTALL_SHORTCUT";

    private Intent mShortcutIntent = null;
    private static final String SHORTCUT_NAME = "KitKat Hacks";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hack20__shortcut);

        mShortcutIntent = new Intent();
        mShortcutIntent.setClassName(getPackageName(), getClass().getName());
        mShortcutIntent.setAction(Intent.ACTION_MAIN);
    }

    public void onCreateShortcut(View view) {
        Intent intent = new Intent();
        intent.setAction(ACTION_INSTALL);
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, mShortcutIntent);
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, SHORTCUT_NAME);
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(getApplicationContext(),
                        R.drawable.ic_shortcut));
        sendBroadcast(intent);
    }

    public void onDeleteShortcut(View view) {
        Intent intent = new Intent();
        intent.setAction(ACTION_UNINSTALL);
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, mShortcutIntent);
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, SHORTCUT_NAME);
        sendBroadcast(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.hack20__shortcut, menu);
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
