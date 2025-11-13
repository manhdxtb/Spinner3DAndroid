package com.app.spinner.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.app.spinner.App;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class Utils {

    public static long timeNow() {
        return System.currentTimeMillis();
    }

    public static String formatTimeMMSS(long deltaTimeSeconds) {
        int minutes = (int) (deltaTimeSeconds / 60);
        int seconds = (int) (deltaTimeSeconds % 60);

        // Định dạng thành "mm:ss"
        return String.format("%02d:%02d", minutes, seconds);
    }

    public static int getVersionCodeApp() {
        try {
            PackageInfo packageInfo = App.self().getApplicationContext().getPackageManager()
                    .getPackageInfo(App.self().getApplicationContext().getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void writeToFileCache(String fileName, String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(App.self().openFileOutput(fileName,
                    Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String readFromFileCache(String fileName) {
        String ret = "";
        try {
            InputStream inputStream = App.self().openFileInput(fileName);
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append("\n").append(receiveString);
                }
                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static String readAssetFile(String fileName) {
        StringBuilder content = new StringBuilder();
        try {
            InputStream inputStream = App.self().getAssets().open(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content.toString().trim();
    }

}
