package fr.barrow.go4lunch.data;

import android.content.Context;

import androidx.lifecycle.LiveData;

import fr.barrow.go4lunch.model.NetworkStateManager;
import fr.barrow.go4lunch.utils.NetworkMonitoring;

public class Repository {

    public NetworkMonitoring mNetworkMonitoring;

    public Repository(Context applicationContext) {
        mNetworkMonitoring = new NetworkMonitoring(applicationContext);
        mNetworkMonitoring.checkNetworkState();
        mNetworkMonitoring.registerNetworkCallbackEvents();
    }

    public LiveData<Boolean> getConnectionStatus() {
        return NetworkStateManager.getInstance().getNetworkConnectivityStatus();
    }

}
