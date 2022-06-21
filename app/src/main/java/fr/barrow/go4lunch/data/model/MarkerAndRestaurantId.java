package fr.barrow.go4lunch.data.model;

import com.google.android.gms.maps.model.MarkerOptions;

public class MarkerAndRestaurantId {

    private MarkerOptions marker;
    private String restaurantId;

    public MarkerAndRestaurantId(MarkerOptions marker, String restaurantId) {
        this.marker = marker;
        this.restaurantId = restaurantId;
    }

    public MarkerOptions getMarker() {
        return marker;
    }

    public String getRestaurantId() {
        return restaurantId;
    }
}
