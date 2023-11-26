package com.example.bmrcalculator;

import static com.example.bmrcalculator.Constants.BMR2;
import static com.example.bmrcalculator.Constants.CALORIES;
import static com.example.bmrcalculator.Constants.CARB;
import static com.example.bmrcalculator.Constants.DATE;
import static com.example.bmrcalculator.Constants.DATE2;
import static com.example.bmrcalculator.Constants.FAT;
import static com.example.bmrcalculator.Constants.FOOD;
import static com.example.bmrcalculator.Constants.PICTURE;
import static com.example.bmrcalculator.Constants.PROTEIN;
import static com.example.bmrcalculator.Constants.TABLE_BMR;
import static com.example.bmrcalculator.Constants.TABLE_NAME_DAILY;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.threetenabp.AndroidThreeTen;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private EventsData events;
    private List<Data> datas = new ArrayList<>();
    private MyAdapter mAdapter;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AndroidThreeTen.init(this);
        final ImageView info_btn = findViewById(R.id.back_Btn);
        final ImageButton add_food = findViewById(R.id.add_btn);
        final TextView secretbtn = findViewById(R.id.BMRcalculater);
        int daybmr = 0 ;
        secretbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("ผู้จัดทำ");
                builder.setMessage("นรินทร์ภัทร์ ลิมปวิทยาพร 6409682603" +
                        "\nันตภณ ว่องพรรณงาม 6409682629" +  "\nปุณณ อัจฉริยปัญญา 6409682801" +  "\nพศิน แสงอรุณ 6409682835" + "\n");
//                1.นรินทร์ภัทร์ ลิมปวิทยาพร 6409682603
//                2.กันตภณ ว่องพรรณงาม 6409682629
//                3.ปุณณ อัจฉริยปัญญา 6409682801
//                4.พศิน แสงอรุณ 6409682835:

                builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // ไม่ทำตามทิบ
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


        Intent intent1 = new Intent(MainActivity.this, Information.class);
        Intent intent2 = new Intent(MainActivity.this, Addfood.class);

        //BMR Calulator Function
        info_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent1);
            }
        });
        //Add Food Function
        add_food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentReceive = getIntent();
                double bmr = intentReceive.getDoubleExtra("bmr",0.0);
                intent2.putExtra("bmr",bmr);
                startActivity(intent2);
            }
        });

        //Show History
        events = new EventsData(MainActivity.this);
        try{
            Cursor cursor = getEvents();
            Cursor cursorBMR = getBMR();
            Cursor cursorCal = getRecentCal();
            showTodayCal(cursorCal);
            setBMR(cursorBMR);
            showEvents(cursor);
        }finally{
            events.close();
        }
    }
    private Cursor getEvents() {
        String[] FROM = {DATE, FOOD, PICTURE ,CARB,CALORIES,PROTEIN,FAT};
        String ORDER_BY = DATE + " DESC";
        SQLiteDatabase db = events.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME_DAILY, FROM, null, null, null, null, ORDER_BY);
        return cursor;
    }
    private Cursor getBMR(){
        String[] FROM = {DATE2, BMR2};
        String ORDER_BY = DATE2 + " DESC";
        SQLiteDatabase db = events.getReadableDatabase();
        Cursor cursor = db.query(TABLE_BMR, FROM, null, null, null, null, ORDER_BY,"1");
        return cursor;
    }

    private Cursor getRecentCal(){
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        String[] FROM = {DATE, CALORIES};
        String ORDER_BY = DATE + " DESC";
        String selection = "SUBSTR(" + DATE + ", 1, 10) = ?";
        String[] selectionArgs = {currentDate};

        SQLiteDatabase db = events.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME_DAILY, FROM, selection, selectionArgs, null, null, ORDER_BY);

        return cursor;
    }

    private void showTodayCal(Cursor cursor){
        int todayCal = 0;
        if(cursor!=null) {
            while(cursor.moveToNext()) {
                todayCal += (int) cursor.getFloat(1);
            }
        }
        final TextView setCal = findViewById(R.id.calValue);
        Cursor forbmr = getBMR() ;
        int bmrs = BMRfind(forbmr) ;
                if(todayCal > bmrs){
                    int check = (todayCal - bmrs) ;
                    if(check <= 100){
                        String todayCalString = String.valueOf(todayCal);
                        String coloredText = "<font color='#7CFC00'>" + todayCalString + "</font>";
                        Spanned spanned = Html.fromHtml(coloredText, Html.FROM_HTML_MODE_LEGACY);
                        setCal.setText(spanned);
                    }else if(check > 100 && check <= 200){
                        String todayCalString = String.valueOf(todayCal);
                        String coloredText = "<font color='#1E90FF'>" + todayCalString + "</font>";
                        Spanned spanned = Html.fromHtml(coloredText, Html.FROM_HTML_MODE_LEGACY);
                        setCal.setText(spanned);
                    }else{
                        String todayCalString = String.valueOf(todayCal);
                        String coloredText = "<font color='#DC143C'>" + todayCalString + "</font>";
                        Spanned spanned = Html.fromHtml(coloredText, Html.FROM_HTML_MODE_LEGACY);
                        setCal.setText(spanned);
                    }
                }else if(bmrs > todayCal){
                    int check = (bmrs - todayCal) ;
                    if(check <= 100){
                        String todayCalString = String.valueOf(todayCal);
                        String coloredText = "<font color='#7CFC00'>" + todayCalString + "</font>";
                        Spanned spanned = Html.fromHtml(coloredText, Html.FROM_HTML_MODE_LEGACY);
                        setCal.setText(spanned);
                    }else if(check > 100 && check <= 200){
                        String todayCalString = String.valueOf(todayCal);
                        String coloredText = "<font color='#1E90FF'>" + todayCalString + "</font>";
                        Spanned spanned = Html.fromHtml(coloredText, Html.FROM_HTML_MODE_LEGACY);
                        setCal.setText(spanned);
                    }else {
                        String todayCalString = String.valueOf(todayCal);
                        String coloredText = "<font color='#DC143C'>" + todayCalString + "</font>";
                        Spanned spanned = Html.fromHtml(coloredText, Html.FROM_HTML_MODE_LEGACY);
                        setCal.setText(spanned);
                    }

                }else{
                    setCal.setText(todayCal);
                }





    }
    private int BMRfind(Cursor cursor){
        int bmr = 0;
        if(cursor!=null) {
            while(cursor.moveToNext()) {
                bmr = (int) cursor.getFloat(1);
            }
        }
        return bmr ;
    }
    private void setBMR(Cursor cursor){
        String bmr = null;
        if(cursor!=null) {
            while(cursor.moveToNext()) {
                bmr = String.valueOf((int) cursor.getFloat(1));
                System.out.println(bmr);
            }
        }

        final TextView setbmr = findViewById(R.id.Yourbmr_value);
        setbmr.setText(bmr);

    }
    private void showEvents(Cursor cursor) {
        datas.clear();
        while(cursor.moveToNext()) {
            if(cursor!=null) {
                String title = cursor.getString(1);
                String imageString = cursor.getString(2);
                String protein = String.valueOf(cursor.getInt(3));
                String fat = String.valueOf(cursor.getInt(6));
                String carb = String.valueOf(cursor.getInt(5));
                String cal = String.valueOf(cursor.getInt(4));
                datas.add(new Data("" + title, "" + cal, "" + protein, "" + carb, "" + fat, imageString));
            }
        }
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        mAdapter = new MyAdapter(datas);
        recyclerView.setAdapter(mAdapter);
    }
}