package fr.barrow.go4lunch.ui;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseUser;

import fr.barrow.go4lunch.R;
import fr.barrow.go4lunch.databinding.ActivityMainBinding;
import fr.barrow.go4lunch.databinding.ActivityMainNavHeaderBinding;
import fr.barrow.go4lunch.ui.manager.UserManager;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ActivityMainNavHeaderBinding navViewHeaderBinding;
    private AppBarConfiguration mAppBarConfiguration;
    private NavController mNavController;
    private UserManager userManager = UserManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        initAppBarConfiguration();
        initNavController();
        initToolbar();
        initDrawer();
        initBottomNavigationView();
        updateUIWithUserData();
    }

    private void updateUIWithUserData(){
        if(userManager.isCurrentUserLogged()){
            FirebaseUser user = userManager.getCurrentUser();

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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return NavigationUI.onNavDestinationSelected(item, mNavController)
                || super.onOptionsItemSelected(item);
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