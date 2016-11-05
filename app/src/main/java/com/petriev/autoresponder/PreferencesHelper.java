package com.petriev.autoresponder;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by evgenii on 05.11.16.
 */

public enum PreferencesHelper {

    INSTANCE;

    private static final String KEY_RESPONSE_TEXT = "key_response_text";
    private static final String KEY_DRIVE_MODE = "key_drive_mode";
    private SharedPreferences preferences;

    public void init(final Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setResponseText(final String responseText) {
        preferences.edit().putString(KEY_RESPONSE_TEXT, responseText).apply();
    }

    public String getResponseText(final String defaultText) {
        return preferences.getString(KEY_RESPONSE_TEXT, defaultText);
    }

    public void setDriveMode(final boolean driveMode) {
        preferences.edit().putBoolean(KEY_DRIVE_MODE, driveMode).apply();
    }

    public boolean isDriveModeOn() {
        return preferences.getBoolean(KEY_DRIVE_MODE, false);
    }
}
