package com.huangtao.meetingroom.fragment;

import com.huangtao.meetingroom.common.MyLazyFragment;

public class MeetingRoomListFragment extends MyLazyFragment {
    public static MeetingRoomListFragment newInstance(){
        return new MeetingRoomListFragment();
    }

    @Override
    protected int getLayoutId() {
        return 0;
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
