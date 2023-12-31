package com.example.bmrcalculator;

import static com.example.bmrcalculator.Constants.CALORIES;
import static com.example.bmrcalculator.Constants.CARB;
import static com.example.bmrcalculator.Constants.DATE;
import static com.example.bmrcalculator.Constants.FAT;
import static com.example.bmrcalculator.Constants.FOOD;
import static com.example.bmrcalculator.Constants.PICTURE;
import static com.example.bmrcalculator.Constants.PROTEIN;
import static com.example.bmrcalculator.Constants.TABLE_NAME_DAILY;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.jakewharton.threetenabp.AndroidThreeTen;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class History extends AppCompatActivity {
    private EventsData events;
    private List<Data> datas = new ArrayList<>();
    private MyAdapter mAdapter;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        AndroidThreeTen.init(this);
        events = new EventsData(History.this);
        try{
            Cursor cursor = getEvents();
            showEvents(cursor);
        }finally{
            events.close();
        }

        final ImageView back_Btn = findViewById(R.id.info_btn);
        back_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(History.this);
                builder.setMessage("Do you want to leave this page ?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(History.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                androidx.appcompat.app.AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                showExitAlertDialog();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

    }
    private Cursor getEvents() {
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String[] FROM = {DATE, FOOD, PICTURE ,CARB,CALORIES,PROTEIN,FAT};
        String ORDER_BY = DATE + " DESC";
        SQLiteDatabase db = events.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME_DAILY, FROM, null, null, null, null, ORDER_BY);
        return cursor;
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
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_history);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        mAdapter = new MyAdapter(datas);
        recyclerView.setAdapter(mAdapter);
    }

    private void showExitAlertDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(History.this);
        builder.setTitle("Exit?");
        builder.setMessage("Do you want to leave this page ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(History.this, MainActivity.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Continue with the back action
            }
        });

        android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}