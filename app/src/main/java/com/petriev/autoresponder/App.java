package com.petriev.autoresponder;

import android.app.Application;

/**
 * Created by evgenii on 05.11.16.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        PreferencesHelper.INSTANCE.init(this);
    }
}
