package com.huangtao.meetingroom.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.Window;
import android.view.WindowManager;

import com.huangtao.meetingroom.R;
import com.huangtao.meetingroom.adapter.BluetoothListAdapter;
import com.huangtao.meetingroom.common.Constants;
import com.huangtao.meetingroom.common.MyActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;

public class BluetoothActivity extends MyActivity {

    @BindView(R.id.list)
    RecyclerView recyclerView;

    private BluetoothListAdapter bluetoothListAdapter;
    private List<BluetoothDevice> bluetooths;

    BluetoothAdapter mBluetoothAdapter;

    BroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_bluetooth;
    }

    @Override
    protected int getTitleBarId() {
        return 0;
    }

    @Override
    protected void initView() {
        bluetooths = new ArrayList<>();
        bluetoothListAdapter = new BluetoothListAdapter(bluetooths);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        recyclerView.setAdapter(bluetoothListAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        bluetoothListAdapter.setOnItemClickListener(new BluetoothListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if(mBluetoothAdapter.isDiscovering()){
                    mBluetoothAdapter.cancelDiscovery();
                }
                BluetoothDevice device = bluetooths.get(position);
                UUID uuid = UUID.fromString(Constants.SPP_UUID);
                try {
                    Constants.bluetoothSocket = device.createRfcommSocketToServiceRecord(uuid);
                    toast("开始连接…");
                    if (Constants.bluetoothSocket != null) {
                        Constants.bluetoothSocket.connect();
                        toast("连接成功");
                        finish();
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void initData() {
        checkBluetoothAndLocationPermission();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }
        // 创建一个接收ACTION_FOUND广播的BroadcastReceiver
        mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                // 发现设备
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // 从Intent中获取设备对象
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice
                            .EXTRA_DEVICE);

                    // 保存设备名称和地址，以便显示
                    System.out.println(device.getName() + " " + device.getAddress());
                    bluetooths.add(device);
                    bluetoothListAdapter.notifyDataSetChanged();
                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                    toast("搜索完毕");
                }
            }
        };
        // 注册BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);

        bluetooths.addAll(mBluetoothAdapter.getBondedDevices());
        bluetoothListAdapter.notifyDataSetChanged();
    }

    private void checkBluetoothAndLocationPermission() {
        //判断是否有访问位置的权限，没有权限，直接申请位置权限
        if ((checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) || (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean grantedLocation = true;
        if(requestCode == 0){
            for(int i : grantResults){
                if(i != PackageManager.PERMISSION_GRANTED){
                    grantedLocation = false;
                    break;
                }
            }
        }

        if(grantedLocation){

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
