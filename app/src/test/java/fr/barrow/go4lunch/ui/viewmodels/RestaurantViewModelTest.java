package fr.barrow.go4lunch.ui.viewmodels;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import android.location.Location;
import android.text.SpannableString;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.jraska.livedata.TestObserver;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import fr.barrow.go4lunch.LiveDataTestUtils;
import fr.barrow.go4lunch.RxImmediateSchedulerRule;
import fr.barrow.go4lunch.data.PlacesStreams;
import fr.barrow.go4lunch.data.RestaurantRepository;
import fr.barrow.go4lunch.data.model.Restaurant;
import fr.barrow.go4lunch.data.model.RestaurantAutocomplete;
import fr.barrow.go4lunch.data.model.placedetails.CombinedPlaceAndString;
import fr.barrow.go4lunch.data.model.placedetails.PlaceDetailsResult;
import io.reactivex.Observable;
import kotlin.jvm.JvmField;

@RunWith(MockitoJUnitRunner.class)
public class RestaurantViewModelTest {

    @Rule
    @JvmField
    public RxImmediateSchedulerRule testSchedulerRule = new RxImmediateSchedulerRule();
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private RestaurantViewModel mRestaurantViewModel;

    @Mock
    private RestaurantRepository mRestaurantRepository;
    @Mock
    private Location mLocation;
    @Mock
    private PlacesStreams mPlacesStreams;
    @Mock
    private PlacesClient mPlacesClient;

    @Before
    public void setUp() {
        mRestaurantViewModel = new RestaurantViewModel(mRestaurantRepository, mPlacesStreams);
    }

    @Test
    public void getLocationString() {
        double lat = 48.8975167;
        double lng = 2.3834317;
        String expected_locationString = "48.8975167,2.3834317";
        when(mLocation.getLatitude()).thenReturn(lat);
        when(mLocation.getLongitude()).thenReturn(lng);
        mRestaurantViewModel.setLocation(mLocation);
        String actual_locationString = mRestaurantViewModel.getLocationString();

        assertEquals(expected_locationString, actual_locationString);
    }

    @Test
    public void setLocation() {
        String expected_string = "locationTest";
        when(mLocation.toString()).thenReturn(expected_string);
        Location expected_location = mLocation;
        mRestaurantViewModel.setLocation(mLocation);
        Location actual_location = mRestaurantViewModel.getLocation();
        String actual_string = actual_location.toString();
        assertEquals(expected_location, actual_location);
        assertEquals(expected_string, actual_string);
    }

    @Test
    public void placeDetailsToRestaurantObject() {
        PlaceDetailsResult placeDetailsResult = new PlaceDetailsResult();
        String string = "";
        Mockito.when(mRestaurantRepository.placeDetailsToRestaurantObject(placeDetailsResult, string)).thenReturn(new Restaurant(null, null, null, null, null, null, null, 1, null, null,null));
        mRestaurantViewModel.placeDetailsToRestaurantObject(placeDetailsResult, string);

        Mockito.verify(mRestaurantRepository, Mockito.times(1)).placeDetailsToRestaurantObject(placeDetailsResult, string);
    }

    @Test
    public void fetchAndUpdateRestaurants() {

        Location location = new Location("MyLocation");
        mRestaurantViewModel.setLocation(location);

        MutableLiveData<List<Restaurant>> result = mRestaurantViewModel.getRestaurantsMutableLiveData();


        PlaceDetailsResult placeDetailsResult = new PlaceDetailsResult();
        String expected_photoUrl = "url.com";
        CombinedPlaceAndString combinedPlaceAndString = new CombinedPlaceAndString(placeDetailsResult, expected_photoUrl);

        when(mPlacesStreams.streamFetchNearbyPlacesAndFetchTheirDetails(anyString(), any())).thenReturn(Observable.just(combinedPlaceAndString));
        when(mRestaurantRepository.placeDetailsToRestaurantObject(placeDetailsResult, expected_photoUrl)).thenReturn(new Restaurant(null, null, null, expected_photoUrl, null, null, null, 1, null, null,null));

        mRestaurantViewModel.fetchAndUpdateRestaurants("");

        LiveDataTestUtils.observeForTesting(result, liveData -> {
            List<Restaurant> actual_restaurantList = liveData.getValue();
            String actual_photoUrl = "";
            if (actual_restaurantList != null) {
                Restaurant actual_restaurant = actual_restaurantList.get(0);
                actual_photoUrl = actual_restaurant.getUrlPicture();
            }

            assertEquals(expected_photoUrl, actual_photoUrl);
        });
    }

