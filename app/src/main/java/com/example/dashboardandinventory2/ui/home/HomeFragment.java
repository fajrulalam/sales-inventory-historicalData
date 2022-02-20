package com.example.dashboardandinventory2.ui.home;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.content.res.ColorStateList;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;

public class HomeFragment extends Fragment implements stockDialog.UpdateStock{

    ArrayList barArrayList;
    String[] labels;
    TextView totalHariIniTextView;
    DatabaseReference reff;
    DatabaseReference reffStock;
    Query revToday;
    Query revMonth;
    Query revYear;
    Query revMakananSales;
    Query revMakananStock;
    Query revMinumanSales;
    Query revMinumanStock;
    TextView pendaptanKapanTextView;
    TextView nominalPendapatanTextView;
    Button dayButton;
    Button monthButton;
    Button yearButton;
    Button alltimeButton;

    FirebaseFirestore fs;
    SwipeRefreshLayout refreshLayout;

    String menu;

    ArrayList<String> makananList;
    ArrayList<String> minumanList;
    ArrayList<Integer> makananSales;
    ArrayList<Integer> makananInventory;
    ArrayList<Integer> minumanSales;
    ArrayList<Integer> minumanInventory;

    //Table
    TextView baksoSales;
    TextView baksoStock;
    TextView kentangSales;
    TextView kentangStock;
    TextView nasbungASales;
    TextView nasbungAStock;
    TextView nasbungBSales;
    TextView nasbungBStock;
    TextView nasiLaukSales;
    TextView nasiLaukStock;
    TextView popmieSales;
    TextView popmieStock;
    TextView siomaySales;
    TextView siomayStock;
    TextView mieAyamSales;
    TextView mieAyamStock;
    TextView nasiAyamSales;
    TextView nasiAyamStock;
    TextView nasiPindangSales;
    TextView nasiPindangStock;
    TextView nasiTelurSales;
    TextView nasiTelurStock;
    TextView pisangGorengSales;
    TextView pisangGorengStock;
    TextView serealSales;
    TextView serealStock;
    TextView tahuGorengSales;
    TextView tahuGorengStock;
    TextView sosisNagetSales;
    TextView sosisNagetStock;



    TextView aquaSales;
    TextView aquaStock;
    TextView cocaColaSales;
    TextView cocaColaStock;
    TextView esKopiDurianSales;
    TextView esKopiDurianStock;
    TextView fantaSales;
    TextView fantaStock;
    TextView fresteaSales;
    TextView fresteaStock;
    TextView kopiHitamSales;
    TextView kopiHitamStock;
    TextView miloSales;
    TextView miloStock;
    TextView spriteSales;
    TextView spriteStock;
    TextView tehGelasSales;
    TextView tehGelasStock;
    TextView tehPucukHarumSales;
    TextView tehPucukHarumStock;
    TextView floridinaSales;
    TextView floridinaStock;
    TextView isoplusSales;
    TextView isoplusStock;









//    private AppBarConfiguration mAppBarConfiguration;
//    private ActivityNavigationDrawerBinding binding;

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

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

        refreshLayout = root.findViewById(R.id.refresh);

        //Define
        makananList = new ArrayList<>();
        minumanList = new ArrayList<>();
        makananInventory = new ArrayList<>();
        makananSales = new ArrayList<>();
        minumanInventory = new ArrayList<>();
        minumanSales = new ArrayList<>();
        pendaptanKapanTextView = (TextView) root.findViewById(R.id.pendapatanKapan);
        nominalPendapatanTextView = (TextView) root.findViewById(R.id.nominalPendapatan);
        dayButton = root.findViewById(R.id.buttonDaily);
        monthButton = root.findViewById(R.id.buttonMonthly);
        yearButton = root.findViewById(R.id.buttonYearly);
        alltimeButton = root.findViewById(R.id.buttonAlltime);
        ProgressDialog progressDialog;

