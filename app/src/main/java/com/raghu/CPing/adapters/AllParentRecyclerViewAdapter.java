package com.raghu.CPing.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.raghu.CPing.R;
import com.raghu.CPing.classes.ContestDetails;
import com.raghu.CPing.classes.PlatformDetails;

import java.util.ArrayList;

public class AllParentRecyclerViewAdapter extends RecyclerView.Adapter {

    private final ArrayList<PlatformDetails> platformDetailsArrayList;

    Context context;

    public AllParentRecyclerViewAdapter(ArrayList<PlatformDetails> platformDetailsArrayList) {
        this.platformDetailsArrayList = platformDetailsArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.all_recycler_view_sub_items, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AllParentRecyclerViewAdapter.MyViewHolder myViewHolder = (MyViewHolder) holder;

        if (platformDetailsArrayList.isEmpty()) {
            myViewHolder.platformName.setVisibility(View.GONE);
            myViewHolder.platformRV.setVisibility(View.GONE);
        } else {
            myViewHolder.platformName.setVisibility(View.VISIBLE);
            myViewHolder.platformRV.setVisibility(View.VISIBLE);

            PlatformDetails platformDetails = platformDetailsArrayList.get(position);

            myViewHolder.platformName.setText(platformDetails.getPlatformName());

            switch (platformDetails.getPlatformName()) {
                case "AtCoder":
                    myViewHolder.platformName.setTextColor(context.getResources().getColor(R.color.atCoderColor));
                    break;
                case "CodeChef":
                    myViewHolder.platformName.setTextColor(context.getResources().getColor(R.color.codeChefColor));
                    break;
                case "CodeForces":
                    myViewHolder.platformName.setTextColor(context.getResources().getColor(R.color.codeForcesColor));
                    break;
                case "HackerEarth":
                    myViewHolder.platformName.setTextColor(context.getResources().getColor(R.color.hackerEarthColor));
                    break;
                case "HackerRank":
                    myViewHolder.platformName.setTextColor(context.getResources().getColor(R.color.hackerRankColor));
                    break;
                case "Kick Start":
                    myViewHolder.platformName.setTextColor(context.getResources().getColor(R.color.kickStartColor));
                    break;
                case "LeetCode":
                    myViewHolder.platformName.setTextColor(context.getResources().getColor(R.color.leetCodeColor));
                    break;
                case "TopCoder":
                    myViewHolder.platformName.setTextColor(context.getResources().getColor(R.color.topCoderColor));
                    break;
            }

            ArrayList<ContestDetails> requiredDetailsArrayList = platformDetails.getPlatformContests();

            // initializing

            myViewHolder.platformRV.setHasFixedSize(true);
            myViewHolder.platformRV.setLayoutManager(new LinearLayoutManager(context));

            ContestDetailsRecyclerViewAdapter platformRVA = new ContestDetailsRecyclerViewAdapter(requiredDetailsArrayList);
            myViewHolder.platformRV.setAdapter(platformRVA);
            platformRVA.notifyDataSetChanged();
        }

    }

    @Override
    public int getItemCount() {
        return platformDetailsArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView platformName;
        RecyclerView platformRV;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            platformName = itemView.findViewById(R.id.platform_name_text_view);
            platformRV = itemView.findViewById(R.id.platform_recycler_view);
        }
    }
}
