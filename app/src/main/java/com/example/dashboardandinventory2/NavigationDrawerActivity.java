package com.example.dashboardandinventory2;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.dashboardandinventory2.databinding.ActivityNavigationDrawerBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;

public class NavigationDrawerActivity extends AppCompatActivity {

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
    private TextView baksoSales;
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
    TextView tehHangatSales;
    TextView tehHangatStock;


    private AppBarConfiguration mAppBarConfiguration;
    private ActivityNavigationDrawerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getSupportActionBar().hide();


        binding = ActivityNavigationDrawerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarNavigationDrawer.toolbar);
        binding.appBarNavigationDrawer.fab.setVisibility(View.GONE);
        binding.appBarNavigationDrawer.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_navigation_drawer);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        fs = FirebaseFirestore.getInstance();

        refreshLayout = findViewById(R.id.refresh);

        //Define
        makananList = new ArrayList<>();
        minumanList = new ArrayList<>();
        makananInventory = new ArrayList<>();
        makananSales = new ArrayList<>();
        minumanInventory = new ArrayList<>();
        minumanSales = new ArrayList<>();
        pendaptanKapanTextView = (TextView) findViewById(R.id.pendapatanKapan);
        nominalPendapatanTextView = (TextView) findViewById(R.id.nominalPendapatan);





        menu = "day";







    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_navigation_drawer);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }



    public String getDate() {
        Long datetime = System.currentTimeMillis();
        Timestamp timestamp = new Timestamp(datetime);
        String date_full = (String) String.valueOf(timestamp);
        String date = date_full.substring(0, 10);
        return date;
    }





}