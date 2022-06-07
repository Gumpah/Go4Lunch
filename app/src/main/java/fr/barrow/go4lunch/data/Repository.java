package fr.barrow.go4lunch.data;

import android.content.Context;
import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import fr.barrow.go4lunch.utils.NetworkMonitoring;
import fr.barrow.go4lunch.utils.NetworkStateManager;

public class Repository {

    public NetworkMonitoring mNetworkMonitoring;

    private final MutableLiveData<Boolean> locationPermissionStatus = new MutableLiveData<>();
    private final MutableLiveData<Boolean> locationPermissionStatusNew = new MutableLiveData<>();

    private Location location;

    public Repository() {
    }

    /*
    public LiveData<Boolean> getLocationPermissionStatus() {
        return locationPermissionStatus;
    }

    public void setLocationPermissionStatus(boolean locationPermissionStatus) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            this.locationPermissionStatus.setValue(locationPermissionStatus);
        } else {
            this.locationPermissionStatus.postValue(locationPermissionStatus);
        }
    }

    public MutableLiveData<Boolean> getLocationPermissionStatusNew() {
        return locationPermissionStatusNew;
    }

    public void setLocationPermissionStatusNew(boolean status) {
        locationPermissionStatusNew.postValue(status);
    }
     */

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
