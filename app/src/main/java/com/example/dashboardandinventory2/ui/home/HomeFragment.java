package com.example.dashboardandinventory2.ui.home;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.dashboardandinventory2.NavigationDrawerActivity;
import com.example.dashboardandinventory2.R;
import com.example.dashboardandinventory2.databinding.FragmentHomeBinding;
import com.example.dashboardandinventory2.stockDialog;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;

public class HomeFragment extends Fragment {






//    private AppBarConfiguration mAppBarConfiguration;
//    private ActivityNavigationDrawerBinding binding;

     HomeViewModel homeViewModel;
     FragmentHomeBinding binding;

     FirebaseFirestore fs;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
//                textView.setText(s);
            }
        });

        fs = FirebaseFirestore.getInstance();


        fs.collection("DailyTransaction").document(getDate()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                Map<String, Object> map = (Map<String, Object>) documentSnapshot.getData();
                long nominalPendapatan = (long) map.get("total");
                String nominalPendapatan_str = String.format("%,d", nominalPendapatan).replace(",", ".");
                binding.nominalPendapatan.setText("Rp. " + nominalPendapatan_str);


            }
        });















        return root;
    }

    public void timeFrameButtonClick(View view) {
        Log.i(TAG, "timeFrameButtonClick: ");


    }

    public String getDate() {
        Long datetime = System.currentTimeMillis();
        Timestamp timestamp = new Timestamp(datetime);
        String date_full = (String) String.valueOf(timestamp);
        String date = date_full.substring(0, 10);
        return date;
    }




}