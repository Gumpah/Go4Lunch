package fr.barrow.go4lunch.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
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
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import fr.barrow.go4lunch.R;
import fr.barrow.go4lunch.databinding.FragmentMapViewBinding;
import fr.barrow.go4lunch.model.Restaurant;
import fr.barrow.go4lunch.model.UserStateItem;
import fr.barrow.go4lunch.utils.MyViewModelFactory;
import io.reactivex.disposables.Disposable;
import pub.devrel.easypermissions.AppSettingsDialog;


public class MapViewFragment extends Fragment implements GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private FragmentMapViewBinding binding;
    private GoogleMap map;
    private FusedLocationProviderClient fusedLocationClient;
    private boolean permissionDenied = false;
    private String apiKey;
    private Disposable disposable;
    private ArrayList<Restaurant> mRestaurants = new ArrayList<>();

    private MyViewModel mMyViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMapViewBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        apiKey = getString(R.string.MAPS_API_KEY);
        configureViewModel();
        initRestaurantsData();
        setLocation();
    }

    private void initRestaurantsData() {
        mMyViewModel.getRestaurantsMutableLiveData().observe(requireActivity(), restaurants -> {
            if (restaurants != null) {
                mRestaurants.clear();
                mRestaurants.addAll(restaurants);
                if (!restaurants.isEmpty()) {
                    initSearchUsersWhoPickedRestaurantFromArray(mRestaurants);
                }
            }
        });
    }

    private void initSearchUsersWhoPickedRestaurantFromArray(ArrayList<Restaurant> restaurants) {
        if (restaurants != null && !restaurants.isEmpty()) {
            mMyViewModel.getUsersWhoPickedARestaurant().observe(requireActivity(), users -> {
                ArrayList<String> pickedRestaurantIdsList = new ArrayList<>();
                if (users != null && !users.isEmpty()) {
                    for (UserStateItem u : users) {
                        if (!mMyViewModel.getCurrentUser().getUid().equals(u.getUid())) pickedRestaurantIdsList.add(u.getPickedRestaurant());
                    }
                }
                map.clear();
                for (Restaurant r : restaurants) {
                    boolean picked = pickedRestaurantIdsList.contains(r.getId());
                    map.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromBitmap(getBitmapIcon(picked)))
                            .position(r.getPosition())
                            .title(r.getName())).setTag(r.getId());
                }
            });
        }
    }

    private Bitmap getBitmapIcon(boolean picked) {
        int height = 150;
        int width = 150;
        BitmapDrawable bitmapDrawable;
        if (picked) {
            bitmapDrawable = (BitmapDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.location_lunch_icon_green, null);
        } else {
            bitmapDrawable = (BitmapDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.location_lunch_icon, null);
        }
        Bitmap b = bitmapDrawable.getBitmap();
        return Bitmap.createScaledBitmap(b, width, height, false);
    }

    private void setupDataRequest() {
        if (mMyViewModel.getLocationString() != null) {
            mMyViewModel.fetchAndUpdateRestaurants(mMyViewModel.getLocationString(), disposable, apiKey);
        }
    }

    public void configureViewModel() {
        mMyViewModel = new ViewModelProvider(this, MyViewModelFactory.getInstance(requireActivity())).get(MyViewModel.class);
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (map != null) {
                map.setMyLocationEnabled(true);
                zoomOnLocation();
                initLocationButton();
            }
        } else {
            locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void initLocationButton() {
        binding.fabBackToCamera.setOnClickListener(v -> zoomOnLocation());
    }

    private ActivityResultLauncher<String> locationPermissionRequest =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                        if (isGranted) { // permission is granted
                            enableMyLocation();
                        } else {
                            permissionDenied = true;
                        }
                    }
            );

    private void showMissingPermissionError() {
        new AppSettingsDialog.Builder(requireActivity()).build().show();
    }

    private void zoomOnLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (map != null) {
                fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
                    if (location != null) {
                        LatLng myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(myLatLng, 18);
                        map.animateCamera(cameraUpdate);
                    }
                });
            }
        } else {
            locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void setLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (map != null) {
                fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
                    if (location != null) {
                        mMyViewModel.setLocation(location);
                        setupDataRequest();
                    }
                });
            }
        } else {
            locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (permissionDenied) {
            showMissingPermissionError();
            permissionDenied = false;
        }
        mMyViewModel.getUsersWhoPickedARestaurant();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setOnMyLocationClickListener(this);
        map.setOnInfoWindowClickListener(this);
        enableMyLocation();
        setLocation();
    }


    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(requireActivity(), "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(requireActivity(), "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    private void disposeWhenDestroy(){
        if (this.disposable != null && !this.disposable.isDisposed()) this.disposable.dispose();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.disposeWhenDestroy();
    }

    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {
        String restaurantId = marker.getTag().toString();
        Intent intent = new Intent(requireContext(), RestaurantDetailsActivity.class);
        intent.putExtra("RESTAURANT_ID", restaurantId);
        requireContext().startActivity(intent);
    }


}