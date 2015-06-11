package com.deviceworks.hero.core.server;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.support.annotation.Nullable;
import android.util.Log;

import com.deviceworks.hero.core.user.HRUser;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.SaveCallback;

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

    private static String kActionIamHereFormat                      =       "hr://imamhere?from=%s&objid=%s";
    private static HRServerManager sInstance = null;

    public static HRServerManager instance() {
        if (sInstance == null)
            sInstance = new HRServerManager();
        return sInstance;
    }

    public void initialize(Context context, Class<? extends Activity> pushClass) {
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
        final ParseObject toUpdateObj = new ParseObject(ParseConstants.collectionLocations);
        HRWhereAreYou.Response response = whereAreYou.getResponse();
        final Location location = response.getLocation();
        toUpdateObj.put(HRServerConstants.ServerKeys.locationInfo,
                toResultJSON(location));
        toUpdateObj.put(HRServerConstants.ServerKeys.location,
                new ParseGeoPoint(location.getLatitude(), location.getLongitude()));
        toUpdateObj.put(HRServerConstants.ServerKeys.timeForLocationLock, response.getTimeTookForLocationLock());
        toUpdateObj.put(HRServerConstants.ServerKeys.userObjectId, HRUser.getInstance().getObjectId());
        toUpdateObj.put(HRServerConstants.ServerKeys.requestedBy, whereAreYou.getRequest().getRequesterObjId());
        toUpdateObj.put(HRServerConstants.ServerKeys.error, response.getErrorReason());
        toUpdateObj.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    sendClientPush(toUpdateObj.getObjectId(), whereAreYou, cb);
                } else {
                    cb.onWhereAreYouUpdated(whereAreYou, e);
                }
            }
        });
    }

    private void sendClientPush(final String objectId, final HRWhereAreYou whereAreYou, final UpdateWhereAreYouCallback cb) {
        try {
            ParsePush push = new ParsePush();
            HRWhereAreYou.Request req = whereAreYou.getRequest();
            push.setChannel(req.getRequesterObjId());
            String iAmHereAction = String.format(kActionIamHereFormat,
                    HRUser.getInstance().getObjectId(),
                    objectId);
            JSONObject data = new JSONObject();
            data.put("alert", "Found at "+whereAreYou.getResponse().getLocation().toString());
            data.put("a", iAmHereAction);
            push.setData(data);
            push.sendInBackground();
            cb.onWhereAreYouUpdated(whereAreYou, null);
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


//    public void updateWhereAreYouResult(final HRWhereAreYou whereAreYou, final UpdateWhereAreYouCallback cb) {
//        ParseQuery<ParseObject> query = new ParseQuery<>(ParseConstants.collectionLocateRequest);
//        query.whereEqualTo(HRServerConstants.ServerKeys.objectId, whereAreYou.getRequest().getTargetObjId());
//        query.findInBackground(new FindCallback<ParseObject>() {
//            @Override
//            public void done(List<ParseObject> list, ParseException e) {
//                if (list != null && !list.isEmpty()) {
//                    ParseObject toUpdateObj = list.get(0);
//                    if (whereAreYou.getResponse().hasError()) {
//                        toUpdateObj.put(HRServerConstants.ServerKeys.error,
//                                whereAreYou.getResponse().getErrorReason());
//                    } else {
//                        final Location location = whereAreYou.getResponse().getLocation();
//                        toUpdateObj.put(HRServerConstants.ServerKeys.response,
//                                toResultJSON(location));
//                        toUpdateObj.put(HRServerConstants.ServerKeys.location,
//                                new ParseGeoPoint(location.getLatitude(), location.getLongitude()));
//                        toUpdateObj.saveInBackground(new SaveCallback() {
//                            @Override
//                            public void done(ParseException e) {
//                                //cb.onWhereAreYouUpdated(whereAreYou, e);
//                                sendClientPush(whereAreYou, cb);
//                            }
//                        });
//                    }
//                } else {
//                    cb.onWhereAreYouUpdated(whereAreYou, e);
//                }
//            }
//        });
//    }
