package fr.barrow.go4lunch.model;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User {
    private String uid;
    private String username;
    private String pickedRestaurant;
    private String pickedRestaurantName;
    private ArrayList<String> likedRestaurants;
    @Nullable
    private String urlPicture;

    public User(String uid, String username, @Nullable String urlPicture) {
        this.uid = uid;
        this.username = username;
        this.urlPicture = urlPicture;
    }

    public User() {

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

    public void setPickedRestaurant(String pickedRestaurant, String pickedRestaurantName) {
        this.pickedRestaurant = pickedRestaurant;
        this.pickedRestaurantName = pickedRestaurantName;
    }

    public void removePickedRestaurant() {
        this.pickedRestaurant = null;
        this.pickedRestaurantName = null;
    }

    public ArrayList<String> getLikedRestaurants() {
        if (likedRestaurants == null) {
            ArrayList<String> emptyArray = new ArrayList<>();
            return emptyArray;
        } else {
            return likedRestaurants;
        }
    }

    public void setLikedRestaurants(ArrayList<String> likedRestaurants) {
        this.likedRestaurants = likedRestaurants;
    }

    public void addLikedRestaurant(String restaurantId) {
        if (likedRestaurants == null) {
            likedRestaurants = new ArrayList<>();
            likedRestaurants.add(restaurantId);
        } else if (!likedRestaurants.contains(restaurantId)) {
            likedRestaurants.add(restaurantId);
        }
    }

    public void removeLikedRestaurant(String restaurantId) {
        this.likedRestaurants.remove(restaurantId);
    }

    @Nullable
    public String getUrlPicture() {
        return urlPicture;
    }

    public void setUrlPicture(@Nullable String urlPicture) {
        this.urlPicture = urlPicture;
    }

    public String getPickedRestaurantName() {
        return pickedRestaurantName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(uid, user.uid) && Objects.equals(username, user.username) && Objects.equals(pickedRestaurant, user.pickedRestaurant) && Objects.equals(pickedRestaurantName, user.pickedRestaurantName) && Objects.equals(likedRestaurants, user.likedRestaurants) && Objects.equals(urlPicture, user.urlPicture);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, username, pickedRestaurant, pickedRestaurantName, likedRestaurants, urlPicture);
    }
}
