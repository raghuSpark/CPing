package com.rr.CPing.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NetworkChangeListener extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!CheckInternet.isConnectedToInternet(context)) {
            // TODO: To be implemented
        }
    }
}
