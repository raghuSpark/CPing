package com.rr.CPing.util;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.rr.CPing.R;

public class NetworkChangeListener extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (CheckInternet.isConnectedToInternet(context)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View layoutView =
                    LayoutInflater.from(context).inflate(R.layout.no_internet_popup_dialog, null);
            builder.setView(layoutView);

            Button launchGameButton =
                    layoutView.findViewById(R.id.no_internet_game_button);
            ImageButton retryButton = layoutView.findViewById(R.id.no_internet_retry_button);

            AlertDialog dialog = builder.create();
            dialog.show();
            dialog.setCancelable(false);

            dialog.getWindow().setGravity(Gravity.CENTER);

            launchGameButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    // TODO: GAME SHOULD BE LAUNCHED
                    onReceive(context, intent);
                }
            });

            retryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    onReceive(context, intent);
                }
            });
        }
    }
}
