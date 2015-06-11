package com.deviceworks.hero.core.location;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.lang.ref.WeakReference;

/**
 * Provides the last known location
 */
public class HRLastKnownLocation implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public interface OnLocationReceivedListener {
        void onLocationReceived(Location location, int errorCode, Exception e);
    }


    private static HRLastKnownLocation sInstance;
    private GoogleApiClient mGoogleApiClient;
    private WeakReference<OnLocationReceivedListener> mLastKnownLocationListener;

    public static HRLastKnownLocation instance(Context context) {
        if (sInstance == null) {
            sInstance = new HRLastKnownLocation();
            sInstance.initialize(context);
        }
        return sInstance;
    }

    private void initialize(Context context) {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public void getLastKnownLocation(OnLocationReceivedListener cb) {
        mLastKnownLocationListener = new WeakReference<>(cb);
        if (!mGoogleApiClient.isConnected() && !mGoogleApiClient.isConnecting()) {
            mGoogleApiClient.connect();
        } else {
            requestLastKnownLocation();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (mLastKnownLocationListener.get() != null) {
            requestLastKnownLocation();
        }
    }

    @Override
    public void onConnectionSuspended(int reason) {
        if (mLastKnownLocationListener.get() != null) {
            Exception e = new Exception("Connection suspended " + String.valueOf(reason));
            mLastKnownLocationListener.get().onLocationReceived(null, HRLocationConstants.ErrorCodes.ConnectionError, e);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (mLastKnownLocationListener.get() != null) {
            Exception e = new Exception("Unable to fetch location" + String.valueOf(connectionResult.getErrorCode()));
            mLastKnownLocationListener.get().onLocationReceived(null, HRLocationConstants.ErrorCodes.ConnectionError, e);
        }
    }

    private void requestLastKnownLocation() {
        if (mLastKnownLocationListener.get() != null) {
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mLastKnownLocationListener.get().onLocationReceived(location, HRLocationConstants.ErrorCodes.NoError, null);
        }
    }

}



