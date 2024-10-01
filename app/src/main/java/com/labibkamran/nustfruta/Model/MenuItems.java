package com.labibkamran.nustfruta.Model;

public class MenuItems {
    private String foodname;
    private String foodprice;
    private String fooddescription;
    private String foodImage;

    // Constructor
    public  MenuItems(){

    }
    public MenuItems(String foodname, String foodprice, String fooddescription, String foodImage) {
        this.foodname = foodname;
        this.foodprice = foodprice;
        this.fooddescription = fooddescription;
        this.foodImage = foodImage;
    }

    // Getters
    public String getFoodName() {
        return foodname;
    }

    public String getFoodPrice() {
        return foodprice;
    }

    public String getFoodDescription() {
        return fooddescription;
    }

    public String getFoodImage() {
        return foodImage;
    }

    // Setters
    public void setFoodName(String foodName) {
        this.foodname = foodName;
    }

    public void setFoodPrice(String foodPrice) {
        this.foodprice = foodPrice;
    }

    public void setFoodDescription(String fooddescription) {
        this.fooddescription = fooddescription;
    }

    public void setFoodImage(String foodImage) {
        this.foodImage = foodImage;
    }
}
