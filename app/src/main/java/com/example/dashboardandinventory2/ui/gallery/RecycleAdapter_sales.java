package com.example.dashboardandinventory2.ui.gallery;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dashboardandinventory2.R;

import java.util.ArrayList;


public class RecycleAdapter_sales extends RecyclerView.Adapter<RecycleAdapter_sales.ViewHolder>{

    ArrayList<String> itemTitle;
    ArrayList<String> sales;

    public RecycleAdapter_sales(ArrayList<String> itemTitle,  ArrayList<String> sales) {
        this.itemTitle = itemTitle;
        this.sales = sales;

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
        View view = layoutInflater.inflate(R.layout.sales_bottom_sheet, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return  viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.itemTitleTextView.setText(String.valueOf(itemTitle.get(position)));
        holder.salesTextView.setText(String.valueOf(sales.get(position)));

    }

    @Override
    public int getItemCount() {
        return itemTitle.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView salesTextView;
        TextView itemTitleTextView;
        TextView customerNoTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            salesTextView = itemView.findViewById(R.id.salesTextView);
            itemTitleTextView = itemView.findViewById(R.id.itemTitleTextView_sales);
        }


    }
}
