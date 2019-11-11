package com.example.dsd.demo.generics;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 泛型 Demo
 * Created by im_dsd on 2019-11-10
 */
public class GenericsDemo extends Activity {

    void demo1() {
        // list 中装某一 View 的子类, 此种方式可以取值，但是不能赋值。
        List<? extends View> list = new ArrayList<>();

        // list 中装任何可以承载 View 的对象，此种方式可以赋值，但是不能取值。
        List<? super View> list1 = new ArrayList<>();
        list1.add(new TextView(this));

        Map<String, List<?>> map = new HashMap<>();

    }
}
