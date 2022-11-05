package com.example.dashboardandinventory2.ui.gallery;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dashboardandinventory2.FragmentBottomSheetFull;
import com.example.dashboardandinventory2.R;
import com.example.dashboardandinventory2.databinding.FragmentGalleryBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;


public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    private FragmentGalleryBinding binding;
    private AutoCompleteTextView autoCompleteTextView;
    private LinearLayout start_end_layout;

    RecyclerView recyclerView;
    ImageButton filterbutton;
    FirebaseFirestore fs;
    RecyclerAdapater recyclerAdapter;
    ArrayList<String> itemTitleList;
    ArrayList<String> customerNoList;
    ArrayList<String> revenueList;
    RecycleAdapter recycleAdapter;
    ArrayList<String> itemTitle;
    ArrayList<String> itemTitleMinuman;
    String daily_monthly_yearly;
    Locale locale;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel = new ViewModelProvider(this).get(GalleryViewModel.class);

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        autoCompleteTextView = root.findViewById(R.id.autoCompleteTextView2);
        start_end_layout = root.findViewById(R.id.start_end_input);

        String[] granularity = getResources().getStringArray(R.array.granularity);
        ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), R.layout.dropdown_granularity, granularity);
        autoCompleteTextView.setAdapter(arrayAdapter);
        autoCompleteTextView.setInputType(View.AUTOFILL_TYPE_NONE);

        binding.startInput.setInputType(View.AUTOFILL_TYPE_NONE);
        fs = FirebaseFirestore.getInstance();

        itemTitleList = new ArrayList<>();
        customerNoList = new ArrayList<>();
        revenueList = new ArrayList<>();
        itemTitle = new ArrayList<>();
        itemTitleMinuman = new ArrayList<>();

//        itemTitleList.add("Test Date");
//        customerNoList.add("99");
//        revenueList.add("10000");

        locale = new Locale("id", "ID");




        recyclerView = root.findViewById(R.id.TimeGranularityRecyclerView);
        filterbutton = root.findViewById(R.id.showAllButton);
        recycleAdapter = new RecycleAdapter(itemTitleList, customerNoList, revenueList);
        recyclerView.setAdapter(recycleAdapter);




        long today = MaterialDatePicker.todayInUtcMilliseconds();
        long month = MaterialDatePicker.thisMonthInUtcMilliseconds();

        MaterialDatePicker datePicker_start = MaterialDatePicker.Builder.datePicker()
                .setSelection(today)
                .setTitleText("Select Start Date").build();

        MaterialDatePicker datePicker_end = MaterialDatePicker.Builder.datePicker()
                .setSelection(today)
                .setTitleText("Select End Date").build();

        MaterialDatePicker dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
                .setSelection(new Pair<>(month, today))
                .setTitleText("Select Date Range Date").build();







        //Populate FnBList
