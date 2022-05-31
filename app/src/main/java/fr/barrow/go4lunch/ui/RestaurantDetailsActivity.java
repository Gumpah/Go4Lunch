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

import fr.barrow.go4lunch.R;
import fr.barrow.go4lunch.databinding.ActivityRestaurantDetailsBinding;
import fr.barrow.go4lunch.model.Restaurant;
import fr.barrow.go4lunch.model.UserStateItem;
import fr.barrow.go4lunch.utils.MyViewModelFactory;

public class RestaurantDetailsActivity extends AppCompatActivity {

    private ActivityRestaurantDetailsBinding binding;

    private MyViewModel mMyViewModel;
    private Restaurant mRestaurant;
    private ArrayList<UserStateItem> mUsers = new ArrayList<>();
    private UserStateItem mUser = new UserStateItem();
    private RestaurantDetailsWorkmatesAdapter adapter;
    private String apiKey;

    private boolean isUIInit;
    private boolean restaurantPick;
    private boolean restaurantLike;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRestaurantDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        apiKey = getString(R.string.MAPS_API_KEY);
        isUIInit = false;
        setContentView(view);
        configureViewModel();
        getRestaurant();
    }

    private void configureViewModel() {
        mMyViewModel = new ViewModelProvider(this, MyViewModelFactory.getInstance(this)).get(MyViewModel.class);
    }

    private void initRecyclerView(View view) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        binding.recyclerview.setLayoutManager(layoutManager);
        adapter = new RestaurantDetailsWorkmatesAdapter(mUsers);
        initLineVisibility();
        binding.recyclerview.setAdapter(adapter);
        initUsersList();
    }

    private void getRestaurant() {
        Intent intent = getIntent();
        String restaurant_id = intent.getStringExtra("RESTAURANT_ID");
        if (mMyViewModel.getRestaurantFromId(restaurant_id) == null) {
            mMyViewModel.fetchRestaurantDetailsAndAddRestaurant(apiKey, restaurant_id, this);
            mMyViewModel.getRestaurantsMutableLiveData().observe(this, list -> {
                if (mMyViewModel.getRestaurantFromId(restaurant_id) != null) {
                    mRestaurant = mMyViewModel.getRestaurantFromId(restaurant_id);
                    initUserData();
                    mMyViewModel.updateUserData();
                    initUI();
                    initRecyclerView(binding.getRoot());
                }
            });
        } else {
            mRestaurant = mMyViewModel.getRestaurantFromId(restaurant_id);
            initUserData();
            mMyViewModel.updateUserData();
            initUI();
            initRecyclerView(binding.getRoot());
        }
    }

    private void initUserData() {
        mUser = mMyViewModel.getUserNew().getValue();
        mMyViewModel.getUserNew().observe(this, user -> {
            mUser = user;
            mMyViewModel.getAllUsersWhoPickedARestaurant(mRestaurant.getId());
            binding.fabChooseRestaurant.setSelected(restaurantPick);
            binding.imageButtonLikeRestaurant.setSelected(restaurantLike);
            if (!isUIInit) {
                restaurantPick =  mRestaurant.getId().equals(mUser.getPickedRestaurant());
                restaurantLike =  mUser.getLikedRestaurants().contains(mRestaurant.getId());
                binding.fabChooseRestaurant.setSelected(restaurantPick);
                binding.imageButtonLikeRestaurant.setSelected(restaurantLike);
                System.out.println("BBB" + restaurantPick + " " + restaurantLike);
                initPickRestaurantButton();
                initLikeRestaurantButton();
            }
        });
    }

    private void initUsersList() {
        mMyViewModel.getAllUsersWhoPickedARestaurant(mRestaurant.getId()).observe(this, users -> {
            if (users.isEmpty()) {
                System.out.println("BLABLA");
            } else {
                System.out.println("BLABLA" + users.get(0).getUsername());
            }
            mUsers.clear();
            mUsers.addAll(users);
            adapter.notifyDataSetChanged();
            initLineVisibility();
        });
    }

    private void initLineVisibility() {
        if (mUsers.isEmpty()) {
            binding.viewLineSeparator.setVisibility(View.INVISIBLE);
        } else {
            binding.viewLineSeparator.setVisibility(View.VISIBLE);
        }
    }

    private void initUI() {
        initRestaurantInfos();
        setupStarsRating();
        initWebsiteButton();
        initCallButton();
    }

    private void initPickRestaurantButton() {
        isUIInit = true;
        binding.fabChooseRestaurant.setOnClickListener(view -> {
            if (restaurantPick) {
                mMyViewModel.removePickedRestaurant();
                restaurantPick = false;
            } else {
                mMyViewModel.setPickedRestaurant(mRestaurant.getId(), mRestaurant.getName());
                restaurantPick = true;
            }
            mMyViewModel.getUserNew();
        });
    }


    private void initLikeRestaurantButton() {
        binding.imageButtonLikeRestaurant.setOnClickListener(view -> {
            if (restaurantLike) {
                mMyViewModel.removeLikedRestaurant(mRestaurant.getId());
                restaurantLike = false;
            } else {
                mMyViewModel.addLikedRestaurant(mRestaurant.getId());
                restaurantLike = true;
            }
            mMyViewModel.getUserNew();
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

    private void initRestaurantInfos() {
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