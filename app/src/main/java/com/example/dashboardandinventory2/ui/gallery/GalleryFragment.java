package com.example.dashboardandinventory2.ui.gallery;

import android.os.Bundle;
import android.text.InputType;
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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.dashboardandinventory2.R;
import com.example.dashboardandinventory2.databinding.FragmentGalleryBinding;

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