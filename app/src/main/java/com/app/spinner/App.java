package com.app.spinner;

import com.google.gson.Gson;

public class App extends app.ads.App {

    private static App mSelf;

    public static App self() {
        return mSelf;
    }

    public static App getInstance() {
        return mSelf;
    }

    public String packageNameApp;

    private Gson gson;
    public int widthScreen, heightScreen;

    @Override
    public void onCreate() {
        super.onCreate();

        mSelf = this;
        getGson();

        packageNameApp = getApplicationContext().getPackageName();

    }

    public Gson getGson() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }
}
