package com.example.dsd.demo.ui.transfer.wechat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.example.dsd.demo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * todo
 * Created by im_dsd on 2019-11-06
 */
public class WeChatDemoActivity extends FragmentActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {
    private ViewPager mViewPager;
    private List<Fragment> mTabs = new ArrayList<Fragment>();
    private FragmentPagerAdapter mAdapter;

    private String[] mTitles = new String[]{"First Fragment!",
        "Second Fragment!", "Third Fragment!", "Fourth Fragment!"};

    private List<WeChatTagView> mTabIndicator = new ArrayList<WeChatTagView>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_wechat_tab_demo);
        mViewPager = findViewById(R.id.id_viewpager);
        initDatas();
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(this);
    }

    private void initDatas() {
        for (String title : mTitles) {
            WechatTabFragment tabFragment = new WechatTabFragment();
            Bundle args = new Bundle();
            args.putString("title", title);
            tabFragment.setArguments(args);
            mTabs.add(tabFragment);
        }

        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return mTabs.size();
            }
            @Override
            public Fragment getItem(int arg0) {
                return mTabs.get(arg0);
            }
        };
        initTabIndicator();
    }

    private void initTabIndicator() {
        WeChatTagView one = findViewById(R.id.id_indicator_1);
        WeChatTagView two = findViewById(R.id.id_indicator_2);
        WeChatTagView three = findViewById(R.id.id_indicator_3);
        WeChatTagView four = findViewById(R.id.id_indicator_4);

        mTabIndicator.add(one);
        mTabIndicator.add(two);
        mTabIndicator.add(three);
        mTabIndicator.add(four);

        one.setOnClickListener(this);
        two.setOnClickListener(this);
        three.setOnClickListener(this);
        four.setOnClickListener(this);
        one.setIconAlpha(1.0f);
    }

    @Override
    public void onPageSelected(int arg0) {
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (positionOffset > 0) {
            WeChatTagView left = mTabIndicator.get(position);
            WeChatTagView right = mTabIndicator.get(position + 1);
            left.setIconAlpha(1 - positionOffset);
            right.setIconAlpha(positionOffset);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View v) {
        resetOtherTabs();
        switch (v.getId()) {
            case R.id.id_indicator_1:
                mTabIndicator.get(0).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(0, false);
                break;
            case R.id.id_indicator_2:
                mTabIndicator.get(1).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(1, false);
                break;
            case R.id.id_indicator_3:
                mTabIndicator.get(2).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(2, false);
                break;
            case R.id.id_indicator_4:
                mTabIndicator.get(3).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(3, false);
                break;

        }
    }

    /**
     * 重置其他的Tab
     */
    private void resetOtherTabs() {
        for (int i = 0; i < mTabIndicator.size(); i++) {
            mTabIndicator.get(i).setIconAlpha(0);
        }
    }
}