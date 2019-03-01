package com.huangtao.meetingroom.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.TimeUtils;
import com.huangtao.dialog.QRCodeDialog;
import com.huangtao.meetingroom.R;
import com.huangtao.meetingroom.adapter.RecyclerAttendantsAdapter;
import com.huangtao.meetingroom.arcsoft.RegisterAndRecognizeActivity;
import com.huangtao.meetingroom.common.Constants;
import com.huangtao.meetingroom.common.MyLazyFragment;
import com.huangtao.meetingroom.helper.CommonUtils;
import com.huangtao.meetingroom.model.Meeting;
import com.huangtao.meetingroom.model.User;
import com.huangtao.meetingroom.network.Network;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.BezierRadarHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class MainBusyFragment extends MyLazyFragment {

    final String TAG = "MainBusyFragment";

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.signin)
    Button signinButton;

    @BindView(R.id.signout)
    Button signoutButton;

    @BindView(R.id.qrcode)
    ImageView qrcode;

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    Meeting meeting;
    String meetingId;
    ProgressDialog progressDialog;
    RecyclerAttendantsAdapter attendantsAdapter;

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
    }

    @Override
    protected void initData() {
        meetingId = CommonUtils.getStringFromSharedPreference(getActivity(), "NextMeetingId");
        final ImageView listRefresh = findViewById(R.id.list_refresh);
        attendantsAdapter = new RecyclerAttendantsAdapter(getContext(), new ArrayList<Map.Entry<String, String>>());
        recyclerView.setAdapter(attendantsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));

        initList(meetingId);
        initRefreshLayout();

        listRefresh.setOnClickListener((v)->{
            listRefresh.setEnabled(false);
            initList(CommonUtils.getStringFromSharedPreference(getActivity(), "NextMeetingId"));
        });

        signinButton.setOnClickListener((v)->{
            startActivityForResult(new Intent(getActivity(), RegisterAndRecognizeActivity.class), 0);
        });

        signoutButton.setOnClickListener((v)->{
            //TODO 将状态设置为结束（取消掉注释）
            //meeting.setStatus(Status.Stopped);
            closeDoor();
            Network.getInstance().modifyMeeting(meeting, meeting.getId()).enqueue(new Callback<Meeting>() {
                @Override
                public void onResponse(Call<Meeting> call, Response<Meeting> response) {
                    Log.i(TAG, "结束会议成功");
                    Log.i(TAG, meeting.toString());
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_layout, new MainFreeFragment(), null)
                            .addToBackStack(null)
                            .commit();
                }

                @Override
                public void onFailure(Call<Meeting> call, Throwable t) {
                    Log.i(TAG, "结束会议失败");
                }
            });
        });

        qrcode.setOnClickListener((v)->{
            QRCodeDialog.Builder dialog = new QRCodeDialog.Builder(getFragmentActivity());
            dialog.setQrcode(CodeUtils.createImage(Constants.ROOM_ID, 400, 400, null));
            dialog.create().show();
        });
    }

    private void closeDoor() {
        if(Constants.bluetoothSocket == null){
            toast("蓝牙未连接");
        } else if(!Constants.bluetoothSocket.isConnected()){
            CommonUtils.connectRelay();
        }
        CommonUtils.closeRelay();
    }

    private void initList(String meetingId){
        final TextView listRefreshTime = findViewById(R.id.list_refresh_time);
        final ImageView listRefresh = findViewById(R.id.list_refresh);
        Network.getInstance().queryMeetingById(meetingId).enqueue(new Callback<Meeting>() {
            @Override
            public void onResponse(Call<Meeting> call, Response<Meeting> response) {
                meeting = response.body();
                List<Map.Entry<String, String>> attendants = new ArrayList<>(meeting.getAttendantsName().entrySet());
                attendants.sort((x,y)->{
                    return CommonUtils.compareDate(x.getValue(), y.getValue());
                });
                attendantsAdapter.setData(attendants);
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

    private void initRefreshLayout() {
        refreshLayout.setRefreshHeader(new BezierRadarHeader(getContext()).setEnableHorizontalDrag(true));
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                initList(meetingId);
                refreshlayout.finishRefresh(true);//传入false表示刷新失败
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadMore(500);//传入false表示加载失败
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){
            toast("人脸识别成功");
            String featureFileName = data.getStringExtra("featureFileName");
            Log.i(TAG, featureFileName);
            Network.getInstance().queryUser(null, null, featureFileName).enqueue(new Callback<List<User>>() {
                @Override
                public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                    Log.i(TAG, "获取user信息成功");
                    String id = response.body().get(0).getId();
                    meeting.getAttendants().put(id, CommonUtils.getTime());
                    initList(meeting.getId());
                    Network.getInstance().modifyMeeting(meeting, meeting.getId()).enqueue(new Callback<Meeting>() {
                        @Override
                        public void onResponse(Call<Meeting> call, Response<Meeting> response) {
                            Log.i(TAG, "修改会议状态成功");
                            Log.i(TAG, meeting.toString());
                        }

                        @Override
                        public void onFailure(Call<Meeting> call, Throwable t) {
                            Log.i(TAG, "修改会议状态失败");

                        }
                    });
                }

                @Override
                public void onFailure(Call<List<User>> call, Throwable t) {

                }
            });
        }
        else {
            toast("人脸识别失败");
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return false;
    }

}
