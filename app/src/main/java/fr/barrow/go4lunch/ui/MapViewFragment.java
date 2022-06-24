package fr.barrow.go4lunch.ui;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
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

import fr.barrow.go4lunch.BuildConfig;
import fr.barrow.go4lunch.R;
import fr.barrow.go4lunch.databinding.FragmentMapViewBinding;
import fr.barrow.go4lunch.data.model.MarkerAndRestaurantId;
import fr.barrow.go4lunch.data.model.Restaurant;
import fr.barrow.go4lunch.data.model.UserStateItem;
import fr.barrow.go4lunch.ui.restaurantdetails.RestaurantDetailsActivity;
import fr.barrow.go4lunch.ui.viewmodels.RestaurantViewModel;
import fr.barrow.go4lunch.ui.viewmodels.UserViewModel;
import fr.barrow.go4lunch.ui.viewmodels.RestaurantViewModelFactory;
import fr.barrow.go4lunch.ui.viewmodels.UserViewModelFactory;
import pub.devrel.easypermissions.AppSettingsDialog;


public class MapViewFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, SearchView.OnQueryTextListener {

    private FragmentMapViewBinding binding;
    private GoogleMap map;
    private FusedLocationProviderClient fusedLocationClient;
    private String apiKey;
    private ArrayList<Restaurant> mRestaurants = new ArrayList<>();

    final String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    private RestaurantViewModel mRestaurantViewModel;
    private UserViewModel mUserViewModel;

