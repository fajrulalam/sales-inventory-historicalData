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

import com.example.dashboardandinventory2.R;
import com.example.dashboardandinventory2.databinding.FragmentGalleryBinding;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    private FragmentGalleryBinding binding;
    private AutoCompleteTextView autoCompleteTextView;
    private LinearLayout start_end_layout;

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
        binding.endInput.setInputType(View.AUTOFILL_TYPE_NONE);

        long today = MaterialDatePicker.todayInUtcMilliseconds();
        long month = MaterialDatePicker.thisMonthInUtcMilliseconds();

        MaterialDatePicker datePicker_start = MaterialDatePicker.Builder.datePicker()
                .setSelection(today)
                .setTitleText("Select Start Date").build();

        MaterialDatePicker datePicker_end = MaterialDatePicker.Builder.datePicker()
                .setSelection(today)
                .setTitleText("Select End Date").build();






        //Dropdown Listener
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                Toast.makeText(getContext(), "Item clicked", Toast.LENGTH_SHORT).show();
                switch (String.valueOf(adapterView.getItemAtPosition(position))) {
                    case "Daily Transaction":
                        start_end_layout.setVisibility(View.VISIBLE);
                        Toast.makeText(getContext(), "Daily Transactions", Toast.LENGTH_SHORT).show();
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
                datePicker_start.show(getChildFragmentManager(), "materal_dateRange");
                datePicker_start.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(Object selection) {
                        Toast.makeText(getContext(), datePicker_start.getHeaderText(), Toast.LENGTH_SHORT).show();
                        binding.startInput.setText(datePicker_start.getHeaderText());
                        datePicker_start.dismiss();

                        binding.endInput.requestFocus();
                    }
                });
            }
        });

        binding.startInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b == true) {
                    datePicker_start.show(getChildFragmentManager(), "materal_dateRange");
                    datePicker_start.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                        @Override
                        public void onPositiveButtonClick(Object selection) {
                            Toast.makeText(getContext(), datePicker_start.getHeaderText(), Toast.LENGTH_SHORT).show();
                            binding.startInput.setText(datePicker_start.getHeaderText());
                            datePicker_start.dismiss();

                            binding.endInput.requestFocus();
                        }
                    });
                }

            }
        });



        //End Date Listner
        binding.endInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePicker_end.show(getChildFragmentManager(), "date_end");
                datePicker_end.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(Object selection) {
                        Log.i("Checkpoint", "Before the if statement" );
                        binding.endInput.setText(datePicker_end.getHeaderText());
                        if (!binding.startInput.getText().toString().matches("")) {
                            Log.i("Checkpoint", "In the if statement" );
                            String str_startDate = binding.startInput.getText().toString();
                            String str_endDate = binding.endInput.getText().toString();
                            DateFormat formatter = new SimpleDateFormat();
                            try {
                                java.util.Date startDate =   formatter.parse(str_startDate);
                                java.util.Date endDate = formatter.parse(str_endDate);
                                Log.i("Start date", "" + startDate.getTime());
                                Log.i("Start date", "" + endDate.getTime());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Log.i("Checkpoint", "If statement rejected" );

                        }
                    }
                });

            }
        });
        binding.endInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b == true) {
                    datePicker_end.show(getChildFragmentManager(), "date_end");
                    datePicker_end.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                        @Override
                        public void onPositiveButtonClick(Object selection) {
                            Log.i("Checkpoint", "Before the if statement" );
                            binding.endInput.setText(datePicker_end.getHeaderText());
                            if (!binding.startInput.getText().toString().matches("")) {
                                Log.i("Checkpoint", "In the if statement" );
                                String str_startDate = binding.startInput.getText().toString();
                                String str_endDate = binding.endInput.getText().toString();
                                SimpleDateFormat formatter = new SimpleDateFormat("MMM dd,yyyy");
                                try {
                                    java.util.Date start_date = formatter.parse(str_startDate);
                                    Date end_date = formatter.parse(str_endDate);
//                                    java.util.Date startDate =   formatter.parse(str_startDate);
//                                    java.util.Date endDate = formatter.parse(str_endDate);
                                    Log.i("Start date", "" + start_date.getTime());
                                    Log.i("End date", "" + end_date.getTime());



                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Log.i("Checkpoint", "If statement rejected" );

                            }
                        }
                    });
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}