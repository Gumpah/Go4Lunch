package fr.barrow.go4lunch.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.MarkerOptions;

import fr.barrow.go4lunch.R;
import fr.barrow.go4lunch.databinding.ActivityMainBinding;
import fr.barrow.go4lunch.databinding.ActivityRestaurantDetailsBinding;
import fr.barrow.go4lunch.model.Restaurant;
import fr.barrow.go4lunch.model.User;
import fr.barrow.go4lunch.utils.MyViewModelFactory;

public class RestaurantDetailsActivity extends AppCompatActivity {

    private ActivityRestaurantDetailsBinding binding;
    private MyViewModel mMyViewModel;
    private Restaurant mRestaurant;
    private MutableLiveData<User> mUserMutableLiveData;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRestaurantDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        configureViewModel();
        mMyViewModel.updateUserData();
        getRestaurant();
        initDataChangeObserve();
        initUI();
    }

    public void configureViewModel() {
        mMyViewModel = new ViewModelProvider(this, MyViewModelFactory.getInstance(this)).get(MyViewModel.class);
    }

    private void getRestaurant() {
        Intent intent = getIntent();
        String restaurant_id = intent.getStringExtra("RESTAURANT_ID");
        mRestaurant = mMyViewModel.getRestaurantFromId(restaurant_id);
    }

    public void initDataChangeObserve() {
        mUserMutableLiveData = mMyViewModel.getUser();
        System.out.println("AAA" + mMyViewModel.getUser().getValue().getUid());
        mUser = mUserMutableLiveData.getValue();
        mUserMutableLiveData.observe(this, user -> {
            System.out.println("AAA" + user.getUid());
            mUser = user;
        });
    }

    private void initUI() {
        initRestaurantInfos();
        setupStarsRating();
        initPickRestaurantButton();
        initLikeRestaurantButton();
    }

    private void initPickRestaurantButton() {
        binding.fabChooseRestaurant.setSelected(mRestaurant.getId().equals(mUser.getPickedRestaurant()));
        binding.fabChooseRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mRestaurant.getId().equals(mUser.getPickedRestaurant())) {
                    mMyViewModel.removePickedRestaurant();
                } else {
                    mMyViewModel.setPickedRestaurant(mRestaurant.getId());
                }
                binding.fabChooseRestaurant.setSelected(mRestaurant.getId().equals(mUser.getPickedRestaurant()));
            }
        });
    }


    private void initLikeRestaurantButton() {
        binding.imageButtonLikeRestaurant.setSelected(mUser.getLikedRestaurants().contains(mRestaurant.getId()));
        binding.imageButtonLikeRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUser.getLikedRestaurants().contains(mRestaurant.getId())) {
                    mMyViewModel.removeLikedRestaurant(mRestaurant.getId());
                } else {
                    mMyViewModel.addLikedRestaurant(mRestaurant.getId());
                }
                binding.imageButtonLikeRestaurant.setSelected(mUser.getLikedRestaurants().contains(mRestaurant.getId()));
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
}