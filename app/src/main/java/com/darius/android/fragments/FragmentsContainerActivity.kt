package com.darius.android.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.darius.android.R

class FragmentsContainerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragments)

        var fragmentName = intent.getStringExtra(FragmentsDemoAdapter.DATA)

        // 类实例化，到这里就可以访问TestReflect类的public属性的成员方法和成员变量了

        if (savedInstanceState == null) {
            //第一次启动Activity
            supportFragmentManager
                    .beginTransaction()
                    .add(R.id.fragments_container, Class.forName(fragmentName).newInstance() as Fragment)
                    .commit()
        }
    }
}
