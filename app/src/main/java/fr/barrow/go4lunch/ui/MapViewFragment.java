package fr.barrow.go4lunch.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.model.Circle;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.OnCameraTrackingChangedListener;
import com.mapbox.mapboxsdk.location.OnLocationCameraTransitionListener;
import com.mapbox.mapboxsdk.location.OnLocationClickListener;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.CircleManager;
import com.mapbox.mapboxsdk.plugins.annotation.CircleOptions;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;

import java.util.List;

import fr.barrow.go4lunch.R;
import fr.barrow.go4lunch.databinding.FragmentMapViewBinding;
import fr.barrow.go4lunch.model.places.Place;
import fr.barrow.go4lunch.utils.PlacesCalls;
import pub.devrel.easypermissions.AppSettingsDialog;


public class MapViewFragment extends Fragment implements OnMapReadyCallback, OnLocationClickListener, OnCameraTrackingChangedListener, PlacesCalls.Callbacks {

    private FragmentMapViewBinding binding;
    private MapView mapView;
    private MapboxMap mMapboxMap;
    private LocationComponent locationComponent;
    private boolean isInTrackingMode;
    private String apiKey;
    private SymbolManager symbolManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Mapbox.getInstance(getActivity(), getString(R.string.mapbox_access_token));
        binding = FragmentMapViewBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        mapSetup(savedInstanceState);
        initButtonListener();
        apiKey = getString(R.string.MAPS_API_KEY);
        return view;
    }

    private void executeHttpRequest() {
        PlacesCalls.fetchNearbyPlaces(this, apiKey);
    }

    private void initLocation() {
        mMapboxMap.setStyle(Style.MAPBOX_STREETS,
                this::enableLocationComponent);
    }

    private void initButtonListener() {
        binding.fabBackToCamera.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(getActivity(), R.string.location_permission_refused, Toast.LENGTH_SHORT).show();
            } else {
                initLocation();
                setInTrackingMode();
            }
        });
    }

    private ActivityResultLauncher<String> locationPermissionRequest =
            registerForActivityResult(new ActivityResultContracts
                            .RequestPermission(), isGranted -> {
                if (isGranted) { // permission is granted
                    initLocation();
                } else {
                    new AppSettingsDialog.Builder(requireActivity()).build().show();
                }
            }
            );

    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            initLocation();
        }
    }

    private void mapSetup(Bundle savedInstanceState){
        mapView = binding.mapView;
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        mMapboxMap = mapboxMap;
        initLocation();
    }

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            executeHttpRequest();
            locationComponent = mMapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(LocationComponentActivationOptions.builder(requireActivity(), loadedMapStyle).build());
            locationComponent.setLocationComponentEnabled(true);
            setInTrackingMode();
            locationComponent.setRenderMode(RenderMode.COMPASS);
            locationComponent.addOnLocationClickListener(this);
            locationComponent.addOnCameraTrackingChangedListener(this);
            loadedMapStyle.addImage("marker-icon",getResources().getDrawable(R.drawable.ic_baseline_location_on_24));
            symbolManager = new SymbolManager(mapView, mMapboxMap, loadedMapStyle);
            symbolManager.setIconAllowOverlap(true);
            symbolManager.setTextAllowOverlap(true);
        } else {
            requestPermissions();
        }
    }

    private void setInTrackingMode() {
        if (!isInTrackingMode) {
            locationComponent = mMapboxMap.getLocationComponent();
            locationComponent.setCameraMode(CameraMode.TRACKING_GPS_NORTH, new OnLocationCameraTransitionListener() {
                @Override
                public void onLocationCameraTransitionFinished(int cameraMode) {
                    isInTrackingMode = true;
                    locationComponent.zoomWhileTracking(16f);
                }
                @Override
                public void onLocationCameraTransitionCanceled(int cameraMode) {
                    isInTrackingMode = false;
                }
            });
            Toast.makeText(getActivity(), R.string.tracking_enabled, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), R.string.tracking_already_enabled, Toast.LENGTH_SHORT).show();
        }
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

    @Override
    public void onResponse(@Nullable List<Place> placesList) {
        if(placesList != null) {
            for (int i = 0;i < placesList.size(); i++) {
                System.out.println(placesList.get(i).getGeometry().getLocation().getLat());
                symbolManager.create(new SymbolOptions()
                        .withLatLng(new LatLng(placesList.get(i).getGeometry().getLocation().getLat(), placesList.get(i).getGeometry().getLocation().getLng()))
                        .withIconImage("marker-icon")
                        .withIconSize(2f)
                        .withTextField(placesList.get(i).getName()));

            }
        }
    }

    @Override
    public void onFailure() {

    }
}