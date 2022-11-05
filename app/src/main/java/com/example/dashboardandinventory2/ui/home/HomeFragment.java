package com.example.dashboardandinventory2.ui.home;

import static android.content.ContentValues.TAG;

import static java.lang.Math.abs;

import android.app.ProgressDialog;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.dashboardandinventory2.FragmentBottomSheetFull;
import com.example.dashboardandinventory2.NavigationDrawerActivity;
import com.example.dashboardandinventory2.R;
import com.example.dashboardandinventory2.databinding.FragmentHomeBinding;
import com.example.dashboardandinventory2.stockDialog;
import com.example.dashboardandinventory2.ui.gallery.GalleryFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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
        resetPadding();
        whatDayIsIt();

        binding.refresh.setRefreshing(true);
        fs.collection("DailyTransaction").document(getDate()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                try {
                    Map<String, Object> map = (Map<String, Object>) documentSnapshot.getData();
                    pendapatanHariIni = (long) map.get("total");
                    String nominalPendapatan_str = String.format("%,d", pendapatanHariIni).replace(",", ".");
                    binding.nominalPendapatan.setText("Rp. " + nominalPendapatan_str);



                } catch (Exception e) {
                    Toast.makeText(getContext(), "Belum ada input hari ini", Toast.LENGTH_SHORT).show();
                }
                getRevenue_7DaysAgo();


                fs.collection("MonthlyTransaction").document(getDate().substring(0, 7)).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        try {
                            Map<String, Object> map = (Map<String, Object>) documentSnapshot.getData();
                            pendapatanBulanIni = (long) map.get("total");
                            String nominalPendapatan_str = String.format("%,d", pendapatanBulanIni).replace(",", ".");
                            binding.pendapatanBulanIni.setText("Rp. " + nominalPendapatan_str);
                        } catch (Exception e) {
                            Toast.makeText(getContext(), "Belum ada input hari ini", Toast.LENGTH_SHORT).show();
                        }
                        //getThisDayLastMonth


                        fs.collection("YearlyTransaction").document(getDate().substring(0, 4)).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                try {
                                    Map<String, Object> map = (Map<String, Object>) documentSnapshot.getData();
                                    pendatanTahunIni = (long) map.get("total");
                                    String nominalPendapatan_str = String.format("%,d", pendatanTahunIni).replace(",", ".");
                                    binding.pendapatanTahunIni.setText("Rp. " + nominalPendapatan_str);
                                } catch (Exception e) {
                                    Toast.makeText(getContext(), "Belum ada input hari ini", Toast.LENGTH_SHORT).show();
                                }
                                //getThisDayLastYear
                            }
                        });

                    }
                });

            }
        });

        binding.refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDailyRevenue();


            }
        });





        binding.DailyRevenueContainer.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();

                Long datetime = System.currentTimeMillis();
                String dateTime_formatted = new SimpleDateFormat("dd MMM yyyy").format(datetime);
                bundle.putString("date", dateTime_formatted);
                bundle.putString("yearly_montly_daily", "daily");

                FragmentBottomSheetFull bottomSheetFull = new FragmentBottomSheetFull();
                bottomSheetFull.setArguments(bundle);
                bottomSheetFull.show(getActivity().getSupportFragmentManager(), bottomSheetFull.getTag());

            }
        });

        binding.MonthlyRevenueContainer.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();


                Long datetime = System.currentTimeMillis();
                String dateTime_formatted = new SimpleDateFormat("MMMM yyyy").format(datetime);
                bundle.putString("date", dateTime_formatted);
                bundle.putString("yearly_montly_daily", "monthly");

                FragmentBottomSheetFull bottomSheetFull = new FragmentBottomSheetFull();
                bottomSheetFull.setArguments(bundle);
                bottomSheetFull.show(getActivity().getSupportFragmentManager(), bottomSheetFull.getTag());
            }
        });

        binding.YearlyRevenueContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();

                Long datetime = System.currentTimeMillis();
                String dateTime_formatted = new SimpleDateFormat("dd MMM yyyy").format(datetime);
                bundle.putString("date", dateTime_formatted.substring(7));
                bundle.putString("yearly_montly_daily", "monthly");

                FragmentBottomSheetFull bottomSheetFull = new FragmentBottomSheetFull();
                bottomSheetFull.setArguments(bundle);
                bottomSheetFull.show(getActivity().getSupportFragmentManager(), bottomSheetFull.getTag());
            }
        });

        binding.HistoryContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GalleryFragment nextFrag= new GalleryFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(((ViewGroup)getView().getParent()).getId(), nextFrag, "findThisFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });

        int hari_ke = whatDayIsIt();
        binding.day1Container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout linearLayout = binding.day1Container;
                if (hari_ke >= 1) {
                    Toast.makeText(getContext(), "Jum'at", Toast.LENGTH_SHORT).show();
                    binding.resetButton.setVisibility(View.VISIBLE);
                    binding.periodeTitle.setText("Jum'at Periode ini");
                    resetPadding();
                    binding.day1Container.setPadding(17, 17, 17, 17);
                }
            }
        });
        binding.day2Container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout linearLayout = binding.day2Container;
                if (hari_ke >= 2) {
                    Toast.makeText(getContext(), "Sabtu", Toast.LENGTH_SHORT).show();
                    binding.resetButton.setVisibility(View.VISIBLE);
                    binding.periodeTitle.setText("Sabtu Periode ini");
                    resetPadding();
                    binding.day2Container.setPadding(17, 17, 17, 17);
                } else {
                    masihHari_x(linearLayout);
                }
            }
        });
        binding.day3Container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout linearLayout = binding.day3Container;
                if (hari_ke >= 3) {
                    Toast.makeText(getContext(), "Minggu", Toast.LENGTH_SHORT).show();
                    binding.resetButton.setVisibility(View.VISIBLE);
                    binding.periodeTitle.setText("Minggu Periode ini");
                    resetPadding();
                    binding.day3Container.setPadding(17, 17, 17, 17);
                } else {
                    masihHari_x(linearLayout);
                }
            }
        });
        binding.day4Container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout linearLayout = binding.day4Container;
                if (hari_ke >= 4) {
                    Toast.makeText(getContext(), "Senin", Toast.LENGTH_SHORT).show();
                    binding.resetButton.setVisibility(View.VISIBLE);
                    binding.periodeTitle.setText("Senin Periode ini");
                    resetPadding();
                    binding.day4Container.setPadding(17, 17, 17, 17);
                } else {
                    masihHari_x(linearLayout);
                }
            }
        });
        binding.day5Container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout linearLayout = binding.day5Container;
                if (hari_ke >= 5) {
                    Toast.makeText(getContext(), "Selasa", Toast.LENGTH_SHORT).show();
                    binding.resetButton.setVisibility(View.VISIBLE);
                    binding.periodeTitle.setText("Selasa Periode ini");
                    resetPadding();
                    binding.day5Container.setPadding(17, 17, 17, 17);
                } else {
                    masihHari_x(linearLayout);
                }
            }
        });
        binding.day6Container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout linearLayout = binding.day6Container;
                if (hari_ke >= 6) {
                    Toast.makeText(getContext(), "Rabu", Toast.LENGTH_SHORT).show();
                    binding.resetButton.setVisibility(View.VISIBLE);
                    binding.periodeTitle.setText("Rabu Periode ini");
                    resetPadding();
                    binding.day6Container.setPadding(17, 17, 17, 17);
                } else {
                    masihHari_x(linearLayout);
                }
            }
        });
        binding.day7Container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout linearLayout = binding.day7Container;
                if (hari_ke >= 7){
                Toast.makeText(getContext(), "Kamis", Toast.LENGTH_SHORT).show();
                binding.resetButton.setVisibility(View.VISIBLE);
                binding.periodeTitle.setText("Kamis Periode ini");
                resetPadding();
                binding.day7Container.setPadding(17,17,17,17);
                }  else {
                    masihHari_x(linearLayout);
                }
            }
        });

        binding.resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Periode ini", Toast.LENGTH_SHORT).show();
                binding.resetButton.setVisibility(View.GONE);
                binding.periodeTitle.setText("Periode ini");
                resetPadding();
            }
        });

















        return root;
    }

    public void masihHari_x(View linearLayout) {
        Toast.makeText(getContext(), "Masih hari " + getThisDayLastWeek().get(1), Toast.LENGTH_SHORT).show();
        linearLayout.setPadding(17,17,17,17);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 2s
                linearLayout.setPadding(22,22,22,22);

            }
        }, 75);
    }


    public int whatDayIsIt() {
        String hari = getThisDayLastWeek().get(1);
        int hari_ke;
        switch (hari) {
            case "Jum'at":
                hari_ke = 1;
                binding.day1.setBackground(getResources().getDrawable(R.drawable.outline_with_green_fill));
                break;
            case "Sabtu":
                hari_ke = 2;
                binding.day1.setBackground(getResources().getDrawable(R.drawable.outline_with_green_fill));
                binding.day2.setBackground(getResources().getDrawable(R.drawable.outline_with_green_fill));
                break;
            case "Minggu":
                hari_ke = 3;
                binding.day1.setBackground(getResources().getDrawable(R.drawable.outline_with_green_fill));
                binding.day2.setBackground(getResources().getDrawable(R.drawable.outline_with_green_fill));
                binding.day3.setBackground(getResources().getDrawable(R.drawable.outline_with_green_fill));

                break;
            case "Senin":
                hari_ke = 4;
                binding.day1.setBackground(getResources().getDrawable(R.drawable.outline_with_green_fill));
                binding.day2.setBackground(getResources().getDrawable(R.drawable.outline_with_green_fill));
                binding.day3.setBackground(getResources().getDrawable(R.drawable.outline_with_green_fill));
                binding.day4.setBackground(getResources().getDrawable(R.drawable.outline_with_green_fill));

                break;
            case "Selasa":
                hari_ke = 5;
                binding.day1.setBackground(getResources().getDrawable(R.drawable.outline_with_green_fill));
                binding.day2.setBackground(getResources().getDrawable(R.drawable.outline_with_green_fill));
                binding.day3.setBackground(getResources().getDrawable(R.drawable.outline_with_green_fill));
                binding.day4.setBackground(getResources().getDrawable(R.drawable.outline_with_green_fill));
                binding.day5.setBackground(getResources().getDrawable(R.drawable.outline_with_green_fill));

                break;
            case "Rabu":
                hari_ke = 6;
                binding.day1.setBackground(getResources().getDrawable(R.drawable.outline_with_green_fill));
                binding.day2.setBackground(getResources().getDrawable(R.drawable.outline_with_green_fill));
                binding.day3.setBackground(getResources().getDrawable(R.drawable.outline_with_green_fill));
                binding.day4.setBackground(getResources().getDrawable(R.drawable.outline_with_green_fill));
                binding.day5.setBackground(getResources().getDrawable(R.drawable.outline_with_green_fill));
                binding.day6.setBackground(getResources().getDrawable(R.drawable.outline_with_green_fill));
                break;
            case "Kamis":
                hari_ke = 7;
                binding.day1.setBackground(getResources().getDrawable(R.drawable.outline_with_green_fill));
                binding.day2.setBackground(getResources().getDrawable(R.drawable.outline_with_green_fill));
                binding.day3.setBackground(getResources().getDrawable(R.drawable.outline_with_green_fill));
                binding.day4.setBackground(getResources().getDrawable(R.drawable.outline_with_green_fill));
                binding.day5.setBackground(getResources().getDrawable(R.drawable.outline_with_green_fill));
                binding.day6.setBackground(getResources().getDrawable(R.drawable.outline_with_green_fill));
                binding.day7.setBackground(getResources().getDrawable(R.drawable.outline_with_green_fill));

                break;
            default:
                hari_ke = 0;
        }

        return hari_ke;

    }




    private void resetPadding() {

        binding.day1Container.setPadding(22,22,22,22);
        binding.day2Container.setPadding(22,22,22,22);
        binding.day3Container.setPadding(22,22,22,22);
        binding.day4Container.setPadding(22,22,22,22);
        binding.day5Container.setPadding(22,22,22,22);
        binding.day6Container.setPadding(22,22,22,22);
        binding.day7Container.setPadding(22,22,22,22);
    }

    private void getDailyRevenue() {
        binding.refresh.setRefreshing(true);

        fs.collection("DailyTransaction").document(getDate()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                try {
                    Map<String, Object> map = (Map<String, Object>) documentSnapshot.getData();
                    pendapatanHariIni = (long) map.get("total");
                    String nominalPendapatan_str = String.format("%,d", pendapatanHariIni).replace(",", ".");
                    binding.nominalPendapatan.setText("Rp. " + nominalPendapatan_str);



                } catch (Exception e) {
                    Toast.makeText(getContext(), "Belum ada input hari ini", Toast.LENGTH_SHORT).show();
                }

                binding.refresh.setRefreshing(false);

//                getRevenue_7DaysAgo();
            }
        });

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
                    String nominalPendapatan_str = "Rp" + String.format("%,d", abs(selisih)).replace(",", ".");
                    StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);

                    if (selisih > 0) {

                        String text = "Naik " + nominalPendapatan_str + " dari hari " + hari + " minggu lalu";
                        SpannableString ss = new SpannableString(text);
                        ForegroundColorSpan fcsGreen = new ForegroundColorSpan(getResources().getColor(R.color.main_green));
                        ss.setSpan(fcsGreen, 4, 4+nominalPendapatan_str.length()+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        ss.setSpan(boldSpan, 0, 4+nominalPendapatan_str.length()+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                        binding.pendapatanTodayLastWeek.setText(ss);
                    } else {
                        String text = "Turun " + nominalPendapatan_str + " dari hari " + hari + " minggu lalu";
                        SpannableString ss = new SpannableString(text);
                        ForegroundColorSpan fcsGreen = new ForegroundColorSpan(getResources().getColor(R.color.purple_700));
                        ss.setSpan(fcsGreen, 5, 5+nominalPendapatan_str.length()+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        ss.setSpan(boldSpan, 0, 5+nominalPendapatan_str.length()+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                        binding.pendapatanTodayLastWeek.setText(ss);
                    }
                    binding.refresh.setRefreshing(false);
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
        cal = new GregorianCalendar();
        cal.add(Calendar.DAY_OF_MONTH, -7);
        Date sevenDaysAgo_date = cal.getTime();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatter_day = new SimpleDateFormat("EEEE, yyyy MM dd", locale);

        String sevenDaysAgo_str = formatter.format(sevenDaysAgo_date);
        String day_date = formatter_day.format(sevenDaysAgo_date);
        String day = day_date.substring(0, day_date.indexOf(","));


        ArrayList_sevenDaysAgo_date_day.add(sevenDaysAgo_str);
        ArrayList_sevenDaysAgo_date_day.add(day);

        return ArrayList_sevenDaysAgo_date_day;
    }




}