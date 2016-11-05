package com.petriev.autoresponder.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.telephony.SmsMessage;
import android.util.Log;

import com.petriev.autoresponder.AutoRespondService;
import com.petriev.autoresponder.SerializableMessage;

import java.util.ArrayList;

/**
 * Created by evgenii on 05.11.16.
 */

public class IncomingSmsReceiver extends BroadcastReceiver {

    private static final String TAG = IncomingSmsReceiver.class.getSimpleName();

    /**
     * copied from Android SDK API 19
     */
    private static SmsMessage[] getMessagesFromIntent(final Intent intent) {
        Object[] messages;
        try {
            messages = (Object[]) intent.getSerializableExtra("pdus");
        } catch (ClassCastException e) {
            return null;
        }

        if (messages == null) {
            return null;
        }

        String format = intent.getStringExtra("format");

        int pduCount = messages.length;
        SmsMessage[] msgs = new SmsMessage[pduCount];

        for (int i = 0; i < pduCount; i++) {
            byte[] pdu = (byte[]) messages[i];
            msgs[i] = getSmsMessageFromPdu(pdu, format);
        }
        return msgs;
    }

    private static SmsMessage getSmsMessageFromPdu(final byte[] pdu, final String format) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                ? SmsMessage.createFromPdu(pdu, format)
                : SmsMessage.createFromPdu(pdu);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        SmsMessage[] messages = getMessagesFromIntent(intent);
        if (messages != null) {
            ArrayList<SerializableMessage> serializableMessages = new ArrayList<>(messages.length);
            for (SmsMessage message : messages) {
                serializableMessages.add(
                        new SerializableMessage(message.getDisplayOriginatingAddress(),
                                message.isEmail(), message.getDisplayMessageBody(),
                                message.getServiceCenterAddress()));
                Log.d(TAG, String.format("Message received: [type = %s, text = %s, address = %s]",
                        message.isEmail() ? "email" : "sms",
                        message.getDisplayMessageBody(),
                        message.getDisplayOriginatingAddress()));
            }
            Intent serviceIntent = new Intent(context, AutoRespondService.class);
            serviceIntent.putExtra(AutoRespondService.EXTRA_MESSAGE, serializableMessages);
            context.startService(serviceIntent);
        }
    }
}
