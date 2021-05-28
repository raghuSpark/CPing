package com.rr.CPing.Utils;

import android.media.Ringtone;

public class MyProperties {
    private static MyProperties mInstance = null;

    public Ringtone ringtone;

    protected MyProperties() {
    }

    public static synchronized MyProperties getInstance() {
        if (null == mInstance) {
            mInstance = new MyProperties();
        }
        return mInstance;
    }
}