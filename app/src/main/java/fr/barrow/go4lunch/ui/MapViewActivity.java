package fr.barrow.go4lunch.ui;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.mapbox.maps.Style;

import fr.barrow.go4lunch.MyViewModel;
import fr.barrow.go4lunch.MyViewModelFactory;
import fr.barrow.go4lunch.R;
import fr.barrow.go4lunch.databinding.ActivityMapViewBinding;

public class MapViewActivity extends AppCompatActivity {

    private ActivityMapViewBinding binding;
    private MyViewModel mMyViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);
        initUI();
        //initViewModel();
    }

    private void initUI(){
        binding = ActivityMapViewBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
    }

    private void initViewModel() {
        mMyViewModel = new ViewModelProvider(this, MyViewModelFactory.getInstance(this)).get(MyViewModel.class);
        mMyViewModel.fetchRestaurants();
    }

    private void mapSetup(){
        binding.mapView2.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS);
    }
}