package com.rr.CPing.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rr.CPing.Handlers.SetRankColorHandler;
import com.rr.CPing.Model.ContestDetails;
import com.rr.CPing.Model.PlatformDetails;
import com.rr.CPing.R;

import java.util.ArrayList;

public class AllParentRecyclerViewAdapter extends RecyclerView.Adapter {

    private final ArrayList<PlatformDetails> platformDetailsArrayList;
    Context context;
    private ContestDetailsRecyclerViewAdapter.OnItemClickListener itemClickListener;

    public AllParentRecyclerViewAdapter(ArrayList<PlatformDetails> platformDetailsArrayList) {
        this.platformDetailsArrayList = platformDetailsArrayList;
    }

    public void setOnItemClickListener(ContestDetailsRecyclerViewAdapter.OnItemClickListener listener) {
        itemClickListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.all_recycler_view_sub_items, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint({"ResourceAsColor", "NotifyDataSetChanged"})
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

            myViewHolder.platformName.setTextColor(new SetRankColorHandler(context).getPlatformColor(platformDetails.getPlatformName()));

            ArrayList<ContestDetails> requiredDetailsArrayList = platformDetails.getPlatformContests();

            // initializing

            myViewHolder.platformRV.setHasFixedSize(true);
            myViewHolder.platformRV.setLayoutManager(new LinearLayoutManager(context));

            ContestDetailsRecyclerViewAdapter platformRVA = new ContestDetailsRecyclerViewAdapter(requiredDetailsArrayList);
            myViewHolder.platformRV.setAdapter(platformRVA);
            platformRVA.notifyDataSetChanged();

            platformRVA.setOnItemClickListener((platFormName, position1) -> {
                if (itemClickListener != null) {
                    if (position1 != RecyclerView.NO_POSITION) {
                        itemClickListener.onItemClick(platformDetails.getPlatformName(), position1);
                    }
                }
            });
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
