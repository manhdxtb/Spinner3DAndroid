package app.ads;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.akexorcist.localizationactivity.ui.LocalizationApplication;
import com.appsflyer.AFAdRevenueData;
import com.appsflyer.AFInAppEventParameterName;
import com.appsflyer.AdRevenueScheme;
import com.appsflyer.AppsFlyerLib;
import com.appsflyer.MediationNetwork;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.ads.AdValue;
import com.google.android.gms.ads.AdapterResponseInfo;
import com.google.android.gms.ads.ResponseInfo;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.jetbrains.annotations.NotNull;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class App extends LocalizationApplication {

    private static App instance;

    public static App self() {
        return instance;
    }

    public String packageNameApp;
    public BaseAdsPopupActivity activityLast;
    public boolean isQuyenShowPopupAds = true;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        packageNameApp = getApplicationContext().getPackageName();
        activityLast = null;
        isQuyenShowPopupAds = true;

        initAppsFlyer();

        FirebaseApp.initializeApp(this);

        FacebookSdk.fullyInitialize();
        AppEventsLogger.activateApp(this);
    }

    private void initAppsFlyer() {
        try {
            AppsFlyerLib.getInstance().setDebugLog(false);
            AppsFlyerLib.getInstance().init("X6Kiaov2ZqhCk3fc7dGnCd", null, this);
            AppsFlyerLib.getInstance().start(this);
        } catch (Exception e) {
        }
    }

    public void logEventAppsflyer(String event) {
        try {
            Map<String, Object> eventValues = new HashMap<>();
            eventValues.put(AFInAppEventParameterName.CONTENT_ID, event);
            AppsFlyerLib.getInstance().logEvent(getApplicationContext(), event, eventValues);
        } catch (Exception e) {
        }
    }

    public void logAd_Impression_Admob_AppsFlyer(AdValue adValue, ResponseInfo responseInfo, String format) {
        if (responseInfo == null) return;
        double value = adValue.getValueMicros() / 1000000.0f;
        AdapterResponseInfo adapterResponseInfo = responseInfo.getLoadedAdapterResponseInfo();
        if (adapterResponseInfo == null) return;

        String adSourceId = adapterResponseInfo.getAdSourceId();
        String adSourceName = adapterResponseInfo.getAdSourceName();

        try {
            AppsFlyerLib appsflyer = AppsFlyerLib.getInstance();

            // Create an instance of AFAdRevenueData
            AFAdRevenueData adRevenueData = new AFAdRevenueData(
                    adSourceName,
                    MediationNetwork.GOOGLE_ADMOB,
                    adValue.getCurrencyCode(),
                    value
            );

            Map<String, Object> additionalParameters = new HashMap<>();
            additionalParameters.put(AdRevenueScheme.COUNTRY, adValue.getCurrencyCode());
            additionalParameters.put(AdRevenueScheme.AD_UNIT, adSourceId);
            additionalParameters.put(AdRevenueScheme.AD_TYPE, format);

            appsflyer.logAdRevenue(adRevenueData, additionalParameters);
        } catch (Exception e) {
        }
    }

    public void logEventFirebaseSAS(String name, String param, String value) {
        try {
            ////////////        Ban 1           Ban 2        //////////////////////////
            if (!TextUtils.isEmpty(name) && (packageNameApp.equals("com.smart.controlcenter.control.tool")
                    || packageNameApp.equals("com.controlcenter.simple.tool.controlpanel")
            )) {
                value = value.replace("https://sascorpvn.com/images/", "");
                Bundle params = new Bundle();
                params.putString(param, value);
                FirebaseAnalytics.getInstance(this).logEvent(name, params);

                android.util.Log.e("Firebase SAS", name + "   " + params.toString());
            }
        } catch (Exception e) {
        }
    }

    public void logEventFirebaseSAS(String name, String param_1, String value_1, String param_2, String value_2) {
        try {
            ////////////        Ban 1           Ban 2        //////////////////////////
            if (!TextUtils.isEmpty(name) && (packageNameApp.equals("com.smart.controlcenter.control.tool")
                    || packageNameApp.equals("com.controlcenter.simple.tool.controlpanel")
            )) {
                value_1 = value_1.replace("https://sascorpvn.com/images/", "");
                Bundle params = new Bundle();
                params.putString(param_1, value_1);
                params.putString(param_2, value_2);
                FirebaseAnalytics.getInstance(this).logEvent(name, params);

                android.util.Log.e("Firebase SAS", name + "   " + params.toString());
            }
        } catch (Exception e) {
        }
    }

    public void logEventFirebaseSAS(String name, String param_1, String value_1, String param_2, String value_2, String param_3, String value_3) {
        try {
            ////////////        Ban 1           Ban 2        //////////////////////////
            if (!TextUtils.isEmpty(name) && (packageNameApp.equals("com.smart.controlcenter.control.tool")
                    || packageNameApp.equals("com.controlcenter.simple.tool.controlpanel")
            )) {
                value_1 = value_1.replace("https://sascorpvn.com/images/", "");
                Bundle params = new Bundle();
                params.putString(param_1, value_1);
                params.putString(param_2, value_2);
                params.putString(param_3, value_3);
                FirebaseAnalytics.getInstance(this).logEvent(name, params);

                android.util.Log.e("Firebase SAS", name + "   " + params.toString());
            }
        } catch (Exception e) {
        }
    }

    public void logEventFirebaseSAS(String name, String param_1, String value_1, String param_2, String value_2, String param_3, String value_3, String param_4, String value_4) {
        try {
            ////////////        Ban 1           Ban 2        //////////////////////////
            if (!TextUtils.isEmpty(name) && (packageNameApp.equals("com.smart.controlcenter.control.tool")
                    || packageNameApp.equals("com.controlcenter.simple.tool.controlpanel")
            )) {
                value_1 = value_1.replace("https://sascorpvn.com/images/", "");
                Bundle params = new Bundle();
                params.putString(param_1, value_1);
                params.putString(param_2, value_2);
                params.putString(param_3, value_3);
                params.putString(param_4, value_4);
                FirebaseAnalytics.getInstance(this).logEvent(name, params);

                android.util.Log.e("Firebase SAS", name + "   " + params.toString());
            }
        } catch (Exception e) {
        }
    }

    public void logScreenSAS(String name, long timeResume) {
        long dentaTime = System.currentTimeMillis() - timeResume;
        if (dentaTime >= 800) {
            if (dentaTime < 1000) {
                dentaTime = 1000;
            }
            logEventFirebaseSAS(name, "engagement_time", "" + dentaTime / 1000);
        }
    }

    public void printKeyHash() {
        try {
            PackageInfo info = App.self().getPackageManager().getPackageInfo(
                    App.self().getPackageName(),
                    PackageManager.GET_SIGNATURES);

            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String keyHash = Base64.encodeToString(md.digest(), Base64.DEFAULT);

                Log.d("KeyHash", keyHash);
            }
        } catch (Exception e) {
            Log.e("KeyHash", "Error:", e);
        }
    }

    @Override
    public @NotNull Locale getDefaultLanguage(@NotNull Context context) {
        return Locale.getDefault();
    }
}
