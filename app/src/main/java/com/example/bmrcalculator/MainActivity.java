package com.example.bmrcalculator;

import static android.provider.BaseColumns._ID;

import static com.example.bmrcalculator.Constants.BMR;
import static com.example.bmrcalculator.Constants.CALORIES;
import static com.example.bmrcalculator.Constants.CARB;
import static com.example.bmrcalculator.Constants.DATE;
import static com.example.bmrcalculator.Constants.FAT;
import static com.example.bmrcalculator.Constants.FOOD;
import static com.example.bmrcalculator.Constants.PICTURE;
import static com.example.bmrcalculator.Constants.PROTEIN;
import static com.example.bmrcalculator.Constants.TABLE_NAME_DAILY;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Picture;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EventsData events;
    private List<Data> datas = new ArrayList<>();
    private MyAdapter mAdapter;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ImageView info_btn = findViewById(R.id.back_Btn);
        final ImageButton add_food = findViewById(R.id.add_btn);

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
        String[] FROM = {DATE, BMR};
        String ORDER_BY = DATE + " ASC";
        SQLiteDatabase db = events.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME_DAILY, FROM, null, null, null, null, ORDER_BY, "1");
        return cursor;
    }
    private void setBMR(Cursor cursor){
        String bmr = null;
            while(cursor.moveToNext()) {
                bmr = String.valueOf((int) cursor.getFloat(1));
                System.out.println(bmr);
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