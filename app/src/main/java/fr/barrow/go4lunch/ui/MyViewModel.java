package fr.barrow.go4lunch.ui;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import fr.barrow.go4lunch.R;
import fr.barrow.go4lunch.data.PlacesStreams;
import fr.barrow.go4lunch.model.RestaurantAutocomplete;
import fr.barrow.go4lunch.model.UserStateItem;
import fr.barrow.go4lunch.data.Repository;
import fr.barrow.go4lunch.data.RestaurantRepository;
import fr.barrow.go4lunch.data.UserRepository;
import fr.barrow.go4lunch.model.Restaurant;
import fr.barrow.go4lunch.model.User;
import fr.barrow.go4lunch.model.placedetails.CombinedPlaceAndString;
import fr.barrow.go4lunch.model.placedetails.PlaceDetailsResult;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class MyViewModel extends ViewModel {

    AutocompleteSessionToken mToken;
    Repository mRepository;
    RestaurantRepository mRestaurantRepository;
    UserRepository mUserRepository;
    MutableLiveData<User> mUser;

    Disposable disposable;

    public MyViewModel(Repository repository, RestaurantRepository restaurantRepository, UserRepository userRepository) {
        mRepository = repository;
        mRestaurantRepository = restaurantRepository;
        mUserRepository = userRepository;
        mUser = new MutableLiveData<>();
        mToken = AutocompleteSessionToken.newInstance();
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


    public void autocompleteRequest(String text, Context context, String apiKey) {
        ArrayList<RestaurantAutocomplete> restaurantsAutocomplete = new ArrayList<>();
        if (getLocation() != null) {
            if (!Places.isInitialized()) {
                Places.initialize(context, apiKey);
            }
            PlacesClient placesClient = Places.createClient(context);
            Location myLocation = getLocation();
            RectangularBounds bounds = RectangularBounds.newInstance(
                    new LatLng(myLocation.getLatitude() - 0.05, myLocation.getLongitude() - 0.05),
                    new LatLng(myLocation.getLatitude() + 0.05, myLocation.getLongitude() + 0.05));

            FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                    .setLocationRestriction(bounds)
                    .setOrigin(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()))
                    .setCountries("FR")
                    .setTypeFilter(TypeFilter.ESTABLISHMENT)
                    .setSessionToken(mToken)
                    .setQuery(text)
                    .build();

            placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
                restaurantsAutocomplete.clear();
                for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                    if (prediction.getPlaceTypes().toString().toLowerCase().contains("restaurant") && prediction.getPrimaryText(null).toString().toLowerCase().contains(text.toLowerCase())) {
                        restaurantsAutocomplete.add(new RestaurantAutocomplete(prediction.getPlaceId(), prediction.getPrimaryText(null).toString(), prediction.getDistanceMeters().toString()));
                    }
                }
                mRestaurantRepository.getRestaurantsAutocompleteMutableLiveData().postValue(restaurantsAutocomplete);
            }).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    Toast.makeText(context, apiException.getMessage(), Toast.LENGTH_SHORT).show();
                }
                restaurantsAutocomplete.clear();
                mRestaurantRepository.getRestaurantsAutocompleteMutableLiveData().postValue(restaurantsAutocomplete);
            });
        }
    }

    public void fetchAndUpdateRestaurants(String location, String apiKey, Context context) {
        mRestaurantRepository.getRestaurantsMutableLiveData().postValue(null);
        ArrayList<Restaurant> restaurants = new ArrayList<>();
        this.disposable = PlacesStreams.streamFetchNearbyPlacesAndFetchTheirDetails(apiKey, location).subscribeWith(new DisposableObserver<CombinedPlaceAndString>() {
            @Override
            public void onNext(CombinedPlaceAndString combinedPlaceAndString) {
                PlaceDetailsResult placeDetailsResult = combinedPlaceAndString.getPlaceDetailsResult();
                String photoUrl = combinedPlaceAndString.getPhotoUrl();
                restaurants.add(placeDetailsToRestaurantObject(placeDetailsResult, photoUrl));
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(context, R.string.restaurant_fetch_error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete() {
                Collections.sort(restaurants, (v1, v2) -> v1.getName().compareTo(v2.getName()));
                mRestaurantRepository.getRestaurantsMutableLiveData().postValue(restaurants);
            }
        });
    }

    public void fetchRestaurantDetailsAndAddRestaurant(String apiKey, String placeId, Context context) {
        mRestaurantRepository.getRestaurantsMutableLiveData().postValue(null);
        ArrayList<Restaurant> restaurants = new ArrayList<>();
        if (mRestaurantRepository.getRestaurantsMutableLiveData().getValue() != null && !mRestaurantRepository.getRestaurantsMutableLiveData().getValue().isEmpty()) {
            restaurants.addAll(mRestaurantRepository.getRestaurantsMutableLiveData().getValue());
        }
        this.disposable = PlacesStreams.streamCombinePlaceDetailsAndPhotoUrl(apiKey, placeId).subscribeWith(new DisposableObserver<CombinedPlaceAndString>() {
            @Override
            public void onNext(CombinedPlaceAndString combinedPlaceAndString) {
                PlaceDetailsResult placeDetailsResult = combinedPlaceAndString.getPlaceDetailsResult();
                String photoUrl = combinedPlaceAndString.getPhotoUrl();
                restaurants.add(placeDetailsToRestaurantObject(placeDetailsResult, photoUrl));
            }

            @Override
            public void onError(Throwable e) {
                //Toast.makeText(requireActivity(), "Impossible de récupérer les restaurants", Toast.LENGTH_SHORT).show();
                Log.e("TAG","On Error"+Log.getStackTraceString(e));
            }

            @Override
            public void onComplete() {
                Collections.sort(restaurants, new Comparator<Restaurant>() {
                    public int compare(Restaurant v1, Restaurant v2) {
                        return v1.getName().compareTo(v2.getName());
                    }
                });
                mRestaurantRepository.getRestaurantsMutableLiveData().postValue(restaurants);
            }
        });
    }

    public MutableLiveData<List<Restaurant>> getRestaurantsMutableLiveData() {
        return mRestaurantRepository.getRestaurantsMutableLiveData();
    }

    public MutableLiveData<List<RestaurantAutocomplete>> getRestaurantsAutocompleteMutableLiveData() {
        return mRestaurantRepository.getRestaurantsAutocompleteMutableLiveData();
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

    @Override
    protected void onCleared() {
        super.onCleared();
        if (this.disposable != null && !this.disposable.isDisposed()) this.disposable.dispose();
    }
}
