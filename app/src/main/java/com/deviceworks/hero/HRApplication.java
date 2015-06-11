package com.deviceworks.hero;

import android.app.Application;
import android.content.Context;

import com.deviceworks.hero.core.server.HRServerManager;

/**
 * The application class
 */
public class HRApplication extends Application {

    Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
        initializeApp();
    }

    private void initializeApp() {
        HRServerManager.instance().initialize(getApplicationContext(), null);
    }
}
