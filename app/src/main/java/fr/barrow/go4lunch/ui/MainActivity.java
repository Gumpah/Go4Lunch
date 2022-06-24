package fr.barrow.go4lunch.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseUser;

import fr.barrow.go4lunch.BuildConfig;
import fr.barrow.go4lunch.R;
import fr.barrow.go4lunch.databinding.ActivityMainBinding;
import fr.barrow.go4lunch.databinding.ActivityMainNavHeaderBinding;
import fr.barrow.go4lunch.data.model.Restaurant;
import fr.barrow.go4lunch.ui.restaurantdetails.RestaurantDetailsActivity;
import fr.barrow.go4lunch.ui.viewmodels.RestaurantViewModel;
import fr.barrow.go4lunch.ui.viewmodels.UserViewModel;
import fr.barrow.go4lunch.ui.viewmodels.RestaurantViewModelFactory;
import fr.barrow.go4lunch.ui.viewmodels.UserViewModelFactory;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityMainBinding binding;
    private ActivityMainNavHeaderBinding navViewHeaderBinding;
    private AppBarConfiguration mAppBarConfiguration;
    private NavController mNavController;

    private RestaurantViewModel mRestaurantViewModel;
    private UserViewModel mUserViewModel;

    private String restaurantId;
    private String apiKey;


    private final Observer<Boolean> activeNetworkStateObserver = this::showConnectionToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureViewModels();
        apiKey = BuildConfig.MAPS_API_KEY;
        initUI();
        initAppBarConfiguration();
        initNavController();
        initToolbar();
        initDrawer();
        initBottomNavigationView();
        updateUIWithAuthenticationUserData();
        initNetworkStatus();
        mUserViewModel.updateLocalUserData();
        initPickedRestaurantFromFirestore();
        initToastObservers();
    }

    private void initToastObservers() {
        mRestaurantViewModel.getToastSenderInteger().observe(this, message -> {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        });
        mRestaurantViewModel.getToastSenderString().observe(this, message -> {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        });
    }

    private void initPickedRestaurantFromFirestore() {
        mUserViewModel.getUpdatedLocalUserData().observe(this, user -> {
            if (user != null) {
                restaurantId = user.getPickedRestaurant();
            }
        });
    }

    private void configureViewModels() {
        mRestaurantViewModel = new ViewModelProvider(this, RestaurantViewModelFactory.getInstance()).get(RestaurantViewModel.class);
        mUserViewModel = new ViewModelProvider(this, UserViewModelFactory.getInstance(this)).get(UserViewModel.class);
    }

    private void initNetworkStatus() {
        mUserViewModel.getConnectionStatus().observe(this, activeNetworkStateObserver);
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

    private void updateUIWithAuthenticationUserData(){
        if(mUserViewModel.isCurrentUserLogged()){
            FirebaseUser user = mUserViewModel.getCurrentUser();
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
                getRestaurant(restaurantId);
            } else {
                Toast.makeText(this, R.string.no_restaurant_chosen, Toast.LENGTH_SHORT).show();
            }
        }
        if (id==R.id.mainMenuDrawer_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);

        }
        if (id==R.id.mainMenuDrawer_logout) {
            mUserViewModel.signOut(this).addOnSuccessListener(aVoid -> {
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

    public void getRestaurant(String restaurantId) {
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

    private void startDetailsActivity(Restaurant restaurant) {
        Intent intent = new Intent(this, RestaurantDetailsActivity.class);
        intent.putExtra("RESTAURANT", restaurant);
        startActivity(intent);
    }
}