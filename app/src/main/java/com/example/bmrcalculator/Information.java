package com.example.bmrcalculator;

import static com.example.bmrcalculator.Constants.BMR2;
import static com.example.bmrcalculator.Constants.DATE2;
import static com.example.bmrcalculator.Constants.TABLE_BMR;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.jakewharton.threetenabp.AndroidThreeTen;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Information extends AppCompatActivity {
    private EventsData events;
    double bmr_cal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infomation);
        AndroidThreeTen.init(this);
        final TextView cal_btn = findViewById(R.id.cal_btn1);
        final ImageView Back_btn = findViewById(R.id.info_btn);
        Intent intent = new Intent(Information.this, MainActivity.class);
        Back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Information.this);
                builder.setMessage("Do you want to leave this page ?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id){
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        cal_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDataValid()) {

                    calulate_bmr();
                    events = new EventsData(Information.this);
                    try{
                        addBMR();
                    }finally {
                        events.close();
                    }

                    Toast.makeText(getApplicationContext(), "Calculate Success", Toast.LENGTH_SHORT).show();
                    new android.os.Handler().postDelayed(
                            new Runnable() {
                                public void run() {
                                    startActivity(intent);
                                }
                            },
                            2000 //
                    );
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Information.this);
                    builder.setMessage("Please fill out the information completely.");
                    builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }

            private boolean isDataValid() {
                EditText height_input = findViewById(R.id.height_input);
                EditText weight_input = findViewById(R.id.weight_input);
                EditText age_input = findViewById(R.id.age_input);
                RadioGroup radioGroupGender = findViewById(R.id.genderGroup);
                RadioGroup radioGroupFrequency = findViewById(R.id.frequencyGroup);

                return !height_input.getText().toString().isEmpty() &&
                        !weight_input.getText().toString().isEmpty() &&
                        !age_input.getText().toString().isEmpty() &&
                        radioGroupGender.getCheckedRadioButtonId() != -1 &&
                        radioGroupFrequency.getCheckedRadioButtonId() != -1;
            }
        });
    }

    private void calulate_bmr(){
        final EditText height_input =findViewById(R.id.height_input);
        final EditText weight_input =findViewById(R.id.weight_input);
        final EditText age_input =findViewById(R.id.age_input);
        final TextView bmr = findViewById(R.id.bmr);

        final RadioGroup radioGroupGender = findViewById(R.id.genderGroup);
        final RadioGroup radioGroupFrequency = findViewById(R.id.frequencyGroup);
        final RadioButton checkedRadioGender = findViewById(radioGroupGender.getCheckedRadioButtonId());
        final RadioButton checkedRadioFrequency = findViewById(radioGroupFrequency.getCheckedRadioButtonId());


        double weight = Double.parseDouble(weight_input.getText().toString());
        double height = Double.parseDouble(height_input.getText().toString());
        double age = Double.parseDouble(age_input.getText().toString());


        if(checkedRadioFrequency.getText().equals("none")){
            if(checkedRadioGender.getText().equals("male")){
                bmr_cal =(88.362+(13.397*weight)+(4.799*height)-(5.677*age))*1.2;
            }else{
                bmr_cal = (447.593+(9.247*weight)+(3.098*height)-(4.330*age))*1.2;
            }
        }else if(checkedRadioFrequency.getText().equals("1-3 days/week")){
            if(checkedRadioGender.getText().equals("male")){
                bmr_cal =(88.362+(13.397*weight)+(4.799*height)-(5.677*age))*1.375;
            }else{
                bmr_cal =(447.593+(9.247*weight)+(3.098*height)-(4.330*age))*1.375;
            }
        }else if(checkedRadioFrequency.getText().equals("3-5 days/week")){
            if(checkedRadioGender.getText().equals("male")){
                bmr_cal =(88.362+(13.397*weight)+(4.799*height)-(5.677*age))*1.55;
            }else{
                bmr_cal =(447.593+(9.247*weight)+(3.098*height)-(4.330*age))*1.55;
            }
        }else if(checkedRadioFrequency.getText().equals("6-7 days/week")){
            if(checkedRadioGender.getText().equals("male")){
                bmr_cal =(88.362+(13.397*weight)+(4.799*height)-(5.677*age))*1.725;
            }else{
                bmr_cal =(447.593+(9.247*weight)+(3.098*height)-(4.330*age))*1.725;
            }
        }else{
            if(checkedRadioGender.getText().equals("male")){
                bmr_cal =(88.362+(13.397*weight)+(4.799*height)-(5.677*age))*1.9;
            }else{
                bmr_cal =(447.593+(9.247*weight)+(3.098*height)-(4.330*age))*1.9;
            }
        }

        int bmrInt = (int)bmr_cal;
        bmr.setText(Integer.toString(bmrInt));
    }

    private void addBMR(){
        SQLiteDatabase db = events.getWritableDatabase();
        LocalDateTime myDateObj = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formattedDate = myDateObj.format(myFormatObj);
        ContentValues values = new ContentValues();
        values.put(BMR2,bmr_cal);
        values.put(DATE2,formattedDate);
        long rowId = db.insert(TABLE_BMR, null, values);

        if (rowId != -1) {
            System.out.println("Inserted into the database with row ID: " + rowId);
        } else {
            System.out.println("Error inserting into the database");
        }
    }

}
