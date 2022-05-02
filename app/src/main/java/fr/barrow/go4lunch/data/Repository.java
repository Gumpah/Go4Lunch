package fr.barrow.go4lunch.data;

import android.content.Context;
import android.location.Location;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import fr.barrow.go4lunch.utils.NetworkStateManager;
import fr.barrow.go4lunch.utils.NetworkMonitoring;

public class Repository {

    public NetworkMonitoring mNetworkMonitoring;

    private final MutableLiveData<Boolean> locationPermissionStatus = new MutableLiveData<>();

    private Location location;

    public Repository(Context applicationContext) {
        mNetworkMonitoring = new NetworkMonitoring(applicationContext);
        mNetworkMonitoring.checkNetworkState();
        mNetworkMonitoring.registerNetworkCallbackEvents();
    }

    public LiveData<Boolean> getConnectionStatus() {
        return NetworkStateManager.getInstance().getNetworkConnectivityStatus();
    }

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

    public String getLocationString() {
        String locationString = (location.getLatitude() + "," + location.getLongitude());
        return locationString;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
