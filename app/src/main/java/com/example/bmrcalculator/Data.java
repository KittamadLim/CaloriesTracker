package com.example.bmrcalculator;

public class Data {
    private String food;
    private String protein;
    private String carb;
    private String fat;
    private String cal;
    private String icon;

    public Data(String food,String cal, String protein, String carb, String fat, String icon){
        this.icon = icon;
        this.food = food;
        this.fat = fat;
        this.carb = carb;
        this.protein = protein;
        this.cal = cal;
    }
    public String getTitle(){
        return food;
    }
    public String getFat(){
        return fat;
    }
    public String getCal(){
        return cal;
    }
    public String getCarb(){
        return carb;
    }
    public String getProtein(){
        return protein;
    }
    public String getIcon() {
        return icon;
    }
}
