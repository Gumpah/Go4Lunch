package fr.barrow.go4lunch.ui.viewmodels;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import static fr.barrow.go4lunch.ListenersTestUtil.testOnSuccessListener;

import android.location.Location;
import android.text.SpannableString;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.jraska.livedata.TestObserver;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import fr.barrow.go4lunch.BuildConfig;
import fr.barrow.go4lunch.RxImmediateSchedulerRule;
import fr.barrow.go4lunch.data.PlacesStreams;
import fr.barrow.go4lunch.data.RestaurantRepository;
import fr.barrow.go4lunch.model.Restaurant;
import fr.barrow.go4lunch.model.RestaurantAutocomplete;
import fr.barrow.go4lunch.model.placedetails.CombinedPlaceAndString;
import fr.barrow.go4lunch.model.placedetails.PlaceDetailsResult;
import io.reactivex.Observable;
import kotlin.jvm.JvmField;

@RunWith(RobolectricTestRunner.class)
public class RestaurantViewModelTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();
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
        Location expected_location = new Location("MyLocation");
        expected_location.setLatitude(lat);
        expected_location.setLongitude(lng);
        mRestaurantViewModel.setLocation(expected_location);
        String actual_locationString = mRestaurantViewModel.getLocationString();

        assertEquals(expected_locationString, actual_locationString);
    }

    @Test
    public void setLocation() {
        double lat = 48.8975167;
        double lng = 2.3834317;
        Location expected_location = new Location("MyLocation");
        expected_location.setLatitude(lat);
        expected_location.setLongitude(lng);
        mRestaurantViewModel.setLocation(expected_location);
        Location actual_location = mRestaurantViewModel.getLocation();

        assertEquals(expected_location, actual_location);
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
    public void fetchAndUpdateRestaurants() throws InterruptedException {

        double lat = 48.8975167;
        double lng = 2.3834317;
        Location location = new Location("MyLocation");
        location.setLatitude(lat);
        location.setLongitude(lng);
        mRestaurantViewModel.setLocation(location);

        MutableLiveData<List<Restaurant>> result = mRestaurantViewModel.getRestaurantsMutableLiveData();

        String apiKey = BuildConfig.MAPS_API_KEY;

        PlaceDetailsResult placeDetailsResult = new PlaceDetailsResult();
        String photoUrl = "1";
        CombinedPlaceAndString combinedPlaceAndString = new CombinedPlaceAndString(placeDetailsResult, photoUrl);

        when(mPlacesStreams.streamFetchNearbyPlacesAndFetchTheirDetails(anyString(), any())).thenReturn(Observable.just(combinedPlaceAndString));
        when(mRestaurantRepository.placeDetailsToRestaurantObject(placeDetailsResult, photoUrl)).thenReturn(new Restaurant(null, null, null, photoUrl, null, null, null, 1, null, null,null));

        mRestaurantViewModel.setLocation(mLocation);
        mRestaurantViewModel.fetchAndUpdateRestaurants(apiKey);

        TestObserver.test(result)
                .awaitValue()
                .assertValue(value -> value.get(0).getUrlPicture().equals(photoUrl));
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
    Task<FindAutocompletePredictionsResponse> mResponse;
    @Mock
    List<AutocompletePrediction> list;
    @Mock
    AutocompletePrediction autocompletePrediction;

    @Test
    public void autocompleteRequest() throws InterruptedException {
        double lat = 48.8975167;
        double lng = 2.3834317;
        Location location = new Location("MyLocation");
        location.setLatitude(lat);
        location.setLongitude(lng);
        mRestaurantViewModel.setLocation(location);

        MutableLiveData<List<RestaurantAutocomplete>> result = mRestaurantViewModel.getRestaurantsAutocompleteMutableLiveData();

        List<Place.Type> typeList = Collections.singletonList(Place.Type.RESTAURANT);
        SpannableString spannableString = new SpannableString("Test");

        String textSearched = "Test";
        String placeId = "1";
        when(mPlacesClient.findAutocompletePredictions(any())).thenReturn(responseTask);
        when(responseTask.addOnSuccessListener(testOnSuccessListener.capture())).thenReturn(responseTask);

        when(response.getAutocompletePredictions()).thenReturn(list);
        when(list.iterator()).thenReturn(Collections.singletonList(autocompletePrediction).iterator());
        when(autocompletePrediction.getPlaceTypes()).thenReturn(typeList);
        when(autocompletePrediction.getPrimaryText(any())).thenReturn(spannableString);
        when(autocompletePrediction.getDistanceMeters()).thenReturn(1);
        when(autocompletePrediction.getPlaceId()).thenReturn(placeId);


        mRestaurantViewModel.autocompleteRequest(textSearched, mPlacesClient);

        testOnSuccessListener.getValue().onSuccess(response);

        TestObserver.test(result)
                .awaitValue()
                .assertValue(value -> value.get(0).getPlaceId().equals(placeId));
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