//        populateFoodBevs();
        dailyTransactionTop14();

        final String[] show_all = {"show_14_days"};
        binding.startInput.setHint("Last 14 days");

        //Show all Button on click listener
        binding.showAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (show_all[0].equals("show_14_days")) {
                    show_all[0] = "show_all";
                    dailyTransaction();
                    binding.startInput.setHint("All Daily Transactions");
                    binding.showAllButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_14));
                } else {
                    dailyTransactionTop14();
                    show_all[0] = "show_14_days";
                    binding.startInput.setHint("Last 14 days");
                    binding.showAllButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_all));

                }

            }
        });


        //Dropdown Listener
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                Toast.makeText(getContext(), "Item clicked", Toast.LENGTH_SHORT).show();
                switch (String.valueOf(adapterView.getItemAtPosition(position))) {
                    case "Daily Transaction":
                        dailyTransactionTop14();
                        show_all[0] = "show_14_days";
                        binding.startInput.setHint("Last 14 days");
                        binding.showAllButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_all));
                        break;

                    case "Monthly Transaction":
                        montlyTransaction();
                        break;
                    case "Yearly Transaction":
                        yearlyTransaction();
                        break;
                }
            }
        });

        //Start Date Listener
        binding.startInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(dateRangePicker.isAdded()) {
                    return;
                }


                FragmentManager fm = getFragmentManager();
                Fragment oldFragment = fm.findFragmentByTag("dateRangePicker");
                if (oldFragment != null) {
                    fm.beginTransaction().remove(oldFragment).commit();
                }

                dateRangePicker.show(getActivity().getSupportFragmentManager(), "dateRangePicker");
                dateRangePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(Object selection) {
                        binding.startInput.setText(dateRangePicker.getHeaderText());

                        Object selection_obj =  selection;
                        String selection_str = selection_obj.toString();
                        Log.i("OBJ SELECTION", selection_str);

                        String[] selections = selection_str.substring(selection_str.indexOf("{") +1, selection_str.indexOf("}")).split(" ");

                        String dateRange_start = selections[0];
                        String dateRange_end = selections[1];

                        long dateRange_start_long = Long.parseLong(dateRange_start);
                        long dateRange_end_long = Long.parseLong(dateRange_end);




//                            String dateRange_end =  dateRange[1] + " " + getYear();



//                            SimpleDateFormat start_end_formatter = new SimpleDateFormat("dd MMM yyyy");


                        long epoch_start_sec = TimeUnit.MILLISECONDS.toSeconds(dateRange_start_long) ;
                        long epoch_end_sec = TimeUnit.MILLISECONDS.toSeconds(dateRange_end_long) + 86400;

                        Log.i("Epoch Start", ""+ epoch_start_sec);
                        Log.i("Epoch End", ""+ epoch_end_sec);

                        fs.collection("DailyTransaction").addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {


                                if (error !=null) {
                                    Log.e("error!", "onEvent", error);
                                    return;
                                }

                                if (value != null){
                                    Log.i("Checkpoint", "Value is detected" );
                                    itemTitleList.clear();
                                    customerNoList.clear();
                                    revenueList.clear();
                                    List<DocumentSnapshot> snapshotList = value.getDocuments();
                                    for (DocumentSnapshot snapshot : snapshotList) {
                                        Map<String, Object> map = snapshot.getData();
                                        Object date_obj = map.get("timestamp");
                                        String date_str = (String.valueOf(date_obj));
                                        String date_str_epoch = date_str.substring(date_str.indexOf("=") +1, date_str.indexOf(","));
                                        Long epoch_long = Long.parseLong(date_str_epoch);

                                        Log.i("Epoch Long", ""+ epoch_long);


                                        if (epoch_long >= epoch_start_sec && epoch_long <= epoch_end_sec) {

                                            ZonedDateTime dateTime = Instant.ofEpochSecond(epoch_long).atZone((ZoneId.of("Asia/Jakarta")));
                                            String dateTime_formatted = dateTime.format(DateTimeFormatter.ofPattern("E, dd MMM yyyy"));
                                            Object customerNo = map.get("customerNumber");
                                            String customerNo_str = (String.valueOf(customerNo));
                                            Object revenue = map.get("total");
                                            revenue = "Rp" + String.format("%,d", revenue).replace(',', '.');

                                            String revenue_str = (String.valueOf(revenue));

//
                                            itemTitleList.add(dateTime_formatted);
                                            customerNoList.add(customerNo_str);
                                            revenueList.add(revenue_str);
                                        }
                                        recycleAdapter = new RecycleAdapter(itemTitleList, customerNoList, revenueList);
                                        recyclerView.setAdapter(recycleAdapter);
                                        dateRangePicker.dismiss();


                                    }
//                                    Log.i("Title List", ""+itemTitleList);
//                                    Log.i("Customer List", ""+customerNoList);
//                                    Log.i("Revemue List", ""+revenueList);
                                    recycleAdapter = new RecycleAdapter(itemTitleList, customerNoList, revenueList);
                                    recyclerView.setAdapter(recycleAdapter);
                                    dateRangePicker.dismiss();

                                }
                            }
                        });
                        getActivity().getFragmentManager().popBackStack();




                        Log.i("Start", dateRange_start);
                        Log.i("End", dateRange_end);

                    }
                });

