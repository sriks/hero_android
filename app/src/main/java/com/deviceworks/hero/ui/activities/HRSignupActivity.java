package com.deviceworks.hero.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import com.deviceworks.hero.R;
import com.deviceworks.hero.core.user.HRUser;
import com.deviceworks.hero.ui.fragments.HRSignupFragment;

/**
 * The signup activity
 */
public class HRSignupActivity extends HRBaseActivity implements HRSignupFragment.OnSignupListener {

    private static String TAG_SIGNUP            =   "signup";
    private static String TAG_LOGIN             =   "login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(R.layout.activity_signup, savedInstanceState);
        getSupportActionBar().setTitle("Create account");
        showSignupOptions();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_signup, menu);
        return true;
    }

    private void showSignupOptions() {
        HRSignupFragment signup = new HRSignupFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.content_layout, signup, TAG_SIGNUP).commit();
    }

    @Override
    public void onSignup(String name, String email, String password) {
        HRUser.NewUserInfo userInfo = HRUser.getInstance().newUserInfo();
        userInfo.name = name;
        userInfo.email = email;
        userInfo.password = password;

        HRUser.getInstance().signup(userInfo, new HRUser.OnSignupCompletionListener() {
            @Override
            public void onSignupCompleted(HRUser.NewUserInfo newUserInfo, Exception e) {
                if (HRUser.isLoggedIn()) {
                    showHomeScreen();
                }
            }
        });
    }

    private void showHomeScreen() {
        Intent i = new Intent(this, HRHomeScreenActivity.class);
        startActivity(i);
        finish();
    }
}
