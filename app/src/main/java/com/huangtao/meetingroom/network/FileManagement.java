package com.huangtao.meetingroom.network;

import android.content.Context;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSAuthCredentialsProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectResult;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.huangtao.meetingroom.common.Constants;
import com.huangtao.meetingroom.model.User;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileManagement {

    private static OSS oss = null;

    public static void init(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 推荐使用OSSAuthCredentialsProvider，token过期后会自动刷新。 String stsServer =
                // "应用服务器地址，例如http://abc.com:8080"
                OSSCredentialProvider credentialProvider = new OSSAuthCredentialsProvider(Constants
                        .OSS_AUTH_SERVER);

                // 该配置类如果不设置，会有默认配置，具体可看该类
                ClientConfiguration conf = new ClientConfiguration();
                conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
                conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
                conf.setMaxConcurrentRequest(5); // 最大并发请求数，默认5个
                conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次

                oss = new OSSClient(context, Constants.OSS_endpoint, credentialProvider, conf);

                Log.i("FileManagement", "init success");
            }
        }).start();
    }

    public static OSS getInstance(Context context) {
        if(oss == null){
            init(context);
            return null;
        }
        return oss;
    }

    public static boolean upload(Context context, File file, String fileName) {
        if(getInstance(context) == null){
            return false;
        }

        // 构造上传请求。
        PutObjectRequest put = new PutObjectRequest(Constants.OSS_BUCKET, fileName, file
                .getAbsolutePath());

        // 异步上传时可以设置进度回调。
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
            }
        });

        final boolean[] uploadResult = {false};
        OSSAsyncTask task = getInstance(context).asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest,
                PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                Log.d("PutObject", "UploadSuccess");
                Log.d("ETag", result.getETag());
                Log.d("RequestId", result.getRequestId());
                uploadResult[0] = true;
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion,
                                  ServiceException serviceException) {
                // 请求异常。
                if (clientExcepion != null) {
                    // 本地异常，如网络异常等。
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常。
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
            }
        });
        Log.i("upload", "going to begin");
        task.waitUntilFinished();
        return uploadResult[0];
    }

    public static boolean download(Context context, final String fileName, final String dir) {
        if(getInstance(context) == null){
            return false;
        }

        // 构造下载文件请求。
        GetObjectRequest get = new GetObjectRequest(Constants.OSS_BUCKET, fileName);

        final boolean[] downloadResult = {false};
        OSSAsyncTask task = getInstance(context).asyncGetObject(get, new OSSCompletedCallback<GetObjectRequest, GetObjectResult>() {
            @Override
            public void onSuccess(GetObjectRequest request, GetObjectResult result) {
                // 请求成功。
                Log.d("asyncGetObject", "DownloadSuccess");
                Log.d("Content-Length", "" + result.getContentLength());

                File dirFile = new File(dir);
                if (!dirFile.exists()) {
                    dirFile.mkdirs();
                }
                File file = new File(dir + fileName);
                try {
                    OutputStream os = new FileOutputStream(file);

                    InputStream inputStream = result.getObjectContent();
                    byte[] buffer = new byte[2048];
                    int len;
                    while ((len = inputStream.read(buffer)) != -1) {
                        // 您可以在此处编写代码来处理下载的数据。
                        os.write(buffer, 0, len);
                    }
                    os.close();
                    inputStream.close();

                    downloadResult[0] = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            // GetObject请求成功，将返回GetObjectResult，其持有一个输入流的实例。返回的输入流，请自行处理。
            public void onFailure(GetObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                // 请求异常。
                if (clientExcepion != null) {
                    // 本地异常，如网络异常等。
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常。
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
            }
        });

        task.waitUntilFinished(); // 等待任务完成。
        return downloadResult[0];
    }

//    public static String getUserHead(Context context, User user) {
//        boolean result = FileManagement.download(context, user.getFaceFile(), Constants.HEAD_DIR);
//        if (result) {
//            File head = new File(Constants.HEAD_DIR + user.getFaceFile());
//            if (head.exists()) {
//                return head.getAbsolutePath();
//            }
//        }
//        return "";
//    }


}