//
            }
        });

        binding.startInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b == true) {

                    if(dateRangePicker.isAdded()) {
                        return;
                    }


                    FragmentManager fm = getFragmentManager();
                    Fragment oldFragment = fm.findFragmentByTag("dateRangePicker");
                    if (oldFragment != null) {
                        fm.beginTransaction().remove(oldFragment).commit();
                    }

                    dateRangePicker.show(getActivity().getSupportFragmentManager(), "dateRangePicker");
                    dateRangePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                        @Override
                        public void onPositiveButtonClick(Object selection) {
                            binding.startInput.setText(dateRangePicker.getHeaderText());

                            Object selection_obj =  selection;
                            String selection_str = selection_obj.toString();
                            Log.i("OBJ SELECTION", selection_str);

                            String[] selections = selection_str.substring(selection_str.indexOf("{") +1, selection_str.indexOf("}")).split(" ");

                            String dateRange_start = selections[0];
                            String dateRange_end = selections[1];

                            long epoch_start_sec = TimeUnit.MILLISECONDS.toSeconds(Long.parseLong(dateRange_start));
                            long epoch_end_sec = TimeUnit.MILLISECONDS.toSeconds(Long.parseLong(dateRange_end)) + 86400;

//                                long epoch_start_sec = TimeUnit.MILLISECONDS.toSeconds(dateRange_start_long) ;
//                                long epoch_end_sec = TimeUnit.MILLISECONDS.toSeconds(dateRange_end_long) + 86400;

                                Log.i("Epoch Start", ""+ epoch_start_sec);
                                Log.i("Epoch End", ""+ epoch_end_sec);

                                fs.collection("DailyTransaction").addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @RequiresApi(api = Build.VERSION_CODES.O)
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {


                                        if (error !=null) {
                                            Log.e("error!", "onEvent", error);
                                            return;
                                        }

                                        if (value != null){
                                            itemTitleList.clear();
                                            customerNoList.clear();
                                            revenueList.clear();
                                            List<DocumentSnapshot> snapshotList = value.getDocuments();
                                            List<String> listEntry = new ArrayList<>();
                                            List<Object> listValue = new ArrayList<>();
                                            for (DocumentSnapshot snapshot : snapshotList) {
                                                Map<String, Object> map = snapshot.getData();

                                                Object date_obj = map.get("timestamp");
                                                String date_str = (String.valueOf(date_obj));
                                                String date_str_epoch;
                                                try {
                                                    Log.i("date_Str", ""+ date_str);
                                                    date_str_epoch = date_str.substring(date_str.indexOf("=") +1, date_str.indexOf(","));
                                                } catch (StringIndexOutOfBoundsException s) {
                                                    return;
                                                }

                                                Long epoch_long = Long.parseLong(date_str_epoch);

                                                Log.i("Epoch Long", ""+ epoch_long);


                                                if (epoch_long >= epoch_start_sec && epoch_long <= epoch_end_sec) {

//                                                    for(Map.Entry<String,Object> entry : map.entrySet()) {
//
//                                                        if (listEntry.contains("date") || listEntry.contains("year") || listEntry.contains("month") || listEntry.contains("customerNumber") || listEntry.contains("timestamp") || listEntry.contains("total")) {
//                                                            listEntry.removeIf(s -> s.contains("date"));
//                                                            listEntry.removeIf(s -> s.contains("year"));
//                                                            listEntry.removeIf(s -> s.contains("month"));
//                                                            listEntry.removeIf(s -> s.contains("customerNumber"));
//                                                            listEntry.removeIf(s -> s.contains("timestamp"));
//                                                            listEntry.removeIf(s -> s.contains("total"));
//                                                            listValue.remove(listValue.size()-1);
//                                                        } else {
//                                                            listEntry.add(entry.getKey());
//                                                            listValue.add(entry.getValue());
//                                                        }
//
//
//                                                    }


                                                    Log.i("EXPERIMENT Entry", listEntry.toString() );
                                                    Log.i("EXPERIMENT Value", listValue.toString() );

                                                    ZonedDateTime dateTime = Instant.ofEpochSecond(epoch_long).atZone((ZoneId.of("Asia/Jakarta")));
                                                    String dateTime_formatted = dateTime.format(DateTimeFormatter.ofPattern("E, dd MMM yyyy"));
                                                    Object customerNo = map.get("customerNumber");
                                                    String customerNo_str = (String.valueOf(customerNo));
                                                    Object revenue = map.get("total");
                                                    revenue = "Rp" + String.format("%,d", revenue).replace(',', '.');

                                                    String revenue_str = (String.valueOf(revenue));

//
                                                    itemTitleList.add(dateTime_formatted);
                                                    customerNoList.add(customerNo_str);
                                                    revenueList.add(revenue_str);
                                                }
                                                recycleAdapter = new RecycleAdapter(itemTitleList, customerNoList, revenueList);
                                                recyclerView.setAdapter(recycleAdapter);
                                                dateRangePicker.dismiss();


                                            }

                                            recycleAdapter = new RecycleAdapter(itemTitleList, customerNoList, revenueList);
                                            recyclerView.setAdapter(recycleAdapter);
                                            dateRangePicker.dismiss();

                                        }
                                    }
                                });
                                getActivity().getFragmentManager().popBackStack();




                            Log.i("Start", dateRange_start);
                            Log.i("End", dateRange_end);

                        }
                    });

//
                }

            }
        });

        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                String date = itemTitleList.get(position);
                if (date.indexOf(",") != -1) {
                    date = date.substring(date.indexOf(", ") + 2, date.length());
                }
                Bundle bundle = new Bundle();
                bundle.putString("date", date);
                bundle.putString("yearly_montly_daily", daily_monthly_yearly);

