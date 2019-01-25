package com.huangtao.meetingroom.helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.huangtao.meetingroom.common.Constants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

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
                //os.close();
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
                //os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void connectRelay() {
        if(Constants.bluetoothDevice != null){
            try {
                Constants.bluetoothSocket = Constants.bluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString(Constants.SPP_UUID));
                if (Constants.bluetoothSocket != null)
                    Constants.bluetoothSocket.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getFormatTime(int start, int end) {
        String first = start / 2 + ":" + ((start + 1) % 2 == 0 ? "30" : "00");
        String last = end / 2 + ":" + ((end + 1) % 2 == 0 ? "30" : "00");
        return first + " - " + last;
    }

    public static String getDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date());
    }

    public static String getTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(new Date());
    }

    public static void saveSharedPreference(Context context, String key, String value){
        SharedPreferences sharedPreferences = context.getSharedPreferences("meeting", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getStringFromSharedPreference(Context context, String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences("meeting", Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }

    public static int date2Time(){
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        int time = hour * 2 + minutes / 30;
        return time;
    }

    public static byte[] getBytes(String filePath){
        File file = new File(filePath);
        ByteArrayOutputStream out = null;
        try {
            FileInputStream in = new FileInputStream(file);
            out = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int i = 0;
            while ((i = in.read(b)) != -1) {

                out.write(b, 0, b.length);
            }
            out.close();
            in.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        byte[] s = out.toByteArray();
        return s;

    }
}
