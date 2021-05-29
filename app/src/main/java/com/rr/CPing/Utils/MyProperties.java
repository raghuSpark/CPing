package com.rr.CPing.Utils;

import android.media.Ringtone;

public class MyProperties {
    private static MyProperties mInstance = null;

    public Ringtone ringtone;
    public Boolean isDismissed;

    protected MyProperties() {
    }

    public static synchronized MyProperties getInstance() {
        if (null == mInstance) {
            mInstance = new MyProperties();
        }
        return mInstance;
    }
}