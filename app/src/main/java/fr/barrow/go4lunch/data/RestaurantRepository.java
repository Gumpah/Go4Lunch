package fr.barrow.go4lunch.data;

import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;

import fr.barrow.go4lunch.model.Restaurant;
import fr.barrow.go4lunch.model.placedetails.PlaceDetails;
import fr.barrow.go4lunch.model.placedetails.PlaceDetailsList;
import fr.barrow.go4lunch.model.placesnearby.Place;

public class RestaurantRepository {

    private static final String COLLECTION_NAME = "restaurants";
    private static final String USERNAME_FIELD = "username";

    private MutableLiveData<ArrayList<Restaurant>> restaurantList;

    private static volatile RestaurantRepository instance;

    private RestaurantRepository() { }

    public static RestaurantRepository getInstance() {
        RestaurantRepository result = instance;
        if (result != null) {
            return result;
        }
        synchronized(RestaurantRepository.class) {
            if (instance == null) {
                instance = new RestaurantRepository();
            }
            return instance;
        }
    }

    // Get the Collection Reference
    private CollectionReference getRestaurantsCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // Create User in Firestore
    public void createRestaurant() {

    }

    public void placeDetailsToRestaurantObject(PlaceDetailsList placeDetails) {
        PlaceDetails myPlaceDetails = placeDetails.getResult();
        String id = myPlaceDetails.getPlaceId();
        String name = myPlaceDetails.getName();
        String address = myPlaceDetails.getFormattedAddress();
        String urlPicture = null;
        String phoneNumber = myPlaceDetails.getInternationalPhoneNumber();
        String website = myPlaceDetails.getWebsite();
        LatLng position = new LatLng(myPlaceDetails.getGeometry().getLocation().getLat(), myPlaceDetails.getGeometry().getLocation().getLng());
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        //int closingTimeHours = myPlaceDetails.getOpeningHours().getPeriods().get(day).getClose();
        LocalTime closingTime;
    }

    public void setRestaurants(ArrayList<Restaurant> restaurants) {
        restaurantList.setValue(restaurants);
    }

    public MutableLiveData<ArrayList<Restaurant>> getRestaurants() {
        return restaurantList;
    }

    /*
    // Get User Data from Firestore
    public Task<DocumentSnapshot> getRestaurantData(){
    }

    // Delete the User from Firestore
    public void deleteRestaurantFromFirestore() {
        String uid = this.getCurrentUser().getUid();
        if(uid != null){
            this.getRestaurantsCollection().document(uid).delete();
        }
    }
    */
}

