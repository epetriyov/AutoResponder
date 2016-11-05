package com.petriev.autoresponder;

import android.Manifest;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.petriev.autoresponder.receivers.AdminReceiver;

/**
 * Created by evgenii on 05.11.16.
 */

public class SpeedListener implements GoogleApiClient.ConnectionCallbacks, LocationListener, GoogleApiClient.OnConnectionFailedListener {


    private static final String TAG = SpeedListener.class.getSimpleName();
    public static final int RESOLUTION_REQUEST_CODE = 2;
    private final ComponentName mComponentName;
    private GoogleApiClient mGoogleApiClient;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private Location mCurrentLocation;
    private long mCurrentTime;
    private static final float MAX_UNLOCKED_SPEED = 15f;
    private final DevicePolicyManager mDevicePolicyManager;
    private Activity mContext;

    public SpeedListener(final Activity context) {
        mContext = context;
        mDevicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        mComponentName = new ComponentName(context, AdminReceiver.class);
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private float getSpeed(final Location location) {
        if (location.hasSpeed()) {
            return location.getSpeed();
        } else {
            return location.distanceTo(mCurrentLocation) / (System.currentTimeMillis() - mCurrentTime);
        }
    }

    private float convertSpeedToKmh(final float speed) {
        return speed * 3.6f;
    }

    public void lockScreen() {
        boolean active = mDevicePolicyManager.isAdminActive(mComponentName);
        if (active) {
            mDevicePolicyManager.lockNow();
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            startRequestLocationUpdates();
        }
    }

    public void startRequestLocationUpdates() {
        mCurrentTime = System.currentTimeMillis();
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (convertSpeedToKmh(getSpeed(location)) > MAX_UNLOCKED_SPEED) {
            Log.d(TAG, String.format("Speed is more than %d km/h, locking the screen", MAX_UNLOCKED_SPEED));
            lockScreen();
        }
        mCurrentTime = System.currentTimeMillis();
        mCurrentLocation = location;
    }

    public void connect() {
        mGoogleApiClient.connect();
    }

    public void disconnect() {
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(mContext, RESOLUTION_REQUEST_CODE);
            } catch (IntentSender.SendIntentException e) {
                Log.d(TAG, "Unable to resolve ConnectionResult", e);
            }
        }
    }
}
