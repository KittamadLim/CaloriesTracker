package com.example.bmrcalculator;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.io.ByteArrayOutputStream;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{
    private List<Data> dataSet;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
        ViewHolder dataObjHolder = new ViewHolder(view);
        return dataObjHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        holder.title.setText(dataSet.get(position).getTitle());
        holder.fat.setText(dataSet.get(position).getFat());
        holder.carb.setText(dataSet.get(position).getCarb());
        holder.cal.setText(dataSet.get(position).getCal());
        holder.protein.setText(dataSet.get(position).getProtein());
        //decode base64 string to image
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] imageBytes = baos.toByteArray();
        imageBytes = Base64.decode(dataSet.get(position).getIcon(), Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        holder.icon.setImageBitmap(decodedImage);
    }

    @Override
    public int getItemCount(){
        return dataSet.size();
    }

    public MyAdapter(List<Data> myDataSet){
        dataSet = myDataSet;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, protein,fat,carb,cal;
        ImageView icon;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            cal = (TextView) itemView.findViewById(R.id.cal);
            carb = (TextView) itemView.findViewById(R.id.carb);
            fat = (TextView) itemView.findViewById(R.id.fat);
            protein = (TextView) itemView.findViewById(R.id.protein);
            icon = (ImageView) itemView.findViewById(R.id.icon);
        }
    }
}