    private ArrayList<MarkerAndRestaurantId> mMarkerAndRestaurantIdList;
    private ArrayList<String> mPickedRestaurantIdsList;

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
        apiKey = BuildConfig.MAPS_API_KEY;
        configureViewModels();
        initRestaurantsData();
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search_menu, menu);
        SearchManager searchManager =
                (SearchManager) requireActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(requireActivity().getComponentName()));
        searchView.setQueryHint(requireActivity().getString(R.string.search_restaurants));
        searchView.setOnQueryTextListener(this);
        searchView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                clearMapAndSetNewMarkers(mMarkerAndRestaurantIdList);
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void initRestaurantsData() {
        mRestaurantViewModel.getRestaurantsMutableLiveData().observe(getViewLifecycleOwner(), restaurants -> {
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
            mUserViewModel.getEveryUserWhoPickedARestaurant().observe(requireActivity(), users -> {
                mPickedRestaurantIdsList = new ArrayList<>();
                if (users != null && !users.isEmpty()) {
                    for (UserStateItem u : users) {
                        mPickedRestaurantIdsList.add(u.getPickedRestaurant());
                    }
                }
                if (map != null) {
                    createMarkerList(restaurants, false);
                }
            });
        }
    }

    private void createMarkerList(ArrayList<Restaurant> restaurants, boolean isInSearchMode) {
        if (restaurants == null) return;
        if (isInSearchMode) {
            ArrayList<MarkerAndRestaurantId> markerAndRestaurantIdList = new ArrayList<>();
            ArrayList<String> restaurantIdList = new ArrayList<>();
            for (Restaurant r : restaurants) {
                restaurantIdList.add(r.getId());
            }
            for (MarkerAndRestaurantId mr : mMarkerAndRestaurantIdList) {
                if (restaurantIdList.contains(mr.getRestaurantId())) markerAndRestaurantIdList.add(mr);
            }
            clearMapAndSetNewMarkers(markerAndRestaurantIdList);
        } else {
            mMarkerAndRestaurantIdList = new ArrayList<>();
            for (Restaurant r : restaurants) {
                boolean picked = mPickedRestaurantIdsList.contains(r.getId());
                MarkerOptions marker = new MarkerOptions()
                        .position(r.getPosition())
                        .title(r.getName());

                Bitmap bitmap = getBitmapIcon(picked);
                if (bitmap != null) marker.icon(BitmapDescriptorFactory.fromBitmap(bitmap));

                mMarkerAndRestaurantIdList.add(new MarkerAndRestaurantId(marker, r.getId()));
            }
            clearMapAndSetNewMarkers(mMarkerAndRestaurantIdList);
        }

    }

    private void clearMapAndSetNewMarkers(ArrayList<MarkerAndRestaurantId> markerAndRestaurantIdList) {
        map.clear();
        if (markerAndRestaurantIdList != null) {
            for (MarkerAndRestaurantId mr : markerAndRestaurantIdList) {
                if (mr.getMarker() != null && mr.getRestaurantId() != null) {
                    map.addMarker(mr.getMarker()).setTag(mr.getRestaurantId());
                }
            }
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
        if (bitmapDrawable != null) {
            Bitmap b = bitmapDrawable.getBitmap();
            return Bitmap.createScaledBitmap(b, width, height, false);
        } else {
            return null;
        }
    }

    private void fetchingRestaurantsDataRequest() {
        if (mRestaurantViewModel.getLocation() != null && mRestaurants.isEmpty()) {
            mRestaurantViewModel.fetchAndUpdateRestaurants(apiKey);
        }
    }

    public void configureViewModels() {
        mRestaurantViewModel = new ViewModelProvider(requireActivity(), RestaurantViewModelFactory.getInstance()).get(RestaurantViewModel.class);
        mUserViewModel = new ViewModelProvider(requireActivity(), UserViewModelFactory.getInstance(requireContext())).get(UserViewModel.class);
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (map != null) {
                map.setMyLocationEnabled(true);
                zoomOnLocation();
                initLocationButton();
            }
        }
    }

    private void initLocationButton() {
        binding.fabBackToCamera.setOnClickListener(v -> zoomOnLocation());
    }

    private final ActivityResultLauncher<String[]> locationPermissionRequest =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), isGranted -> {
                        if (isGranted.containsValue(true)) {
                            setLocation();
                        } else if (isGranted.containsValue(false)){
                            showMissingPermissionError();
                        }
                    }
            );

    private void showMissingPermissionError() {
        new AppSettingsDialog.Builder(requireActivity())
                .setThemeResId(R.style.AlertDialog)
                .setTitle(R.string.location_permission_denied_dialog_title)
                .setRationale(R.string.location_permission_denied_dialog_message)
                .build()
                .show();
    }

    private void zoomOnLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (map != null) {
                fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
                    if (location != null) {
                        LatLng myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(myLatLng, 18);
                        map.animateCamera(cameraUpdate);
                    }
                });
            }
        }
    }

    private void setLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (map != null) {
                fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
                    if (location != null) {
                        mRestaurantViewModel.setLocation(location);
                        fetchingRestaurantsDataRequest();
                        enableMyLocation();
                    }
                });
            }
        } else {
            locationPermissionRequest.launch(PERMISSIONS);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mUserViewModel.getEveryUserWhoPickedARestaurant();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setOnInfoWindowClickListener(this);
        setLocation();
    }

    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {
        if (marker.getTag() != null) {
            String restaurantId = marker.getTag().toString();
            if (mRestaurantViewModel.getRestaurantFromId(restaurantId) == null) {
                mRestaurantViewModel.fetchRestaurantDetailsAndAddRestaurant(apiKey, restaurantId);
                mRestaurantViewModel.getRestaurantsMutableLiveData().observe(this, list -> {
                    if (mRestaurantViewModel.getRestaurantFromId(restaurantId) != null) {
                        Restaurant mRestaurant = mRestaurantViewModel.getRestaurantFromId(restaurantId);
                        startDetailsActivity(mRestaurant);
                    }
                });
            } else {
                Restaurant mRestaurant = mRestaurantViewModel.getRestaurantFromId(restaurantId);
                startDetailsActivity(mRestaurant);
            }
        }
    }

    private void startDetailsActivity(Restaurant restaurant) {
        Intent intent = new Intent(requireContext(), RestaurantDetailsActivity.class);
        intent.putExtra("RESTAURANT", restaurant);
        requireContext().startActivity(intent);
    }

    private void filter(String text) {
        ArrayList<Restaurant> restaurants = new ArrayList<>();
        if (mRestaurants != null && !mRestaurants.isEmpty()) {
            if(text.isEmpty()){
                clearMapAndSetNewMarkers(mMarkerAndRestaurantIdList);
            } else {
                text = text.toLowerCase();
                for(Restaurant restaurant: mRestaurants){
                    if(restaurant.getName().toLowerCase().contains(text)){
                        restaurants.add(restaurant);
                    }
                }
                createMarkerList(restaurants, true);
            }
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        filter(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        filter(newText);
        return false;
    }
}