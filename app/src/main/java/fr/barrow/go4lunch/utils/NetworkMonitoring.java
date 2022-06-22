package fr.barrow.go4lunch.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;

import androidx.annotation.NonNull;

public class NetworkMonitoring extends ConnectivityManager.NetworkCallback {

    private static final String TAG = NetworkMonitoring.class.getSimpleName();

    private final NetworkRequest mNetworkRequest;
    private final ConnectivityManager mConnectivityManager;
    private final NetworkStateManager mNetworkStateManager;
    private boolean registeringStatus;

    public NetworkMonitoring(Context context) {
        mNetworkRequest = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build();

        mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        mNetworkStateManager = NetworkStateManager.getInstance();
    }

    @Override
    public void onAvailable(@NonNull Network network) {
        super.onAvailable(network);
        mNetworkStateManager.setNetworkConnectivityStatus(true);
    }

    @Override
    public void onLost(@NonNull Network network) {
        super.onLost(network);
        mNetworkStateManager.setNetworkConnectivityStatus(false);
    }

    public void registerNetworkCallbackEvents() {
        if (!registeringStatus) {
            mConnectivityManager.registerNetworkCallback(mNetworkRequest, this);
            registeringStatus = true;
        }
    }

    @SuppressWarnings("deprecation")
    public void checkNetworkState() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                Network network = mConnectivityManager.getActiveNetwork();
                NetworkCapabilities actNw = mConnectivityManager.getNetworkCapabilities(network);
                mNetworkStateManager.setNetworkConnectivityStatus(network != null
                        && actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        } else {
            try {
                android.net.NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
                mNetworkStateManager.setNetworkConnectivityStatus(networkInfo != null
                        && networkInfo.isConnected());
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
}
