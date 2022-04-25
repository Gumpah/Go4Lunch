package fr.barrow.go4lunch.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;

import fr.barrow.go4lunch.R;
import fr.barrow.go4lunch.databinding.ActivityMainBinding;
import fr.barrow.go4lunch.databinding.ActivityRestaurantDetailsBinding;
import fr.barrow.go4lunch.model.Restaurant;
import fr.barrow.go4lunch.utils.MyViewModelFactory;

public class RestaurantDetailsActivity extends AppCompatActivity {

    private ActivityRestaurantDetailsBinding binding;
    private MyViewModel mMyViewModel;
    private Restaurant mRestaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureViewModel();
        getRestaurant();
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

    private void initUI() {
        binding = ActivityRestaurantDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        initRestaurantInfos();
        setupStarsRating();
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