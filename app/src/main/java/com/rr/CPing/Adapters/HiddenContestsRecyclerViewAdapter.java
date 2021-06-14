package com.rr.CPing.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rr.CPing.Handlers.SetRankColorHandler;
import com.rr.CPing.Model.HiddenContestsClass;
import com.rr.CPing.R;

import java.util.ArrayList;

public class HiddenContestsRecyclerViewAdapter extends RecyclerView.Adapter {

    private ArrayList<HiddenContestsClass> hiddenContestsArrayList;
    private Context context;

    public HiddenContestsRecyclerViewAdapter(ArrayList<HiddenContestsClass> hiddenContestsArrayList) {
        this.hiddenContestsArrayList = hiddenContestsArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.hidden_contests_recycler_view_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        HiddenContestsRecyclerViewAdapter.MyViewHolder myViewHolder = (MyViewHolder) holder;

        HiddenContestsClass contest = hiddenContestsArrayList.get(position);
        myViewHolder.contestName.setText(contest.getContestName());
        myViewHolder.platformName.setText(contest.getPlatformName());

        myViewHolder.platformName.setTextColor(new SetRankColorHandler(context).getPlatformColor(contest.getPlatformName()));
    }

    @Override
    public int getItemCount() {
        return hiddenContestsArrayList.size();
    }

    public void filteredList(ArrayList<HiddenContestsClass> mFilteredList) {
        hiddenContestsArrayList = mFilteredList;
        notifyDataSetChanged();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView contestName, platformName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            contestName = itemView.findViewById(R.id.hidden_contest_name);
            platformName = itemView.findViewById(R.id.hidden_platform_name);
        }
    }
}
