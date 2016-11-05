package com.petriev.autoresponder.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.petriev.autoresponder.SerializableMessage;

/**
 * Created by evgenii on 05.11.16.
 */

public class OutcomingSmsReceiver extends BroadcastReceiver {
    public static final String ACTION_SENT = "com.petriev.autoresponder.ACTION_SENT";
    public static final String ACTION_DELIVERY = "com.petriev.autoresponder.ACTION_DELIVERY";
    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    private static final String TAG = OutcomingSmsReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        SerializableMessage message = (SerializableMessage) intent.getSerializableExtra(EXTRA_MESSAGE);
        String action = ACTION_SENT.equals(intent.getAction()) ? "sent" : "delivered";
        Log.d(TAG, String.format("Message was %s: [type = %s, text = %s, address = %s]",
                action,
                message.isEmail() ? "email" : "sms",
                message.getText(),
                message.getAddress()));
    }
}
