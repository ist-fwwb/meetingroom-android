package com.huangtao.meetingroom.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;

import com.huangtao.meetingroom.R;
import com.huangtao.meetingroom.activity.MainActivity;
import com.huangtao.meetingroom.adapter.MeetingroomListAdapter;
import com.huangtao.meetingroom.common.Constants;
import com.huangtao.meetingroom.common.MyLazyFragment;
import com.huangtao.meetingroom.helper.CommonUtils;
import com.huangtao.meetingroom.model.MeetingRoom;
import com.huangtao.meetingroom.network.Network;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GuideSecondFragment extends MyLazyFragment {
    ViewPager viewPager;

    public void setViewPager(ViewPager viewPager) {
        this.viewPager = viewPager;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_meetingroom_list;
    }

    @BindView(R.id.list)
    RecyclerView recyclerView;

    MeetingroomListAdapter meetingroomListAdapter;

    List<MeetingRoom> datas;

    @Override
    protected int getTitleBarId() {
        return R.id.title;
    }

    @Override
    protected void initView() {
        datas = new ArrayList<>();
        meetingroomListAdapter = new MeetingroomListAdapter(datas);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        recyclerView.setAdapter(meetingroomListAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration
                .VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    protected void initData() {
        Network.getInstance().queryMeetingroom(null, null).enqueue(new Callback<List<MeetingRoom>>() {
            @Override
            public void onResponse(Call<List<MeetingRoom>> call, Response<List<MeetingRoom>> response) {
                if (response.body() != null) {
                    datas.addAll(response.body());
                    meetingroomListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<MeetingRoom>> call, Throwable t) {
                toast("加载失败");
                t.printStackTrace();
            }
        });

        meetingroomListAdapter.setOnItemClickListener(new MeetingroomListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //TODO 上传deviceId
                Constants.ROOM_ID = datas.get(position).getId();
                Constants.ROOM_NAME = datas.get(position).getLocation();
                if (viewPager != null) viewPager.setCurrentItem(2);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
