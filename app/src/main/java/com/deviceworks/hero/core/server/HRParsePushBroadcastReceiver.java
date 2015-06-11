package com.deviceworks.hero.core.server;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.PowerManager;
import android.util.Log;

import com.deviceworks.hero.core.location.HRLastKnownLocation;
import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONObject;

/**
 * Push receiver overriding Parse push broadcast receiver.
 */
public class HRParsePushBroadcastReceiver extends ParsePushBroadcastReceiver {

    private static String TAG = "hero";

    @Override
    protected void onPushReceive(final Context context, final Intent intent) {
        if (getCommand(intent).equals(HRServerConstants.Commands.whereAreYou)) {
            // TODO: Check for whereareyou request
            // Handling silent notifications for whereareyou request
            final PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            final PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "wakelock");
            wakeLock.acquire();
            Log.d("hero", "received push");
            final long reqStartTime = System.currentTimeMillis();
            HRLastKnownLocation.instance(context).getLastKnownLocation(new HRLastKnownLocation.OnLocationReceivedListener() {
                @Override
                public void onLocationReceived(Location location, final int error, Exception e) {
                    final long locLockTime = System.currentTimeMillis() - reqStartTime;
                    HRWhereAreYou whereAreYou = new HRWhereAreYou();
                    whereAreYou.setRequest(intent);
                    if (location != null) {
                        Log.d(TAG, "lastknownlocation " + location.toString());
                        whereAreYou.setResponse(location, locLockTime, error);
                    } else {
                        Log.d(TAG, "error for lastknownlocation "+((e != null)?(e.getLocalizedMessage()):("Unknown")));
                        whereAreYou.setResponse(location, 0, error);
                    }

                    HRServerManager.instance().updateWhereAreYouResult(whereAreYou,
                            new HRServerManager.UpdateWhereAreYouCallback() {
                                @Override
                                public void onWhereAreYouUpdated(HRWhereAreYou whereAreYou, Exception e) {
                                    wakeLock.release();
                                }
                            });
                }
            });
        } else {

        }

        // TODO: Remove after dev and move to else.
        super.onPushReceive(context, intent);
    }

    @Override
    protected void onPushOpen(Context context, Intent intent) {
        super.onPushOpen(context, intent);
    }

    @Override
    protected void onPushDismiss(Context context, Intent intent) {
        super.onPushDismiss(context, intent);
    }

    private String getCommand(Intent intent) {
        try {
            JSONObject json = new JSONObject(intent.getStringExtra("com.parse.Data"));
            String actionUri = json.getString("a");
            Uri uri = Uri.parse(actionUri);
            String scheme = uri.getScheme();
            if (scheme != null && scheme.equals("hr")) {
                String host = uri.getHost();
                return host;
            }
            return actionUri;
        } catch (Exception e){
            e.printStackTrace();
        }

        return HRServerConstants.Commands.notAComand;
    }
}

// eof