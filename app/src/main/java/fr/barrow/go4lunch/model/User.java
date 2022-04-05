package fr.barrow.go4lunch.model;

import androidx.annotation.Nullable;

import java.util.List;

public class User {
    private String uid;
    private String username;
    private String pickedRestaurant;
    private List<String> likedRestaurants;
    @Nullable
    private String urlPicture;

    public User(String uid, String username, @Nullable String urlPicture) {
        this.uid = uid;
        this.username = username;
        this.urlPicture = urlPicture;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPickedRestaurant() {
        return pickedRestaurant;
    }

    public void setPickedRestaurant(String pickedRestaurant) {
        this.pickedRestaurant = pickedRestaurant;
    }

    public List<String> getLikedRestaurants() {
        return likedRestaurants;
    }

    public void setLikedRestaurants(List<String> likedRestaurants) {
        this.likedRestaurants = likedRestaurants;
    }

    @Nullable
    public String getUrlPicture() {
        return urlPicture;
    }

    public void setUrlPicture(@Nullable String urlPicture) {
        this.urlPicture = urlPicture;
    }
}
