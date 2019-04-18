package com.huangtao.meetingroom.fragment;

import android.content.Intent;
import android.widget.Button;

import com.huangtao.meetingroom.R;
import com.huangtao.meetingroom.activity.MainActivity;
import com.huangtao.meetingroom.common.MyLazyFragment;

import butterknife.BindView;

public class GuideThirdFragment extends MyLazyFragment {
    @BindView(R.id.start)
    Button button;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_guide3;
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
        button.setOnClickListener((v) -> {

            Intent intent=new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setClass(getContext(),MainActivity.class);
            startActivity(intent);
        });
    }
}
