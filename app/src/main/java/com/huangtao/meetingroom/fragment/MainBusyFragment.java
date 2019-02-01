package com.huangtao.meetingroom.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.blankj.utilcode.util.TimeUtils;
import com.huangtao.dialog.QRCodeDialog;
import com.huangtao.meetingroom.R;
import com.huangtao.meetingroom.adapter.AttendantsAdapter;
import com.huangtao.meetingroom.arcsoft.RegisterAndRecognizeActivity;
import com.huangtao.meetingroom.common.Constants;
import com.huangtao.meetingroom.common.MyLazyFragment;
import com.huangtao.meetingroom.helper.CommonUtils;
import com.huangtao.meetingroom.model.Meeting;
import com.huangtao.meetingroom.model.User;
import com.huangtao.meetingroom.model.meta.Status;
import com.huangtao.meetingroom.mvp.copy.CopyContract;
import com.huangtao.meetingroom.network.Network;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class MainBusyFragment extends MyLazyFragment {

    final String TAG = "MainBusyFragment";

    @BindView(R.id.list)
    ListView listView;

    @BindView(R.id.signin)
    Button signinButton;

    @BindView(R.id.signout)
    Button signoutButton;

    @BindView(R.id.qrcode)
    ImageView qrcode;


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
            startActivityForResult(new Intent(getActivity(), RegisterAndRecognizeActivity.class), 0);
        });

        signoutButton.setOnClickListener((v)->{
            //TODO 将状态设置为结束（取消掉注释）
            //meeting.setStatus(Status.Stopped);
            closeDoor();
            Network.getInstance().modifyMeeting(meeting.getId(), meeting).enqueue(new Callback<Meeting>() {
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
                    Network.getInstance().modifyMeeting(meeting.getId(), meeting).enqueue(new Callback<Meeting>() {
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
}
