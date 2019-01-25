package com.huangtao.meetingroom.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.blankj.utilcode.util.TimeUtils;
import com.huangtao.meetingroom.R;
import com.huangtao.meetingroom.activity.MainActivity;
import com.huangtao.meetingroom.adapter.MeetingAdapter;
import com.huangtao.meetingroom.arcsoft.RegisterAndRecognizeActivity;
import com.huangtao.meetingroom.common.Constants;
import com.huangtao.meetingroom.common.MyLazyFragment;
import com.huangtao.meetingroom.helper.CommonUtils;
import com.huangtao.meetingroom.model.Meeting;
import com.huangtao.meetingroom.model.User;
import com.huangtao.meetingroom.model.meta.Status;
import com.huangtao.meetingroom.network.FileManagement;
import com.huangtao.meetingroom.network.Network;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class MainFreeFragment extends MyLazyFragment {

    ProgressDialog progressDialog;

    @BindView(R.id.list)
    ListView listView;

    @BindView(R.id.start)
    Button startButton;

    MeetingAdapter meetingAdapter;

    List<Meeting> meetings;
    Meeting nextMeeting;

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
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("正在加载中……");
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

        startButton.setOnClickListener((v) ->{
            nextMeeting = nextMeeting(meetings);
            if (nextMeeting == null) {
                toast("当前时间没有会议");
            }
            else {
                //getActivity().getSupportFragmentManager().beginTransaction().
                //        replace(R.id.fragment_layout, new MainBusyFragment(), null).commit();
                CommonUtils.saveSharedPreference(getActivity(), "NextMeetingId",nextMeeting.getId());
                prepareFilesBeforeRecognize(nextMeeting);
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
                meetingAdapter.setDatas(meetings);
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

    private Meeting nextMeeting(List<Meeting> meetings){
        if (meetings != null){
            int time = CommonUtils.date2Time() + 1;
            for (Meeting meeting : meetings){
                if (meeting.getStartTime() == time || meeting.getStartTime() == time-1) return meeting;
            }
        }
        return null;
    }

    private void prepareFilesBeforeRecognize(Meeting meeting){
        Map<String ,String> attendants = meeting.getAttendants();
        List<String> ids = new ArrayList<>(attendants.keySet());
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
                        if(!head.exists()) {
                            boolean result = FileManagement.download(getFragmentActivity().getApplicationContext(),
                                    fileName, Constants.HEAD_DIR);
                            if (!result) {
                                toast("参与者人脸信息拉取失败");
                                progressDialog.dismiss();
                                return;
                                //new HeadHandler().sendEmptyMessage(0);
                            }
                        }
                    }
                    CommonUtils.saveSharedPreference(getActivity(), "FaceNames", sb.toString());
                    new HeadHandler().sendEmptyMessage(0);
                }).run();

            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                toast("参与者人脸信息拉取失败");
            }
        });
    }

    @SuppressLint("HandlerLeak")
    private class HeadHandler extends android.os.Handler {
        @Override
        public void handleMessage(Message msg) {
            progressDialog.dismiss();
            //toast("拉取成功");
            startActivityForResult(new Intent(getActivity(), RegisterAndRecognizeActivity.class), 0);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            //TODO 上传会议信息并跳转到开会中fragment
            toast("人脸识别成功");
            String faceFeatureFile = data.getStringExtra("faceFeatureFile");
            Network.getInstance().queryUser(null, null, faceFeatureFile).enqueue(new Callback<List<User>>() {
                @Override
                public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                    String id = response.body().get(0).getId();
                    nextMeeting.getAttendants().put(id, CommonUtils.getTime());
                }

                @Override
                public void onFailure(Call<List<User>> call, Throwable t) {

                }
            });
            Network.getInstance().modifyMeeting(nextMeeting).enqueue(new Callback<Meeting>() {
                @Override
                public void onResponse(Call<Meeting> call, Response<Meeting> response) {

                }

                @Override
                public void onFailure(Call<Meeting> call, Throwable t) {

                }
            });
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_layout, new MainBusyFragment(), null)
                    .addToBackStack(null)
                    .commit();
        }
        else {
            toast("人脸识别失败");
        }
    }
}
