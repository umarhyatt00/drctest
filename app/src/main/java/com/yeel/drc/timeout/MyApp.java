package com.yeel.drc.timeout;

import android.app.Application;
import android.content.Context;


import java.util.Timer;
import java.util.TimerTask;

public class MyApp extends Application implements LogoutListener {
    Timer timer;
    private LogoutListener listener;
    private static MyApp application;
    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
    }

    public static Context getYeelAppInstance() {
        return application;
    }

    public void startUserSession() {
        //420000
        cancelTimer();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (listener != null) {
                    listener.onSessonLogout();
                }
            }
        }, 300000
        );
    }

    private void cancelTimer() {

        if(timer!= null) timer.cancel();
    }

    public void registerSessionListener(LogoutListener listener) {
        this.listener = listener;
    }

    public void onUserInteracted() {
        startUserSession();
    }

    @Override
    public void onSessonLogout() {
        //new CommonLogout(getApplicationContext());
    }
}

