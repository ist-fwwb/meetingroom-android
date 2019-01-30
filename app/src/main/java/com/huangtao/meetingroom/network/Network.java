package com.huangtao.meetingroom.network;

import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Network {

    private static Api api;

    public static Api getInstance() {
        if (api == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    //设置数据解析器
                    .addConverterFactory(GsonConverterFactory.create())
                    //设置网络请求的Url地址
                    .baseUrl("http://47.106.8.44:31000")
//                    .baseUrl("http://192.168.0.102:8080/")
                    .build();
            // 创建网络请求接口的实例
            api = retrofit.create(Api.class);
        }
        return api;
    }

    public static Api getInstance(Converter.Factory factory) {
        Retrofit retrofit = new Retrofit.Builder()
                //设置数据解析器
                .addConverterFactory(factory)
                //设置网络请求的Url地址
                .baseUrl("http://47.106.8.44:31000")
                .build();
        // 创建网络请求接口的实例
        return retrofit.create(Api.class);

    }

}
