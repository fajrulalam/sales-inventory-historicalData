package com.example.dashboardandinventory2.ui.gallery;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.dashboardandinventory2.R;

import java.util.ArrayList;

public class RecyclerAdapater extends RecyclerView.Adapter<RecyclerAdapater.ViewHolder> {
    private static final String TAG = "RecycleAdapter";
    int count = 0;

    ArrayList<String> Timestamp;
    ArrayList<String> CustomerNumber;
    ArrayList<String> Revenue;

    public RecyclerAdapater(ArrayList<String> timestamp, ArrayList<String> customerNumber, ArrayList<String> revenue) {
        this.Timestamp = timestamp;
        this.CustomerNumber = customerNumber;
        this.Revenue = revenue;

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
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.customerNoTextView.setText(String.valueOf(CustomerNumber.get(position)));
        holder.itemTitleTextView.setText(String.valueOf(Timestamp.get(position)));
        holder.revenueTextview.setText(String.valueOf(Revenue.get(position)));


//        int bungkus = Integer.parseInt(NewBungkusArrayList.get(position));
//        if (bungkus == 1 ) {
//            holder.noCustomerTextView.setBackgroundColor(Color.parseColor("#F9A825"));
//
//        } else if (bungkus == 2) {
//            holder.noCustomerTextView.setBackgroundColor(Color.parseColor("#FFC62828"));
//            holder.waktuPengambilan.setText(NewWaktuPengambilan.get(position));
//            holder.waktuPengambilan.setVisibility(View.VISIBLE);
//
//        }
//        holder.pesananTextView.setText(String.valueOf(NewOrders.get(position)));
//        holder.noCustomerTextView.setText(String.valueOf(NewCustomerNumber.get(position)));




    }

    @Override
    public int getItemCount() {
        return Timestamp.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView revenueTextview;
        TextView itemTitleTextView;
        TextView customerNoTextView;

        TextView noCustomerTextView;
        TextView pesananTextView;
        TextView waktuPengambilan;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            revenueTextview = itemView.findViewById(R.id.revenueTextView);
            itemTitleTextView = itemView.findViewById(R.id.itemTitleTextView);
            customerNoTextView = itemTitleTextView.findViewById(R.id.customerNoTextview);


        }
    }
}
