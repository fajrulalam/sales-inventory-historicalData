package com.example.dashboardandinventory2.ui.gallery;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dashboardandinventory2.R;

import java.util.ArrayList;


public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder>{

    ArrayList<String> timestamp;
    ArrayList<String> customerNumber;
    ArrayList<String> revenue;

    public RecycleAdapter(ArrayList<String> timestamp, ArrayList<String> customerNumber, ArrayList<String> revenue) {
        this.timestamp = timestamp;
        this.customerNumber = customerNumber;
        this.revenue = revenue;

//        this.NewCustomerNumber = newCustomerNumber;
//        this.NewOrders = newOrders;
//        this.NewQuantity = NewQuantity;
//        this.NewBungkusArrayList = NewBungkusArrayList;
//        this.NewWaktuPengambilan =  NewWaktuPengambilan;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.historical_data, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return  viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.customerNoTextView.setText(String.valueOf(customerNumber.get(position)));
        holder.itemTitleTextView.setText(String.valueOf(timestamp.get(position)));
        holder.revenueTextview.setText(String.valueOf(revenue.get(position)));

    }

    @Override
    public int getItemCount() {
        return timestamp.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView revenueTextview;
        TextView itemTitleTextView;
        TextView customerNoTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            revenueTextview = itemView.findViewById(R.id.revenueTextView);
            itemTitleTextView = itemView.findViewById(R.id.itemTitleTextView);
            customerNoTextView = itemView.findViewById(R.id.customerNoTextview);
        }
    }
}
