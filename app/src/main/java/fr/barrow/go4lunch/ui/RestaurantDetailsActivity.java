package fr.barrow.go4lunch.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import fr.barrow.go4lunch.R;
import fr.barrow.go4lunch.databinding.ActivityRestaurantDetailsBinding;
import fr.barrow.go4lunch.model.Restaurant;
import fr.barrow.go4lunch.model.UserStateItem;
import fr.barrow.go4lunch.ui.viewmodels.UserViewModel;
import fr.barrow.go4lunch.utils.viewmodelsfactories.UserViewModelFactory;

public class RestaurantDetailsActivity extends AppCompatActivity {

    private ActivityRestaurantDetailsBinding binding;

    //private MyViewModel mMyViewModel;
    private UserViewModel mUserViewModel;

    private Restaurant mRestaurant;
    private UserStateItem mUser = new UserStateItem();
    private RestaurantDetailsWorkmatesAdapter adapter;

    private boolean isUIInit;
    private boolean restaurantPick;
    private boolean restaurantLike;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRestaurantDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        isUIInit = false;
        setContentView(view);
        configureViewModel();
        getRestaurant();
    }

    private void configureViewModel() {
        //mMyViewModel = new ViewModelProvider(this, MyViewModelFactory.getInstance(this)).get(MyViewModel.class);
        mUserViewModel = new ViewModelProvider(this, UserViewModelFactory.getInstance(this)).get(UserViewModel.class);
    }

    private void initRecyclerView(View view) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        binding.recyclerview.setLayoutManager(layoutManager);
        adapter = new RestaurantDetailsWorkmatesAdapter(new ArrayList<>());
        binding.recyclerview.setAdapter(adapter);
        initUsersList();
    }

    private void getRestaurant() {
        Intent intent = getIntent();
        if (intent != null){
            Restaurant restaurant = intent.getParcelableExtra("RESTAURANT");
            if (restaurant != null){
                mRestaurant = restaurant;
                initUserData();
                mUserViewModel.updateLocalUserData();
                initUI();
                initRecyclerView(binding.getRoot());
            }
        }
    }

    private void initUserData() {
        mUser = mUserViewModel.getUpdatedLocalUserData().getValue();
        mUserViewModel.getUpdatedLocalUserData().observe(this, user -> {
            mUser = user;
            mUserViewModel.getEveryFirestoreUserWhoPickedThisRestaurant(mRestaurant.getId());
            binding.fabChooseRestaurant.setSelected(restaurantPick);
            binding.imageButtonLikeRestaurant.setSelected(restaurantLike);
            if (!isUIInit) {
                restaurantPick =  mRestaurant.getId().equals(mUser.getPickedRestaurant());
                restaurantLike =  mUser.getLikedRestaurants().contains(mRestaurant.getId());
                binding.fabChooseRestaurant.setSelected(restaurantPick);
                binding.imageButtonLikeRestaurant.setSelected(restaurantLike);
                initPickRestaurantButton();
                initLikeRestaurantButton();
            }
        });
    }

    private void initUsersList() {
        mUserViewModel.getEveryFirestoreUserWhoPickedThisRestaurant(mRestaurant.getId()).observe(this, users -> {
            adapter.setData(users);
            initLineVisibility(users);
        });
    }

    private void initLineVisibility(List<UserStateItem> users) {
        if (users.isEmpty()) {
            binding.viewLineSeparator.setVisibility(View.INVISIBLE);
        } else {
            binding.viewLineSeparator.setVisibility(View.VISIBLE);
        }
    }

    private void initUI() {
        initRestaurantInfo();
        setupStarsRating();
        initWebsiteButton();
        initCallButton();
    }

    private void initPickRestaurantButton() {
        isUIInit = true;
        binding.fabChooseRestaurant.setOnClickListener(view -> {
            if (restaurantPick) {
                mUserViewModel.removePickedRestaurant();
                restaurantPick = false;
            } else {
                mUserViewModel.setPickedRestaurant(mRestaurant.getId(), mRestaurant.getName());
                restaurantPick = true;
            }
            mUserViewModel.getUpdatedLocalUserData();
        });
    }


    private void initLikeRestaurantButton() {
        binding.imageButtonLikeRestaurant.setOnClickListener(view -> {
            if (restaurantLike) {
                mUserViewModel.removeLikedRestaurant(mRestaurant.getId());
                restaurantLike = false;
            } else {
                mUserViewModel.addLikedRestaurant(mRestaurant.getId());
                restaurantLike = true;
            }
            mUserViewModel.getUpdatedLocalUserData();
        });
    }

    private void initWebsiteButton() {
        String website = mRestaurant.getWebsite();
        binding.imageButtonWebsiteRestaurant.setOnClickListener(v -> {
            if (website != null) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(website));
                startActivity(i);
            } else {
                Toast.makeText(binding.getRoot().getContext(), R.string.toast_noWebsite, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initCallButton() {
        String phoneNumber = mRestaurant.getPhoneNumber();
        binding.imageButtonCallRestaurant.setOnClickListener(v -> {
            if (phoneNumber != null) {
                Intent i = new Intent(Intent.ACTION_DIAL);
                i.setData(Uri.parse("tel:" + phoneNumber));
                startActivity(i);
            } else {
                Toast.makeText(binding.getRoot().getContext(), R.string.toast_noPhoneNumber, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initRestaurantInfo() {
        binding.textViewRestaurantName.setText(mRestaurant.getName());
        binding.textViewRestaurantTypeAndAddress.setText(mRestaurant.getAddress());
        Glide.with(binding.getRoot())
                .load(mRestaurant.getUrlPicture())
                .centerCrop()
                .error(R.drawable.backgroundblurred)
                .into(binding.imageViewRestaurantPhoto);
    }

    private void setupStarsRating () {
        switch (mRestaurant.getRating()) {
            case 1:
                binding.imageViewStar1.setVisibility(View.VISIBLE);
                break;
            case 2:
                binding.imageViewStar1.setVisibility(View.VISIBLE);
                binding.imageViewStar2.setVisibility(View.VISIBLE);
                break;
            case 3:
                binding.imageViewStar1.setVisibility(View.VISIBLE);
                binding.imageViewStar2.setVisibility(View.VISIBLE);
                binding.imageViewStar3.setVisibility(View.VISIBLE);
                break;
            default:
                binding.imageViewStar1.setVisibility(View.INVISIBLE);
                binding.imageViewStar2.setVisibility(View.INVISIBLE);
                binding.imageViewStar3.setVisibility(View.INVISIBLE);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isUIInit = false;
    }
}