    @Test
    public void fetchRestaurantDetailsAndAddRestaurant() throws InterruptedException {
        MutableLiveData<List<Restaurant>> result = mRestaurantViewModel.getRestaurantsMutableLiveData();

        int expected_size;
        if (result.getValue() == null) {
            expected_size = 1;
        } else {
            expected_size = result.getValue().size() + 1;
        }

        PlaceDetailsResult placeDetailsResult = new PlaceDetailsResult();
        String placeId = "3";
        String photoUrl = "2";
        CombinedPlaceAndString combinedPlaceAndString = new CombinedPlaceAndString(placeDetailsResult, photoUrl);
        when(mPlacesStreams.streamCombinePlaceDetailsAndPhotoUrl(anyString(), anyString())).thenReturn(Observable.just(combinedPlaceAndString));
        when(mRestaurantRepository.placeDetailsToRestaurantObject(placeDetailsResult, photoUrl)).thenReturn(new Restaurant(placeId, null, null, photoUrl, null, null, null, 1, null, null,null));

        mRestaurantViewModel.fetchRestaurantDetailsAndAddRestaurant("", placeId);

        TestObserver.test(result)
                .awaitValue()
                .assertValue(value -> value.size() == expected_size);

    }

    @Mock
    Task<FindAutocompletePredictionsResponse> responseTask;
    @Mock
    FindAutocompletePredictionsResponse response;
    @Mock
    List<AutocompletePrediction> list;
    @Mock
    AutocompletePrediction autocompletePrediction;
    @Mock
    SpannableString spannableStringName;
    @Mock
    SpannableString spannableStringAddress;

    @Captor
    ArgumentCaptor<OnSuccessListener<FindAutocompletePredictionsResponse>> onSuccessListenerCaptorFindAutocompletePredictionsResponse;

    @Test
    public void autocompleteRequest() throws InterruptedException {
        Location location = new Location("MyLocation");
        mRestaurantViewModel.setLocation(location);

        MutableLiveData<List<RestaurantAutocomplete>> result = mRestaurantViewModel.getRestaurantsAutocompleteMutableLiveData();

        List<Place.Type> typeList = Collections.singletonList(Place.Type.RESTAURANT);

        String expected_restaurantName = "Test";
        String expected_placeId = "1";
        String expected_address = "Address";
        int distance = 100;
        String expected_distance = "100";
        when(mPlacesClient.findAutocompletePredictions(any(FindAutocompletePredictionsRequest.class))).thenReturn(responseTask);
        when(responseTask.addOnSuccessListener(onSuccessListenerCaptorFindAutocompletePredictionsResponse.capture())).thenReturn(responseTask);

        when(response.getAutocompletePredictions()).thenReturn(list);
        when(list.iterator()).thenReturn(Collections.singletonList(autocompletePrediction).iterator());
        when(autocompletePrediction.getPlaceTypes()).thenReturn(typeList);
        when(autocompletePrediction.getPrimaryText(any())).thenReturn(spannableStringName);
        when(spannableStringName.toString()).thenReturn("Test");
        when(autocompletePrediction.getSecondaryText(any())).thenReturn(spannableStringAddress);
        when(spannableStringAddress.toString()).thenReturn(expected_address);
        when(autocompletePrediction.getDistanceMeters()).thenReturn(distance);
        when(autocompletePrediction.getPlaceId()).thenReturn(expected_placeId);


        mRestaurantViewModel.autocompleteRequest(expected_restaurantName, mPlacesClient);

        onSuccessListenerCaptorFindAutocompletePredictionsResponse.getValue().onSuccess(response);

        LiveDataTestUtils.observeForTesting(result, liveData -> {
            List<RestaurantAutocomplete> actual_restaurantList = liveData.getValue();
            RestaurantAutocomplete actual_restaurant = new RestaurantAutocomplete(null, null, null, null);
            if (actual_restaurantList != null) {
                actual_restaurant = actual_restaurantList.get(0);
            }
            String actual_restaurantName = actual_restaurant.getName();
            String actual_placeId = actual_restaurant.getPlaceId();
            String actual_address = actual_restaurant.getAddress();
            String actual_distance = actual_restaurant.getDistance();
            assertEquals(expected_restaurantName, actual_restaurantName);
            assertEquals(expected_placeId, actual_placeId);
            assertEquals(expected_address, actual_address);
            assertEquals(expected_distance, actual_distance);
        });
        TestObserver.test(result)
                .awaitValue()
                .assertValue(value -> value.get(0).getPlaceId().equals(expected_placeId));
    }

    @Test
    public void getRestaurantFromId() {
        String expected_restaurantId = "2";
        Restaurant restaurantA = new Restaurant("1", "test", null, null, null, null, null, 0, null, null, null);
        Restaurant restaurantB = new Restaurant(expected_restaurantId, "test", null, null, null, null, null, 0, null, null, null);
        List<Restaurant> list = Arrays.asList(restaurantA, restaurantB);
        mRestaurantViewModel.getRestaurantsMutableLiveData().setValue(list);

        String actual_restaurantId = mRestaurantViewModel.getRestaurantFromId(expected_restaurantId).getId();

        assertEquals(expected_restaurantId, actual_restaurantId);

        Restaurant actual_restaurant = mRestaurantViewModel.getRestaurantFromId("test");

        assertNull(actual_restaurant);
    }
}