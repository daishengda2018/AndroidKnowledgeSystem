package com.darius.android.activities.lifecycle;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.darius.android.R;

/**
 * Activity生命周期
 */
public class ActivitiesLifeCycleActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getName();
    private final static String NAME = "name";
    private final static String SEX = "sex";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activities_life_cycle_activity);
        Log.d(TAG,"onCreate");
    }

    /*
      Android 5.0以上的API，需要在AndroidManifests中为Activity配置android:persistableMode="persistAcrossReboots"才能使用。
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        String name = savedInstanceState.getString(NAME);
        String sex = savedInstanceState.getString(SEX);
        Log.d(TAG, "onCreate API > 21  " + name + "   " + sex);
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
        outState.putString(NAME, "DSD");
        outState.putString(SEX, "MAN");
        Log.d(TAG, "onSaveInstanceState API > V21");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG,"onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String name = savedInstanceState.getString(NAME);
        String sex = savedInstanceState.getString(SEX);
        Log.d(TAG, "onRestoreInstanceState " + sex + "   " + name);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(TAG,"onConfigurationChanged");
    }
}
