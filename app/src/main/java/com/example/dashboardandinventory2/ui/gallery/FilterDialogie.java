package com.example.dashboardandinventory2.ui.gallery;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dashboardandinventory2.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class FilterDialogie extends AppCompatDialogFragment {

    private RecyclerView recyclerViewMakanan;
    private RecyclerView recyclerViewMinuman;
    private RecycleAdapter recycleAdapterMakanan;
    private ArrayAdapter recycleAdapterMinuman;
    private ArrayList<String> makananList;
    private ArrayList<String> minumanList;
    FirebaseFirestore fs;



    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        fs = FirebaseFirestore.getInstance();
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.filter_dialogue, null);

        recyclerViewMakanan = view.findViewById(R.id.pilihanFilterRecycleViewMakanan);
        recyclerViewMinuman = view.findViewById(R.id.pilihanFilterRecycleViewMinuman);

        makananList = getArguments().getStringArrayList("makananList");
        minumanList = getArguments().getStringArrayList("minumanList");

//        recycleAdapterMakanan = new ArrayAdapter<>(getContext(), R.layout.dropdown_granularity, makananList);
//        recycleAdapterMinuman = new ArrayAdapter<>(getContext(), R.layout.dropdown_granularity, minumanList);

        recyclerViewMakanan.setAdapter(recycleAdapterMakanan);






        return builder.create();
    }
}