//                Toast.makeText(getContext(), date, Toast.LENGTH_SHORT).show();

                FragmentBottomSheetFull bottomSheetFull = new FragmentBottomSheetFull();
                bottomSheetFull.setArguments(bundle);
                bottomSheetFull.show(getActivity().getSupportFragmentManager(), bottomSheetFull.getTag());
            }
        });





        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC+7"));
        calendar.clear();















//        final TextView textView = binding.textGallery;
//        galleryViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        return root;
    }


    public void showFilterDialog(){
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("makananList", itemTitle);
        bundle.putStringArrayList("minumanList", itemTitleMinuman);
    }

    public String getYear() {
        Long datetime = System.currentTimeMillis();
        Timestamp timestamp = new Timestamp(datetime);
        String date_full = (String) String.valueOf(timestamp);
        String year = date_full.substring(0, 4);
        return year;
    }

    public String getDate() {
        Long datetime = System.currentTimeMillis();
        Timestamp timestamp = new Timestamp(datetime);
        String date_full = (String) String.valueOf(timestamp);
        String year = date_full.substring(0, 10);
        return year;
    }

    public void yearlyTransaction() {
        daily_monthly_yearly = "yearly";

        start_end_layout.setVisibility(View.GONE);
        Toast.makeText(getContext(), "Yearly Transactions", Toast.LENGTH_SHORT).show();
        fs.collection("YearlyTransaction").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {


                if (error !=null) {
                    Log.e("error!", "onEvent", error);
                    return;
                }

                if (value != null){
                    Log.i("Checkpoint", "Value is detected" );
                    itemTitleList.clear();
                    customerNoList.clear();
                    revenueList.clear();
                    List<DocumentSnapshot> snapshotList = value.getDocuments();
                    for (DocumentSnapshot snapshot : snapshotList) {
                        Map<String, Object> map = snapshot.getData();
                        Object date_obj = map.get("timestamp");
                        String date_str = (String.valueOf(date_obj));
                        String date_str_epoch = date_str.substring(date_str.indexOf("=") +1, date_str.indexOf(","));
                        Long epoch_long = Long.parseLong(date_str_epoch);
                        ZonedDateTime dateTime = Instant.ofEpochSecond(epoch_long).atZone((ZoneId.of("Asia/Jakarta")) );
                        String dateTime_formatted = dateTime.format(DateTimeFormatter.ofPattern("YYYY"));
                        Object customerNo = map.get("customerNumber");
                        String customerNo_str = (String.valueOf(customerNo));
                        Object revenue = map.get("total");
                        revenue = "Rp" + String.format("%,d", revenue).replace(',', '.');

                        String revenue_str = (String.valueOf(revenue));

//
                        itemTitleList.add(dateTime_formatted);
                        customerNoList.add(customerNo_str);
                        revenueList.add(revenue_str);



                    }
//                                    Log.i("Title List", ""+itemTitleList);
//                                    Log.i("Customer List", ""+customerNoList);
//                                    Log.i("Revemue List", ""+revenueList);
                    recycleAdapter = new RecycleAdapter(itemTitleList, customerNoList, revenueList);
                    recyclerView.setAdapter(recycleAdapter);

                }
            }
        });
    }

    public void montlyTransaction() {
        daily_monthly_yearly = "monthly";
        start_end_layout.setVisibility(View.GONE);
        Toast.makeText(getContext(), "Monthly Transactions", Toast.LENGTH_SHORT).show();
        fs.collection("MonthlyTransaction").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {


                if (error !=null) {
                    Log.e("error!", "onEvent", error);
                    return;
                }

                if (value != null){
                    Log.i("Checkpoint", "Value is detected" );
                    itemTitleList.clear();
                    customerNoList.clear();
                    revenueList.clear();
                    List<DocumentSnapshot> snapshotList = value.getDocuments();
                    for (DocumentSnapshot snapshot : snapshotList) {
                        Map<String, Object> map = snapshot.getData();
                        Object date_obj = map.get("timestamp");
                        String date_str = (String.valueOf(date_obj));
                        String date_str_epoch = date_str.substring(date_str.indexOf("=") +1, date_str.indexOf(","));
                        Long epoch_long = Long.parseLong(date_str_epoch);
                        ZonedDateTime dateTime = Instant.ofEpochSecond(epoch_long).atZone((ZoneId.of("Asia/Jakarta")) );
                        String dateTime_formatted = dateTime.format(DateTimeFormatter.ofPattern("MMMM yyyy"));
                        Object customerNo = map.get("customerNumber");
                        String customerNo_str = (String.valueOf(customerNo));
                        Object revenue = map.get("total");
                        revenue = "Rp" + String.format("%,d", revenue).replace(',', '.');

                        String revenue_str = (String.valueOf(revenue));

//
                        itemTitleList.add(dateTime_formatted);
                        customerNoList.add(customerNo_str);
                        revenueList.add(revenue_str);



                    }
//                                    Log.i("Title List", ""+itemTitleList);
//                                    Log.i("Customer List", ""+customerNoList);
//                                    Log.i("Revemue List", ""+revenueList);
                    recycleAdapter = new RecycleAdapter(itemTitleList, customerNoList, revenueList);
                    recyclerView.setAdapter(recycleAdapter);

                }
            }
        });

    }

    public void dailyTransactionTop14() {
        // do daily transaction
        daily_monthly_yearly = "daily";
        start_end_layout.setVisibility(View.VISIBLE);
        Toast.makeText(getContext(), "Last 14 Daily Transactions", Toast.LENGTH_SHORT).show();
        fs.collection("DailyTransaction").orderBy("date", Query.Direction.DESCENDING).limit(15).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {


                if (error !=null) {
                    Log.e("error!", "onEvent", error);
                    return;
                }

                if (value != null){
                    Log.i("Checkpoint", "Value is detected" );
                    itemTitleList.clear();
                    customerNoList.clear();
                    revenueList.clear();
                    List<DocumentSnapshot> snapshotList = value.getDocuments();
                    for (DocumentSnapshot snapshot : snapshotList) {
                        Map<String, Object> map = snapshot.getData();
                        Object date_obj = map.get("timestamp");
                        String date_str = (String.valueOf(date_obj));
                        String date_str_epoch = date_str.substring(date_str.indexOf("=") +1, date_str.indexOf(","));
                        Long epoch_long = Long.parseLong(date_str_epoch);
                        ZonedDateTime dateTime = Instant.ofEpochSecond(epoch_long).atZone((ZoneId.of("Asia/Jakarta")) );
                        String dateTime_formatted = dateTime.format(DateTimeFormatter.ofPattern("E, dd MMM yyyy", locale));
                        Object customerNo = map.get("customerNumber");
                        String customerNo_str = (String.valueOf(customerNo));
                        Object revenue = map.get("total");
                        revenue = "Rp" + String.format("%,d", revenue).replace(',', '.');

                        String revenue_str = (String.valueOf(revenue));

//
                        itemTitleList.add(dateTime_formatted);
                        customerNoList.add(customerNo_str);
                        revenueList.add(revenue_str);



                    }

                    recycleAdapter = new RecycleAdapter(itemTitleList, customerNoList, revenueList);
                    recyclerView.setAdapter(recycleAdapter);

                }
            }
        });
    }


    public void dailyTransaction() {
        // do daily transaction
        daily_monthly_yearly = "daily";
        start_end_layout.setVisibility(View.VISIBLE);
        Toast.makeText(getContext(), "Show All Daily Transactions", Toast.LENGTH_SHORT).show();
        fs.collection("DailyTransaction").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {


                if (error !=null) {
                    Log.e("error!", "onEvent", error);
                    return;
                }

                if (value != null){
                    Log.i("Checkpoint", "Value is detected" );
                    itemTitleList.clear();
                    customerNoList.clear();
                    revenueList.clear();
                    List<DocumentSnapshot> snapshotList = value.getDocuments();
                    for (DocumentSnapshot snapshot : snapshotList) {
                        Map<String, Object> map = snapshot.getData();
                        Object date_obj = map.get("timestamp");
                        String date_str = (String.valueOf(date_obj));
                        String date_str_epoch = date_str.substring(date_str.indexOf("=") +1, date_str.indexOf(","));
                        Long epoch_long = Long.parseLong(date_str_epoch);
                        ZonedDateTime dateTime = Instant.ofEpochSecond(epoch_long).atZone((ZoneId.of("Asia/Jakarta")) );
                        String dateTime_formatted = dateTime.format(DateTimeFormatter.ofPattern("E, dd MMM yyyy", locale));
                        Object customerNo = map.get("customerNumber");
                        String customerNo_str = (String.valueOf(customerNo));
                        Object revenue = map.get("total");
                        revenue = "Rp" + String.format("%,d", revenue).replace(',', '.');

                        String revenue_str = (String.valueOf(revenue));

//
                        itemTitleList.add(dateTime_formatted);
                        customerNoList.add(customerNo_str);
                        revenueList.add(revenue_str);



                    }

                    recycleAdapter = new RecycleAdapter(itemTitleList, customerNoList, revenueList);
                    recyclerView.setAdapter(recycleAdapter);

                }
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}



