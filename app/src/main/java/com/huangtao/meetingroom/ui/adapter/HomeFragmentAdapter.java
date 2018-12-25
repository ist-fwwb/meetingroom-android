package com.huangtao.meetingroom.ui.adapter;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.huangtao.base.BaseFragmentAdapter;
import com.huangtao.meetingroom.common.MyLazyFragment;
import com.huangtao.meetingroom.ui.fragment.TestFragmentA;
import com.huangtao.meetingroom.ui.fragment.TestFragmentB;
import com.huangtao.meetingroom.ui.fragment.TestFragmentC;
import com.huangtao.meetingroom.ui.fragment.TestFragmentD;

import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 主页界面 ViewPager + Fragment 适配器
 */
public final class HomeFragmentAdapter extends BaseFragmentAdapter<MyLazyFragment> {

    public HomeFragmentAdapter(FragmentActivity activity) {
        super(activity);
    }

    @Override
    protected void init(FragmentManager manager, List<MyLazyFragment> list) {
        list.add(TestFragmentA.newInstance());
        list.add(TestFragmentB.newInstance());
        list.add(TestFragmentC.newInstance());
        list.add(TestFragmentD.newInstance());
    }
}