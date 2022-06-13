package fr.barrow.go4lunch.model;

public class RestaurantAutocomplete {

    private String placeId;
    private String name;
    private String distance;
    private String address;

    public RestaurantAutocomplete(String id, String name, String distance, String address) {
        this.placeId = id;
        this.name = name;
        this.distance = distance;
        this.address = address;
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

    public String getAddress() {
        return address;
    }
}
