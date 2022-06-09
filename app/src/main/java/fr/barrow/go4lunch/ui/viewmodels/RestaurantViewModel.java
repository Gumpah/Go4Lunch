package fr.barrow.go4lunch.ui.viewmodels;

import android.location.Location;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.barrow.go4lunch.R;
import fr.barrow.go4lunch.data.PlacesStreams;
import fr.barrow.go4lunch.data.RestaurantRepository;
import fr.barrow.go4lunch.model.Restaurant;
import fr.barrow.go4lunch.model.RestaurantAutocomplete;
import fr.barrow.go4lunch.model.placedetails.CombinedPlaceAndString;
import fr.barrow.go4lunch.model.placedetails.PlaceDetailsResult;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class RestaurantViewModel extends ViewModel {

    AutocompleteSessionToken mToken;
    RestaurantRepository mRestaurantRepository;
    Disposable disposable;
    MutableLiveData<List<Restaurant>> myRestaurantList;
    MutableLiveData<List<RestaurantAutocomplete>> myRestaurantAutocompleteList;
    MutableLiveData<Integer> mToastsInteger;
    MutableLiveData<String> mToastsString;

    Location location;

    public RestaurantViewModel(RestaurantRepository restaurantRepository) {
        mRestaurantRepository = restaurantRepository;
        myRestaurantList = new MutableLiveData<>();
        myRestaurantAutocompleteList = new MutableLiveData<>();
        mToastsInteger = new MutableLiveData<>();
        mToastsString = new MutableLiveData<>();
    }

    public Restaurant placeDetailsToRestaurantObject(PlaceDetailsResult placeDetails, String photoUrl) {
        return mRestaurantRepository.placeDetailsToRestaurantObject(placeDetails, photoUrl);
    }

    public Restaurant getRestaurantFromId(String id) {
        if (myRestaurantList.getValue() != null) {
            for (Restaurant r : myRestaurantList.getValue()) {
                if (r.getId().equals(id)) return r;
            }
        }
        return null;
    }

    public void autocompleteRequest(String text, PlacesClient placesClient) {
        ArrayList<RestaurantAutocomplete> restaurantsAutocomplete = new ArrayList<>();
        if (getLocation() != null) {
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
                        String distance = "";
                        if (prediction.getDistanceMeters() != null) {
                            distance = prediction.getDistanceMeters().toString();
                        }
                        restaurantsAutocomplete.add(new RestaurantAutocomplete(prediction.getPlaceId(), prediction.getPrimaryText(null).toString(), distance));
                    }
                }
                getRestaurantsAutocompleteMutableLiveData().postValue(restaurantsAutocomplete);
            }).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    getToastSenderString().postValue(apiException.getMessage());
                }
                restaurantsAutocomplete.clear();
                getRestaurantsAutocompleteMutableLiveData().postValue(restaurantsAutocomplete);
            });
        }
    }

    public void fetchAndUpdateRestaurants(String apiKey) {
        if (getLocation() == null) return;
        if (myRestaurantList.getValue() != null) {
            if (!myRestaurantList.getValue().isEmpty()) return;
        }
        getRestaurantsMutableLiveData().postValue(null);
        ArrayList<Restaurant> restaurants = new ArrayList<>();
        this.disposable = PlacesStreams.streamFetchNearbyPlacesAndFetchTheirDetails(apiKey, getLocationString()).subscribeWith(new DisposableObserver<CombinedPlaceAndString>() {
            @Override
            public void onNext(CombinedPlaceAndString combinedPlaceAndString) {
                PlaceDetailsResult placeDetailsResult = combinedPlaceAndString.getPlaceDetailsResult();
                String photoUrl = combinedPlaceAndString.getPhotoUrl();
                restaurants.add(placeDetailsToRestaurantObject(placeDetailsResult, photoUrl));
            }

            @Override
            public void onError(Throwable e) {
                getToastSenderInteger().postValue(R.string.restaurant_fetch_error);
            }

            @Override
            public void onComplete() {
                Collections.sort(restaurants, (v1, v2) -> v1.getName().compareTo(v2.getName()));
                getRestaurantsMutableLiveData().postValue(restaurants);
            }
        });
    }

    public void fetchRestaurantDetailsAndAddRestaurant(String apiKey, String placeId) {
        ArrayList<Restaurant> restaurants = new ArrayList<>();
        if (getRestaurantsMutableLiveData().getValue() != null && !getRestaurantsMutableLiveData().getValue().isEmpty()) {
            restaurants.addAll(getRestaurantsMutableLiveData().getValue());
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
                getToastSenderInteger().postValue(R.string.restaurant_fetch_error);
            }

            @Override
            public void onComplete() {
                Collections.sort(restaurants, (v1, v2) -> v1.getName().compareTo(v2.getName()));
                getRestaurantsMutableLiveData().postValue(restaurants);
            }
        });
    }

    public MutableLiveData<List<Restaurant>> getRestaurantsMutableLiveData() {
        return myRestaurantList;
    }

    public MutableLiveData<List<RestaurantAutocomplete>> getRestaurantsAutocompleteMutableLiveData() {
        return myRestaurantAutocompleteList;
    }

    public MutableLiveData<Integer> getToastSenderInteger() {
        return mToastsInteger;
    }

    public MutableLiveData<String> getToastSenderString() {
        return mToastsString;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (this.disposable != null && !this.disposable.isDisposed()) this.disposable.dispose();
    }

    public String getLocationString() {
        return (location.getLatitude() + "," + location.getLongitude());
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
