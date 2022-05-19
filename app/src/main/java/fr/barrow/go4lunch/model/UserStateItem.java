package fr.barrow.go4lunch.model;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Objects;

public class UserStateItem {
    private String uid;
    private String username;
    private String pickedRestaurant;
    private String pickedRestaurantName;
    private ArrayList<String> likedRestaurants;
    @Nullable
    private String urlPicture;

    public UserStateItem(User user) {
        if (user != null) {
            this.uid = user.getUid();
            this.username = user.getUsername();
            this.urlPicture = user.getUrlPicture();
            this.pickedRestaurant = user.getPickedRestaurant();
            this.pickedRestaurantName = user.getPickedRestaurantName();
            this.likedRestaurants = user.getLikedRestaurants();
        }
    }

    public UserStateItem() {

    }

    public UserStateItem(String uid, String username, @Nullable String urlPicture) {
        this.uid = uid;
        this.username = username;
        this.urlPicture = urlPicture;
    }

    public String getUid() {
        return uid;
    }

    public String getUsername() {
        return username;
    }

    public String getPickedRestaurant() {
        return pickedRestaurant;
    }

    public ArrayList<String> getLikedRestaurants() {
        if (likedRestaurants == null) {
            ArrayList<String> emptyArray = new ArrayList<>();
            return emptyArray;
        } else {
            return likedRestaurants;
        }
    }

    @Nullable
    public String getUrlPicture() {
        return urlPicture;
    }

    public String getPickedRestaurantName() {
        return pickedRestaurantName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserStateItem that = (UserStateItem) o;
        return Objects.equals(uid, that.uid) && Objects.equals(username, that.username) && Objects.equals(pickedRestaurant, that.pickedRestaurant) && Objects.equals(likedRestaurants, that.likedRestaurants) && Objects.equals(urlPicture, that.urlPicture);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, username, pickedRestaurant, likedRestaurants, urlPicture);
    }
}
