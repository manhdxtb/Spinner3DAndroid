package com.app.spinner.util;

import android.app.Activity;
import android.app.Dialog;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.app.spinner.R;

public class PopupCustom {
    public static void showPopupChooseStrokeType(Activity activity, int select, OnItemClickListener listener) {
        final Dialog dialog = new Dialog(activity, R.style.PopupCustom);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_choose_brush);
        dialog.setCancelable(true);

        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(window.getAttributes());
            lp.dimAmount = 0.7f;

            DisplayMetrics displayMetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

            int marginPx = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 15, activity.getResources().getDisplayMetrics());

            lp.width = displayMetrics.widthPixels - (2 * marginPx);


            window.setAttributes(lp);
        }

        View[] items = new View[4];
        items[0] = dialog.findViewById(R.id.item_0);
        items[1] = dialog.findViewById(R.id.item_1);
        items[2] = dialog.findViewById(R.id.item_2);
        items[3] = dialog.findViewById(R.id.item_3);

        for (int i = 0; i < items.length; i++) {
            final int position = i;
            if (items[i] != null) {
                items[i].setOnClickListener(v -> {
                    listener.onItemClick(position);
                    dialog.dismiss();
                });

                if (i == select) {
                    items[i].setSelected(true);
                } else {
                    items[i].setSelected(false);
                }
            }
        }

        dialog.show();
    }

    public static void showPopupChooseColor(Activity activity, int select, OnItemClickListener listener) {
        final Dialog dialog = new Dialog(activity, R.style.PopupCustom);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_choose_color);
        dialog.setCancelable(true);

        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(window.getAttributes());
            lp.dimAmount = 0.7f;

            DisplayMetrics displayMetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

            int marginPx = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 15, activity.getResources().getDisplayMetrics());

            lp.width = displayMetrics.widthPixels - (2 * marginPx);


            window.setAttributes(lp);
        }

        View[] items = new View[10];
        items[0] = dialog.findViewById(R.id.item_0);
        items[1] = dialog.findViewById(R.id.item_1);
        items[2] = dialog.findViewById(R.id.item_2);
        items[3] = dialog.findViewById(R.id.item_3);
        items[4] = dialog.findViewById(R.id.item_4);
        items[5] = dialog.findViewById(R.id.item_5);
        items[6] = dialog.findViewById(R.id.item_6);
        items[7] = dialog.findViewById(R.id.item_7);
        items[8] = dialog.findViewById(R.id.item_8);
        items[9] = dialog.findViewById(R.id.item_9);

        for (int i = 0; i < items.length; i++) {
            final int position = i;
            if (items[i] != null) {
                items[i].setOnClickListener(v -> {
                    listener.onItemClick(position);
                    dialog.dismiss();
                });

                if (i == select) {
                    items[i].setSelected(true);
                } else {
                    items[i].setSelected(false);
                }
            }
        }

        dialog.show();
    }

    public static void showPopupChoosePattern(Activity activity, int select, OnItemClickListener listener) {
        final Dialog dialog = new Dialog(activity, R.style.PopupCustom);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_choose_pattern);
        dialog.setCancelable(true);

        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(window.getAttributes());
            lp.dimAmount = 0.7f;

            DisplayMetrics displayMetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

            int marginPx = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 15, activity.getResources().getDisplayMetrics());

            lp.width = displayMetrics.widthPixels - (2 * marginPx);


            window.setAttributes(lp);
        }

        View[] items = new View[12];
        items[0] = dialog.findViewById(R.id.item_0);
        items[1] = dialog.findViewById(R.id.item_1);
        items[2] = dialog.findViewById(R.id.item_2);
        items[3] = dialog.findViewById(R.id.item_3);
        items[4] = dialog.findViewById(R.id.item_4);
        items[5] = dialog.findViewById(R.id.item_5);
        items[6] = dialog.findViewById(R.id.item_6);
        items[7] = dialog.findViewById(R.id.item_7);
        items[8] = dialog.findViewById(R.id.item_8);
        items[9] = dialog.findViewById(R.id.item_9);
        items[10] = dialog.findViewById(R.id.item_10);
        items[11] = dialog.findViewById(R.id.item_11);

        for (int i = 0; i < items.length; i++) {
            final int position = i;
            if (items[i] != null) {
                items[i].setOnClickListener(v -> {
                    listener.onItemClick(position);
                    dialog.dismiss();
                });

                if (i == select) {
                    items[i].setSelected(true);
                } else {
                    items[i].setSelected(false);
                }
            }
        }

        dialog.show();
    }
}
