package fr.barrow.go4lunch.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.OnCameraTrackingChangedListener;
import com.mapbox.mapboxsdk.location.OnLocationClickListener;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;

import fr.barrow.go4lunch.R;
import fr.barrow.go4lunch.databinding.FragmentMapViewBinding;
import fr.barrow.go4lunch.utils.LocationPermissionHelper;
import fr.barrow.go4lunch.utils.LocationPermissionHelperInterface;


public class MapViewFragment extends Fragment implements LocationPermissionHelperInterface, OnMapReadyCallback, OnLocationClickListener, OnCameraTrackingChangedListener {

    private FragmentMapViewBinding binding;
    private PermissionsManager permissionsManager;
    private LocationPermissionHelper locationPermissionHelper;
    private MapView mapView;
    private MapboxMap mMapboxMap;
    private LocationComponent locationComponent;
    private boolean isInTrackingMode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Mapbox.getInstance(getActivity(), getString(R.string.mapbox_access_token));
        binding = FragmentMapViewBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        mapSetup(savedInstanceState);
        return view;
    }

    private void mapSetup(Bundle savedInstanceState){
        mapView = binding.mapView;
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        locationPermissionHelper = new LocationPermissionHelper(new WeakReference<Activity>(getActivity()), this);

    }

    @Override
    public void onMapReady(@NonNull @NotNull MapboxMap mapboxMap) {
        mMapboxMap = mapboxMap;
    mMapboxMap.setStyle(Style.LIGHT, new Style.OnStyleLoaded() {
        @Override
        public void onStyleLoaded(@NonNull @NotNull Style style) {
            enableLocationComponent(style);
        }
    });
    }

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        locationPermissionHelper.checkPermissions(loadedMapStyle);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void whenMapReady(Style loadedMapstyle) {
        LocationComponentOptions customLocationComponentOptions = LocationComponentOptions.builder(getActivity())
                .elevation(5)
                .accuracyAlpha(.6f)
                .accuracyColor(Color.RED)
                .foregroundDrawable(R.drawable.ic_baseline_location_on_24)
                .build();

        locationComponent = mMapboxMap.getLocationComponent();

        LocationComponentActivationOptions locationComponentActivationOptions =
                LocationComponentActivationOptions.builder(getActivity(), loadedMapstyle)
                        .locationComponentOptions(customLocationComponentOptions)
                        .build();

        locationComponent.activateLocationComponent(locationComponentActivationOptions);

        locationComponent.setLocationComponentEnabled(true);

        locationComponent.setCameraMode(CameraMode.TRACKING);

        locationComponent.setRenderMode(RenderMode.COMPASS);

        locationComponent.addOnLocationClickListener(this);

        locationComponent.addOnCameraTrackingChangedListener(this);

        binding.fabBackToCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isInTrackingMode) {
                    isInTrackingMode = true;
                    locationComponent.setCameraMode(CameraMode.TRACKING);
                    locationComponent.zoomWhileTracking(16f);
                    Toast.makeText(getActivity(), R.string.tracking_enabled,
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), R.string.tracking_already_enabled,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @SuppressWarnings( {"MissingPermission"})
    @Override
    public void onLocationComponentClick() {
        if (locationComponent.getLastKnownLocation() != null) {
            Toast.makeText(getActivity(), String.format(getResources().getString(R.string.current_location),
                    locationComponent.getLastKnownLocation().getLatitude(),
                    locationComponent.getLastKnownLocation().getLongitude()), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onCameraTrackingDismissed() {
        isInTrackingMode = false;
    }

    @Override
    public void onCameraTrackingChanged(int currentMode) {

    }

    @SuppressWarnings( {"MissingPermission"})
    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapView.onDestroy();
        binding = null;
    }
}