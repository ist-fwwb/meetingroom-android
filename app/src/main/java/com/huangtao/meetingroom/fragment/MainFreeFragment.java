package com.huangtao.meetingroom.fragment;

import com.huangtao.meetingroom.R;
import com.huangtao.meetingroom.common.MyLazyFragment;

public class MainFreeFragment extends MyLazyFragment {

    public static MainFreeFragment newInstance() {
        MainFreeFragment fragment = new MainFreeFragment();
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main_free;
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
