package fr.barrow.go4lunch.model;

import androidx.annotation.Nullable;


public class Restaurant {
    private String id;
    private String name;
    private String foodType;
    private String address;
    private int note;
    private int closingTimeHours;
    private int closingTimeMinutes;
    @Nullable
    private String urlPicture;

    public Restaurant(String id, String name, String foodType, String address, @Nullable String urlPicture) {
        this.id = id;
        this.name = name;
        this.foodType = foodType;
        this.address = address;
        this.urlPicture = urlPicture;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFoodType() {
        return foodType;
    }

    public void setFoodType(String foodType) {
        this.foodType = foodType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getNote() {
        return note;
    }

    public void setNote(int note) {
        this.note = note;
    }

    public int getClosingTimeHours() {
        return closingTimeHours;
    }

    public void setClosingTimeHours(int closingTimeHours) {
        this.closingTimeHours = closingTimeHours;
    }

    public int getClosingTimeMinutes() {
        return closingTimeMinutes;
    }

    public void setClosingTimeMinutes(int closingTimeMinutes) {
        this.closingTimeMinutes = closingTimeMinutes;
    }

    @Nullable
    public String getUrlPicture() {
        return urlPicture;
    }

    public void setUrlPicture(@Nullable String urlPicture) {
        this.urlPicture = urlPicture;
    }
}
