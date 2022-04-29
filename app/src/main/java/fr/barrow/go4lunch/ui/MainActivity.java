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

import fr.barrow.go4lunch.R;
import fr.barrow.go4lunch.databinding.ActivityMainBinding;
import fr.barrow.go4lunch.databinding.ActivityMainNavHeaderBinding;
import fr.barrow.go4lunch.utils.MyViewModelFactory;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    private ActivityMainBinding binding;
    private ActivityMainNavHeaderBinding navViewHeaderBinding;
    private AppBarConfiguration mAppBarConfiguration;
    private NavController mNavController;

    private MyViewModel mMyViewModel;

    private final Observer<Boolean> activeNetworkStateObserver = new Observer<Boolean>() {
        @Override
        public void onChanged(Boolean isConnected) {
            showConnectionToast(isConnected);
        }
    };

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

    private void initTest() {
        mMyViewModel.getUserNew().observe(this, user -> {
            System.out.println("OOO" + user.getPickedRestaurant());
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

            if(user.getPhotoUrl() != null){
                setProfilePicture(user.getPhotoUrl());
            }
            setTextUserData(user);
        }
    }

    private void setProfilePicture(Uri profilePictureUrl){
        Glide.with(this)
                .load(profilePictureUrl)
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
        mNavController = navHostFragment.getNavController();
    }

    private void initBottomNavigationView() {
        NavigationUI.setupWithNavController(binding.bottomNavViewMainNav, mNavController);

    }

    private void initToolbar() {
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

        }
        if (id==R.id.mainMenuDrawer_settings) {

        }
        if (id==R.id.mainMenuDrawer_logout) {
            mMyViewModel.signOut(this).addOnSuccessListener(aVoid -> {
                startActivity(new Intent(this, LoginActivity.class));
                finish(); });
        }
        //This is for maintaining the behavior of the Navigation view
        NavigationUI.onNavDestinationSelected(item, mNavController);
        //This is for closing the drawer after acting on it
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