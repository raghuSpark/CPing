package com.rr.CPing.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rr.CPing.R;
import com.rr.CPing.SharedPref.SharedPrefConfig;
import com.rr.CPing.model.PlatformListItem;

import java.util.ArrayList;

public class PlatformAdapter extends BaseAdapter {

    private final Context context;
    private final ArrayList<PlatformListItem> platformNames;

    public PlatformAdapter(Context context, ArrayList<PlatformListItem> platformName) {
        this.context = context;
        this.platformNames = platformName;
    }

    public void setSelectedIndex(int id, String username, boolean update) {
        boolean isEnabled = platformNames.get(id).isEnabled(),
                isAllowed = platformNames.get(id).isUserNameAllowed();

        if (isAllowed) {
            platformNames.get(id).setEnabled(!username.isEmpty());
            platformNames.get(id).setUserName(username);
        } else {
            platformNames.get(id).setEnabled(!isEnabled);
        }

        PlatformListItem platformListItem = platformNames.get(id);
        platformNames.remove(id);

        if (platformListItem.isEnabled()) platformNames.add(0, platformListItem);
        else platformNames.add(platformListItem);

        if (!update)
            SharedPrefConfig.writePlatformsCount(context, Math.max(SharedPrefConfig.readPlatformsCount(context) + (platformListItem.isEnabled() ? 1 : -1), 0));

        saveData();

        notifyDataSetChanged();
    }

    private void saveData() {
        SharedPrefConfig.writePlatformsSelected(context, platformNames);
    }

    @Override
    public int getCount() {
        return platformNames.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.platform_card, parent, false);

        ImageView platformImageView = convertView.findViewById(R.id.platform_image_view);
        ImageView pinImageView = convertView.findViewById(R.id.pin_image_view);

        TextView platformTextView = convertView.findViewById(R.id.platform_name_text_view);
        TextView usernameTextView = convertView.findViewById(R.id.platform_user_name_text_view);

        platformImageView.setImageResource(platformNames.get(position).getLogo());

        platformTextView.setText(platformNames.get(position).getPlatformName());

        if (platformNames.get(position).isUserNameAllowed() && !platformNames.get(position).getUserName().isEmpty()) {
            usernameTextView.setVisibility(View.VISIBLE);
            usernameTextView.setText(String.format("@%s", platformNames.get(position).getUserName()));
        } else
            usernameTextView.setVisibility(View.GONE);

        if (platformNames.get(position).isEnabled())
            pinImageView.setVisibility(View.VISIBLE);
        else
            pinImageView.setVisibility(View.GONE);

        return convertView;
    }
}