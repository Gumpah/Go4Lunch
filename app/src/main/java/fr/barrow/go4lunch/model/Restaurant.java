package fr.barrow.go4lunch.model;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;
import java.util.Date;


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
    private String closingTime;
    @Nullable
    private Date closingTimeDate;
    @Nullable
    private Date openingTimeDate;

    public Restaurant(String id, String name, String address, @Nullable String urlPicture, @Nullable String phoneNumber, @Nullable String website, LatLng position, int rating, @Nullable String closingTime, @Nullable Date openingTimeDate, @Nullable Date closingTimeDate) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.urlPicture = urlPicture;
        this.phoneNumber = phoneNumber;
        this.website = website;
        this.position = position;
        this.rating = rating;
        this.closingTime = closingTime;
        this.openingTimeDate = openingTimeDate;
        this.closingTimeDate = closingTimeDate;
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

    @Nullable
    public String getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(@Nullable String closingTime) {
        this.closingTime = closingTime;
    }

    @Nullable
    public Date getClosingTimeDate() {
        return closingTimeDate;
    }

    public void setClosingTimeDate(@Nullable Date closingTimeDate) {
        this.closingTimeDate = closingTimeDate;
    }

    @Nullable
    public Date getOpeningTimeDate() {
        return openingTimeDate;
    }

    public void setOpeningTimeDate(@Nullable Date openingTimeDate) {
        this.openingTimeDate = openingTimeDate;
    }
}
