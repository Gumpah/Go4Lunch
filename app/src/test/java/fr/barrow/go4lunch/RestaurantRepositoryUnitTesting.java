package fr.barrow.go4lunch;

import static org.junit.Assert.assertEquals;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import fr.barrow.go4lunch.data.RestaurantRepository;
import fr.barrow.go4lunch.model.Restaurant;
import fr.barrow.go4lunch.model.placedetails.Close;
import fr.barrow.go4lunch.model.placedetails.Open;
import fr.barrow.go4lunch.model.placedetails.OpeningHoursDetails;
import fr.barrow.go4lunch.model.placedetails.Period;
import fr.barrow.go4lunch.model.placedetails.PlaceDetails;
import fr.barrow.go4lunch.model.placedetails.PlaceDetailsResult;
import fr.barrow.go4lunch.model.placesnearby.Geometry;
import fr.barrow.go4lunch.model.placesnearby.Location;

public class RestaurantRepositoryUnitTesting {

    private RestaurantRepository mRestaurantRepository;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() {
        mRestaurantRepository = new RestaurantRepository();
    }

    @Test
    public void placeDetailsToRestaurantObject() {
        String expected_placeId = "1";
        String expected_placeName = "Test";
        String addressFull = "123 Rue De La Paix, 75000 Paris, France";
        String expected_address = "123 Rue De La Paix";
        String expected_phoneNumber = "+33 1 46 05 12 11";
        String expected_website = "https://openclassrooms.com/fr/";
        String expected_photoUrl = "https://oc-assets.imgix.net/modules/sdzv4-main/latest/images/home_banner-5ed8a0400525d8dcbe81.png";
        double latitude = 48.8975167;
        double longitude = 2.3834317;
        LatLng expected_position = new LatLng(latitude,longitude);

        PlaceDetails placeDetails = new PlaceDetails();
        Location location = new Location();
        location.setLat(latitude);
        location.setLng(longitude);
        Geometry geometry = new Geometry();
        geometry.setLocation(location);
        placeDetails.setPlaceId(expected_placeId);
        placeDetails.setName(expected_placeName);
        placeDetails.setFormattedAddress(addressFull);
        placeDetails.setInternationalPhoneNumber(expected_phoneNumber);
        placeDetails.setWebsite(expected_website);
        placeDetails.setGeometry(geometry);
        PlaceDetailsResult placeDetailsResult = new PlaceDetailsResult();
        placeDetailsResult.setResult(placeDetails);

        Restaurant restaurant = mRestaurantRepository.placeDetailsToRestaurantObject(placeDetailsResult,expected_photoUrl);

        String actual_placeId = restaurant.getId();
        String actual_placeName = restaurant.getName();
        String actual_address = restaurant.getAddress();
        String actual_phoneNumber = restaurant.getPhoneNumber();
        String actual_website = restaurant.getWebsite();
        LatLng actual_position = restaurant.getPosition();
        String actual_photoUrl = restaurant.getUrlPicture();

        assertEquals(expected_placeId, actual_placeId);
        assertEquals(expected_placeName, actual_placeName);
        assertEquals(expected_address, actual_address);
        assertEquals(expected_phoneNumber, actual_phoneNumber);
        assertEquals(expected_website, actual_website);
        assertEquals(expected_position, actual_position);
        assertEquals(expected_photoUrl, actual_photoUrl);
    }

    @Test
    public void getPeriodOfTodayIndex_shouldReturnIndexOfToday() {
        //Test won't work if actual time is between 23h56 to 00h02
        Calendar cal = Calendar.getInstance();
        Date date = new Date();
        cal.setTime(date);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)-1;
        cal.add(Calendar.DATE, -1);
        int dayOfWeekYesterday = cal.get(Calendar.DAY_OF_WEEK)-1;

        Open open = new Open();
        Close close = new Close();
        open.setDay(dayOfWeekYesterday);
        close.setDay(dayOfWeekYesterday);
        open.setTime("0001");
        close.setTime("2359");
        Period period = new Period();
        period.setOpen(open);
        period.setClose(close);

        open = new Open();
        close = new Close();
        open.setDay(dayOfWeek);
        close.setDay(dayOfWeek);
        open.setTime("0001");
        close.setTime("2359");
        Period period2 = new Period();
        period2.setOpen(open);
        period2.setClose(close);

        List<Period> periods = Arrays.asList(period, period2);
        OpeningHoursDetails details = new OpeningHoursDetails();
        details.setPeriods(periods);

        String expected_index = "1";
        String actual_index = mRestaurantRepository.getPeriodOfTodayIndex(details);

