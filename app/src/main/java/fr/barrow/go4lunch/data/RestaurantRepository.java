package fr.barrow.go4lunch.data;

import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import fr.barrow.go4lunch.R;
import fr.barrow.go4lunch.model.Restaurant;
import fr.barrow.go4lunch.model.User;
import fr.barrow.go4lunch.model.UserStateItem;
import fr.barrow.go4lunch.model.placedetails.CombinedPlaceAndString;
import fr.barrow.go4lunch.model.placedetails.OpeningHoursDetails;
import fr.barrow.go4lunch.model.placedetails.Period;
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
                Collections.sort(restaurants, new Comparator<Restaurant>() {
                    public int compare(Restaurant v1, Restaurant v2) {
                        return v1.getName().compareTo(v2.getName());
                    }
                });
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
        String phoneNumber = place.getInternationalPhoneNumber();
        String website = place.getWebsite();
        LatLng position = new LatLng(place.getGeometry().getLocation().getLat(), place.getGeometry().getLocation().getLng());
        int rating = ratingToIntOn3(place.getRating());
        String closingTime = null;
        Date closingTimeDate = null;
        Date openingTimeDate = null;

        String indexString = getPeriodOfToday(place.getOpeningHours());
        if (indexString != null) {
            int index = Integer.parseInt(indexString);
            closingTime = place.getOpeningHours().getPeriods().get(index).getClose().getTime();
            Calendar c = Calendar.getInstance();
            int closingHour = Integer.parseInt(closingTime.substring(0,2));
            int closingMinute = Integer.parseInt(closingTime.substring(2,4));
            c.set(Calendar.HOUR_OF_DAY, closingHour);
            c.set(Calendar.MINUTE, closingMinute);
            if (!place.getOpeningHours().getPeriods().get(index).getClose().getDay().equals(place.getOpeningHours().getPeriods().get(index).getOpen().getDay()) && !place.getOpeningHours().getPeriods().get(index).getClose().getDay().equals(getDayOfWeekFromJavaCalendar(false))) c.add(Calendar.DATE, 1);
            closingTimeDate = c.getTime();

            String openingTime = place.getOpeningHours().getPeriods().get(index).getOpen().getTime();
            Calendar o = Calendar.getInstance();
            int openingHour = Integer.parseInt(openingTime.substring(0,2));
            int openingMinute = Integer.parseInt(openingTime.substring(2,4));
            o.set(Calendar.HOUR_OF_DAY, openingHour);
            o.set(Calendar.MINUTE, openingMinute);
            if (!place.getOpeningHours().getPeriods().get(index).getClose().getDay().equals(place.getOpeningHours().getPeriods().get(index).getOpen().getDay()) && place.getOpeningHours().getPeriods().get(index).getClose().getDay().equals(getDayOfWeekFromJavaCalendar(false))) o.add(Calendar.DATE, -1);
            openingTimeDate = o.getTime();
        }
        return new Restaurant(id, name, address, photoUrl, phoneNumber, website, position, rating, closingTime, openingTimeDate, closingTimeDate);
    }

    private String getPeriodOfToday(OpeningHoursDetails openingHoursDetails) {
        if (openingHoursDetails != null) {
            Calendar n = Calendar.getInstance();
            n.set(Calendar.HOUR_OF_DAY, 20);
            n.set(Calendar.MINUTE, 0);
            Date now = n.getTime();
            int dayOfWeek = getDayOfWeekFromJavaCalendar(false);
            int dayOfWeekBefore = getDayOfWeekFromJavaCalendar(true);
            int index = 0;
            String periodIndex = null;
            for (Period p : openingHoursDetails.getPeriods()) {
                if(p.getOpen().getDay() == dayOfWeekBefore && p.getClose().getDay() == dayOfWeek && now.before(stringToDate(p.getClose().getTime()))) {
                    return String.valueOf(index);
                }
                if (p.getOpen().getDay() == dayOfWeek) {
                    periodIndex = String.valueOf(index);
                }
                index++;
            }
            return periodIndex;
        }
        return null;
    }

    private Date stringToDate(String dateString) {
        Calendar c = Calendar.getInstance();
        int closingHour = Integer.parseInt(dateString.substring(0,2));
        int closingMinute = Integer.parseInt(dateString.substring(2,4));
        c.set(Calendar.HOUR_OF_DAY, closingHour);
        c.set(Calendar.MINUTE, closingMinute);
        return c.getTime();
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

    private int getDayOfWeekFromJavaCalendar(boolean bool) {
        Calendar cal = new GregorianCalendar();
        Date date = new Date();
        cal.setTime(date);
        if (bool) cal.add(Calendar.DATE, -1);
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

