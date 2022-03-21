package fr.barrow.go4lunch.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.mapbox.maps.Style;

import fr.barrow.go4lunch.R;
import fr.barrow.go4lunch.databinding.ActivityMainBinding;
import fr.barrow.go4lunch.databinding.ActivityMapViewBinding;



public class MapViewActivity extends AppCompatActivity {

    private ActivityMapViewBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);
        initUI();
    }



    private void initUI(){
        binding = ActivityMapViewBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
    }

    private void mapSetup(){
        binding.mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS);
    }
}