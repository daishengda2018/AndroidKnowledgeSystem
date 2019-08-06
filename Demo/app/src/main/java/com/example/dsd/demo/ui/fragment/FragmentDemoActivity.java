package com.example.dsd.demo.ui.fragment;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.example.dsd.demo.R;

/**
 * Fragment Demo
 */
public class FragmentDemoActivity extends AppCompatActivity {
    FragmentA mFragmentA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_demo);

        // 1。 防止Fragment重叠的方式 ：  在创建的时候的时候添加Tag并存储起来
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("FragmentA");
        if (fragment == null) {
            mFragmentA = new FragmentA();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.container, mFragmentA, "FragmentA").commit();
        } else {
            mFragmentA = (FragmentA) fragment;
        }

        // 2 。通过savedInstanceState是否null，判断是否已经创建过
        if (savedInstanceState == null) {
            mFragmentA = new FragmentA();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.container, mFragmentA, "FragmentA").commit();
        } else {
            mFragmentA = (FragmentA) getSupportFragmentManager().findFragmentByTag("FragmentA");
        }

        //------------------------------------------//
        // 设置过场动画
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.setCustomAnimations()
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
         // 3。防止Fragment重叠的方式 ： 在Activity异常销毁的时候，不做保存
         // super.onSaveInstanceState(outState, outPersistentState);
    }
}
