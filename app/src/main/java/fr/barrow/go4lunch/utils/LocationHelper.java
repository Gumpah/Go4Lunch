package fr.barrow.go4lunch.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;

import fr.barrow.go4lunch.data.Repository;
import fr.barrow.go4lunch.ui.MapViewFragment;

public class LocationHelper {

    /*
    private MapViewFragment mActivity;
    private Context mContext;
    private Repository mRepository;

    public LocationHelper(MapViewFragment activity, Context context) {
        mActivity = activity;
        mContext = context;
        mRepository = new Repository(context);
    }

    private ActivityResultLauncher<String> locationPermissionRequest =
            mActivity.registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                            mRepository.setLocationPermissionStatusNew(isGranted);
                    }
            );

    public boolean enableMyLocation() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            return false;
        }
    }

 */
}
