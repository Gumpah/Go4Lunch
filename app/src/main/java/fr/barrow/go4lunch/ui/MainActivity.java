package fr.barrow.go4lunch.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseUser;

import fr.barrow.go4lunch.R;
import fr.barrow.go4lunch.databinding.ActivityMainBinding;
import fr.barrow.go4lunch.databinding.ActivityMainNavHeaderBinding;
import fr.barrow.go4lunch.utils.MyViewModelFactory;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityMainBinding binding;
    private ActivityMainNavHeaderBinding navViewHeaderBinding;
    private AppBarConfiguration mAppBarConfiguration;
    private NavController mNavController;

    private MyViewModel mMyViewModel;
    private String restaurantId;


    private final Observer<Boolean> activeNetworkStateObserver = this::showConnectionToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureViewModel();
        initUI();
        initAppBarConfiguration();
        initNavController();
        initToolbar();
        initDrawer();
        initBottomNavigationView();
        updateUIWithUserData();
        initNetworkStatus();
        mMyViewModel.updateUserData();
        initTest();
    }

    private void getLocation() {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    mMyViewModel.setLocation(location);
                }
            });
        } else {
            locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private final ActivityResultLauncher<String> locationPermissionRequest =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                        if (isGranted) {
                            getLocation();
                        }
                    }
            );

    private void initTest() {
        mMyViewModel.getUserNew().observe(this, user -> {
            if (user != null) {
                restaurantId = user.getPickedRestaurant();
            }
        });
    }

    private void configureViewModel() {
        mMyViewModel = new ViewModelProvider(this, MyViewModelFactory.getInstance(this)).get(MyViewModel.class);
    }

    private void initNetworkStatus() {
        mMyViewModel.getConnectionStatus().observe(this, activeNetworkStateObserver);
    }

    private void showConnectionToast(boolean isConnected) {
        if (isConnected) {
            Toast.makeText(
                    this, R.string.internet_connected,
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            Toast.makeText(
                    this, R.string.internet_disconnected,
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void updateUIWithUserData(){
        if(mMyViewModel.isCurrentUserLogged()){
            FirebaseUser user = mMyViewModel.getCurrentUser();
            if (user != null) {
                if(user.getPhotoUrl() != null){
                    setProfilePicture(user.getPhotoUrl());
                } else {
                    setDefaultProfilePicture();
                }
                setTextUserData(user);
            }
        }
    }

    private void setProfilePicture(Uri profilePictureUrl){
        Glide.with(this)
                .load(profilePictureUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(navViewHeaderBinding.imageViewUserAvatar);
    }

    private void setDefaultProfilePicture() {
        Glide.with(this)
                .load(R.drawable.ic_person)
                .apply(RequestOptions.circleCropTransform())
                .into(navViewHeaderBinding.imageViewUserAvatar);
    }

    private void setTextUserData(FirebaseUser user){
        //Get email & username from User
        String email = TextUtils.isEmpty(user.getEmail()) ? getString(R.string.info_no_email_found) : user.getEmail();
        String username = TextUtils.isEmpty(user.getDisplayName()) ? getString(R.string.info_no_username_found) : user.getDisplayName();

        //Update views with data
        navViewHeaderBinding.textViewUserName.setText(username);
        navViewHeaderBinding.textViewUserMail.setText(email);
    }

    private void initAppBarConfiguration() {
        mAppBarConfiguration =
                new AppBarConfiguration.Builder(R.id.navigation_mapView, R.id.navigation_listView, R.id.navigation_workmates)
                        .setOpenableLayout(binding.drawerLayout)
                        .build();
    }

    private void initUI() {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        View viewHeader = binding.navigationViewSideMenu.getHeaderView(0);
        navViewHeaderBinding = ActivityMainNavHeaderBinding.bind(viewHeader);
    }

    private void initNavController() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_host_fragment);
        if (navHostFragment != null) mNavController = navHostFragment.getNavController();
    }

    private void initBottomNavigationView() {
        NavigationUI.setupWithNavController(binding.bottomNavViewMainNav, mNavController);

    }

    private void initToolbar() {
        setSupportActionBar(binding.activityMainToolbar);
        NavigationUI.setupWithNavController(binding.activityMainToolbar, mNavController, mAppBarConfiguration);
    }

    private void initDrawer() {
        NavigationUI.setupWithNavController(binding.navigationViewSideMenu, mNavController);
        binding.navigationViewSideMenu.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id=item.getItemId();
        if (id==R.id.mainMenuDrawer_lunch) {
            if (restaurantId != null) {
                Intent intent = new Intent(this, RestaurantDetailsActivity.class);
                intent.putExtra("RESTAURANT_ID", restaurantId);
                startActivity(intent);
            } else {
                Toast.makeText(this, R.string.no_restaurant_chosen, Toast.LENGTH_SHORT).show();
            }
        }
        if (id==R.id.mainMenuDrawer_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        if (id==R.id.mainMenuDrawer_logout) {
            mMyViewModel.signOut(this).addOnSuccessListener(aVoid -> {
                startActivity(new Intent(this, LoginActivity.class));
                finish(); });
        }
        NavigationUI.onNavDestinationSelected(item, mNavController);
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}