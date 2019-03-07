package com.huangtao.meetingroom.activity;

import android.graphics.Color;

import com.huangtao.libguide.BaseGuideActivity;
import com.huangtao.libguide.BezierBannerDot;
import com.huangtao.meetingroom.R;
import com.huangtao.meetingroom.fragment.guide1;
import com.huangtao.meetingroom.fragment.guide2;
import com.huangtao.meetingroom.fragment.guide3;

public class GuideActivity extends BaseGuideActivity {
    @Override
    protected void initData() {
        addFragment(guide1.newInstance("1", "1"));
        addFragment(guide2.newInstance("2", "2"));
        addFragment(guide3.newInstance("3", "3"));
    }

    @Override
    protected void setIndicator(BezierBannerDot indicator) {
        indicator.setSelectedColor(getResources().getColor(R.color.colorPrimary));
        indicator.setUnSelectedColor(Color.RED);
    }
}
