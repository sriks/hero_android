package com.deviceworks.hero.ui.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import com.deviceworks.hero.R;

/**
 * Base activity for all activities.
 */
public class HRBaseActivity extends ActionBarActivity {

    public void onCreate(int layoutResId, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutResId);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

}