        assertEquals(expected_index, actual_index);
    }

    @Test
    public void getPeriodOfTodayIndex_shouldReturnIndexOfYesterday() {
        //Test won't work if actual time is between 23h56 to 00h02
        Calendar cal = Calendar.getInstance();
        Date date = new Date();
        cal.setTime(date);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)-1;
        cal.add(Calendar.DATE, -1);
        int dayOfWeekYesterday = cal.get(Calendar.DAY_OF_WEEK)-1;

        Open open = new Open();
        Close close = new Close();
        open.setDay(dayOfWeekYesterday);
        close.setDay(dayOfWeek);
        open.setTime("0001");
        close.setTime("2357");
        Period period = new Period();
        period.setOpen(open);
        period.setClose(close);

        open = new Open();
        close = new Close();
        open.setDay(dayOfWeek);
        close.setDay(dayOfWeek);
        open.setTime("2358");
        close.setTime("2359");
        Period period2 = new Period();
        period2.setOpen(open);
        period2.setClose(close);

        List<Period> periods = Arrays.asList(period, period2);
        OpeningHoursDetails details = new OpeningHoursDetails();
        details.setPeriods(periods);

        String expected_index = "0";
        String actual_index = mRestaurantRepository.getPeriodOfTodayIndex(details);

        assertEquals(expected_index, actual_index);
    }

    @Test
    public void getPeriodOfTodayIndex_shouldReturnNullIfNotOpenToday() {
        //Test won't work if actual time is between 23h56 to 00h02
        Calendar cal = Calendar.getInstance();
        Date date = new Date();
        cal.setTime(date);
        cal.add(Calendar.DATE, -1);
        int dayOfWeekYesterday = cal.get(Calendar.DAY_OF_WEEK)-1;
        cal = Calendar.getInstance();
        date = new Date();
        cal.setTime(date);
        cal.add(Calendar.DATE, 1);
        int dayOfWeekTomorrow = cal.get(Calendar.DAY_OF_WEEK)-1;

        Open open = new Open();
        Close close = new Close();
        open.setDay(dayOfWeekYesterday);
        close.setDay(dayOfWeekYesterday);
        open.setTime("0001");
        close.setTime("2359");
        Period period = new Period();
        period.setOpen(open);
        period.setClose(close);

        open = new Open();
        close = new Close();
        open.setDay(dayOfWeekTomorrow);
        close.setDay(dayOfWeekTomorrow);
        open.setTime("0001");
        close.setTime("2359");
        Period period2 = new Period();
        period2.setOpen(open);
        period2.setClose(close);

        List<Period> periods = Arrays.asList(period, period2);
        OpeningHoursDetails details = new OpeningHoursDetails();
        details.setPeriods(periods);

        String expected_index = null;
        String actual_index = mRestaurantRepository.getPeriodOfTodayIndex(details);

        assertEquals(expected_index, actual_index);
    }

    @Test
    public void getPeriodOfTodayIndex_shouldReturnNullIfParameterNull() {
        String expected_index = null;
        String actual_index = mRestaurantRepository.getPeriodOfTodayIndex(null);

        assertEquals(expected_index, actual_index);
    }

    @Test
    public void stringToDate() {
        String stringToConvert = "1230";
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 12);
        c.set(Calendar.MINUTE, 30);

        Date expected_date = c.getTime();
        Date actual_date = mRestaurantRepository.stringToDate(stringToConvert);

        assertEquals(expected_date, actual_date);
    }

    @Test
    public void ratingToIntOn3_shouldReturnSuperiorInt() {
        double ratingOn3 = 1.8;
        double ratingOn5 = ratingOn3*5/3;
        int expected_ratingOn3 = 2;
        int actual_ratingOn3 = mRestaurantRepository.ratingToIntOn3(ratingOn5);

        assertEquals(expected_ratingOn3, actual_ratingOn3);
    }

    @Test
    public void ratingToIntOn3_shouldReturnInferiorInt() {
        double ratingOn3 = 1.2;
        double ratingOn5 = ratingOn3*5/3;
        int expected_ratingOn3 = 1;
        int actual_ratingOn3 = mRestaurantRepository.ratingToIntOn3(ratingOn5);

        assertEquals(expected_ratingOn3, actual_ratingOn3);
    }

    @Test
    public void ratingToIntOn3_shouldReturn0() {
        int expected_ratingOn3 = 0;
        int actual_ratingOn3 = mRestaurantRepository.ratingToIntOn3(null);

        assertEquals(expected_ratingOn3, actual_ratingOn3);
    }

    @Test
    public void getPlaceAPIDayOfWeek_shouldReturnToday() {
        Calendar cal = Calendar.getInstance();
        Date date = new Date();
        cal.setTime(date);
        int expected_dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)-1;
        int actual_dayOfWeek = mRestaurantRepository.getPlaceAPIDayOfWeek(false);

        assertEquals(expected_dayOfWeek, actual_dayOfWeek);
    }

    @Test
    public void getPlaceAPIDayOfWeek_shouldReturnYesterday() {
        Calendar cal = Calendar.getInstance();
        Date date = new Date();
        cal.setTime(date);
        cal.add(Calendar.DATE, -1);
        int expected_dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)-1;
        int actual_dayOfWeek = mRestaurantRepository.getPlaceAPIDayOfWeek(true);

        assertEquals(expected_dayOfWeek, actual_dayOfWeek);
    }

    @Test
    public void getRestaurantFromId() {
        String expected_restaurantId = "2";
        Restaurant restaurantA = new Restaurant("1", "test", null, null, null, null, null, 0, null, null, null);
        Restaurant restaurantB = new Restaurant(expected_restaurantId, "test", null, null, null, null, null, 0, null, null, null);
        List<Restaurant> list = Arrays.asList(restaurantA, restaurantB);
        mRestaurantRepository.getRestaurantsMutableLiveData().setValue(list);

        String actual_restaurantId = mRestaurantRepository.getRestaurantFromId(expected_restaurantId).getId();

        assertEquals(expected_restaurantId, actual_restaurantId);
    }
}
