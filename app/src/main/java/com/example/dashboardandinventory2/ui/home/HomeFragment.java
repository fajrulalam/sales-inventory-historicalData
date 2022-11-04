package com.example.dashboardandinventory2.ui.home;

import static android.content.ContentValues.TAG;

import static java.lang.Math.abs;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Map;

public class HomeFragment extends Fragment {






//    private AppBarConfiguration mAppBarConfiguration;
//    private ActivityNavigationDrawerBinding binding;

     HomeViewModel homeViewModel;
     FragmentHomeBinding binding;

     long pendapatanHariIni;
     long pendapatan7HariYLL;
     long pendapatanBulanIni;
     long pendapatanBulanLalu;
     long pendatanTahunIni;
     long pendapatanTahunLalu;
     Locale locale;

     FirebaseFirestore fs;
        Calendar cal;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        cal = new GregorianCalendar();
        locale = new Locale("id", "ID");





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

                try {
                    Map<String, Object> map = (Map<String, Object>) documentSnapshot.getData();
                    pendapatanHariIni = (long) map.get("total");
                    String nominalPendapatan_str = String.format("%,d", pendapatanHariIni).replace(",", ".");
                    binding.nominalPendapatan.setText("Rp. " + nominalPendapatan_str);

                    getRevenue_7DaysAgo();
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Belum ada input hari ini", Toast.LENGTH_SHORT).show();
                }
            }
        });

















        return root;
    }

    public void timeFrameButtonClick(View view) {
        Log.i(TAG, "timeFrameButtonClick: ");


    }

    public void getRevenue_7DaysAgo() {
        fs.collection("DailyTransaction").document(getThisDayLastWeek().get(0)).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                try {
                    Map<String, Object> map = (Map<String, Object>) documentSnapshot.getData();
                    pendapatan7HariYLL = (long) map.get("total");
                    long selisih = pendapatanHariIni - pendapatan7HariYLL;
                    Log.i("hari ini", "" +pendapatanHariIni);
                    Log.i("7 hari yll", "" +pendapatan7HariYLL);
                    Log.i("selisih", "" +selisih);
                    String hari = getThisDayLastWeek().get(1);
                    String nominalPendapatan_str = "Rp. " + String.format("%,d", abs(selisih)).replace(",", ".");

                    if (selisih > 0) {
                        binding.pendapatanTodayLastWeek.setText("Naik " + nominalPendapatan_str + " dari hari " + hari + " minggu lalu");
                    } else {
                        binding.pendapatanTodayLastWeek.setText("Turun " + nominalPendapatan_str + " dari hari " + hari + " minggu lalu");
                    }
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Belum ada input hari ini", Toast.LENGTH_SHORT).show();
                }



            }
        });
    }

    public String getDate() {
        Long datetime = System.currentTimeMillis();
        Timestamp timestamp = new Timestamp(datetime);
        String date_full = (String) String.valueOf(timestamp);
        String date = date_full.substring(0, 10);
        return date;
    }

    public ArrayList<String> getThisDayLastWeek() {
        ArrayList<String> ArrayList_sevenDaysAgo_date_day = new ArrayList<>();
        cal.add(Calendar.DAY_OF_MONTH, -7);
        Date sevenDaysAgo_date = cal.getTime();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatter_day = new SimpleDateFormat("EEE, yyyy MM dd", locale);

        String sevenDaysAgo_str = formatter.format(sevenDaysAgo_date);
        String day_date = formatter_day.format(sevenDaysAgo_date);
        String day = day_date.substring(0, day_date.indexOf(","));
        if (day.matches("Jum")) {
            day = day + "'at";
        }

        ArrayList_sevenDaysAgo_date_day.add(sevenDaysAgo_str);
        ArrayList_sevenDaysAgo_date_day.add(day);

        return ArrayList_sevenDaysAgo_date_day;
    }




}