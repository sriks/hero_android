package com.deviceworks.hero.core.server;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a where are you request and result
 */
public class HRWhereAreYou {

    private Request mRequest;
    private Response mResponse;

    public void setRequest(Intent intent) {
        mRequest = new Request(intent);
    }

    public Request getRequest() {
        return mRequest;
    }

    public void setResponse(Location location, long locLockTime, int error) {
        mResponse = new Response(location, error);
    }

    public Response getResponse() {
        return mResponse;
    }

    public class Request {
        public String mRequester;
        public String mTarget;
        String mActionUri;

        public Request(Intent intent) {
            try {
                JSONObject json = new JSONObject(intent.getStringExtra("com.parse.Data"));
                String actionUri = json.getString("a");
                Uri uri = Uri.parse(actionUri);
                mRequester = uri.getQueryParameter(HRServerConstants.ServerKeys.from);
                mTarget = uri.getQueryParameter(HRServerConstants.ServerKeys.target);
                mActionUri = uri.toString();
            } catch (JSONException exception) {
                exception.printStackTrace();
                throw new RuntimeException(exception.getMessage());
            }
        }

        public String getRequesterObjId() {
            return mRequester;
        }

        public String getTargetObjId() {
            return mTarget;
        }

        public String getActionUri() {
            return mActionUri;
        }
    }

    public class Response {
        private Location mLocation;
        private int mErrorReason;
        private long mLocationLockTime;

        public Response(Location location, int errorReason) {
            mLocation = location;
            mErrorReason = errorReason;
        }

        public Location getLocation() {
            return mLocation;
        }

        public int getErrorReason() {
            return mErrorReason;
        }

        public boolean hasError() {
            return (mErrorReason > 0);
        }

        public long getTimeTookForLocationLock() {
            return mLocationLockTime;
        }
    }

}
