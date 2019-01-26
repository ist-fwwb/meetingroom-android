package com.huangtao.meetingroom.fragment;

import com.huangtao.meetingroom.R;
import com.huangtao.meetingroom.common.MyLazyFragment;
import com.huangtao.meetingroom.model.Meeting;

public class MainBusyFragment extends MyLazyFragment {
    Meeting meeting;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main_busy;
    }

    @Override
    protected int getTitleBarId() {
        return 0;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }
}
