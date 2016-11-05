package com.petriev.autoresponder;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private static final int REQUEST_CODE = 1;

    private static final String[] REQUIRED_PERMISSIONS = new String[]{
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.ACCESS_FINE_LOCATION};

    private SpeedListener mSpeedListener;

    private CompoundButton mDriveModeSwitch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSpeedListener = new SpeedListener(this);
        mDriveModeSwitch = (CompoundButton) findViewById(R.id.checkDriveMode);
        mDriveModeSwitch.setChecked(PreferencesHelper.INSTANCE.isDriveModeOn());
        mDriveModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferencesHelper.INSTANCE.setDriveMode(isChecked);
            }
        });
        findViewById(R.id.btnSaveResponseText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditResponseDialog();
            }
        });
        requestPermission();
    }

    private void openEditResponseDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_response, null);
        final EditText editResponse = (EditText) view.findViewById(R.id.editResponse);
        editResponse.setText(PreferencesHelper.INSTANCE.getResponseText(getString(R.string.default_response_text)));
        new AlertDialog.Builder(this)
                .setTitle(R.string.hint_response)
                .setView(view)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!TextUtils.isEmpty(editResponse.getText().toString().trim())) {
                            PreferencesHelper.INSTANCE.setResponseText(editResponse.getText().toString());
                            dialog.dismiss();
                        }
                    }
                })
                .create().show();
    }

    private void requestPermission() {
        List<String> deniedPermissions = new ArrayList<>();
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                deniedPermissions.add(permission);
            }
        }
        if (deniedPermissions.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    deniedPermissions.toArray(new String[deniedPermissions.size()]),
                    REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                switch (permissions[i]) {
                    case Manifest.permission.ACCESS_FINE_LOCATION:
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            mSpeedListener.startRequestLocationUpdates();
                            return;
                        }
                        break;
                    default:
                        break;
                }

            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mSpeedListener.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSpeedListener.disconnect();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (SpeedListener.RESOLUTION_REQUEST_CODE == requestCode && resultCode == RESULT_OK) {
            mSpeedListener.connect();
        }
    }
}
