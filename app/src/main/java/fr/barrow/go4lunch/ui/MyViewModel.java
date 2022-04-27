package fr.barrow.go4lunch.ui;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import fr.barrow.go4lunch.data.Repository;
import fr.barrow.go4lunch.data.RestaurantRepository;
import fr.barrow.go4lunch.data.UserRepository;
import fr.barrow.go4lunch.model.Restaurant;
import fr.barrow.go4lunch.model.User;
import fr.barrow.go4lunch.model.placedetails.PlaceDetailsResult;

public class MyViewModel extends ViewModel {
    Repository mRepository;
    RestaurantRepository mRestaurantRepository;
    UserRepository mUserRepository;
    MutableLiveData<ArrayList<Restaurant>> mRestaurantList;
    MutableLiveData<User> mUser;

    public MyViewModel(Repository repository, RestaurantRepository restaurantRepository, UserRepository userRepository) {
        mRepository = repository;
        mRestaurantRepository = restaurantRepository;
        mUserRepository = userRepository;
        mRestaurantList = new MutableLiveData<>();
        mUser = new MutableLiveData<>();
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

    public MutableLiveData<User> getUser() {
        return mUser;
    }

    public CollectionReference getUsersCollection(){
        return mUserRepository.getUsersCollection();
    }

    public void createUser() {
       mUserRepository.createUser();
    }

    public Task<DocumentSnapshot> getUserData() {
        return mUserRepository.getUserData();
    }

    public void updateUserData() {
        mUserRepository.updateUserData();
        mUser.setValue(mUserRepository.getUser());
    }

    public void deleteUserFromFirestore() {
        mUserRepository.deleteUserFromFirestore();
    }

    public Boolean isCurrentUserLogged(){
        return (mUserRepository.getCurrentUser() != null);
    }

    public void setPickedRestaurant(String restaurantId) {
        mUserRepository.setPickedRestaurant(restaurantId);
        mUser.setValue(mUserRepository.getUser());
    }

    public void removePickedRestaurant() {
        mUserRepository.removePickedRestaurant();
        mUser.setValue(mUserRepository.getUser());
    }

    public void addLikedRestaurant(String restaurantId) {
        mUserRepository.addLikedRestaurant(restaurantId);
        mUser.setValue(mUserRepository.getUser());
    }

    public void removeLikedRestaurant(String restaurantId) {
        mUserRepository.removeLikedRestaurant(restaurantId);
        mUser.setValue(mUserRepository.getUser());
    }

    @Nullable
    public FirebaseUser getCurrentUser(){
        return mUserRepository.getCurrentUser();
    }

    public Task<Void> signOut(Context context){
        return mUserRepository.signOut(context);
    }

    public Task<Void> deleteUser(Context context){
        return mUserRepository.deleteUser(context);
    }
}
