package app.ads;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.ads.nativetemplates.R;

public class ShowNativeAdsOverlay extends DialogFragment {

    private static ShowNativeAdsOverlay dialogNativeOverlay;
    private static PopupNetworkAds.OnShowAdCompleteListener listener;

    public static ShowNativeAdsOverlay self() {
        return dialogNativeOverlay;
    }

    public static void show(FragmentActivity activity) {
        try {
            if (NativeAdmobOverlayAds.isAdLoaded()) {
                dialogNativeOverlay = new ShowNativeAdsOverlay();
                dialogNativeOverlay.show(activity.getSupportFragmentManager(), "Ads_Native_Overlay_Dialog");
            }
        } catch (Exception e) {
            dialogNativeOverlay = null;
        }
        setListener(null);
    }

    public static boolean isShow() {
        if (dialogNativeOverlay != null) {
            return true;
        }
        return false;
    }

    public static void setListener(PopupNetworkAds.OnShowAdCompleteListener listener) {
        ShowNativeAdsOverlay.listener = listener;
    }

    private View viewDialog, viewContent;
    private TextView txtDemNguoc;
    private View closeButton;
    private Handler handler;

    public ShowNativeAdsOverlay() {
        dialogNativeOverlay = this;
        handler = new Handler();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogTheme);
        setCancelable(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewDialog = inflater.inflate(R.layout.dialog_native_ads_overlay_fullscreen, container, false);
        viewContent = viewDialog.findViewById(R.id.dialog_content);
        closeButton = viewDialog.findViewById(R.id.btn_close);
        txtDemNguoc = viewDialog.findViewById(R.id.txt_overlay_demnguoc);
        return viewDialog;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        closeButton.setOnClickListener(v -> {
            NativeAdmobOverlayAds.destroyNative();
            NativeAdmobOverlayAds.loadNativeAd();
            if (listener != null) {
                listener.onCloseAdComplete();
            }

            PopupNetworkAds.setupPopupAds(App.self());

            dismiss();
        });

        if (NativeAdmobOverlayAds.isAdLoaded()) {
            NativeAdmobOverlayAds.showNativeAd(viewDialog.findViewById(R.id.app_nativeads_full));

            txtDemNguoc.setVisibility(View.GONE);
            closeButton.setVisibility(View.VISIBLE);
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                viewContent.setVisibility(View.VISIBLE);
            }
        }, 1000);
    }

    public void startCountDownTimer() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    txtDemNguoc.setText("3");
                    txtDemNguoc.setVisibility(View.VISIBLE);
                    closeButton.setVisibility(View.GONE);

                    CountDownTimer timer = new CountDownTimer(3000, 200) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            // Tính toán số giây còn lại và progress
                            long secondsLeft = millisUntilFinished / 1000;
                            float progressValue = (3000f - millisUntilFinished) / 3000f;

                            // Cập nhật View
                            txtDemNguoc.setText(String.valueOf(secondsLeft));
                        }

                        @Override
                        public void onFinish() {
                            txtDemNguoc.setVisibility(View.GONE);
                            closeButton.setVisibility(View.VISIBLE);
                        }
                    };

                    timer.start();
                } catch (Exception e) {
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        // Full screen
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            }
        }

        if (!NativeAdmobOverlayAds.isAdLoaded()) {
            dismiss();
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);

        dialogNativeOverlay = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        listener = null;
    }
}