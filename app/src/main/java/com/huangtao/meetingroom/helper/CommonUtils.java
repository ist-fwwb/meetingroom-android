package com.huangtao.meetingroom.helper;

import com.huangtao.meetingroom.common.Constants;

import java.io.IOException;
import java.io.OutputStream;

public class CommonUtils {

    public static byte[] getHexBytes(String message) {
        int len = message.length() / 2;
        char[] chars = message.toCharArray();
        String[] hexStr = new String[len];
        byte[] bytes = new byte[len];
        for (int i = 0, j = 0; j < len; i += 2, j++) {
            hexStr[j] = "" + chars[i] + chars[i + 1];
            bytes[j] = (byte) Integer.parseInt(hexStr[j], 16);
        }
        return bytes;
    }

    public static void openRelay() {
        if(Constants.bluetoothSocket != null && Constants.bluetoothSocket.isConnected()){
            try {
                OutputStream os = Constants.bluetoothSocket.getOutputStream();
                os.write(CommonUtils.getHexBytes(Constants.BLUETOOTH_OPEN));
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void closeRelay() {
        if(Constants.bluetoothSocket != null && Constants.bluetoothSocket.isConnected()){
            try {
                OutputStream os = Constants.bluetoothSocket.getOutputStream();
                os.write(CommonUtils.getHexBytes(Constants.BLUETOOTH_CLOSE));
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
