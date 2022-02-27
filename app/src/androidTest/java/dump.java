import android.util.Log;
import android.view.View;

import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class dump {
//
//    //End Date Listner
//        binding.endInput.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            datePicker_end.show(getChildFragmentManager(), "date_end");
//            datePicker_end.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
//                @Override
//                public void onPositiveButtonClick(Object selection) {
//                    Log.i("Checkpoint", "Before the if statement" );
//                    binding.endInput.setText(datePicker_end.getHeaderText());
//                    if (!binding.startInput.getText().toString().matches("")) {
//                        Log.i("Checkpoint", "In the if statement" );
//                        String str_startDate = binding.startInput.getText().toString();
//                        String str_endDate = binding.endInput.getText().toString();
//                        DateFormat formatter = new SimpleDateFormat();
//                        try {
//                            java.util.Date startDate =   formatter.parse(str_startDate);
//                            java.util.Date endDate = formatter.parse(str_endDate);
//                            Log.i("Start date", "" + startDate.getTime());
//                            Log.i("Start date", "" + endDate.getTime());
//                        } catch (ParseException e) {
//                            e.printStackTrace();
//                        }
//                    } else {
//                        Log.i("Checkpoint", "If statement rejected" );
//
//                    }
//                }
//            });
//
//        }
//    });
//        binding.endInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//        @Override
//        public void onFocusChange(View view, boolean b) {
//            if (b == true) {
//                datePicker_end.show(getChildFragmentManager(), "date_end");
//                datePicker_end.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
//                    @Override
//                    public void onPositiveButtonClick(Object selection) {
//                        Log.i("Checkpoint", "Before the if statement" );
//                        binding.endInput.setText(datePicker_end.getHeaderText());
//                        if (!binding.startInput.getText().toString().matches("")) {
//                            Log.i("Checkpoint", "In the if statement" );
//                            String str_startDate = binding.startInput.getText().toString();
//                            String str_endDate = binding.endInput.getText().toString();
//                            SimpleDateFormat formatter = new SimpleDateFormat("MMM dd,yyyy");
//                            try {
//                                java.util.Date start_date = formatter.parse(str_startDate);
//                                Date end_date = formatter.parse(str_endDate);
////                                    java.util.Date startDate =   formatter.parse(str_startDate);
////                                    java.util.Date endDate = formatter.parse(str_endDate);
//                                Log.i("Start date", "" + start_date.getTime());
//                                Log.i("End date", "" + end_date.getTime());
//
//
//
//                            } catch (ParseException e) {
//                                e.printStackTrace();
//                            }
//                        } else {
//                            Log.i("Checkpoint", "If statement rejected" );
//
//                        }
//                    }
//                });
//            }
//        }
//    });
}
