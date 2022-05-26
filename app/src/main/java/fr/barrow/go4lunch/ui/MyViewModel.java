package fr.barrow.go4lunch.ui;

import android.content.Context;
import android.location.Location;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import fr.barrow.go4lunch.model.UserStateItem;
import fr.barrow.go4lunch.data.Repository;
import fr.barrow.go4lunch.data.RestaurantRepository;
import fr.barrow.go4lunch.data.UserRepository;
import fr.barrow.go4lunch.model.Restaurant;
import fr.barrow.go4lunch.model.User;
import fr.barrow.go4lunch.model.placedetails.PlaceDetailsResult;
import io.reactivex.disposables.Disposable;

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

    public MyViewModel(RestaurantRepository restaurantRepository, UserRepository userRepository) {
        mRestaurantRepository = restaurantRepository;
        mUserRepository = userRepository;
        mRestaurantList = new MutableLiveData<>();
        mUser = new MutableLiveData<>();
    }

    public void fetchAndUpdateRestaurants(String location, Disposable disposable, String apiKey) {
       mRestaurantRepository.fetchAndUpdateRestaurants(location, disposable, apiKey);
    }

    public void fetchRestaurantDetailsAndAddRestaurant(String apiKey, String placeId) {
        mRestaurantRepository.fetchRestaurantDetailsAndAddRestaurant(apiKey, placeId);
    }

    public MutableLiveData<List<Restaurant>> getRestaurantsMutableLiveData() {
        return mRestaurantRepository.getRestaurantsMutableLiveData();
    }

    private LiveData<List<UserStateItem>> mapListDataToViewState(LiveData<List<User>> users) {
        return Transformations.map(users, user -> {
            List<UserStateItem> userViewStateItems = new ArrayList<>();
            for (User u : user) {
                userViewStateItems.add(
                        new UserStateItem(u)
                );
            }
            return userViewStateItems;
        });
    }

    private LiveData<UserStateItem> mapDataToViewState(LiveData<User> user) {
        return Transformations.map(user, UserStateItem::new);
    }

    public LiveData<List<UserStateItem>> getAllUsersWhoPickedARestaurant(String restaurantId) {
        return mapListDataToViewState(mUserRepository.getAllUsersWhoPickedARestaurant(restaurantId));
    }

    public LiveData<List<UserStateItem>> getUsersWhoPickedARestaurant() {
        return mapListDataToViewState(mUserRepository.getUsersWhoPickedARestaurant());
    }

    public LiveData<List<UserStateItem>> getAllUsers() {
        return mapListDataToViewState(mUserRepository.getAllUsers());
    }

    public LiveData<UserStateItem> getUserNew() {
        return mapDataToViewState(mUserRepository.getUserNew());
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

    public String getLocationString() {
        return mRepository.getLocationString();
    }

    public Location getLocation() {
        return mRepository.getLocation();
    }

    public void setLocation(Location location) {
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

    public void setPickedRestaurant(String restaurantId, String restaurantName) {
        mUserRepository.setPickedRestaurant(restaurantId, restaurantName);
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
