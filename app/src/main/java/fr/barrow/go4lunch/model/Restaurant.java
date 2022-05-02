package fr.barrow.go4lunch.model;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;


public class Restaurant {
    private String id;
    private String name;
    private String foodType;
    private String address;
    private int rating;
    @Nullable
    private String urlPicture;
    @Nullable
    private String phoneNumber;
    @Nullable
    private String website;
    private LatLng position;
    @Nullable
    private String closingTimeHours;
    @Nullable
    private String closingTimeMinutes;

    public Restaurant(String id, String name, String address, @Nullable String urlPicture, @Nullable String phoneNumber, @Nullable String website, LatLng position, int rating, @Nullable String closingTimeHours, @Nullable String closingTimeMinutes) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.urlPicture = urlPicture;
        this.phoneNumber = phoneNumber;
        this.website = website;
        this.position = position;
        this.rating = rating;
        this.closingTimeHours = closingTimeHours;
        this.closingTimeMinutes = closingTimeMinutes;
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

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    @Nullable
    public String getUrlPicture() {
        return urlPicture;
    }

    public void setUrlPicture(@Nullable String urlPicture) {
        this.urlPicture = urlPicture;
    }

    @Nullable
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(@Nullable String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Nullable
    public String getWebsite() {
        return website;
    }

    public void setWebsite(@Nullable String website) {
        this.website = website;
    }

    public String getClosingTimeHours() {
        return closingTimeHours;
    }

    public void setClosingTimeHours(String closingTimeHours) {
        this.closingTimeHours = closingTimeHours;
    }

    public String getClosingTimeMinutes() {
        return closingTimeMinutes;
    }

    public void setClosingTimeMinutes(String closingTimeMinutes) {
        this.closingTimeMinutes = closingTimeMinutes;
    }
}
