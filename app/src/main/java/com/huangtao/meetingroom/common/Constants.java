package com.huangtao.meetingroom.common;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class Constants {

    public static String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
    public static BluetoothDevice bluetoothDevice = null;
    public static BluetoothSocket bluetoothSocket = null;
    public static String BLUETOOTH_OPEN = "A00101A2";
    public static String BLUETOOTH_CLOSE = "A00100A1";

    // arcsoft
    public static final String APP_ID = "F1vNFhZAoVSdhNXaCzpLvBvcMW9aeqH6e68aSH5GUB3C";
    public static final String SDK_KEY = "7Lq9LdfNhCJxkw4zbBPk2hBz3KhbiuBSXhy3sGq3EogE";

    // ali oss
    public static final String OSS_AccessKeyId = "LTAIqMIT5KX4oGAT";
    public static final String OSS_AccessKeySecret = "wYwZdNHrnvAiM9GNddiXqaeHcB4xfz";
    public static final String OSS_BUCKET = "face-file";
    public static final String OSS_DIR_FACE = OSS_BUCKET + "/face-file";
    public static final String OSS_DIR_FEATURE = "face-feature-file";
    public static final String OSS_endpoint = "http://oss-cn-shanghai.aliyuncs.com";
    public static final String OSS_AUTH_SERVER = "http://129.211.119.51:7080/";
}
