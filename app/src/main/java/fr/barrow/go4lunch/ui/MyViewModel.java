package fr.barrow.go4lunch.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import fr.barrow.go4lunch.data.Repository;
import fr.barrow.go4lunch.data.RestaurantRepository;
import fr.barrow.go4lunch.model.Restaurant;
import fr.barrow.go4lunch.model.placedetails.PlaceDetailsResult;

public class MyViewModel extends ViewModel {
    Repository mRepository;
    RestaurantRepository mRestaurantRepository;
    MutableLiveData<ArrayList<Restaurant>> mRestaurantList;

    public MyViewModel(Repository repository, RestaurantRepository restaurantRepository) {
        mRepository = repository;
        mRestaurantRepository = restaurantRepository;
        mRestaurantList = new MutableLiveData<>();
    }


    public LiveData<Boolean> getConnectionStatus() {
        return mRepository.getConnectionStatus();
    }

    public LiveData<Boolean> getLocationPermissionStatus() {
        return mRepository.getLocationPermissionStatus();
    }

    public void setLocationPermissionStatus(boolean locationPermissionStatus) {
        mRepository.setLocationPermissionStatus(locationPermissionStatus);
    }

    public Restaurant placeDetailsToRestaurantObject(PlaceDetailsResult placeDetails, String photoUrl) {
        return mRestaurantRepository.placeDetailsToRestaurantObject(placeDetails, photoUrl);
    }

    public void addRestaurant(Restaurant restaurant) {
        mRestaurantRepository.addRestaurant(restaurant);
        mRestaurantList.setValue(mRestaurantRepository.getRestaurants());
    }

    public void setRestaurants(ArrayList<Restaurant> restaurants) {
        mRestaurantRepository.setRestaurants(restaurants);
        mRestaurantList.setValue(mRestaurantRepository.getRestaurants());
    }

    public MutableLiveData<ArrayList<Restaurant>> getRestaurants() {
        return mRestaurantList;
    }

    public void clearRestaurants() {
        mRestaurantRepository.clearRestaurants();
    }

    public Restaurant getRestaurantFromId(String id) {
        return mRestaurantRepository.getRestaurantFromId(id);
    }

    public String getLocation() {
        return mRepository.getLocation();
    }

    public void setLocation(String location) {
        mRepository.setLocation(location);
    }
}
