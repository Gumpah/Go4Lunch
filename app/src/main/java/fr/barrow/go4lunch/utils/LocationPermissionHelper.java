package fr.barrow.go4lunch.utils;

import android.app.Activity;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.maps.Style;

import java.lang.ref.WeakReference;
import java.util.List;

import fr.barrow.go4lunch.R;

public class LocationPermissionHelper extends AppCompatActivity {

    private WeakReference<Activity> mActivity;
    private LocationPermissionHelperInterface calling;

    public LocationPermissionHelper(WeakReference<Activity> activity, LocationPermissionHelperInterface locationPermissionHelperInterface) {
        mActivity = activity;
        calling = locationPermissionHelperInterface;
    }

    private PermissionsManager mPermissionsManager;

    public void checkPermissions(Style loadedMapstyle) {
        if (PermissionsManager.areLocationPermissionsGranted(mActivity.get())) {
            // Permission sensitive logic called here, such as activating the Maps SDK's LocationComponent to show the device's location
            calling.whenMapReady(loadedMapstyle);
        } else {
            mPermissionsManager = new PermissionsManager(new PermissionsListener() {
                @Override
                public void onExplanationNeeded(List<String> permissionsToExplain) {
                    Toast.makeText(
                            mActivity.get(), R.string.location_permission_refused,
                            Toast.LENGTH_SHORT
                    ).show();
                }

                @Override
                public void onPermissionResult(boolean granted) {
                    if (granted) {
                        calling.whenMapReady(loadedMapstyle);
                    } else {

                    }
                }
            });
            mPermissionsManager.requestLocationPermissions(mActivity.get());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPermissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
