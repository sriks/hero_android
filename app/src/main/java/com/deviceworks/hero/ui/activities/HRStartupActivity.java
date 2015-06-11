package com.deviceworks.hero.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.deviceworks.hero.R;
import com.deviceworks.hero.core.user.HRUser;

/**
 * The activity that is shown when app is started.
 */
public class HRStartupActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (HRUser.isLoggedIn()) {
            // Show homescreen
            Intent i = new Intent(this, HRHomeScreenActivity.class);
            startActivity(i);
        } else {
            // Show signup
            Intent i = new Intent(this, HRSignupActivity.class);
            startActivity(i);
        }

        finish();
    }
}
