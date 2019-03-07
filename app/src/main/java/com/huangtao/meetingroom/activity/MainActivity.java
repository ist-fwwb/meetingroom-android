package com.huangtao.meetingroom.activity;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

import com.huangtao.meetingroom.R;
import com.huangtao.meetingroom.common.Constants;
import com.huangtao.meetingroom.common.MyActivity;
import com.huangtao.meetingroom.fragment.MainFreeFragment;
import com.huangtao.meetingroom.fragment.MeetingRoomListFragment;
import com.huangtao.meetingroom.helper.CommonUtils;

import java.io.IOException;

import butterknife.BindView;

//TODO 引导配置会议室页面

public class MainActivity extends MyActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.open)
    Button open;
    @BindView(R.id.close)
    Button close;

    @BindView(R.id.fragment_layout)
    FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected int getTitleBarId() {
        return 0;
    }

    @Override
    protected void initView() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Constants.bluetoothSocket == null){
                    toast("蓝牙未连接");
                    return;
                } else if(!Constants.bluetoothSocket.isConnected()){
                    CommonUtils.connectRelay();
                }
                CommonUtils.openRelay();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Constants.bluetoothSocket == null){
                    toast("蓝牙未连接");
                } else if(!Constants.bluetoothSocket.isConnected()){
                    CommonUtils.connectRelay();
                }
                CommonUtils.closeRelay();
            }
        });

        if (Constants.ROOM_ID.isEmpty()) switchFragment(MeetingRoomListFragment.newInstance());
        else switchFragment(MainFreeFragment.getInstance());
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_bluetooth) {
            startActivity(BluetoothActivity.class);
        } else if (id == R.id.nav_meetingRoom) {
            switchFragment(MeetingRoomListFragment.newInstance());
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void switchFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.fragment_layout, fragment);
        transaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(Constants.bluetoothSocket != null){
            try {
                Constants.bluetoothSocket.close();
                Constants.bluetoothSocket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
