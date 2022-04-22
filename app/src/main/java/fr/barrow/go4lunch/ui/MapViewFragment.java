package fr.barrow.go4lunch.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

import fr.barrow.go4lunch.R;
import fr.barrow.go4lunch.databinding.ActivityLoginBinding;
import fr.barrow.go4lunch.databinding.FragmentMapViewBinding;
import fr.barrow.go4lunch.model.Restaurant;
import fr.barrow.go4lunch.model.placedetails.PlaceDetailsList;
import fr.barrow.go4lunch.utils.MyViewModelFactory;
import fr.barrow.go4lunch.utils.PlacesStreams;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import pub.devrel.easypermissions.AppSettingsDialog;


public class MapViewFragment extends Fragment implements GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, OnMapReadyCallback {

    private FragmentMapViewBinding binding;
    private GoogleMap map;
    private FusedLocationProviderClient fusedLocationClient;
    private boolean permissionDenied = false;
    private String apiKey;
    private Disposable disposable;

    private MyViewModel mMyViewModel;
    private MutableLiveData<ArrayList<Restaurant>> mRestaurantList;

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
        initDataChangeObserve();
        executeHttpRequestWithRetrofit();
    }

    public void initDataChangeObserve() {
        mRestaurantList = mMyViewModel.getRestaurants();
        mRestaurantList.observe(requireActivity(), list -> {
            for (Restaurant r : list) {
                map.addMarker(new MarkerOptions()
                        .position(r.getPosition())
                        .title(r.getName()));
            }
        });
    }

    public void configureViewModel() {
        mMyViewModel = new ViewModelProvider(this, MyViewModelFactory.getInstance(requireActivity())).get(MyViewModel.class);
    }

    private void executeHttpRequestWithRetrofit(){
        this.disposable = PlacesStreams.streamFetchNearbyPlacesAndFetchFirstPlaceDetails(apiKey).subscribeWith(new DisposableObserver<PlaceDetailsList>() {
            @Override
            public void onNext(PlaceDetailsList placeDetailsList) {
                mMyViewModel.addRestaurant(mMyViewModel.placeDetailsToRestaurantObject(placeDetailsList));
                Log.e("TAG","On Next");
            }

            @Override
            public void onError(Throwable e) {
                Log.e("TAG","On Error"+Log.getStackTraceString(e));
            }

            @Override
            public void onComplete() {
                Log.e("TAG","On Complete !!");
            }
        });
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
        binding.fabBackToCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoomOnLocation();
            }
        });
    }

    private ActivityResultLauncher<String> locationPermissionRequest =
            registerForActivityResult(new ActivityResultContracts
                            .RequestPermission(), isGranted -> {
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
                fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            com.google.android.gms.maps.model.LatLng myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(myLatLng, 18);
                            map.animateCamera(cameraUpdate);
                        }
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
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setOnMyLocationClickListener(this);
        enableMyLocation();
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
}