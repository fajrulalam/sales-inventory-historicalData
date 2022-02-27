package com.example.dashboardandinventory2.ui.gallery;

import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dashboardandinventory2.R;
import com.example.dashboardandinventory2.databinding.FragmentGalleryBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;


public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    private FragmentGalleryBinding binding;
    private AutoCompleteTextView autoCompleteTextView;
    private LinearLayout start_end_layout;

    RecyclerView recyclerView;
    FirebaseFirestore fs;
    RecyclerAdapater recyclerAdapter;
    ArrayList<String> itemTitleList;
    ArrayList<String> customerNoList;
    ArrayList<String> revenueList;
    RecycleAdapter recycleAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);

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

        itemTitleList.add("Test Date");
        customerNoList.add("99");
        revenueList.add("10000");


        recyclerView = root.findViewById(R.id.TimeGranularityRecyclerView);
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






        //Dropdown Listener
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                Toast.makeText(getContext(), "Item clicked", Toast.LENGTH_SHORT).show();
                switch (String.valueOf(adapterView.getItemAtPosition(position))) {
                    case "Daily Transaction":
                        start_end_layout.setVisibility(View.VISIBLE);
                        Toast.makeText(getContext(), "Daily Transactions", Toast.LENGTH_SHORT).show();
                        fs.collection("DailyTransaction").addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                if (error !=null) {
                                    Log.e("error!", "onEvent", error);
                                    return;
                                }

                                if (value != null){
                                    Log.i("Checkpoint", "Value is detected" );
                                    List<DocumentSnapshot> snapshotList = value.getDocuments();
                                    for (DocumentSnapshot snapshot : snapshotList) {
                                        Map<String, Object> map = snapshot.getData();
                                        Object date_obj = map.get("timestamp");
                                        String date_str = (String.valueOf(date_obj));
                                        String date_str_real = date_str.substring(date_str.indexOf("=") +1, date_str.indexOf(","));
                                        Object customerNo = map.get("customerNumber");
                                        String customerNo_str = (String.valueOf(customerNo));
                                        Object revenue = map.get("total");
                                        String revenue_str = (String.valueOf(revenue));

//
                                        itemTitleList.add(date_str_real);
                                        customerNoList.add(customerNo_str);
                                        revenueList.add(revenue_str);



                                    }
                                    Log.i("Title List", ""+itemTitleList);
//                                    Log.i("Customer List", ""+customerNoList);
//                                    Log.i("Revemue List", ""+revenueList);
//                                    recycleAdapter = new RecycleAdapter(itemTitleList, customerNoList, revenueList);
//                                    recyclerView.setAdapter(recycleAdapter);
                                }
                            }
                        });
                        break;

                    case "Monthly Transaction":
                        start_end_layout.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Monthly Transactions", Toast.LENGTH_SHORT).show();
                        break;
                    case "Yearly Transaction":
                        start_end_layout.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Yearly Transactions", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        //Start Date Listener
        binding.startInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dateRangePicker.show(getChildFragmentManager(), "dateRangePicker");
                dateRangePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(Object selection) {
                        Toast.makeText(getContext(), dateRangePicker.getHeaderText().replace("—", "2022").replace("-", "2022"), Toast.LENGTH_SHORT).show();

                    }
                });

//                datePicker_start.show(getChildFragmentManager(), "materal_dateRange");
//                datePicker_start.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
//                    @Override
//                    public void onPositiveButtonClick(Object selection) {
//                        Toast.makeText(getContext(), datePicker_start.getHeaderText(), Toast.LENGTH_SHORT).show();
//                        binding.startInput.setText(datePicker_start.getHeaderText());
//                        datePicker_start.dismiss();
//
//                        binding.endInput.requestFocus();
//                    }
//                });
            }
        });

        binding.startInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b == true) {

                    dateRangePicker.show(getChildFragmentManager(), "dateRangePicker");
                    dateRangePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                        @Override
                        public void onPositiveButtonClick(Object selection) {
                            binding.startInput.setText(dateRangePicker.getHeaderText());
                            String[] dateRange = dateRangePicker.getHeaderText().split("–");
                            String dateRange_start = dateRange[0] + getYear();
                            String dateRange_end =  dateRange[1] + " " + getYear();
                            Log.i("Start", dateRange_start);
                            Log.i("End", dateRange_end);

                        }
                    });

//
                }

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

    public String getYear() {
        Long datetime = System.currentTimeMillis();
        Timestamp timestamp = new Timestamp(datetime);
        String date_full = (String) String.valueOf(timestamp);
        String year = date_full.substring(0, 4);
        return year;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}