package com.labibkamran.nustfruta.Model;

public class CartItems {
    private String foodname;
    private String foodprice;
    private String fooddescription;
    private String foodImage;
    private int foodquantity;

    public CartItems(){

    }
    // Constructor
    public CartItems(String foodname, String foodprice, String fooddescription, String foodImage, int foodquantity) {
        this.foodname = foodname;
        this.foodprice = foodprice;
        this.fooddescription = fooddescription;
        this.foodImage = foodImage;
        this.foodquantity = foodquantity;
    }

    // Getters
    public String getFoodname() {
        return foodname;
    }

    public String getFoodprice() {
        return foodprice;
    }

    public String getFooddescription() {
        return fooddescription;
    }

    public String getFoodImage() {
        return foodImage;
    }

    public int getFoodquantity() {
        return foodquantity;
    }

    // Setters
    public void setFoodname(String foodname) {
        this.foodname = foodname;
    }

    public void setFoodprice(String foodprice) {
        this.foodprice = foodprice;
    }

    public void setFooddescription(String fooddescription) {
        this.fooddescription = fooddescription;
    }

    public void setFoodImage(String foodImage) {
        this.foodImage = foodImage;
    }

    public void setFoodquantity(int foodquantity) {
        this.foodquantity = foodquantity;
    }
}
