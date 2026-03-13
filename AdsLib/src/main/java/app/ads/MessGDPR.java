package app.ads;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.ump.ConsentDebugSettings;
import com.google.android.ump.ConsentForm;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.FormError;
import com.google.android.ump.UserMessagingPlatform;

public class MessGDPR {

    public interface MessGDPRListener {
        public void onDone(boolean canRequestAds);

        public void onError();
    }

    private static MessGDPR messGDPR;

    public static MessGDPR getInstance() {
        if (messGDPR == null) {
            messGDPR = new MessGDPR();
        }
        return messGDPR;
    }

    private Activity activity;
    private ConsentInformation consentInformation;
    private ConsentForm consentForm;
    private MessGDPRListener messGDPRListener;

    public void resetMess() {
        if (consentInformation != null) {
            consentInformation.reset();
        }
    }

    public void onCreate(Activity activity, MessGDPRListener listener) {
        this.activity = activity;
        this.messGDPRListener = listener;
        ConsentDebugSettings debugSettings = new ConsentDebugSettings.Builder(activity)
                .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
                .addTestDeviceHashedId("9BCC85DE600BAA005B804C60F5867FBD")
                .build();
        ConsentRequestParameters params = new ConsentRequestParameters
                .Builder()
//                .setConsentDebugSettings(debugSettings)
                .build();

        consentInformation = UserMessagingPlatform.getConsentInformation(activity);
        if (consentInformation.canRequestAds()) {
            try {
                messGDPRListener.onDone(true);
                messGDPRListener = null;
            } catch (Exception e) {
            }
        } else {
            consentInformation.requestConsentInfoUpdate(
                    activity,
                    params,
                    new ConsentInformation.OnConsentInfoUpdateSuccessListener() {
                        @Override
                        public void onConsentInfoUpdateSuccess() {
                            // The consent information state was updated.
                            // You are now ready to check if a form is available.
                            if (consentInformation.isConsentFormAvailable()) {
                                loadForm();
                            } else {
                                if (consentInformation.canRequestAds() && messGDPRListener != null) {
                                    messGDPRListener.onDone(true);
                                    messGDPRListener = null;
                                }
                            }
                        }
                    },
                    new ConsentInformation.OnConsentInfoUpdateFailureListener() {
                        @Override
                        public void onConsentInfoUpdateFailure(FormError formError) {
                            // Handle the error.
                            Log.e("XXX Admob", "onConsentInfoUpdateFailure    " + formError.getMessage());
                            if (messGDPRListener != null) {
                                messGDPRListener.onError();
                            }
                        }
                    });
        }
    }

    private void loadForm() {
        // Loads a consent form. Must be called on the main thread.
        UserMessagingPlatform.loadConsentForm(
                activity,
                new UserMessagingPlatform.OnConsentFormLoadSuccessListener() {
                    @Override
                    public void onConsentFormLoadSuccess(ConsentForm consentForm) {
                        MessGDPR.this.consentForm = consentForm;
                        if (consentInformation.getConsentStatus() == ConsentInformation.ConsentStatus.REQUIRED) {
                            MessGDPR.this.consentForm.show(
                                    activity,
                                    new ConsentForm.OnConsentFormDismissedListener() {
                                        @Override
                                        public void onConsentFormDismissed(@Nullable FormError formError) {
                                            if (consentInformation.getConsentStatus() == ConsentInformation.ConsentStatus.OBTAINED) {
                                                // App can start requesting ads.
                                            }

                                            if (consentInformation.canRequestAds() && messGDPRListener != null) {
                                                messGDPRListener.onDone(true);
                                                messGDPRListener = null;
                                            }

                                            // Handle dismissal by reloading form.
                                            loadForm();
                                        }
                                    });
                        } else {
                            if (consentInformation.canRequestAds() && messGDPRListener != null) {
                                messGDPRListener.onDone(true);
                                messGDPRListener = null;
                            }
                        }
                    }
                },
                new UserMessagingPlatform.OnConsentFormLoadFailureListener() {
                    @Override
                    public void onConsentFormLoadFailure(FormError formError) {
                        // Handle Error.
                        Log.e("XXX Admob", "onConsentFormLoadFailure  " + formError.getMessage());
                        if (messGDPRListener != null) {
                            messGDPRListener.onError();
                        }
                    }
                }
        );
    }

}
