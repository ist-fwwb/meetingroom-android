package com.huangtao.meetingroom.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.TimeUtils;
import com.huangtao.dialog.QRCodeDialog;
import com.huangtao.meetingroom.R;
import com.huangtao.meetingroom.adapter.RecyclerMeetingAdapter;
import com.huangtao.meetingroom.arcsoft.RegisterAndRecognizeActivity;
import com.huangtao.meetingroom.common.Constants;
import com.huangtao.meetingroom.common.MyLazyFragment;
import com.huangtao.meetingroom.common.MyRecyclerViewAdapter;
import com.huangtao.meetingroom.helper.CommonUtils;
import com.huangtao.meetingroom.model.ForeignGuest;
import com.huangtao.meetingroom.model.Meeting;
import com.huangtao.meetingroom.model.TimeInterval;
import com.huangtao.meetingroom.model.User;
import com.huangtao.meetingroom.model.meta.MeetingType;
import com.huangtao.meetingroom.model.meta.Status;
import com.huangtao.meetingroom.network.FileManagement;
import com.huangtao.meetingroom.network.Network;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.BezierRadarHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class MainFreeFragment extends MyLazyFragment {
    static MainFreeFragment mainFreeFragment = null;
    private static final String TAG = "MainFreefragment";
    static final int VERIFY = 1;
    static final int ADDMEETING = 2;

    ProgressDialog progressDialog;

    @BindView(R.id.start)
    Button startButton;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.qrcode)
    ImageView qrcode;

    @BindView(R.id.guest)
    Button guestButton;

    @BindView(R.id.register)
    Button registerButton;

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    @BindView(R.id.meetingroom_name)
    TextView meetingroom_name;

    List<Meeting> meetings;
    Meeting nextMeeting;
    MyRecyclerViewAdapter myRecyclerViewAdapter;

    public static MainFreeFragment getInstance() {
        if (mainFreeFragment == null){
            synchronized (MainFreeFragment.class){
                if (mainFreeFragment == null) {
                    mainFreeFragment = new MainFreeFragment();
                }
            }
        }
        return mainFreeFragment;
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
        //View header = LayoutInflater.from(getFragmentActivity()).inflate(R.layout.item_main_free_header, null, false);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("正在加载中……");
        meetingroom_name.setText(Constants.ROOM_NAME);
        //recyclerView.addHeader(header);
    }

    @Override
    protected void initData() {
        // 用bindview会null。。
        final ImageView listRefresh = findViewById(R.id.list_refresh);
        myRecyclerViewAdapter = new RecyclerMeetingAdapter(getContext(), meetings);
        recyclerView.setAdapter(myRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
        initList();
        initRefreshLayout();

        listRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listRefresh.setEnabled(false);
                initList();
            }
        });

        startButton.setOnClickListener((v) ->{
            nextMeeting = nextMeeting(meetings);
            if (nextMeeting == null) {
                toast("当前时间没有会议");
            }
            else {
                //getActivity().getSupportFragmentManager().beginTransaction().
                //        replace(R.id.fragment_layout, new MainBusyFragment(), null).commit();
                CommonUtils.saveSharedPreference(getActivity(), "NextMeetingId",nextMeeting.getId());
                prepareFilesBeforeRecognize(nextMeeting, VERIFY);
            }
        });

        registerButton.setOnClickListener((v)->{
            initList();
            nextMeeting = generateNextMeeting();
            if (nextMeeting == null){
                toast("当前时间段会议室已被预订");
            }
            else {
                prepareAllUsersFilesBeforeRecognize();
            }
        });

        guestButton.setOnClickListener((v) ->{
            nextMeeting = nextMeeting(meetings);
            if (nextMeeting == null) {
                toast("当前时间没有会议");
            }
            else {
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.alert_dialog, null);
                new AlertDialog.Builder(getActivity()).setTitle("嘉宾验证")
                        .setView(view)
                        .setPositiveButton("确认", (dialog, which) -> {
                            String checkNum = ((EditText)view.findViewById(R.id.checkNum)).getText().toString();
                            ForeignGuest foreignGuest = checkGuestSuccess(checkNum);
                            if (foreignGuest != null){
                                toast("欢迎你, "+foreignGuest.getName());
                                new FragmentHandler().sendEmptyMessage(0);
                            }
                            else {
                                toast("验证失败，请重试");
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
        });

        qrcode.setOnClickListener((v)->{
            QRCodeDialog.Builder dialog = new QRCodeDialog.Builder(getFragmentActivity());
            dialog.setQrcode(CodeUtils.createImage(Constants.ROOM_ID, 400, 400, null));
            dialog.create().show();
        });
    }

    private Meeting generateNextMeeting() {
        TimeInterval timeInterval = findNextFreeTimeInterval();
        if (timeInterval == null) return null;
        Meeting meeting = new Meeting();
        meeting.setHeading(CommonUtils.getDate() + "临时预定");
        meeting.setDescription("临时预定");
        meeting.setRoomId(Constants.ROOM_ID);
        meeting.setDate(CommonUtils.getDate());
        meeting.setStartTime(timeInterval.getStart());
        meeting.setEndTime(timeInterval.getEnd());
        meeting.setNeedSignIn(false);
        meeting.setType(MeetingType.COMMON);
        meeting.setStatus(Status.Pending);
        return meeting;
    }

    private TimeInterval findNextFreeTimeInterval() {
        int start = CommonUtils.date2Time();
        int end = start + 2;
        TimeInterval res = new TimeInterval(start, end);
        for (Meeting meeting : meetings){
            if (meeting.isOverLapped(res)) return null;
        }
        return res;
    }

    private ForeignGuest checkGuestSuccess(String checkNum) {
        List<ForeignGuest> guests = nextMeeting.getForeignGuestList();
        for (ForeignGuest foreignGuest : guests){
            if (foreignGuest.getPhone().equals(checkNum) || foreignGuest.getUUID().equals(checkNum)) return foreignGuest;
        }
        return null;
    }

    private void initRefreshLayout() {
        refreshLayout.setRefreshHeader(new BezierRadarHeader(getContext()).setEnableHorizontalDrag(true));
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                initList();
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


    private void initList() {
        final TextView listRefreshTime = findViewById(R.id.list_refresh_time);
        final ImageView listRefresh = findViewById(R.id.list_refresh);

        Network.getInstance().queryMeeting(CommonUtils.getDate(), Constants.ROOM_ID, Status.Pending).enqueue(new Callback<List<Meeting>>() {
            @Override
            public void onResponse(Call<List<Meeting>> call, Response<List<Meeting>> response) {
                meetings = response.body();
                Collections.sort(meetings, (x,y)->{
                   return x.getStartTime() - y.getStartTime();
                });
                myRecyclerViewAdapter.setData(meetings);
                listRefreshTime.setText("上次更新: " + TimeUtils.millis2String(System.currentTimeMillis()));
                listRefresh.setEnabled(true);
            }

            @Override
            public void onFailure(Call<List<Meeting>> call, Throwable t) {
                toast("刷新失败");
                listRefresh.setEnabled(true);
            }
        });
    }

    private Meeting nextMeeting(List<Meeting> meetings){
        if (meetings != null){
            int time = CommonUtils.date2Time() + 1;
            for (Meeting meeting : meetings){
                if (meeting.getStartTime() == time || meeting.getStartTime() == time-1) return meeting;
            }
        }
        return null;
    }

    private void prepareAllUsersFilesBeforeRecognize(){
        prepareFilesBeforeRecognize(null, ADDMEETING);
    }

    private void prepareFilesBeforeRecognize(Meeting meeting, int requestCode){
        Log.d(TAG, "prepareFilesBeforeRecognize: start");
        List<String> ids = null;
        if (meeting != null){
            ids = new ArrayList<>(meeting.getAttendants().keySet());
        }
        Network.getInstance().queryUser(null, ids, null).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                List<User> users = response.body();
                progressDialog.show();
                new Thread(()->{
                    StringBuilder sb = new StringBuilder();
                    for (User user : users){
                        final String fileName = user.getFeatureFile();
                        sb.append(fileName); sb.append(' ');
                        File head = new File(Constants.HEAD_DIR + fileName);
                        Log.d(TAG, "onResponse: " + head.getAbsolutePath());
                        if(!head.exists()) {
                            Log.d(TAG, "onResponse: head not exit");
                            FileManagement.download(getFragmentActivity().getApplicationContext(),
                                    fileName, Constants.HEAD_DIR);
                        }
                    }
                    CommonUtils.saveSharedPreference(getActivity(), "FaceNames", sb.toString());
                    progressDialog.dismiss();
                    //toast("拉取成功");
                    startActivityForResult(new Intent(getActivity(), RegisterAndRecognizeActivity.class), requestCode);
                }).run();
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                toast("参与者人脸信息拉取失败");
            }
        });
    }

    @SuppressLint("HandlerLeak")
    private class FragmentHandler extends android.os.Handler {
        @Override
        public void handleMessage(Message msg) {
            MainBusyFragment.getInstance().setMeeting(nextMeeting);
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_layout, MainBusyFragment.getInstance(), null)
                    .addToBackStack(null)
                    .commit();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case VERIFY:
                verifyCallback(resultCode, data);
                break;
            case ADDMEETING:
                addMeetingCallback(resultCode, data);
                break;
        }
    }

    private void addMeetingCallback(int resultCode, Intent data) {
        if (resultCode == RESULT_OK){
            String featureFileName = data.getStringExtra("featureFileName");
            Log.i("freeFragment", featureFileName);
            Network.getInstance().queryUser(null, null, featureFileName).enqueue(new Callback<List<User>>() {
                @Override
                public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                    Log.i("freeFragment", "获取user信息成功");
                    User user = response.body().get(0);
                    //TODO 修改会议状态
                    nextMeeting.setHostId(user.getId());
                    //nextMeeting.setStatus(Status.Running);
                    Network.getInstance().appointMeetingroom(nextMeeting).enqueue(new MeetingCallback(user));
                }

                @Override
                public void onFailure(Call<List<User>> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
        else {
            toast("人脸识别失败");
        }
    }

    private void processMeeting(User user) {
        if (nextMeeting.modifyMeetingSuccessful()){
            toast("欢迎你， " + user.getName());
            openDoor();
            new FragmentHandler().sendEmptyMessage(0);
        }
        else {
            toast("网络出了点问题");
        }
    }

    private void verifyCallback(int resultCode, Intent data) {
        if (resultCode == RESULT_OK){
            String featureFileName = data.getStringExtra("featureFileName");
            Log.i("freeFragment", featureFileName);
            Network.getInstance().queryUser(null, null, featureFileName).enqueue(new Callback<List<User>>() {
                @Override
                public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                    Log.i("freeFragment", "获取user信息成功");
                    User user = response.body().get(0);
                    nextMeeting.getAttendants().put(user.getId(), CommonUtils.getTime());
                    //TODO 设置会议状态(取消注释）
                    //nextMeeting.setStatus(Status.Running);
                    Network.getInstance().modifyMeeting(nextMeeting, nextMeeting.getId()).enqueue(new MeetingCallback(user));
                }

                @Override
                public void onFailure(Call<List<User>> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
        else {
            toast("人脸识别失败");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initList();
    }

    private void openDoor() {
        if(Constants.bluetoothSocket == null){
            toast("蓝牙未连接");
        } else if(!Constants.bluetoothSocket.isConnected()){
            CommonUtils.connectRelay();
        }
        CommonUtils.openRelay();
    }

    private class MeetingCallback implements Callback<Meeting> {
        private final User user;

        public MeetingCallback(User user) {
            this.user = user;
        }

        @Override
        public void onResponse(Call<Meeting> call, Response<Meeting> response) {
            nextMeeting = response.body();
            processMeeting(user);
        }

        @Override
        public void onFailure(Call<Meeting> call, Throwable t) {
            toast("网络错误");
        }
    }
}
