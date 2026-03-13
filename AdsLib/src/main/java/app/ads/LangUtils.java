package app.ads;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;

import java.util.LinkedHashSet;
import java.util.Locale;

public final class LangUtils {

    public static void setAppLocale(Activity activity, String languageCode, Class<?> classStart) {
        Locale locale = new Locale(languageCode);
        LocaleList localeList = new LocaleList(locale);
        LocaleList.setDefault(localeList);
        Resources resources = activity.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocales(localeList);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        Intent intent = new Intent(activity, classStart);
        activity.finishAffinity();
        activity.startActivity(intent);
    }

    public static boolean updateLanguage(Context context, String language) {
        if (TextUtils.isEmpty(language)) {
            language = "en";
        }

        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        return updateResources(context.getApplicationContext(), locale);
    }

    private static boolean updateResources(@NonNull Context context, @NonNull Locale locale) {
        Locale.setDefault(locale);

        Resources res = context.getResources();
        Configuration conf = res.getConfiguration();
        //noinspection deprecation
        Locale current = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ? conf.getLocales().get(0) : conf.locale;

        try {
            if (current.getLanguage().equals(locale.getLanguage())) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        AppCompatDelegate.setApplicationLocales(LocaleListCompat.create(locale));

        conf = new Configuration(conf);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            setLocaleApi24(conf, locale);
        } else {
            conf.setLocale(locale);
        }
        //noinspection deprecation
        res.updateConfiguration(conf, res.getDisplayMetrics());
        return true;
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private static void setLocaleApi24(@NonNull Configuration config, @NonNull Locale locale) {
        LocaleList defaultLocales = LocaleList.getDefault();
        LinkedHashSet<Locale> locales = new LinkedHashSet<>(defaultLocales.size() + 1);
        // Bring the target locale to the front of the list
        // There's a hidden API, but it's not currently used here.
        locales.add(locale);
        for (int i = 0; i < defaultLocales.size(); ++i) {
            locales.add(defaultLocales.get(i));
        }
        config.setLocales(new LocaleList(locales.toArray(new Locale[0])));
    }

}
