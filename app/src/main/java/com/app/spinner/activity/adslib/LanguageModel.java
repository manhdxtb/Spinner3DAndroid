package com.app.spinner.activity.adslib;

public class LanguageModel {
    private int logoLanguage;
    private String language;
    private String lg;
    public LanguageModel(int logoLanguage, String language, String lg) {
        this.logoLanguage = logoLanguage;
        this.language = language;
        this.lg = lg;
    }

    public int getLogoLanguage() {
        return logoLanguage;
    }

    public void setLogoLanguage(int logoLanguage) {
        this.logoLanguage = logoLanguage;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLg() {
        return lg;
    }

    public void setLg(String lg) {
        this.lg = lg;
    }
}