package fr.barrow.go4lunch.model;

public class RestaurantAutocomplete {

    private String placeId;
    private String name;
    private String distance;

    public RestaurantAutocomplete(String id, String name, String distance) {
        this.placeId = id;
        this.name = name;
        this.distance = distance;
    }

    public String getPlaceId() {
        return placeId;
    }

    public String getName() {
        return name;
    }

    public String getDistance() {
        return distance;
    }
}
