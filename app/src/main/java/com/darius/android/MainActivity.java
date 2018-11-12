package com.darius.android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerViewContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerViewContent = findViewById(R.id.recycler_view_content);
        mRecyclerViewContent.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerViewContent.setAdapter(new MainRecyclerAdapter(this));
    }

}
