package com.example.bmrcalculator;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.bmrcalculator.Constants.BMR;
import static com.example.bmrcalculator.Constants.BMR2;
import static com.example.bmrcalculator.Constants.CARB;
import static com.example.bmrcalculator.Constants.DATE2;
import static com.example.bmrcalculator.Constants.FAT;
import static com.example.bmrcalculator.Constants.PROTEIN;
import static com.example.bmrcalculator.Constants.TABLE_BMR;
import static com.example.bmrcalculator.Constants.TABLE_NAME_DAILY;
import static com.example.bmrcalculator.Constants.FOOD;
import static com.example.bmrcalculator.Constants.PICTURE;
import static com.example.bmrcalculator.Constants.CALORIES;
import static com.example.bmrcalculator.Constants.DATE;

public class EventsData extends SQLiteOpenHelper {

    public EventsData(Context ctx) {
        super(ctx, "events.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Table for Daily Records
        String createTableDaily = "CREATE TABLE " + TABLE_NAME_DAILY + " ("
                + DATE + " TEXT NOT NULL, "
                + PICTURE + " TEXT NOT NULL, "
                + FOOD + " TEXT NOT NULL, "
                + PROTEIN + " INTEGER, "
                + FAT + " INTEGER, "
                + CARB + " INTEGER, "
                + BMR + " DOUBLE, "
                + CALORIES + " INTEGER);";
        db.execSQL(createTableDaily);

        // Create Table for BMR Records
        db.execSQL("CREATE TABLE " + TABLE_BMR + " ("
                + DATE2 + " TEXT NOT NULL, "
                + BMR2 + " DOUBLE);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_DAILY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BMR);
        onCreate(db);
    }
}
