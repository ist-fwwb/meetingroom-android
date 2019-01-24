package com.huangtao.meetingroom.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.blankj.utilcode.util.TimeUtils;
import com.huangtao.meetingroom.R;
import com.huangtao.meetingroom.adapter.MeetingAdapter;
import com.huangtao.meetingroom.common.MyLazyFragment;
import com.huangtao.meetingroom.model.Meeting;
import com.huangtao.meetingroom.model.meta.Status;
import com.huangtao.meetingroom.network.Network;

import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainFreeFragment extends MyLazyFragment {

    @BindView(R.id.list)
    ListView listView;

    MeetingAdapter meetingAdapter;

    public static MainFreeFragment newInstance() {
        return new MainFreeFragment();
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
        View header = LayoutInflater.from(getFragmentActivity()).inflate(R.layout.item_main_free_header, null, false);
        listView.addHeaderView(header);
    }

    @Override
    protected void initData() {
        // 用bindview会null。。
        final ImageView listRefresh = findViewById(R.id.list_refresh);

        meetingAdapter = new MeetingAdapter();
        listView.setAdapter(meetingAdapter);

        initList();

        listRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listRefresh.setEnabled(false);
                initList();
            }
        });
    }

    private void initList() {
        final TextView listRefreshTime = findViewById(R.id.list_refresh_time);
        final ImageView listRefresh = findViewById(R.id.list_refresh);

        Network.getInstance().queryMeeting(null, null, Status.Pending).enqueue(new Callback<List<Meeting>>() {
            @Override
            public void onResponse(Call<List<Meeting>> call, Response<List<Meeting>> response) {
                meetingAdapter.setDatas(response.body());
                listRefreshTime.setText("上次更新: " + TimeUtils.millis2String(System.currentTimeMillis()));
                listRefresh.setEnabled(true);
                toast("刷新成功");
            }

            @Override
            public void onFailure(Call<List<Meeting>> call, Throwable t) {
                toast("刷新失败");
                listRefresh.setEnabled(true);
            }
        });
    }
}
