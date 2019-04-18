package com.huangtao.meetingroom.activity;

import android.graphics.Color;

import com.huangtao.libguide.BaseGuideActivity;
import com.huangtao.libguide.BezierBannerDot;
import com.huangtao.meetingroom.R;
import com.huangtao.meetingroom.fragment.GuideFirstFragment;
import com.huangtao.meetingroom.fragment.GuideSecondFragment;
import com.huangtao.meetingroom.fragment.GuideThirdFragment;

public class GuideActivity extends BaseGuideActivity {
    @Override
    protected void initData() {
        GuideFirstFragment fragment1 = new GuideFirstFragment();
        fragment1.setViewPager(viewPager);
        GuideSecondFragment fragment2 = new GuideSecondFragment();
        fragment2.setViewPager(viewPager);
        GuideThirdFragment fragment3 = new GuideThirdFragment();
        addFragment(fragment1);
        addFragment(fragment2);
        addFragment(fragment3);
    }

    @Override
    protected void setIndicator(BezierBannerDot indicator) {
        indicator.setSelectedColor(getResources().getColor(R.color.colorPrimary));
        indicator.setUnSelectedColor(Color.RED);
    }
}
