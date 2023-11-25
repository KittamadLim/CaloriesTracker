package com.example.bmrcalculator;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.database.sqlite.SQLiteDatabase;
import static com.example.bmrcalculator.Constants.BMR;
import static com.example.bmrcalculator.Constants.TABLE_NAME_INFO;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

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
        DecimalFormat formatter = new DecimalFormat("#,###.##");
        final TextView cal_btn = findViewById(R.id.cal_btn1);
        final TextView Back_btn = findViewById(R.id.back_Btn);
        Back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Information.this);
                builder.setMessage("Do you want to leave this page ?");
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(Information.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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
                calulate_bmr();
                events = new EventsData(Information.this);
                try{
                    addBMR();
                }finally {
                    events.close();
                }
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
                bmr_cal =(66+(13.7*weight)+(5*height)-(6.8*age))*1.2;
            }else{
                bmr_cal = (665+(9.6*weight)+(1.8*height)-(4.7*age))*1.2;
            }
        }else if(checkedRadioFrequency.getText().equals("1-3 days/week")){
            if(checkedRadioGender.getText().equals("male")){
                bmr_cal =(66+(13.7*weight)+(5*height)-(6.8*age))*1.375;
            }else{
                bmr_cal =(665+(9.6*weight)+(1.8*height)-(4.7*age))*1.375;
            }
        }else if(checkedRadioFrequency.getText().equals("3-5 days/week")){
            if(checkedRadioGender.getText().equals("male")){
                bmr_cal =(66+(13.7*weight)+(5*height)-(6.8*age))*1.55;
            }else{
                bmr_cal =(665+(9.6*weight)+(1.8*height)-(4.7*age))*1.55;
            }
        }else if(checkedRadioFrequency.getText().equals("6-7 days/week")){
            if(checkedRadioGender.getText().equals("male")){
                bmr_cal =(66+(13.7*weight)+(5*height)-(6.8*age))*1.725;
            }else{
                bmr_cal =(665+(9.6*weight)+(1.8*height)-(4.7*age))*1.725;
            }
        }else{
            if(checkedRadioGender.getText().equals("male")){
                bmr_cal =(66+(13.7*weight)+(5*height)-(6.8*age))*1.9;
            }else{
                bmr_cal =(665+(9.6*weight)+(1.8*height)-(4.7*age))*1.9;
            }
        }

        bmr.setFilters(new InputFilter[]{
                new DecimalDigitsInputFilter(8, 2)
        });
        bmr.setText(Double.toString(bmr_cal));
    }

    private void addBMR(){
        SQLiteDatabase db = events.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(BMR,bmr_cal);
        long rowId = db.insert(TABLE_NAME_INFO, null, values);

        if (rowId != -1) {
            System.out.println("Inserted into the database with row ID: " + rowId);
        } else {
            System.out.println("Error inserting into the database");
        }
    }

}

class DecimalDigitsInputFilter implements InputFilter {
    private Pattern mPattern;
    DecimalDigitsInputFilter(int digits, int digitsAfterZero) {
        mPattern = Pattern.compile("[0-9]{0," + (digits - 1) + "}+((\\.[0-9]{0," + (digitsAfterZero - 1) +        "})?)||(\\.)?");
    }
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend)
    {
        Matcher matcher = mPattern.matcher(dest);
        if (!matcher.matches())
            return "";
        return null;
    }
}