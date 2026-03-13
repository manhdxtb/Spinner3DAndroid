package app.ads;

import android.content.Context;
import android.content.SharedPreferences;

public class RemoteConfig {
    public static int remote_time_restart_home_screen, remote_ads_interval, remote_time_reload_collap_ad, remote_ads_offset_openapp, remote_show_banner_ads_home, remote_time_start_show_popup, remote_defaut_home_bottom_tab, remote_max_native_ads = 8;
    public static boolean remote_rating_popup, remote_show_collap_ad, remote_show_banner_ads_home_top, remote_show_native_som_on_list, remote_update_fast_weather, remote_native_splash = true, remote_ads_resume_app = true,
            remote_show_language_screen = true, remote_show_language_ads = true, remote_show_onboarding_preview, remote_ads_native_on = true, remote_ads_native_overlay = true, remote_ads_openapp_on = true, remote_ads_popup_switch_home_category = true, remote_config_only_us = false, remote_buy_vip_active = false, dialog_accessibility_permission = true;

    private static final String PREF_NAME = "remote_config_prefs";

    public static void saveToPreferences() {
        Context context = App.self();
        if (context == null) return;

        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt("remote_time_restart_home_screen", remote_time_restart_home_screen);
        editor.putInt("remote_ads_interval", remote_ads_interval);
        editor.putInt("remote_time_reload_collap_ad", remote_time_reload_collap_ad);
        editor.putInt("remote_ads_offset_openapp", remote_ads_offset_openapp);
        editor.putInt("remote_show_banner_ads_home", remote_show_banner_ads_home);
        editor.putInt("remote_time_start_show_popup", remote_time_start_show_popup);
        editor.putInt("remote_defaut_home_bottom_tab", remote_defaut_home_bottom_tab);

        editor.putBoolean("remote_rating_popup", remote_rating_popup);
        editor.putBoolean("remote_show_collap_ad", remote_show_collap_ad);
        editor.putBoolean("remote_show_banner_ads_home_top", remote_show_banner_ads_home_top);
        editor.putBoolean("remote_show_native_som_on_list", remote_show_native_som_on_list);
        editor.putBoolean("remote_update_fast_weather", remote_update_fast_weather);
        editor.putBoolean("remote_native_splash", remote_native_splash);
        editor.putBoolean("remote_ads_resume_app", remote_ads_resume_app);
        editor.putBoolean("remote_show_language_screen", remote_show_language_screen);
        editor.putBoolean("remote_show_language_ads", remote_show_language_ads);
        editor.putBoolean("remote_show_onboarding_preview", remote_show_onboarding_preview);
        editor.putBoolean("remote_ads_native_on", remote_ads_native_on);
        editor.putBoolean("remote_ads_native_overlay", remote_ads_native_overlay);
        editor.putBoolean("remote_ads_openapp_on", remote_ads_openapp_on);
        editor.putBoolean("remote_ads_popup_switch_home_category", remote_ads_popup_switch_home_category);
        editor.putBoolean("remote_config_only_us", remote_config_only_us);
        editor.putBoolean("remote_buy_vip_active", remote_buy_vip_active);
        editor.putBoolean("dialog_accessibility_permission", dialog_accessibility_permission);

        editor.apply();
    }

    public static void loadFromPreferences() {
        Context context = App.self();
        if (context == null) return;

        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        remote_ads_interval = prefs.getInt("remote_ads_interval", 10);
        remote_time_start_show_popup = prefs.getInt("remote_time_start_show_popup", 0);
        remote_ads_offset_openapp = prefs.getInt("remote_ads_offset_openapp", 30);
        remote_time_reload_collap_ad = prefs.getInt("remote_time_reload_collap_ad", 30);
        remote_time_restart_home_screen = prefs.getInt("remote_time_restart_home_screen", 180);
        remote_show_banner_ads_home = prefs.getInt("remote_show_banner_ads_home", 2);
        remote_defaut_home_bottom_tab = prefs.getInt("remote_defaut_home_bottom_tab", 0);

        remote_ads_native_on = prefs.getBoolean("remote_ads_native_on", true);
        remote_native_splash = prefs.getBoolean("remote_native_splash", true);
        remote_ads_openapp_on = prefs.getBoolean("remote_ads_openapp_on", true);
        remote_show_language_ads = prefs.getBoolean("remote_show_language_ads", true);
        remote_show_onboarding_preview = prefs.getBoolean("remote_show_onboarding_preview", true);
        remote_show_collap_ad = prefs.getBoolean("remote_show_collap_ad", true);
        remote_show_banner_ads_home_top = prefs.getBoolean("remote_show_banner_ads_home_top", true);
        remote_show_native_som_on_list = prefs.getBoolean("remote_show_native_som_on_list", true);
        remote_rating_popup = prefs.getBoolean("remote_rating_popup", true);
        remote_show_language_screen = prefs.getBoolean("remote_show_language_screen", true);
        remote_ads_popup_switch_home_category = prefs.getBoolean("remote_ads_popup_switch_home_category", true);

        remote_update_fast_weather = prefs.getBoolean("remote_update_fast_weather", false);


        remote_ads_resume_app = prefs.getBoolean("remote_ads_resume_app", true);
        remote_ads_native_overlay = prefs.getBoolean("remote_ads_native_overlay", true);
        dialog_accessibility_permission = prefs.getBoolean("dialog_accessibility_permission", true);

        remote_config_only_us = prefs.getBoolean("remote_config_only_us", false);
        remote_buy_vip_active = prefs.getBoolean("remote_buy_vip_active", false);
    }
}
