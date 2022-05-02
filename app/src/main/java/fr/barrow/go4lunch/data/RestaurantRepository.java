package fr.barrow.go4lunch.data;

import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import fr.barrow.go4lunch.R;
import fr.barrow.go4lunch.model.Restaurant;
import fr.barrow.go4lunch.model.User;
import fr.barrow.go4lunch.model.placedetails.CombinedPlaceAndString;
import fr.barrow.go4lunch.model.placedetails.PlaceDetails;
import fr.barrow.go4lunch.model.placedetails.PlaceDetailsResult;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class RestaurantRepository {

    private ArrayList<Restaurant> mRestaurantList;
    private final MutableLiveData<List<Restaurant>> myRestaurantList = new MutableLiveData<>();
    private Disposable disposable;

    public RestaurantRepository() {
        mRestaurantList = new ArrayList<>();
    }

    public void fetchAndUpdateRestaurants(String location, Disposable disposable, String apiKey) {
        this.disposable = disposable;
        myRestaurantList.postValue(null);
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
                //Toast.makeText(requireActivity(), "Impossible de récupérer les restaurants", Toast.LENGTH_SHORT).show();
                Log.e("TAG","On Error"+Log.getStackTraceString(e));
            }

            @Override
            public void onComplete() {
                myRestaurantList.postValue(restaurants);
            }
        });
    }

    public MutableLiveData<List<Restaurant>> getRestaurantsMutableLiveData() {
        return myRestaurantList;
    }

    public Restaurant placeDetailsToRestaurantObject(PlaceDetailsResult placeDetails, String photoUrl) {
        PlaceDetails place = placeDetails.getResult();
        String id = place.getPlaceId();
        String name = place.getName();
        String address = place.getFormattedAddress().split(",")[0];
        String urlPicture = photoUrl;
        String phoneNumber = place.getInternationalPhoneNumber();
        String website = place.getWebsite();
        LatLng position = new LatLng(place.getGeometry().getLocation().getLat(), place.getGeometry().getLocation().getLng());
        int rating = ratingToIntOn3(place.getRating());
        int dayOfWeek = getDayOfWeekFromJavaCalendar();
        String closingTimeHours = null;
        String closingTimeMinutes = null;
        if (place.getOpeningHours() != null && place.getOpeningHours().getPeriods().size() >  dayOfWeek) {
            String closingTime = place.getOpeningHours().getPeriods().get(dayOfWeek).getClose().getTime();
            closingTimeHours = closingTime.substring(0,1);
            closingTimeMinutes = closingTime.substring(2,3);
        }
        return new Restaurant(id, name, address, urlPicture, phoneNumber, website, position, rating, closingTimeHours, closingTimeMinutes);
    }

    private int ratingToIntOn3(Double r) {
        if (r != null) {
            Double rating = r*3/5;
            int ratingNoDecimalInt = rating.intValue();
            if (rating-0.9 > (double) ratingNoDecimalInt) {
                return ratingNoDecimalInt;
            } else {
                return ratingNoDecimalInt+1;
            }
        }
        return 0;
    }

    private int getDayOfWeekFromJavaCalendar() {
        Calendar cal = new GregorianCalendar();
        Date date = new Date();
        cal.setTime(date);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        switch (dayOfWeek) {
            case 1:
                return 6;
            case 2:
                return 0;
            case 3:
                return 1;
            case 4:
                return 2;
            case 5:
                return 3;
            case 6:
                return 4;
            case 7:
                return 5;
            default:
                return 0;
        }
    }

    public void addRestaurant(Restaurant restaurant) {
        if (mRestaurantList == null) {
            mRestaurantList = new ArrayList<>();
        }
        mRestaurantList.add(restaurant);
    }

    public void setRestaurants(ArrayList<Restaurant> restaurants) {
        mRestaurantList = new ArrayList<>(restaurants);
    }

    public void clearRestaurants() {
        mRestaurantList = new ArrayList<>();
    }

    public ArrayList<Restaurant> getRestaurants() {
        return mRestaurantList;
    }

    public Restaurant getRestaurantFromId(String id) {
        for (Restaurant r : myRestaurantList.getValue()) {
            if (r.getId().equals(id)) return r;
        }
        return null;
    }
}

