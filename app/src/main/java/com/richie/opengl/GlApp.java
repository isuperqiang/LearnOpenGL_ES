package com.richie.opengl;

import android.app.Application;
import android.content.Context;

/**
 * @author LiuQiang on 2018.08.03
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
    }
}
