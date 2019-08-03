package com.richie.opengl;

import android.app.Application;
import android.content.Context;

import com.richie.easylog.LoggerConfig;
import com.richie.easylog.LoggerFactory;

/**
 * @author Richie on 2018.08.03
 */
public class GlApp extends Application {

    private static Context sContext;

    public static Context getContext() {
        return sContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
        LoggerFactory.init(new LoggerConfig.Builder().context(this).logcatEnabled(true).build());
    }
}
