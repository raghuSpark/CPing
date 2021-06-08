package com.rr.CPing.Utils;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.rr.CPing.R;

public class NetworkChangeListener extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (CheckInternet.isConnectedToInternet(context)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View layoutView =
                    LayoutInflater.from(context).inflate(R.layout.no_internet_popup_dialog, null);
            builder.setView(layoutView);

            Button retryButton = layoutView.findViewById(R.id.no_internet_retry_button);

            AlertDialog dialog = builder.create();
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.getWindow().setWindowAnimations(R.style.PopupDialogAnimation);
            dialog.setCancelable(false);
            dialog.show();

            retryButton.setOnClickListener(v -> {
                dialog.dismiss();
                onReceive(context, intent);
            });
        }
    }
}