package com.example.bmrcalculator;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import static android.provider.BaseColumns._ID;
import static com.example.bmrcalculator.Constants.BMR;
import static com.example.bmrcalculator.Constants.CARB;
import static com.example.bmrcalculator.Constants.FAT;
import static com.example.bmrcalculator.Constants.PROTEIN;
import static com.example.bmrcalculator.Constants.TABLE_NAME_INFO;
import static com.example.bmrcalculator.Constants.TABLE_NAME_DAILY;
import static com.example.bmrcalculator.Constants.FOOD;
import static com.example.bmrcalculator.Constants.PICTURE;
import static com.example.bmrcalculator.Constants.CALORIES;
import static com.example.bmrcalculator.Constants.DATE;

public class EventsData extends SQLiteOpenHelper {
    public EventsData(Context ctx){
        super(ctx, "events.db", null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME_DAILY + " ("
                + DATE + " TEXT NOT NULL, "
                + PICTURE + " TEXT NOT NULL, "
                + FOOD + " TEXT NOT NULL, "
                + PROTEIN + " INTEGER, "
                + FAT + " INTEGER, "
                + CARB + " INTEGER, "
                + BMR + " DOUBLE, "
                + CALORIES + " INTEGER);"  );
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS events");
        onCreate(db);
    }
}