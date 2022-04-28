package fr.barrow.go4lunch.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import fr.barrow.go4lunch.R;
import fr.barrow.go4lunch.model.UserStateItem;
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
    private ArrayList<UserStateItem> mUsers = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private RestaurantDetailsWorkmatesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRestaurantDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        configureViewModel();
        getRestaurant();
        initRecyclerView(view);
        mMyViewModel.updateUserData();
        initDataChangeObserve();
        initUI();
    }

    private void configureViewModel() {
        mMyViewModel = new ViewModelProvider(this, MyViewModelFactory.getInstance(this)).get(MyViewModel.class);
    }

    private void initRecyclerView(View view) {
        mRecyclerView = binding.recyclerview;
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        adapter = new RestaurantDetailsWorkmatesAdapter(mUsers);
        mRecyclerView.setAdapter(adapter);
        initUsersList();
    }

    private void getRestaurant() {
        Intent intent = getIntent();
        String restaurant_id = intent.getStringExtra("RESTAURANT_ID");
        mRestaurant = mMyViewModel.getRestaurantFromId(restaurant_id);
    }

    public void initDataChangeObserve() {
        mUser = mMyViewModel.getUser().getValue();
        mMyViewModel.getUser().observe(this, user -> {
            mUser = user;
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
        });
    }

    private void initUI() {
        initRestaurantInfos();
        setupStarsRating();
        initPickRestaurantButton();
        initLikeRestaurantButton();
        initWebsiteButton();
        initCallButton();
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
                mMyViewModel.getAllUsersWhoPickedARestaurant(mRestaurant.getId());
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

    private void initWebsiteButton() {
        String website = mRestaurant.getWebsite();
        binding.imageButtonWebsiteRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (website != null) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(website));
                    startActivity(i);
                } else {
                    Toast.makeText(binding.getRoot().getContext(), R.string.toast_noWebsite, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initCallButton() {
        String phoneNumber = mRestaurant.getPhoneNumber();
        binding.imageButtonCallRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phoneNumber != null) {
                    Intent i = new Intent(Intent.ACTION_DIAL);
                    i.setData(Uri.parse("tel:" + phoneNumber));
                    startActivity(i);
                } else {
                    Toast.makeText(binding.getRoot().getContext(), R.string.toast_noPhoneNumber, Toast.LENGTH_SHORT).show();
                }
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