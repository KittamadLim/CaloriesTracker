package com.example.bmrcalculator;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.jakewharton.threetenabp.AndroidThreeTen;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static com.example.bmrcalculator.Constants.BMR;
import static com.example.bmrcalculator.Constants.CALORIES;
import static com.example.bmrcalculator.Constants.CARB;
import static com.example.bmrcalculator.Constants.DATE;
import static com.example.bmrcalculator.Constants.FAT;
import static com.example.bmrcalculator.Constants.FOOD;
import static com.example.bmrcalculator.Constants.PICTURE;
import static com.example.bmrcalculator.Constants.PROTEIN;
import static com.example.bmrcalculator.Constants.TABLE_NAME_DAILY;

public class Addfood extends AppCompatActivity {
    private Uri image_uri;
    private EventsData events;
    ActivityResultLauncher<Intent> activityResultLauncher3 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        try {
                                Uri uri = data.getData();
                                image_uri = uri;
                            try {
                                ImageView imageView = (ImageView) findViewById(R.id.imageViewFood);
                                imageView.getLayoutParams().height = 400;
                                imageView.getLayoutParams().width = 400;
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                                imageView.setImageBitmap(bitmap);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            TextView txt = (TextView) findViewById(R.id.textView);
                            txt.setText("Gallery : "+uri);
                        } catch (Exception e) {
                            Log.e("Log", "Error from Gallery Activity");
                        }
                    }
                }
            });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfood);
        AndroidThreeTen.init(this);
        final ImageButton btn = findViewById(R.id.imageButton);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                Intent.createChooser(intent, "Select photo from");
                activityResultLauncher3.launch(intent);
            }
        });

        final ImageView back_Btn = findViewById(R.id.info_btn);
        back_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(Addfood.this);
                builder.setMessage("Do you want to leave this page ?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(Addfood.this, MainActivity.class);
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

        final TextView tip_btn = findViewById(R.id.help_btn);
        tip_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ในที่นี้ YourActivity.this คือ context ของ Activity นี้
                AlertDialog.Builder builder = new AlertDialog.Builder(Addfood.this);
                builder.setTitle("Nutrient");
                builder.setMessage("Protein : 4 Kilocalories /1 g" +
                        "\nCarbohydrate : 4 Kilocalories / 1 g" +  "\nFat : 9 Kilocalories / 1 g");

                builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // ไม่ทำตามทิบ
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        final TextView cal_btn = findViewById(R.id.calculatefood_btn) ;
        cal_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int t_cal = calcualteCalories() ;
                System.out.println(t_cal);
                final TextView setcal = findViewById(R.id.calview) ;
                setcal.setText(String.valueOf(t_cal));
            }
        });
        final Button save_btn = findViewById(R.id.save_button);
        save_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isDataValid()){
                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(Addfood.this);
                    builder.setMessage("Do you want to add this food ?");

                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // ทำงานเมื่อกด Yes
                            events = new EventsData(Addfood.this);
                            try {
                                addDailies();
                                // ตัวอย่างการปิด AlertDialog หลังจากทำงานเสร็จสิ้น
                                dialog.dismiss();
                            } finally {
                                events.close();
                            }
                            Intent intent = new Intent(Addfood.this, MainActivity.class);
                            startActivity(intent);

                        }
                    });

                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // ทำงานเมื่อกด No
                            // ตัวอย่างการปิด AlertDialog หลังจากทำงานเสร็จสิ้น
                            dialog.dismiss();
                        }
                    });

                    // สร้างและแสดง AlertDialog
                    androidx.appcompat.app.AlertDialog dialog = builder.create();
                    dialog.show();
                }else{
                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(Addfood.this);
                    builder.setMessage("Please fill in all fields. If there is no data for any field, enter 0.");
                    builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
                    androidx.appcompat.app.AlertDialog dialog = builder.create();
                    dialog.show();
                }
                }


            private boolean isDataValid() {
                 EditText food_input = findViewById(R.id.InputFood);
                 EditText protein_input = findViewById(R.id.InputProtein);
                 EditText carb_input = findViewById(R.id.InputFlour);
                 EditText fat_input = findViewById(R.id.Inputfat);
                return !food_input.getText().toString().isEmpty() &&
                        !protein_input.getText().toString().isEmpty() &&
                        !carb_input.getText().toString().isEmpty() &&
                        !fat_input.getText().toString().isEmpty();
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

    private void showExitAlertDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Addfood.this);
        builder.setTitle("Exit?");
        builder.setMessage("Do you want to leave this page ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Addfood.this, MainActivity.class);
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

    private int calcualteCalories(){
        final EditText protein_input = findViewById(R.id.InputProtein);
        final EditText carb_input = findViewById(R.id.InputFlour);
        final EditText fat_input = findViewById(R.id.Inputfat);

        //Calculate Calories
        int protein = Integer.parseInt(protein_input.getText().toString());
        int carb = Integer.parseInt(carb_input.getText().toString());
        int fat = Integer.parseInt(fat_input.getText().toString());
        int cal_input = (protein*4)+(carb*4)+(fat*9);
        return cal_input;
    }

    private void addDailies(){
        final EditText food_input = findViewById(R.id.InputFood);

        final EditText protein_input = findViewById(R.id.InputProtein);
        final EditText carb_input = findViewById(R.id.InputFlour);
        final EditText fat_input = findViewById(R.id.Inputfat);
        Intent intentReceive = getIntent();
        double bmr = intentReceive.getDoubleExtra("bmr",0.0);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        LocalDateTime myDateObj = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formattedDate = myDateObj.format(myFormatObj);
        Bitmap bitmap=null;
        String imageString=null;



        try{
            if(image_uri!=null)
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), image_uri);
            else
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.plus);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] imageBytes = baos.toByteArray();
            imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        SQLiteDatabase db = events.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DATE,formattedDate);
        values.put(PICTURE,imageString);
        values.put(FOOD, food_input.getText().toString());
        values.put(PROTEIN, Integer.parseInt(protein_input.getText().toString()));
        values.put(CARB,Integer.parseInt(carb_input.getText().toString()));
        values.put(FAT,Integer.parseInt(fat_input.getText().toString()));
        values.put(CALORIES,calcualteCalories());
        values.put(BMR,bmr);
        long rowId = db.insert(TABLE_NAME_DAILY, null, values);

        if (rowId != -1) {
            System.out.println("Inserted into the database with row ID: " + rowId);
        } else {
            System.out.println("Error inserting into the database");
        }
    }
}