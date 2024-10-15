package com.example.techwash.Model;

import java.io.Serializable;

public class Auto implements Serializable {
    private String autoId;
    private String AutoName;
    private String Address;
    private String ImageAuto;
    private int Price;
    private float Rating;
    private String userId;

    public Auto(){}

    public Auto(String autoId, String autoName, String address, String imageAuto, int price, float rating, String userId) {
        this.autoId = autoId;
        AutoName = autoName;
        Address = address;
        ImageAuto = imageAuto;
        Price = price;
        Rating = rating;
        this.userId = userId;
    }

    public String getAutoName() {
        return AutoName;
    }

    public void setAutoName(String autoName) {
        AutoName = autoName;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getImageAuto() {
        return ImageAuto;
    }

    public void setImageAuto(String imageAuto) {
        ImageAuto = imageAuto;
    }

    public int getPrice() {
        return Price;
    }

    public void setPrice(int price) {
        Price = price;
    }

    public float getRating() {
        return Rating;
    }

    public void setRating(float rating) {
        Rating = rating;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    // Getter và Setter cho autoId
    public String getAutoId() {
        return autoId;
    }

    public void setAutoId(String autoId) {
        this.autoId = autoId;
    }
}
