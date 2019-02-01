package com.huangtao.dialog;

import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

import com.huangtao.base.BaseDialog;
import com.huangtao.base.BaseDialogFragment;

public class QRCodeDialog {

    public static class Builder
            extends BaseDialogFragment.Builder<Builder> {

        private boolean mAutoDismiss = true; // 设置点击按钮后自动消失

        private ImageView qrcode;
        private TextView attendNum;

        public Builder(FragmentActivity activity) {
            super(activity);
            initialize();
        }

        public Builder(FragmentActivity activity, int themeResId) {
            super(activity, themeResId);
            initialize();
        }

        private void initialize() {
            setContentView(R.layout.dialog_qrcode);
            setAnimStyle(BaseDialog.AnimStyle.IOS);
            setGravity(Gravity.CENTER);

            qrcode = findViewById(R.id.qrcode);
            attendNum = findViewById(R.id.attend_number);
        }

        public Builder setQrcode(Bitmap bm) {
            qrcode.setImageBitmap(bm);
            return this;
        }

        public Builder setMessage(CharSequence text) {
            attendNum.setText("会议码: " + text);
            return this;
        }


        public Builder setAutoDismiss(boolean dismiss) {
            mAutoDismiss = dismiss;
            return this;
        }
    }
}