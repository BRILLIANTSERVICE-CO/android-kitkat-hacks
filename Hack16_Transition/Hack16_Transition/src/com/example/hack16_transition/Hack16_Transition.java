
package com.example.hack16_transition;

import android.app.Activity;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.Scene;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class Hack16_Transition extends Activity {

    private Scene mScene1;
    private Scene mScene2;
    private TransitionSet mTransitionSet;

    private ViewGroup mRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hack16__transition);

        mRoot = (ViewGroup) findViewById(R.id.root);

        mScene1 = Scene.getSceneForLayout(mRoot, R.layout.transition_1, this);
        mScene2 = Scene.getSceneForLayout(mRoot, R.layout.transition_2, this);

        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.addTarget(R.id.button);

        mTransitionSet = new TransitionSet();
        mTransitionSet.addTransition(changeBounds);
        mTransitionSet.setOrdering(TransitionSet.ORDERING_SEQUENTIAL);
        mTransitionSet.setDuration(1000);
    }

    public void onClickBtn(View v) {
        TransitionManager.go(mScene2, mTransitionSet);
    }

    public void onClickBtn2(View v) {
        TransitionManager.go(mScene1, mTransitionSet);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.hack16__transition, menu);
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
