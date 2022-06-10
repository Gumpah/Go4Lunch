package fr.barrow.go4lunch.data;

import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import fr.barrow.go4lunch.model.Restaurant;
import fr.barrow.go4lunch.model.RestaurantAutocomplete;
import fr.barrow.go4lunch.model.placedetails.OpeningHoursDetails;
import fr.barrow.go4lunch.model.placedetails.Period;
import fr.barrow.go4lunch.model.placedetails.PlaceDetails;
import fr.barrow.go4lunch.model.placedetails.PlaceDetailsResult;

public class RestaurantRepository {

    public RestaurantRepository() {
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

        String indexString = getPeriodOfTodayIndex(place.getOpeningHours());
        if (indexString != null) {
            int index = Integer.parseInt(indexString);
            closingTime = place.getOpeningHours().getPeriods().get(index).getClose().getTime();
            Calendar c = Calendar.getInstance();
            int closingHour = Integer.parseInt(closingTime.substring(0,2));
            int closingMinute = Integer.parseInt(closingTime.substring(2,4));
            c.set(Calendar.HOUR_OF_DAY, closingHour);
            c.set(Calendar.MINUTE, closingMinute);
            if (!place.getOpeningHours().getPeriods().get(index).getClose().getDay().equals(place.getOpeningHours().getPeriods().get(index).getOpen().getDay()) && !place.getOpeningHours().getPeriods().get(index).getClose().getDay().equals(getPlaceAPIDayOfWeek(false))) c.add(Calendar.DATE, 1);
            closingTimeDate = c.getTime();

            String openingTime = place.getOpeningHours().getPeriods().get(index).getOpen().getTime();
            Calendar o = Calendar.getInstance();
            int openingHour = Integer.parseInt(openingTime.substring(0,2));
            int openingMinute = Integer.parseInt(openingTime.substring(2,4));
            o.set(Calendar.HOUR_OF_DAY, openingHour);
            o.set(Calendar.MINUTE, openingMinute);
            if (!place.getOpeningHours().getPeriods().get(index).getClose().getDay().equals(place.getOpeningHours().getPeriods().get(index).getOpen().getDay()) && place.getOpeningHours().getPeriods().get(index).getClose().getDay().equals(getPlaceAPIDayOfWeek(false))) o.add(Calendar.DATE, -1);
            openingTimeDate = o.getTime();
        }
        return new Restaurant(id, name, address, photoUrl, phoneNumber, website, position, rating, closingTime, openingTimeDate, closingTimeDate);
    }

    public String getPeriodOfTodayIndex(OpeningHoursDetails openingHoursDetails) {
        if (openingHoursDetails != null) {
            Date now = new Date();
            int dayOfWeek = getPlaceAPIDayOfWeek(false);
            int dayOfWeekBefore = getPlaceAPIDayOfWeek(true);
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

    public Date stringToDate(String dateString) {
        Calendar c = Calendar.getInstance();
        int closingHour = Integer.parseInt(dateString.substring(0,2));
        int closingMinute = Integer.parseInt(dateString.substring(2,4));
        c.set(Calendar.HOUR_OF_DAY, closingHour);
        c.set(Calendar.MINUTE, closingMinute);
        return c.getTime();
    }

    public int ratingToIntOn3(Double r) {
        if (r != null) {
            Double rating = r*3/5;
            int ratingNoDecimalInt = rating.intValue();
            if (rating-0.8 >= (double) ratingNoDecimalInt) {
                return ratingNoDecimalInt+1;
            } else {
                return ratingNoDecimalInt;
            }
        }
        return 0;
    }

    public int getPlaceAPIDayOfWeek(boolean bool) {
        Calendar cal = new GregorianCalendar();
        Date date = new Date();
        cal.setTime(date);
        if (bool) cal.add(Calendar.DATE, -1);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek-1;
    }
}