        //Define Makanan Table
        baksoSales = root.findViewById(R.id.sales_1);
        baksoStock = root.findViewById(R.id.stock_1);
        kentangSales = root.findViewById(R.id.sales_2);
        kentangStock = root.findViewById(R.id.stock_2);
        mieAyamSales = root.findViewById(R.id.sales_3);
        mieAyamStock = root.findViewById(R.id.stock_3);
        nasbungASales = root.findViewById(R.id.sales_4);
        nasbungAStock = root.findViewById(R.id.stock_4);
        nasbungBSales = root.findViewById(R.id.sales_5);
        nasbungBStock = root.findViewById(R.id.stock_5);
        nasiAyamSales = root.findViewById(R.id.sales_6);
        nasiAyamStock = root.findViewById(R.id.stock_6);
        nasiPindangSales = root.findViewById(R.id.sales_7);
        nasiPindangStock = root.findViewById(R.id.stock_7);
        nasiTelurSales = root.findViewById(R.id.sales_8);
        nasiTelurStock = root.findViewById(R.id.stock_8);
        pisangGorengSales = root.findViewById(R.id.sales_9);
        pisangGorengStock = root.findViewById(R.id.stock_9);
        popmieSales = root.findViewById(R.id.sales_10);
        popmieStock = root.findViewById(R.id.stock_10);
        serealSales = root.findViewById(R.id.sales_11);
        serealStock = root.findViewById(R.id.stock_11);
        tahuGorengSales = root.findViewById(R.id.sales_12);
        tahuGorengStock = root.findViewById(R.id.stock_12);
        siomaySales = root.findViewById(R.id.sales_13);
        siomayStock = root.findViewById(R.id.stock_13);
        sosisNagetSales = root.findViewById(R.id.sales_14);
        sosisNagetStock = root.findViewById(R.id.stock_14);

        //Define Minuman Table
        aquaSales = root.findViewById(R.id.sales2_1);
        aquaStock = root.findViewById(R.id.stock2_1);
        cocaColaSales = root.findViewById(R.id.sales2_2);
        cocaColaStock = root.findViewById(R.id.stock2_2);
        esKopiDurianSales = root.findViewById(R.id.sales2_3);
        esKopiDurianStock = root.findViewById(R.id.stock2_3);
        tehGelasSales = root.findViewById(R.id.sales2_4);
        tehGelasStock = root.findViewById(R.id.stock2_4);
        fantaSales = root.findViewById(R.id.sales2_5);
        fantaStock = root.findViewById(R.id.stock2_5);
        floridinaSales = root.findViewById(R.id.sales2_6);
        floridinaStock = root.findViewById(R.id.stock2_6);
        fresteaSales = root.findViewById(R.id.sales2_7);
        fresteaStock = root.findViewById(R.id.stock2_7);
        isoplusSales = root.findViewById(R.id.sales2_8);
        isoplusStock = root.findViewById(R.id.stock2_8);
        kopiHitamSales = root.findViewById(R.id.sales2_9);
        kopiHitamStock = root.findViewById(R.id.stock2_9);
        miloSales = root.findViewById(R.id.sales2_10);
        miloStock = root.findViewById(R.id.stock2_10);
        spriteSales = root.findViewById(R.id.sales2_11);
        spriteStock = root.findViewById(R.id.stock2_11);
        tehPucukHarumSales = root.findViewById(R.id.sales2_12);
        tehPucukHarumStock = root.findViewById(R.id.stock2_12);


//        reff = FirebaseDatabase.getInstance("https://point-of-sales-app-25e2b-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("TransacationDetail");
//        reffStock = FirebaseDatabase.getInstance("https://point-of-sales-app-25e2b-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("StockCount");
//        revToday = reff.orderByChild("timeStamp").startAt(getDate()).endAt(getDate() + "\uf8ff");
//        revMonth = reff.orderByChild("timeStamp").startAt(getMonth()).endAt(getMonth() + "\uf8ff");
//        revYear = reff.orderByChild("timeStamp").startAt(getYear()).endAt(getYear() + "\uf8ff");



//        totalHariIniTextView = findViewById(R.id.TotalHariIni);
//        revToday.addListenerForSingleValueEvent(revenueCount);

        InsertMenu();
        resetData();
        insertZeros();

        menu = "day";

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateStock();

                switch (menu) {
                    case "day":
                        querySales("DailyTransaction", getDate());
                        break;
                    case "month":
                        querySales("MonthlyTransaction", getMonth());
                        break;
                    case "year":
                    case "alltime":
                        querySales("YearlyTransaction", getYear());
                        break;

                }
                refreshLayout.setRefreshing(false);


//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//
//
//
//                    }
//                }, 100);






            }
        });

        updateStock();
        querySales("DailyTransaction", getDate());









        return root;
    }

    public void timeFrameButtonClick(View view) {
        Log.i(TAG, "timeFrameButtonClick: ");


        monthButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.buttonNotSelected)));
        dayButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.buttonNotSelected)));
        yearButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.buttonNotSelected)));
        alltimeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.buttonNotSelected)));
