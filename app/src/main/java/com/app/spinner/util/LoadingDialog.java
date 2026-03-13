package com.app.spinner.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

import com.app.spinner.R;

public class LoadingDialog {
    private Dialog dialog;

    public void show(Context context) {
        // Khởi tạo dialog
        dialog = new Dialog(context);

        // 1. Xóa tiêu đề mặc định
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 2. Gán layout đã tạo
        dialog.setContentView(R.layout.layout_loading);

        // 3. QUAN TRỌNG: Làm trong suốt khung nền trắng của Dialog
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        // 4. Không cho phép người dùng hủy bằng cách nhấn back hoặc nhấn ra ngoài
        dialog.setCancelable(false);

        dialog.show();
    }

    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}