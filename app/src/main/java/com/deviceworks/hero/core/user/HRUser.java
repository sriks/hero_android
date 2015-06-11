package com.deviceworks.hero.core.user;

import com.deviceworks.hero.core.server.HRServerConstants;
import com.deviceworks.hero.core.server.HRServerManager;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

/**
 * The user manager class.
 */
public class HRUser {

    public interface OnSignupCompletionListener {
        void onSignupCompleted(NewUserInfo newUserInfo, Exception e);
    }

    public interface OnLoginCompletionListener {
        void onLoginCompleted(Exception e);
    }

    public class NewUserInfo {
        public String name;
        public String email;
        public String password;
    }

    static HRUser sInstance;

    private ParseUser getParseUser() {
        return ParseUser.getCurrentUser();
    }

    public static HRUser getInstance() {
        if (sInstance == null) {
            sInstance = new HRUser();
        }
        return sInstance;
    }

    public NewUserInfo newUserInfo() {
        return new NewUserInfo();
    }

    public static boolean isLoggedIn() {
        return (ParseUser.getCurrentUser() != null);
    }

    public void signup(final NewUserInfo newUserInfo, final OnSignupCompletionListener cb) {
        ParseUser user = new ParseUser();
        user.setEmail(newUserInfo.email);
        user.setUsername(newUserInfo.email);
        user.setPassword(newUserInfo.password);
        user.put(HRServerConstants.ServerKeys.name, newUserInfo.name);

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    login(newUserInfo.email, newUserInfo.password, new OnLoginCompletionListener() {
                        @Override
                        public void onLoginCompleted(Exception e) {
                            cb.onSignupCompleted(newUserInfo, e);
                        }
                    });
                } else {
                    cb.onSignupCompleted(newUserInfo, e);
                }
            }
        });
    }

    public void login(final String userid, final String password, final OnLoginCompletionListener cb) {
        ParseUser.logInInBackground(userid, password, new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (parseUser != null) {
                    HRServerManager.instance().registerForPN();
                }

                cb.onLoginCompleted(e);
            }
        });
    }

    public void logout() {
        ParseUser.logOutInBackground();
    }

    public String getObjectId() {
        return getParseUser().getObjectId();
    }

}
