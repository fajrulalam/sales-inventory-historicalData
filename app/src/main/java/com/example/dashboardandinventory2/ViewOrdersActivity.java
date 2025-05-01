package com.example.dashboardandinventory2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.dashboardandinventory2.databinding.ActivityViewOrdersBinding;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class ViewOrdersActivity extends AppCompatActivity {

    ActivityViewOrdersBinding binding;
    FirebaseFirestore fs;
    ArrayList<OrderBlock> newPesananArrayListServed;
    ArrayList<OrderBlock> newPesananArrayListPending;
    RecyclerAdapter2 recyclerAdapter_Served;
    RecyclerAdapter2 recyclerAdapter_Pending;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewOrdersBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        newPesananArrayListServed = new ArrayList<>();
        newPesananArrayListPending = new ArrayList<>();
        
        // Initialize RecyclerAdapter2 with context and order block lists
        recyclerAdapter_Served = new RecyclerAdapter2(this, newPesananArrayListServed);
        recyclerAdapter_Pending = new RecyclerAdapter2(this, newPesananArrayListPending);
        
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
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up timers to prevent memory leaks
        if (recyclerAdapter_Served != null) {
            recyclerAdapter_Served.stopAllTimers();
        }
        if (recyclerAdapter_Pending != null) {
            recyclerAdapter_Pending.stopAllTimers();
        }
    }

    private void fetchRecentlyServed() {
        fs.collection("RecentlyServed").orderBy("timestampServe", Query.Direction.DESCENDING).limit(50).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.i("Error", "onEvent", error);
                    return;
                }

                if (value != null) {
                    List<DocumentSnapshot> snapshotList = value.getDocuments();
                    newPesananArrayListServed.clear();
                    
                    for (DocumentSnapshot snapshot : snapshotList) {
                        Map<String, Object> map = snapshot.getData();

                        if (map == null) continue;
                        
                        try {
                            // Parse bungkus/take-away status
                            int bungkus = 0;
                            if (map.containsKey("bungkus_or_not")) {
                                bungkus = Integer.parseInt(String.valueOf(map.get("bungkus_or_not")));
                            } else if (map.containsKey("bungkus")) {
                                bungkus = Integer.parseInt(String.valueOf(map.get("bungkus")));
                            }
                            
                            if (bungkus == 2) continue; // Skip certain orders based on existing logic
                            
                            // Parse customer info
                            int customerNumber = Integer.parseInt(String.valueOf(map.get("customerNumber")));
                            String namaCustomer = map.containsKey("namaCustomer") ? 
                                    String.valueOf(map.get("namaCustomer")) : "Customer";
                            
                            // Get order items
                            ArrayList<NewOrderItem> orderItems = new ArrayList<>();
                            
                            // Handle orderItems array format for RecentlyServed collection
                            if (map.containsKey("orderItems") && map.get("orderItems") instanceof List) {
                                List<Map<String, Object>> orderItemsList = (List<Map<String, Object>>) map.get("orderItems");
                                
                                Log.d("RecentlyServed", "Found " + orderItemsList.size() + " order items");
                                
                                for (Map<String, Object> item : orderItemsList) {
                                    // Check if this is the RecentlyServed format with direct fields
                                    if (item.containsKey("namaPesanan") && item.containsKey("quantity") && 
                                        (item.containsKey("preparedQuantity") || item.containsKey("orderType"))) {
                                        
                                        // This is RecentlyServed format
                                        String namaPesanan = String.valueOf(item.get("namaPesanan"));
                                        String orderType = item.containsKey("orderType") ? 
                                                String.valueOf(item.get("orderType")) : "take-away";
                                        
                                        int quantity = item.containsKey("quantity") ?
                                                Integer.parseInt(String.valueOf(item.get("quantity"))) : 1;
                                        
                                        int preparedQuantity = item.containsKey("preparedQuantity") ?
                                                Integer.parseInt(String.valueOf(item.get("preparedQuantity"))) : quantity;
                                        
                                        String status = item.containsKey("status") ?
                                                String.valueOf(item.get("status")) : "completed";
                                        
                                        Log.d("RecentlyServed", "Item: " + namaPesanan + 
                                               " (" + orderType + ") - " + preparedQuantity + "/" + quantity + 
                                               " Status: " + status);
                                        
                                        // Create order item directly from the fields
                                        NewOrderItem orderItem = new NewOrderItem(
                                            namaPesanan,
                                            orderType,
                                            quantity,
                                            status
                                        );
                                        orderItem.setPreparedQuantity(preparedQuantity);
                                        orderItems.add(orderItem);
                                        
                                    } else {
                                        // This is Status collection format with dineInQuantity/takeAwayQuantity
                                        String namaPesanan = String.valueOf(item.get("namaPesanan"));
                                        
                                        // Get dineInQuantity and takeAwayQuantity
                                        int dineInQuantity = item.containsKey("dineInQuantity") ?
                                            Integer.parseInt(String.valueOf(item.get("dineInQuantity"))) : 0;
                                        
                                        int takeAwayQuantity = item.containsKey("takeAwayQuantity") ?
                                            Integer.parseInt(String.valueOf(item.get("takeAwayQuantity"))) : 0;
                                        
                                        // Create dine-in order item if quantity > 0
                                        if (dineInQuantity > 0) {
                                            NewOrderItem orderItem = new NewOrderItem(
                                                namaPesanan,
                                                "dine-in",
                                                dineInQuantity,
                                                "completed"
                                            );
                                            orderItem.setPreparedQuantity(dineInQuantity); // Mark as fully served
                                            orderItems.add(orderItem);
                                        }
                                        
                                        // Create take-away order item if quantity > 0
                                        if (takeAwayQuantity > 0) {
                                            NewOrderItem orderItem = new NewOrderItem(
                                                namaPesanan,
                                                "take-away",
                                                takeAwayQuantity,
                                                "completed"
                                            );
                                            orderItem.setPreparedQuantity(takeAwayQuantity); // Mark as fully served
                                            orderItems.add(orderItem);
                                        }
                                    }
                                }
                            } else if (map.containsKey("rincianPesanan")) {
                                // Fallback to old rincianPesanan format 
                                String rincianPesanan = map.get("rincianPesanan").toString();
                                NewOrderItem orderItem = new NewOrderItem(
                                    rincianPesanan,
                                    bungkus == 1 ? "take-away" : "dine-in",
                                    1,
                                    "completed"
                                );
                                orderItem.setPreparedQuantity(1); // Mark as served
                                orderItems.add(orderItem);
                            }
                            
                            // Format timestamp for display and calculate duration
                            String hourSecond = "";
                            String durationStr = "...";
                            
                            if (map.containsKey("waktuPesan")) {
                                Object waktuPesanObj = map.get("waktuPesan");
                                if (waktuPesanObj instanceof Timestamp) {
                                    // Handle Timestamp format
                                    Timestamp timestamp = (Timestamp) waktuPesanObj;
                                    Date date = timestamp.toDate();
                                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
                                    sdf.setTimeZone(TimeZone.getTimeZone(ZoneId.of("Asia/Jakarta")));
                                    hourSecond = sdf.format(date);
                                    
                                    // Calculate duration if timestampServe is available
                                    if (map.containsKey("timestampServe") && map.get("timestampServe") instanceof Timestamp) {
                                        Timestamp serveTimestamp = (Timestamp) map.get("timestampServe");
                                        long durationSeconds = serveTimestamp.getSeconds() - timestamp.getSeconds();
                                        int minutes = (int) (durationSeconds / 60);
                                        int seconds = (int) (durationSeconds % 60);
                                        durationStr = minutes + "m " + seconds + "s";
                                    }
                                } else {
                                    // Fallback to legacy timestamp format
                                    try {
                                        String waktuPesan = waktuPesanObj.toString();
                                        waktuPesan = waktuPesan.substring(waktuPesan.indexOf("=")+1, waktuPesan.indexOf(","));
                                        int waktuPesan_int = Integer.parseInt(waktuPesan);
                                        
                                        Date date = new Date(waktuPesan_int * 1000);
                                        SimpleDateFormat sdf = new SimpleDateFormat("EEEE,MMMM d,yyyy HH:mm:ss", Locale.ENGLISH);
                                        sdf.setTimeZone(TimeZone.getTimeZone(ZoneId.of("Asia/Jakarta")));
                                        String formattedDate = sdf.format(date);
                                        hourSecond = formattedDate.substring(formattedDate.length()-8, formattedDate.length()-3);
                                        
                                        // Calculate duration if timestampServe is available
                                        if (map.containsKey("timestampServe")) {
                                            String waktuServe = map.get("timestampServe").toString();
                                            waktuServe = waktuServe.substring(waktuServe.indexOf("=")+1, waktuServe.indexOf(","));
                                            int waktuServe_int = Integer.parseInt(waktuServe);
                                            
                                            int duration = waktuServe_int - waktuPesan_int;
                                            int second = duration % 60;
                                            int minute = duration / 60;
                                            durationStr = minute + "m " + second + "s";
                                        }
                                    } catch (Exception e) {
                                        Log.e("ParseError", "Error parsing legacy timestamp", e);
                                    }
                                }
                            }
                            
                            // Get waktuPengambilan if available
                            String waktuPengambilan = map.containsKey("waktuPengambilan") ? 
                                    String.valueOf(map.get("waktuPengambilan")) : "Tidak Memesan";
                            
                            // Debug log for order items
                            Log.d("ServedOrders", "Customer #" + customerNumber + " has " + orderItems.size() + " items");
                            for (NewOrderItem item : orderItems) {
                                Log.d("ServedOrders", "Item: " + item.getNamaPesanan() + 
                                      " (" + item.getOrderType() + ") - " + 
                                      item.getPreparedQuantity() + "/" + item.getQuantity());
                            }
                            
                            // Create OrderBlock with servingTime and add to list
                            OrderBlock orderBlock = new OrderBlock(
                                    bungkus,
                                    customerNumber,
                                    namaCustomer,
                                    orderItems,
                                    waktuPengambilan,
                                    hourSecond,  // Display time
                                    durationStr  // Serving duration
                            );
                            
                            newPesananArrayListServed.add(orderBlock);
                        } catch (Exception e) {
                            Log.e("ParseError", "Error parsing served order data: " + e.getMessage(), e);
                        }
                    }

                    recyclerAdapter_Served.notifyDataSetChanged();
                } else {
                    Log.e("NULL", "onEvent: query snapshot was null");
                }
            }
        });
    }

    private void fetchPendingOrders() {
        fs.collection("Status").orderBy("waktuPesan", Query.Direction.DESCENDING).limit(50).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.i("Error", "onEvent", error);
                    return;
                }

                if (value != null) {
                    List<DocumentSnapshot> snapshotList = value.getDocuments();
                    newPesananArrayListPending.clear();
                    
                    for (DocumentSnapshot snapshot : snapshotList) {
                        Map<String, Object> map = snapshot.getData();
                        if (map == null) continue;
                        
                        try {
                            // Parse bungkus/take-away status
                            int bungkus = 0;
                            if (map.containsKey("bungkus")) {
                                bungkus = Integer.parseInt(String.valueOf(map.get("bungkus")));
                            } else if (map.containsKey("bungkus_or_not")) {
                                bungkus = Integer.parseInt(String.valueOf(map.get("bungkus_or_not")));
                            }
                            
                            if (bungkus == 2) continue; // Skip certain orders based on existing logic
                            
                            // Parse customer info
                            int customerNumber = Integer.parseInt(String.valueOf(map.get("customerNumber")));
                            String namaCustomer = map.containsKey("namaCustomer") ? 
                                    String.valueOf(map.get("namaCustomer")) : "Customer";
                            
                            // Get order items
                            ArrayList<NewOrderItem> orderItems = new ArrayList<>();
                            
                            if (map.containsKey("orderItems") && map.get("orderItems") instanceof List) {
                                List<Map<String, Object>> orderItemsList = (List<Map<String, Object>>) map.get("orderItems");
                                
                                Log.d("PendingOrders", "Found " + orderItemsList.size() + " order items");
                                
                                for (Map<String, Object> item : orderItemsList) {
                                    // Check if this is the direct format with quantity and orderType fields
                                    if (item.containsKey("namaPesanan") && item.containsKey("quantity") && 
                                        (item.containsKey("preparedQuantity") || item.containsKey("orderType"))) {
                                        
                                        // This is direct format
                                        String namaPesanan = String.valueOf(item.get("namaPesanan"));
                                        String orderType = item.containsKey("orderType") ? 
                                                String.valueOf(item.get("orderType")) : "take-away";
                                        
                                        int quantity = item.containsKey("quantity") ?
                                                Integer.parseInt(String.valueOf(item.get("quantity"))) : 1;
                                        
                                        int preparedQuantity = item.containsKey("preparedQuantity") ?
                                                Integer.parseInt(String.valueOf(item.get("preparedQuantity"))) : 0;
                                        
                                        String status = item.containsKey("status") ?
                                                String.valueOf(item.get("status")) : "pending";
                                        
                                        Log.d("PendingOrders", "Item: " + namaPesanan + 
                                               " (" + orderType + ") - " + preparedQuantity + "/" + quantity + 
                                               " Status: " + status);
                                        
                                        // Create order item directly from the fields
                                        NewOrderItem orderItem = new NewOrderItem(
                                            namaPesanan,
                                            orderType,
                                            quantity,
                                            status
                                        );
                                        orderItem.setPreparedQuantity(preparedQuantity);
                                        orderItems.add(orderItem);
                                        
                                    } else {
                                        // This is Status collection format with dineInQuantity/takeAwayQuantity
                                        String namaPesanan = String.valueOf(item.get("namaPesanan"));
                                        
                                        // Get dineInQuantity and takeAwayQuantity
                                        int dineInQuantity = item.containsKey("dineInQuantity") ?
                                            Integer.parseInt(String.valueOf(item.get("dineInQuantity"))) : 0;
                                        
                                        int takeAwayQuantity = item.containsKey("takeAwayQuantity") ?
                                            Integer.parseInt(String.valueOf(item.get("takeAwayQuantity"))) : 0;
                                        
                                        // Create dine-in order item if quantity > 0
                                        if (dineInQuantity > 0) {
                                            orderItems.add(new NewOrderItem(
                                                namaPesanan,
                                                "dine-in",
                                                dineInQuantity,
                                                "pending"
                                            ));
                                        }
                                        
                                        // Create take-away order item if quantity > 0
                                        if (takeAwayQuantity > 0) {
                                            orderItems.add(new NewOrderItem(
                                                namaPesanan,
                                                "take-away",
                                                takeAwayQuantity,
                                                "pending"
                                            ));
                                        }
                                    }
                                }
                            } else if (map.containsKey("rincianPesanan")) {
                                // Fallback to old rincianPesanan format if available
                                String rincianPesanan = map.get("rincianPesanan").toString();
                                NewOrderItem orderItem = new NewOrderItem(
                                    rincianPesanan,
                                    bungkus == 1 ? "take-away" : "dine-in",
                                    1,
                                    "pending"
                                );
                                orderItems.add(orderItem);
                            }
                            
                            // Parse time information
                            String waktuPengambilan = map.containsKey("waktuPengambilan") ? 
                                    String.valueOf(map.get("waktuPengambilan")) : "Tidak Memesan";
                            
                            // Format timestamp for display and extract timestamp for count-up timer
                            String hourSecond = "";
                            long orderTimestampMs = 0;
                            
                            if (map.containsKey("waktuPesan")) {
                                Object waktuPesanObj = map.get("waktuPesan");
                                if (waktuPesanObj instanceof Timestamp) {
                                    Timestamp timestamp = (Timestamp) waktuPesanObj;
                                    Date date = timestamp.toDate();
                                    
                                    // Get the display time
                                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
                                    sdf.setTimeZone(TimeZone.getTimeZone(ZoneId.of("Asia/Jakarta")));
                                    hourSecond = sdf.format(date);
                                    
                                    // Get the timestamp in milliseconds for count-up timer
                                    orderTimestampMs = date.getTime();
                                } else {
                                    // Fallback to old timestamp parsing if needed
                                    String waktuPesan = waktuPesanObj.toString();
                                    waktuPesan = waktuPesan.substring(waktuPesan.indexOf("=")+1, waktuPesan.indexOf(","));
                                    int waktuPesan_int = Integer.parseInt(waktuPesan);
                                    Date date = new Date(waktuPesan_int * 1000);
                                    SimpleDateFormat sdf = new SimpleDateFormat("EEEE,MMMM d,yyyy HH:mm:ss", Locale.ENGLISH);
                                    sdf.setTimeZone(TimeZone.getTimeZone(ZoneId.of("Asia/Jakarta")));
                                    String formattedDate = sdf.format(date);
                                    hourSecond = formattedDate.substring(formattedDate.length()-8, formattedDate.length()-3);
                                    
                                    // Get the timestamp in milliseconds for count-up timer
                                    orderTimestampMs = waktuPesan_int * 1000L;
                                }
                            }
                            
                            // Create OrderBlock with timestamp for count-up timer
                            OrderBlock orderBlock = new OrderBlock(
                                    bungkus,
                                    customerNumber,
                                    namaCustomer,
                                    orderItems,
                                    waktuPengambilan,
                                    hourSecond,
                                    orderTimestampMs
                            );
                            
                            newPesananArrayListPending.add(orderBlock);
                        } catch (Exception e) {
                            Log.e("ParseError", "Error parsing pending order data: " + e.getMessage(), e);
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
    void changeStatus(Type type) {
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
}