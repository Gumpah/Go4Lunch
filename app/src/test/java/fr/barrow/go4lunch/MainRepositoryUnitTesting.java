package fr.barrow.go4lunch;

import static org.junit.Assert.assertEquals;

import android.location.Location;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import fr.barrow.go4lunch.data.Repository;

@RunWith(RobolectricTestRunner.class)
public class MainRepositoryUnitTesting {

    private Repository mRepository;

    @Before
    public void setUp() {
        mRepository = new Repository();
    }

    @Test
    public void setAndGetLocationString() {
        double lat = 48.8975167;
        double lng = 2.3834317;
        String expected_locationString = "48.8975167,2.3834317";
        Location expected_location = new Location("MyLocation");
        expected_location.setLatitude(lat);
        expected_location.setLongitude(lng);
        mRepository.setLocation(expected_location);
        Location actual_location = mRepository.getLocation();
        String actual_locationString = mRepository.getLocationString();

        assertEquals(expected_locationString, actual_locationString);
        assertEquals(expected_location, actual_location);
    }
}
