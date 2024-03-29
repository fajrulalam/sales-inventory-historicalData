package com.example.dashboardandinventory2;

import android.app.Dialog;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.dashboardandinventory2.ui.gallery.RecycleAdapter;
import com.example.dashboardandinventory2.ui.gallery.RecycleAdapter_sales;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

///**
// * A simple {@link Fragment} subclass.
// * Use the {@link FragmentBottomSheetFull#newInstance} factory method to
// * create an instance of this fragment.
// */
public class FragmentBottomSheetFull extends BottomSheetDialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentBottomSheetFull() {
        // Required empty public constructor
    }

    private AppBarLayout appBarLayout;
    private LinearLayout linearLayout;
    private TextView appBarLayoutTextView;
    private ArrayList<String> itemTitleMinuman;
    private ArrayList<String> itemTitleMakanan;
    private ArrayList<String> itemTitle;
    private  ArrayList<String> noOfSales_makanan;
    private  ArrayList<String> noOfSales_minuman;
    private RecyclerView recyclerView_bottomSheetMakanan;
    private RecyclerView recyclerView_bottomSheetMinuman;
    private FirebaseFirestore fs;




    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        final View view = View.inflate(getContext(), R.layout.fragment_bottom_sheet_full, null);
        dialog.setContentView(view);

        Bundle bundle = this.getArguments();
        String date = bundle.getString("date");
        String yearly_montly_daily = bundle.getString("yearly_montly_daily");
        String collection = "";

        Log.i("Date", date);

//        Date dateformatter =

        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());
        bottomSheetBehavior.setPeekHeight(BottomSheetBehavior.PEEK_HEIGHT_AUTO);

        appBarLayoutTextView = view.findViewById(R.id.appBarLayoutTextView);
        appBarLayout = view.findViewById(R.id.appBarLayout);
        linearLayout = view.findViewById(R.id.linearLayout);
        hideView(appBarLayout);


        itemTitleMinuman = new ArrayList<>();
        itemTitleMakanan = new ArrayList<>();
        itemTitle = new ArrayList<>();
        noOfSales_makanan = new ArrayList<>();
        noOfSales_minuman = new ArrayList<>();
        recyclerView_bottomSheetMakanan = view.findViewById(R.id.RecycleView_salesBottomSheetMakanan);
        recyclerView_bottomSheetMinuman = view.findViewById(R.id.RecycleView_salesBottomSheetMinuman);
//        RecycleAdapter_sales recycleAdapter_sales = new RecycleAdapter_sales(itemTitle, noOfSales);
//        recyclerView_bottomSheet.setAdapter(recycleAdapter_sales);
        fs = FirebaseFirestore.getInstance();

        String document = "";
        String[] date_split = date.split(" ");
        String[] date_split_dash = date.split("-");
//        collection = "DailyTransaction";

        //true if it's YEARLY
        if (date.length() == 4) {
            document = date;
            collection = "YearlyTransaction";

        }

        // true if it's MONTHLY
        if (date_split.length == 2  ^ (date_split_dash.length == 2) ) {
            SimpleDateFormat formatter2 = new SimpleDateFormat("MMMM yyyy");
            try {
                Date month = formatter2.parse(date);
                long month_epoch = month.getTime();
                Date month_date_Date = new Date(month_epoch);
                String dateTime_formatted = new SimpleDateFormat("yyyy-MM").format(month_date_Date);
                document = dateTime_formatted;
            } catch (ParseException parseException) {
                parseException.printStackTrace();
            }
            collection = "MonthlyTransaction";

        }


        //true if it's DAILY
        if ((date_split.length == 3)) {
            SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
            try {
                Date date1 = formatter.parse(date);
                long month_epoch = date1.getTime();
                Date month_date_Date = new Date(month_epoch);
                String dateTime_formatted = new SimpleDateFormat("yyyy-MM-dd").format(month_date_Date);
                document = dateTime_formatted;
            } catch (ParseException parseException) {
                parseException.printStackTrace();
            }

            collection = "DailyTransaction";


        }

        Log.i("Document", document);



        appBarLayoutTextView.setText(date);

        fs.collection(collection).document(document).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String, Object> map = (Map<String, Object>) documentSnapshot.getData();
                SortedMap<String, Object> map_sorted = new TreeMap<>();
                map_sorted.putAll(map);
                Log.i("Map sorted", map_sorted.toString());
                for(Map.Entry<String,Object> entry : map_sorted.entrySet()) {

                        switch (entry.getKey()){
                            case "Aqua 600ml":
                            case "Coca Cola":
                            case "Es Kopi Durian":
                            case "Es Teh":
                            case "Fanta":
                            case "Floridina":
                            case "Frestea":
                            case "Isoplus":
                            case "Kopi Hitam":
                            case "Es Milo":
                            case "Es Dawet Cincau":
                            case "Sprite":
                            case "Teh Panas":
                            case "Teh Pucuk Harum":
                                itemTitleMinuman.add(entry.getKey());
                                noOfSales_minuman.add(entry.getValue().toString());
                                break;
                            default:
                                itemTitle.add(entry.getKey());
                                Log.i("entry", itemTitle.toString());
                                noOfSales_makanan.add(entry.getValue().toString());
                                break;
                        }

                        //remove metadata "date" "year" "month" "customerNumber" "timeStamp" "total"
                        String[] metadatas = {"date", "year", "month", "customerNumber", "timestamp", "total"};
                        for (String metadata : metadatas) {
                            int index = itemTitle.indexOf(metadata);
                            if(index != -1) {
                                itemTitle.remove(index);
                                noOfSales_makanan.remove(index);
                            }
                        }

                }
                RecycleAdapter_sales recycleAdapter_sales = new RecycleAdapter_sales(itemTitle, noOfSales_makanan);
                RecycleAdapter_sales recycleAdapter_sales_minuman = new RecycleAdapter_sales(itemTitleMinuman, noOfSales_minuman);
                recyclerView_bottomSheetMakanan.setAdapter(recycleAdapter_sales);
                recyclerView_bottomSheetMinuman.setAdapter(recycleAdapter_sales_minuman);


            }
        });


//       fs.collection()



        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (BottomSheetBehavior.STATE_EXPANDED == newState) {
                    showView(appBarLayout, getActionBarSize());
                    hideView(linearLayout);
                }

                if (BottomSheetBehavior.STATE_COLLAPSED == newState)   {
                    hideView(appBarLayout);
                    showView(linearLayout, getActionBarSize());
                }

                if (BottomSheetBehavior.STATE_HIDDEN == newState) {
                    dismiss();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        view.findViewById(R.id.closeSheet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });


        return dialog;
    }


    private void hideView(View view) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = 0;
        view.setLayoutParams(params);
    }

    private void showView(View view, int size) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = size;
        view.setLayoutParams(params);
    }

    private int getActionBarSize(){
        final TypedArray typedArray =getContext().getTheme().obtainStyledAttributes(new int[]{
                android.R.attr.actionBarSize
        });

        return (int) typedArray.getDimension(0, 0);
    }
}