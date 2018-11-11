package com.darius.android.activities.lifecycle;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.darius.android.R;

/**
 * Activity生命周期
 */
public class ActivitiesLifeCycleActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activities_life_cycle_activity);

        Log.d(TAG,"onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG,"onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG,"onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d(TAG,"onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.d(TAG,"onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        Log.d(TAG,"onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d(TAG,"onDestroy");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Log.d(TAG,"onNewIntent");
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

        Log.d(TAG,"onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        Log.d(TAG,"onRestoreInstanceState");
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        Log.d(TAG,"onConfigurationChanged");
    }
}
