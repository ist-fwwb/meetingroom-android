package com.huangtao.meetingroom.fragment;

import android.app.ProgressDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.blankj.utilcode.util.TimeUtils;
import com.huangtao.meetingroom.R;
import com.huangtao.meetingroom.adapter.AttendantsAdapter;
import com.huangtao.meetingroom.common.MyLazyFragment;
import com.huangtao.meetingroom.helper.CommonUtils;
import com.huangtao.meetingroom.model.Meeting;
import com.huangtao.meetingroom.mvp.copy.CopyContract;
import com.huangtao.meetingroom.network.Network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainBusyFragment extends MyLazyFragment {

    final String TAG = "MainBusyFragment";

    @BindView(R.id.list)
    ListView listView;

    @BindView(R.id.signin)
    Button signinButton;

    Meeting meeting;
    ProgressDialog progressDialog;

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
        View header = LayoutInflater.from(getFragmentActivity()).inflate(R.layout.item_main_busy_header, null, false);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("正在加载中……");
        listView.addHeaderView(header);
    }

    @Override
    protected void initData() {
        final ImageView listRefresh = findViewById(R.id.list_refresh);
        initList(CommonUtils.getStringFromSharedPreference(getActivity(), "NextMeetingId"));
        listRefresh.setOnClickListener((v)->{
            listRefresh.setEnabled(false);
            initList(CommonUtils.getStringFromSharedPreference(getActivity(), "NextMeetingId"));
        });
        signinButton.setOnClickListener((v)->{
            //TODO 人脸识别
        });
    }

    private void initList(String meetingId){
        final TextView listRefreshTime = findViewById(R.id.list_refresh_time);
        final ImageView listRefresh = findViewById(R.id.list_refresh);
        //TODO 拿到会议信息并更新或初始化变量meeting
        Network.getInstance().queryMeetingById(meetingId).enqueue(new Callback<Meeting>() {
            @Override
            public void onResponse(Call<Meeting> call, Response<Meeting> response) {
                meeting = response.body();
                List<Map.Entry<String, String>> attendants = new ArrayList<>(meeting.getAttendants().entrySet());
                attendants.sort((x,y)->{
                    return CommonUtils.compareDate(x.getValue(), y.getValue());
                });
                listView.setAdapter(new AttendantsAdapter(attendants));
                listRefreshTime.setText("上次更新: " + TimeUtils.millis2String(System.currentTimeMillis()));
                listRefresh.setEnabled(true);
                toast("刷新成功");
                Log.i(TAG, meeting.toString());
            }

            @Override
            public void onFailure(Call<Meeting> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

}
