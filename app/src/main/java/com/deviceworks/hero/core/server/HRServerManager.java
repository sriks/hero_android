package com.deviceworks.hero.core.server;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.support.annotation.Nullable;
import android.util.Log;

import com.deviceworks.hero.core.user.HRUser;
import com.parse.Parse;
import com.parse.ParseCrashReporting;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseSession;
import com.parse.SaveCallback;
import com.parse.SendCallback;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * The Server Manager using Parse BAAS.
 */
public class HRServerManager {
    public interface UpdateWhereAreYouCallback {
        void onWhereAreYouUpdated(HRWhereAreYou whereAreYou, Exception e);
    }

    private static class ParseConstants {
        static final String collectionLocateRequest                 =       "LocateRequests";
        static final String collectionLocations                     =       "Locations";
    }

    private static HRServerManager sInstance = null;
    private static String kActionIamHereFormat                        =       "hr://imamhere/v1?from=%s&lat=%.6f&lng=%.6f";

    public static HRServerManager instance() {
        if (sInstance == null)
            sInstance = new HRServerManager();
        return sInstance;
    }

    public void initialize(Context context, Class<? extends Activity> pushClass) {
        ParseCrashReporting.enable(context);
        Parse.initialize(context, "2FFZFbjK9tOdu8ZTuWLN8Oxg7FN968JK6CIGeuGO", "WHPXcS4QWG54RURQVMkphrns4c1kXaSudw4sq19f");
    }

    public void registerForPN() {
        ParsePush.subscribeInBackground(HRUser.getInstance().getObjectId(), new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }
        });
    }

    public void updateWhereAreYouResult(final HRWhereAreYou whereAreYou, final UpdateWhereAreYouCallback cb) {
        sendClientPush(whereAreYou, 3, cb);
    }

    private void sendClientPush(final HRWhereAreYou whereAreYou, final int retries, final UpdateWhereAreYouCallback cb) {
        try {
            ParsePush push = new ParsePush();
            HRWhereAreYou.Request req = whereAreYou.getRequest();
            push.setChannel(req.getRequesterObjId());
            final Location location = whereAreYou.getResponse().getLocation();
            String iAmHereAction = String.format(kActionIamHereFormat,
                    HRUser.getInstance().getObjectId(),
                    location.getLatitude(),
                    location.getLongitude());

            JSONObject data = new JSONObject();
            String alert = String.format("Found %s", HRUser.getInstance().getName());
            data.put("alert", alert);
            data.put("a", iAmHereAction);
            push.setData(data);
            push.sendInBackground(new SendCallback() {
                @Override
                public void done(ParseException e) {
                    // TODO: When retires are done, save as an issue to server AND
                    // TODO: Add a future task to send response when network is ready.
                    if ((e != null) && (retries > 0)) {
                        // Retry
                        sendClientPush(whereAreYou, retries-1, cb);
                    } else {
                        // Done with retries or all is well.
                        cb.onWhereAreYouUpdated(whereAreYou, null);
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
            cb.onWhereAreYouUpdated(whereAreYou, e);
        }
    }

    @Nullable
    private JSONObject toResultJSON(Location location) {
        JSONObject json = new JSONObject();
        try {
            JSONObject locJSON = new JSONObject();
            locJSON.put("latitude", location.getLatitude());
            locJSON.put("longitude", location.getLongitude());
            json.put("location", locJSON);
            json.put("accuracy", location.getAccuracy());
            json.put("speed", location.getSpeed());
            json.put("bearing", location.getBearing());
            json.put("time", location.getTime());
            json.put("altitude", location.getAltitude());
            return json;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}


// eof

// Legacy code for reference

//    public void updateWhereAreYouResult(final HRWhereAreYou whereAreYou, final UpdateWhereAreYouCallback cb) {
//        // TODO: Send location as part of push notification
//        final ParseObject toUpdateObj = new ParseObject(ParseConstants.collectionLocations);
//        HRWhereAreYou.Response response = whereAreYou.getResponse();
//        final Location location = response.getLocation();
//        toUpdateObj.put(HRServerConstants.ServerKeys.locationInfo,
//                toResultJSON(location));
//        toUpdateObj.put(HRServerConstants.ServerKeys.location,
//                new ParseGeoPoint(location.getLatitude(), location.getLongitude()));
//        toUpdateObj.put(HRServerConstants.ServerKeys.timeForLocationLock, response.getTimeTookForLocationLock());
//        toUpdateObj.put(HRServerConstants.ServerKeys.userObjectId, HRUser.getInstance().getObjectId());
//        toUpdateObj.put(HRServerConstants.ServerKeys.requestedBy, whereAreYou.getRequest().getRequesterObjId());
//        toUpdateObj.put(HRServerConstants.ServerKeys.error, response.getErrorReason());
//        toUpdateObj.saveInBackground(new SaveCallback() {
//            @Override
//            public void done(ParseException e) {
//                if (e == null) {
//                    sendClientPush(toUpdateObj.getObjectId(), whereAreYou, cb);
//                } else {
//                    ParseObject issueObj = new ParseObject("Issues");
//                    issueObj.put("description", (e.getMessage() != null)?(e.getMessage()):"No desc");
//                    issueObj.put("error_code", e.getCode());
//                    issueObj.put("action", whereAreYou.getRequest().getActionUri());
//                    issueObj.put(HRServerConstants.ServerKeys.userObjectId, HRUser.getInstance().getObjectId());
//                    issueObj.saveEventually();
//                    cb.onWhereAreYouUpdated(whereAreYou, e);
//                }
//            }
//        });
//    }

