package com.petriev.autoresponder.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by evgenii on 05.11.16.
 */
public class AdminReceiver extends BroadcastReceiver {
    private static final String TAG = AdminReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, String.format("Received intent with action: %s", intent.getAction()));
    }
}
