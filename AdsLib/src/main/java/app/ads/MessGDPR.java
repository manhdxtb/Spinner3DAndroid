package app.ads;

import android.app.Activity;
import android.util.Log;

import com.google.android.ump.ConsentDebugSettings;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.UserMessagingPlatform;

/**
 * Lớp xử lý GDPR theo chuẩn Google UMP SDK mới nhất.
 */
public class MessGDPR {

    public interface MessGDPRListener {
        void onDone(boolean canRequestAds);

        void onError();
    }

    private static MessGDPR messGDPR;
    private ConsentInformation consentInformation;

    public static MessGDPR getInstance() {
        if (messGDPR == null) {
            messGDPR = new MessGDPR();
        }
        return messGDPR;
    }

    /**
     * Reset trạng thái đồng thuận (chủ yếu dùng để test).
     */
    public void resetMess(Activity activity) {
        if (consentInformation == null) {
            consentInformation = UserMessagingPlatform.getConsentInformation(activity);
        }
        consentInformation.reset();
    }

    /**
     * Khởi tạo và hiển thị Form đồng thuận GDPR tại Splash Screen.
     */
    public void onCreate(Activity activity, MessGDPRListener listener) {
        ConsentDebugSettings debugSettings = new ConsentDebugSettings.Builder(activity)
                .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
                .addTestDeviceHashedId("B97A04AA8B5F250AFA63BF41A73093DA")
                .build();

        ConsentRequestParameters params = new ConsentRequestParameters.Builder()
//                .setConsentDebugSettings(debugSettings) // Comment dòng này khi release
                .setTagForUnderAgeOfConsent(false)
                .build();

        consentInformation = UserMessagingPlatform.getConsentInformation(activity);

        // Google khuyến nghị luôn gọi requestConsentInfoUpdate khi khởi động ứng dụng
        consentInformation.requestConsentInfoUpdate(
                activity,
                params,
                () -> {
                    // Kiểm tra và hiển thị form nếu cần thiết
                    UserMessagingPlatform.loadAndShowConsentFormIfRequired(
                            activity,
                            formError -> {
                                if (formError != null) {
                                    Log.e("GDPR", "onConsentFormDismissed Error: " + formError.getMessage());
                                }
                                // Bất kể thành công hay lỗi form, gọi onDone để tiếp tục vào App
                                if (listener != null) {
                                    listener.onDone(consentInformation.canRequestAds());
                                }
                            }
                    );
                },
                formError -> {
                    Log.e("GDPR", "onConsentInfoUpdateFailure: " + formError.getMessage());
                    if (listener != null) {
                        listener.onError();
                    }
                });
    }

    /**
     * Kiểm tra xem có cần hiển thị nút "Quyền riêng tư" (Privacy Options) trong Settings không.
     */
    public boolean isPrivacyOptionsRequired() {
        return consentInformation != null &&
                consentInformation.getPrivacyOptionsRequirementStatus() == ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED;
    }

    /**
     * Hiển thị lại form thay đổi tùy chọn đồng thuận (dùng trong màn hình Settings).
     */
    public void showPrivacyForm(Activity activity, MessGDPRListener listener) {
        UserMessagingPlatform.showPrivacyOptionsForm(activity, formError -> {
            if (formError != null) {
                if (listener != null) listener.onError();
            } else {
                if (listener != null) listener.onDone(consentInformation.canRequestAds());
            }
        });
    }
}
