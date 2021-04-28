package com.raghu.CPing.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.raghu.CPing.R;
import com.raghu.CPing.classes.PlatformListItem;

import java.util.ArrayList;

public class PlatformAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<PlatformListItem> platformNames;
    private SharedPreferences sharedPreferences;

    public PlatformAdapter(Context context, ArrayList<PlatformListItem> platformName){
        this.context = context;
        this.platformNames = platformName;

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setSelectedIndex(int id, String username, Button saveBtn){
        boolean isEnabled = platformNames.get(id).isEnabled();
        boolean isAllowed = platformNames.get(id).isUsernameAllowed();
        if(isAllowed){
            platformNames.get(id).setEnabled(!username.isEmpty());
            platformNames.get(id).setUsername(username);
        }else{
            platformNames.get(id).setEnabled(!isEnabled);
        }

        PlatformListItem platformListItem = platformNames.get(id);
        platformNames.remove(id);
        if(platformListItem.isEnabled()) platformNames.add(0, platformListItem);
        else platformNames.add(platformListItem);
        sharedPreferences.edit().putInt("count", sharedPreferences.getInt("count",0)+(platformListItem.isEnabled()?1:-1)).apply();

        saveBtn.setEnabled(sharedPreferences.getInt("count", 0) != 0);

        saveData();
        notifyDataSetChanged();
    }

    private void saveData() {
        Gson gson = new Gson();
        String json = gson.toJson(platformNames);
        sharedPreferences.edit().putString("platformNames", json).apply();
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.platform_card, parent, false);
        TextView platformTextView = convertView.findViewById(R.id.platformTextView);
        TextView usernameTextView = convertView.findViewById(R.id.platformUsernameTextView);
        platformTextView.setText(platformNames.get(position).getPlatformName());
        if(!platformNames.get(position).getUsername().isEmpty()) usernameTextView.setText("@"+platformNames.get(position).getUsername());
        if(platformNames.get(position).isEnabled()) convertView.setBackgroundColor(Color.RED);
        return convertView;
    }
}