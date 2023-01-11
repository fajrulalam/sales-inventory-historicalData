package com.example.dashboardandinventory2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.FloatLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.dashboardandinventory2.databinding.ActivityViewOrdersBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;


public class ViewOrdersActivity extends AppCompatActivity {

    ActivityViewOrdersBinding binding;
    FirebaseFirestore fs;
    ArrayList<Order> newPesananArrayListServed;
    ArrayList<Order> newPesananArrayListPending;
    RecyclerAdapter recyclerAdapter_Served;
    RecyclerAdapter recyclerAdapter_Pending;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewOrdersBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        newPesananArrayListServed = new ArrayList<Order>();
        newPesananArrayListPending = new ArrayList<Order>();
        recyclerAdapter_Served = new RecyclerAdapter(newPesananArrayListServed);
        recyclerAdapter_Pending = new RecyclerAdapter(newPesananArrayListPending);
        binding.recyclerView.setAdapter(recyclerAdapter_Served);
        binding.recyclerView2.setAdapter(recyclerAdapter_Pending);


        fs = FirebaseFirestore.getInstance();
        fetchRecentlyServed();
        fetchPendingOrders();








        binding.servedOrdersLinearLayout.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                changeStatus(Type.Served);
            }
        });

        binding.pendingOrdersLinearLayout.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                changeStatus(Type.Pending);
            }
        });








    }

    private void fetchRecentlyServed() {
        fs.collection("RecentyServed").orderBy("timestampServe", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.i("Error", "onEvent", error);
                    return;
                }

                if (value != null){
                    List<DocumentSnapshot> snapshotList = value.getDocuments();
                    newPesananArrayListServed.clear();
                    for (DocumentSnapshot snapshot : snapshotList) {

                        Map<String, Object> map = (Map<String, Object>) snapshot.getData();
                        Object customerNumber_object = map.get("customerNumber");
                        int customerNumber_int;

//                        Log.i("MAP UNCHECKED:", map.toString());

//                        try {
                        int bungkus = Integer.parseInt(String.valueOf(map.get("bungkus_or_not")));
                        if (bungkus != 2) {
                            customerNumber_int = Integer.parseInt(String.valueOf(customerNumber_object));
                            Object pesanan_object = map.get("itemID");
                            String pesanan_String = (String.valueOf(pesanan_object));
                            Object quantity_object = map.get("quantity");
                            String quantity_string = (String.valueOf(quantity_object));
                            Object bungkus_object = map.get("bungkus_or_not");
                            String bungkus_string = String.valueOf(bungkus_object);
                            int bungkus_int = Integer.parseInt(bungkus_string);
                            String rincianPesanan = map.get("rincianPesanan").toString();

                            String waktuPesan = map.get("waktuPesan").toString();
                            String waktuServe = map.get("timestampServe").toString();
                            waktuPesan = waktuPesan.substring(waktuPesan.indexOf("=")+1, waktuPesan.indexOf(","));
                            int waktuPesan_int = Integer.parseInt(waktuPesan);
                            waktuServe = waktuServe.substring(waktuServe.indexOf("=")+1, waktuServe.indexOf(","));
                            int waktuServe_int = Integer.parseInt(waktuServe);

                            int duration = waktuServe_int - waktuPesan_int;

                            int second = duration % 60;
                            int minute = duration / 60;
                            String second_str = ""+second;
//                            if (second <10 ) {
//                                second_str = "0" + second_str;
//                            }
                            String duration_str = minute + "m " + second_str +"s";
                            Log.i("DURATION", duration_str);

                            Date date = new Date(waktuPesan_int *1000);
                            SimpleDateFormat sdf = new SimpleDateFormat("EEEE,MMMM d,yyyy HH:mm:ss", Locale.ENGLISH);
                            sdf.setTimeZone(TimeZone.getTimeZone(ZoneId.of("Asia/Jakarta")));
                            String formattedDate = sdf.format(date);
                            String hourSecond = formattedDate.substring(formattedDate.length()-8, formattedDate.length()-3);

//                            System.out.println(formattedDate); // Tuesday,November 1,2011 12:00,AM


                            newPesananArrayListServed.add(
                                    new Order(String.valueOf(customerNumber_int), bungkus_int, rincianPesanan, duration_str, hourSecond)
                            );
                        }




                    }

                    recyclerAdapter_Served.notifyDataSetChanged();

                } else {
                    Log.e("NULL", "onEvent: query snapshot was null");
                }
            }
        });
    }

    private void fetchPendingOrders(){
        fs.collection("Status").orderBy("waktuPesan", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.i("Error", "onEvent", error);
                    return;
                }

                if (value != null){
                    List<DocumentSnapshot> snapshotList = value.getDocuments();
                    newPesananArrayListPending.clear();
                    for (DocumentSnapshot snapshot : snapshotList) {

                        Map<String, Object> map = (Map<String, Object>) snapshot.getData();
                        Object customerNumber_object = map.get("customerNumber");
                        int customerNumber_int;

//                        Log.i("MAP UNCHECKED:", map.toString());

//                        try {
                        int bungkus = Integer.parseInt(String.valueOf(map.get("bungkus")));
                        if (bungkus != 2) {
                            customerNumber_int = Integer.parseInt(String.valueOf(customerNumber_object));
                            Log.i("CUSTOMERNUMBER", customerNumber_int+"");
                            Object pesanan_object = map.get("itemID");
                            String pesanan_String = (String.valueOf(pesanan_object));
                            Object quantity_object = map.get("quantity");
                            String quantity_string = (String.valueOf(quantity_object));
                            Object bungkus_object = map.get("bungkus");
                            String bungkus_string = String.valueOf(bungkus_object);
                            int bungkus_int = Integer.parseInt(bungkus_string);
                            List<String> itemID_uncombined = Arrays.asList(pesanan_String.split("\\s*,\\s"));
                            List<String> quantity_uncombined = Arrays.asList(quantity_string.split("\\s*,\\s"));
                            int i = 0;
                            String item_quantity_combined = "";
                            while (i<itemID_uncombined.size()) {
                                String item_container = itemID_uncombined.get(i);
                                String quantiy_container = quantity_uncombined.get(i);
                                if (i == itemID_uncombined.size() -1) {
                                    item_quantity_combined += item_container + " (" + quantiy_container + ")";
                                } else {
                                    item_quantity_combined += item_container + " (" + quantiy_container + ") , ";
                                }
                                i++;
                            }

                            String waktuPesan = map.get("waktuPesan").toString();
                            waktuPesan = waktuPesan.substring(waktuPesan.indexOf("=")+1, waktuPesan.indexOf(","));
                            int waktuPesan_int = Integer.parseInt(waktuPesan);

                            Date date = new Date(waktuPesan_int *1000);
                            SimpleDateFormat sdf = new SimpleDateFormat("EEEE,MMMM d,yyyy HH:mm:ss", Locale.ENGLISH);
                            sdf.setTimeZone(TimeZone.getTimeZone(ZoneId.of("Asia/Jakarta")));
                            String formattedDate = sdf.format(date);
                            String hourSecond = formattedDate.substring(formattedDate.length()-8, formattedDate.length()-3);




                            newPesananArrayListPending.add(
                                    new Order(String.valueOf(customerNumber_int), bungkus_int, item_quantity_combined, "...", hourSecond)
                            );
                        }




                    }

                    recyclerAdapter_Pending.notifyDataSetChanged();

                } else {
                    Log.e("NULL", "onEvent: query snapshot was null");
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void changeStatus(Type type){

        binding.pendingOrdersActiveLine.setVisibility(View.INVISIBLE);
        binding.servedOrdersActiveLine.setVisibility(View.INVISIBLE);
        binding.recyclerView.setVisibility(View.GONE);
        binding.recyclerView2.setVisibility(View.GONE);
        Typeface poppins_reg = getResources().getFont(R.font.poppins_regular);
        Typeface poppins_bold = getResources().getFont(R.font.poppins_bold);
        binding.servedOrdersTextView.setTypeface(poppins_reg);
        binding.pendingOrdersTextView.setTypeface(poppins_reg);
        newPesananArrayListPending.clear();
        newPesananArrayListServed.clear();

        if (type == Type.Served) {
            binding.servedOrdersActiveLine.setVisibility(View.VISIBLE);
            binding.servedOrdersTextView.setTypeface(poppins_bold);
            binding.recyclerView.setVisibility(View.VISIBLE);
            fetchRecentlyServed();

        }

        if (type == Type.Pending) {
            Log.i("Pending", newPesananArrayListPending.toString());
            binding.pendingOrdersActiveLine.setVisibility(View.VISIBLE);
            binding.pendingOrdersTextView.setTypeface(poppins_bold);
            binding.recyclerView2.setVisibility(View.VISIBLE);
            fetchPendingOrders();

        }



    }

    enum Type {
        Served,
        Pending
    }



    class Order {
        String customerNumber;
        int bungkus_or_not;
        String rincianPesanan;
        String timeRequired;
        String waktuPesan;

        public Order(String customerNumber, int bungkus_or_not, String rincianPesanan, String timeRequired, String waktuPesan) {
            this.customerNumber = customerNumber;
            this.bungkus_or_not = bungkus_or_not;
            this.rincianPesanan = rincianPesanan;
            this.timeRequired = timeRequired;
            this.waktuPesan = waktuPesan;
        }
    }

    class RecyclerAdapter extends RecyclerView.Adapter<ViewOrdersActivity.RecyclerAdapter.ViewHolder>{

        ArrayList<Order> orders;

        public RecyclerAdapter(ArrayList<Order> orders) {
            this.orders = orders;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.singleview_orders, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            int bungkus = (orders.get(position).bungkus_or_not);
            if (bungkus == 1 ) {
                holder.relativeLayout.setBackgroundColor(Color.parseColor("#F9A825"));

            } else if (bungkus == 2) {
                holder.relativeLayout.setBackgroundColor(Color.parseColor("#FFC62828"));
//                holder.waktuPengambilan.setText(newPesananArrayList.get(position).waktuPengambilan);
//                holder.waktuPengambilan.setVisibility(View.VISIBLE);
            }
            holder.detailPesanan.setText(String.valueOf(orders.get(position).rincianPesanan));
            holder.nomorPesanan.setText(String.valueOf(orders.get(position).customerNumber));
            holder.waktuServe.setText(String.valueOf(orders.get(position).timeRequired));
            holder.waktuPesan.setText(String.valueOf(orders.get(position).waktuPesan));
        }

        @Override
        public int getItemCount() {
            return orders.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            RelativeLayout relativeLayout;
            TextView nomorPesanan;
            TextView waktuServe;
            TextView detailPesanan;
            TextView waktuPesan;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                waktuPesan = itemView.findViewById(R.id.waktuPesan);
                nomorPesanan = itemView.findViewById(R.id.nomorPesanan);
                relativeLayout = itemView.findViewById(R.id.relativeLayout);
                waktuServe = itemView.findViewById(R.id.waktuServe);
                detailPesanan = itemView.findViewById(R.id.detailPesanan);
            }
        }
    }
}