//        makananSales.clear();
//        makananInventory.clear();
//        minumanInventory.clear();
//        minumanSales.clear();

        view.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.buttonSelected)));
//        sterilizeValue();
        resetData();
//        makananSales.clear();
//        makananInventory.clear();
//        minumanInventory.clear();
//        minumanSales.clear();





        switch (view.getTag().toString()) {
            case "day":
                menu = "day";
                querySales("DailyTransaction", getDate());
                pendaptanKapanTextView.setText("Pendapatan Hari ini");
                break;
            case "month":
                menu = "month";
                querySales("MonthlyTransaction", getMonth());
                pendaptanKapanTextView.setText("Pendapatan Bulan ini");
                break;
            case "year":
                menu = "year";
                querySales("YearlyTransaction", getYear());
                pendaptanKapanTextView.setText("Pendapatan Tahun ini");
                break;
            case "alltime":
                menu = "alltime";
                querySales("YearlyTransaction", getYear());
                pendaptanKapanTextView.setText("Pendapatan All Time");
                break;

        }




    }

    public void updateStock(){
        fs.collection("Stock").document("Stocks").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String, Object> map = (Map<String, Object>) documentSnapshot.getData();
                Object sales1_obj = map.get("Bakso");
                String sales1_str = sales1_obj.toString();
                baksoStock.setText(sales1_str);
                Object sales2_obj = map.get("Kentang G");
                String sales2_str = sales2_obj.toString();
                kentangStock.setText(sales2_str);
                Object sales3_obj = map.get("Mie Ayam");
                String sales3_str = sales3_obj.toString();
                mieAyamStock.setText(sales3_str);
                Object sales4_obj = map.get("NasBung A");
                String sales4_str = sales4_obj.toString();
                nasbungAStock.setText(sales4_str);
                Object sales5_obj = map.get("NasBung B");
                String sales5_str = sales5_obj.toString();
                nasbungBStock.setText(sales5_str); //5
                Object sales6_obj = map.get("Nasi Ayam");
                String sales6_str = sales6_obj.toString();
                nasiAyamStock.setText(sales6_str);
                Object sales7_obj = map.get("Nasi Pindang");
                String sales7_str = sales7_obj.toString();
                nasiPindangStock.setText(sales7_str);
                Object sales8_obj = map.get("Nasi Telur");
                String sales8_str = sales8_obj.toString();
                nasiTelurStock.setText(sales8_str);
                Object sales9_obj = map.get("Pisang G");
                String sales9_str = sales9_obj.toString();
                pisangGorengStock.setText(sales9_str);
                Object sales10_obj = map.get("Popmie");
                String sales10_str = sales10_obj.toString();
                popmieStock.setText(sales10_str); //10
                Object sales11_obj = map.get("Sereal");
                String sales11_str = sales11_obj.toString();
                serealStock.setText(sales11_str);
                Object sales12_obj = map.get("Tahu G");
                String sales12_str = sales12_obj.toString();
                tahuGorengStock.setText(sales12_str);
                Object sales13_obj = map.get("Siomay");
                String sales13_str = sales13_obj.toString();
                siomayStock.setText(sales13_str);
                Object sales14_obj = map.get("Sosis Naget");
                String sales14_str = sales14_obj.toString();
                sosisNagetStock.setText(sales14_str);
                Object sales15_obj = map.get("Aqua 600ml");
                String sales15_str = sales15_obj.toString();
                aquaStock.setText(sales15_str); //15
                Object sales16_obj = map.get("Coca-Cola");
                String sales16_str = sales16_obj.toString();
                cocaColaStock.setText(sales16_str);
                Object sales17_obj = map.get("Es Kopi Durian");
                String sales17_str = sales17_obj.toString();
                esKopiDurianStock.setText(sales17_str);
                Object sales18_obj = map.get("Es Teh");
                String sales18_str = sales18_obj.toString();
                tehGelasStock.setText(sales18_str);
                Object sales19_obj = map.get("Fanta");
                String sales19_str = sales19_obj.toString();
                fantaStock.setText(sales19_str);
                Object sales20_obj = map.get("Floridina");
                String sales20_str = sales20_obj.toString();
                floridinaStock.setText(sales20_str); //20
                Object sales21_obj = map.get("Frestea");
                String sales21_str = sales21_obj.toString();
                fresteaStock.setText(sales21_str);
                Object sales22_obj = map.get("Isoplus");
                String sales22_str = sales22_obj.toString();
                isoplusStock.setText(sales22_str);
                Object sales23_obj = map.get("Kopi Hitam");
                String sales23_str = sales23_obj.toString();
                kopiHitamStock.setText(sales23_str);
                Object sales24_obj = map.get("Milo");
                String sales24_str = sales24_obj.toString();
                miloStock.setText(sales24_str);
                Object sales25_obj = map.get("Sprite");
                String sales25_str = sales25_obj.toString();
                spriteStock.setText(sales25_str);
                Object sales26_obj = map.get("Teh Pucuk Harum");
                String sales26_str = sales26_obj.toString();
                tehPucukHarumStock.setText(sales26_str);
            }
        });

    }

    public void InsertMenu() {
        //Makanan
        makananList.add("Bakso"); //0
        makananList.add("Kentang G");
        makananList.add("Mie Ayam");
        makananList.add("NasBung A");
        makananList.add("NasBung B");
        makananList.add("Nasi Ayam");
        makananList.add("Nasi Pindang"); // 7
        makananList.add("Nasi Telur");
        makananList.add("Pisang G");
        makananList.add("Popmie");
        makananList.add("Sereal");
        makananList.add("Tahu G");
        makananList.add("Siomay");
        makananList.add("Sosis Naget"); //14


        //Minuman
        minumanList.add("Aqua 600ml"); //0
        minumanList.add("Coca-Cola");
        minumanList.add("Es Kopi Durian");
        minumanList.add("Es Teh");
        minumanList.add("Fanta");
        minumanList.add("Floridina");
        minumanList.add("Frestea"); //7
        minumanList.add("Isoplus");
        minumanList.add("Kopi Hitam");
        minumanList.add("Milo");
        minumanList.add("Sprite");
        minumanList.add("Teh Pucuk Harum"); //12



        Log.i("Menu:", "Has been successfully inserted");


    }

    public void querySales(String collection, String document) {
        fs.collection(collection).document(document).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String, Object> map = (Map<String, Object>) documentSnapshot.getData();
                Object totalRev = map.get("total");
                Log.i("Total", totalRev.toString());
                String totalRev_str = totalRev.toString();
                int totalRev_int = Integer.parseInt(totalRev_str);
                totalRev_str = String.format("%,d", totalRev_int).replace(',', '.');
                nominalPendapatanTextView.setText("Rp "+totalRev_str);


                Object sales1_obj = map.get("Bakso");
                String sales1_str = sales1_obj.toString();
                baksoSales.setText(sales1_str);
                Object sales2_obj = map.get("Kentang G");
                String sales2_str = sales2_obj.toString();
                kentangSales.setText(sales2_str);
                Object sales3_obj = map.get("Mie Ayam");
                String sales3_str = sales3_obj.toString();
                mieAyamSales.setText(sales3_str);
                Object sales4_obj = map.get("NasBung A");
                String sales4_str = sales4_obj.toString();
                nasbungASales.setText(sales4_str);
                Object sales5_obj = map.get("NasBung B");
                String sales5_str = sales5_obj.toString();
                nasbungBSales.setText(sales5_str); //5
                Object sales6_obj = map.get("Nasi Ayam");
                String sales6_str = sales6_obj.toString();
                nasiAyamSales.setText(sales6_str);
                Object sales7_obj = map.get("Nasi Pindang");
                String sales7_str = sales7_obj.toString();
                nasiPindangSales.setText(sales7_str);
                Object sales8_obj = map.get("Nasi Telur");
                String sales8_str = sales8_obj.toString();
                nasiTelurSales.setText(sales8_str);
                Object sales9_obj = map.get("Pisang G");
                String sales9_str = sales9_obj.toString();
                pisangGorengSales.setText(sales9_str);
                Object sales10_obj = map.get("Popmie");
                String sales10_str = sales10_obj.toString();
                popmieSales.setText(sales10_str); //10
                Object sales11_obj = map.get("Sereal");
                String sales11_str = sales11_obj.toString();
                serealSales.setText(sales11_str);
                Object sales12_obj = map.get("Tahu G");
                String sales12_str = sales12_obj.toString();
                tahuGorengSales.setText(sales12_str);
                Object sales13_obj = map.get("Siomay");
                String sales13_str = sales13_obj.toString();
                siomaySales.setText(sales13_str);
                Object sales14_obj = map.get("Sosis Naget");
                String sales14_str = sales14_obj.toString();
                sosisNagetSales.setText(sales14_str);
                Object sales15_obj = map.get("Aqua 600ml");
                String sales15_str = sales15_obj.toString();
                aquaSales.setText(sales15_str); //15
                Object sales16_obj = map.get("Coca-Cola");
                String sales16_str = sales16_obj.toString();
                cocaColaSales.setText(sales16_str);
                Object sales17_obj = map.get("Es Kopi Durian");
                String sales17_str = sales17_obj.toString();
                esKopiDurianSales.setText(sales17_str);
                Object sales18_obj = map.get("Es Teh");
                String sales18_str = sales18_obj.toString();
                tehGelasSales.setText(sales18_str);
                Object sales19_obj = map.get("Fanta");
                String sales19_str = sales19_obj.toString();
                fantaSales.setText(sales19_str);
                Object sales20_obj = map.get("Floridina");
                String sales20_str = sales20_obj.toString();
                floridinaSales.setText(sales20_str); //20
                Object sales21_obj = map.get("Frestea");
                String sales21_str = sales21_obj.toString();
                fresteaSales.setText(sales21_str);
                Object sales22_obj = map.get("Isoplus");
                String sales22_str = sales22_obj.toString();
                isoplusSales.setText(sales22_str);
                Object sales23_obj = map.get("Kopi Hitam");
                String sales23_str = sales23_obj.toString();
                kopiHitamSales.setText(sales23_str);
                Object sales24_obj = map.get("Milo");
                String sales24_str = sales24_obj.toString();
                miloSales.setText(sales24_str);
                Object sales25_obj = map.get("Sprite");
                String sales25_str = sales25_obj.toString();
                spriteSales.setText(sales25_str);
                Object sales26_obj = map.get("Teh Pucuk Harum");
                String sales26_str = sales26_obj.toString();
                tehPucukHarumSales.setText(sales26_str);
            }
        });
    }


    //Reset the Sales and Inventory
    public void insertZeros(){
        int i = 0;
        while (i < makananList.size()) {
            makananSales.add(0);
            makananInventory.add(0);
            i++;
        }
        int j = 0;
        while (j < minumanList.size()) {
            minumanInventory.add(0);
            minumanSales.add(0);
            j++;
        }

    }
    public void resetData() {
        //Populate Inventory and Sales List with 0's
        makananSales.clear();
        minumanSales.clear();
        int i = 0;
        while (i < makananList.size()) {
            makananSales.add(0);
            i++;
        }
        int j = 0;
        while (j < minumanList.size()) {
            minumanSales.add(0);
            j++;
        }
    }


    public String getDate() {
        Long datetime = System.currentTimeMillis();
        Timestamp timestamp = new Timestamp(datetime);
        String date_full = (String) String.valueOf(timestamp);
        String date = date_full.substring(0, 10);
        return date;
    }

    public String getMonth() {
        Long datetime = System.currentTimeMillis();
        Timestamp timestamp = new Timestamp(datetime);
        String date_full = (String) String.valueOf(timestamp);
        String month = date_full.substring(0, 7);
        return month;
    }

    public String getYear() {
        Long datetime = System.currentTimeMillis();
        Timestamp timestamp = new Timestamp(datetime);
        String date_full = (String) String.valueOf(timestamp);
        String year = date_full.substring(0, 4);
        return year;
    }

    public void makananStockClick(View view){
        String item = makananList.get(Integer.parseInt(view.getTag().toString()));
        Bundle bundle = new Bundle();
        bundle.putString("item", item);
        stockDialog stockDialog = new stockDialog();
        stockDialog.setArguments(bundle);
        stockDialog.show(getChildFragmentManager(), "test");



    }

    public void minumanStockClick(View view) {
        String item = minumanList.get(Integer.parseInt(view.getTag().toString()));
        Bundle bundle = new Bundle();
        bundle.putString("item", item);
        stockDialog stockDialog = new stockDialog();
        stockDialog.setArguments(bundle);
        stockDialog.show(getChildFragmentManager(), "test");

    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void UpdateStock(String document, int tambahanStock) {
        fs.collection("Stock").document("Stocks").update(document, FieldValue.increment(tambahanStock)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getContext(), document + "stock updated +" + tambahanStock, Toast.LENGTH_SHORT).show();
                updateStock();
            }
        });
    }
}