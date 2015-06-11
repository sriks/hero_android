package com.deviceworks.hero.core.server;

/**
 * All Server constants
 */
public class HRServerConstants {

    public static class ServerKeys {
        public static String objectId                           =   "objectId";
        public static String from                               =   "from";
        public static String target                             =   "target";
        public static String location                           =   "location";
        public static String locationInfo                       =   "location_info";
        public static String timeForLocationLock                =   "time_for_loc_lock";
        public static String requestedBy                        =   "requested_by";
        public static String userObjectId                       =   "user_objectid";
        public static String error                              =   "error";

        // User
        public static String name                               =   "name";
        public static String groups                             =   "groups";
    }

    /**
     * List of commands sent from server
     */
    public static class Commands {
        public static String notAComand                         =   "none";
        public static String whereAreYou                        =   "whereareyou";
    }
}
