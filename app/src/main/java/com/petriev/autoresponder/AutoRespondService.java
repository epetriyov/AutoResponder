package com.petriev.autoresponder;

import android.Manifest;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;

import com.petriev.autoresponder.receivers.OutcomingSmsReceiver;

import java.util.ArrayList;

/**
 * Created by evgenii on 05.11.16.
 */

public class AutoRespondService extends IntentService {
    public static final String EXTRA_MESSAGE = "extra_messages";
    private final SmsManager mSmsManager;

    public AutoRespondService(String name) {
        super(name);
        mSmsManager = SmsManager.getDefault();
    }

    public AutoRespondService() {
        this(null);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (PreferencesHelper.INSTANCE.isDriveModeOn() &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                        == PackageManager.PERMISSION_GRANTED) {
            ArrayList<SerializableMessage> messages = (ArrayList<SerializableMessage>) intent.getSerializableExtra(EXTRA_MESSAGE);
            for (SerializableMessage message : messages) {
                if (message.getAddress() != null) {
                    Intent sendIntent = new Intent(OutcomingSmsReceiver.ACTION_SENT);
                    sendIntent.putExtra(OutcomingSmsReceiver.EXTRA_MESSAGE, message);
                    PendingIntent sendPendingIntent = PendingIntent.getBroadcast(this, 0,
                            sendIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    Intent deliveryIntent = new Intent(OutcomingSmsReceiver.ACTION_DELIVERY);
                    deliveryIntent.putExtra(OutcomingSmsReceiver.EXTRA_MESSAGE, message);
                    PendingIntent deliveryPendingIntent = PendingIntent.getBroadcast(this, 0,
                            deliveryIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    mSmsManager.sendTextMessage(message.getAddress(), message.getServiceCenterAddress(),
                            PreferencesHelper.INSTANCE.getResponseText(getString(R.string.default_response_text)),
                            sendPendingIntent, deliveryPendingIntent);
                }
            }
        }
    }
}
