package com.example.dashboardandinventory2.ui.home;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.dashboardandinventory2.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;


public class stockDialog extends AppCompatDialogFragment   {

    private EditText editText;
    private TextView currentStock_textview;
    private TextView title;
    private UpdateStock updateStock;
    private Button okButton;
    private Button cancelButton;
    private FirebaseFirestore fs;
    private String currentStock_str;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        fs = FirebaseFirestore.getInstance();





        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.stock_dialog, null);

        String item = getArguments().getString("item");
        fs.collection("Stock").document("Stocks").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String, Object> map = (Map<String, Object>) documentSnapshot.getData();
                Object currentStock_obj = map.get(item);
                String currentStock_str = currentStock_obj.toString();
                currentStock_textview.setText("Current Stock: " + currentStock_str);



            }
        });

        builder.setView(view);
        title = view.findViewById(R.id.judul);
        editText = view.findViewById(R.id.tambahanStockEditText);
        editText.setTransformationMethod(new NumericKeyBoardTransformationMethod());
        currentStock_textview = view.findViewById(R.id.currentStockTextView);
        okButton = view.findViewById(R.id.okButton);
        cancelButton = view.findViewById(R.id.cancelButton);

        title.setText(item + " Stock");


        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editText.getText().toString().matches("")) {
                    try {
                    int tambahanstok = Integer.parseInt(editText.getText().toString());
                    updateStock.UpdateStock(item, tambahanstok);
                    stockDialog.this.dismiss();
                    }  catch (ClassCastException e) {
                        Toast.makeText(getContext(), "You must enter integer value", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Enter an integer value", Toast.LENGTH_LONG).show();
                }

            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stockDialog.this.dismiss();
            }
        });

        return builder.create();

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            updateStock = (UpdateStock) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement ExampleDialogListener");
        }
    }

    public interface UpdateStock {
        void UpdateStock(String document, int tambahanStock);
    }

    private class NumericKeyBoardTransformationMethod extends PasswordTransformationMethod {
        @Override
        public CharSequence getTransformation(CharSequence source, View view) {
            return source;
        }
    }
}


