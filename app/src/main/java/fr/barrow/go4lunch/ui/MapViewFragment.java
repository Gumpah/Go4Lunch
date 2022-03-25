package fr.barrow.go4lunch.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.mapbox.maps.Style;

import fr.barrow.go4lunch.databinding.FragmentMapViewBinding;


public class MapViewFragment extends Fragment {

    private FragmentMapViewBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMapViewBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        mapSetup();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void mapSetup(){
        binding.mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS);
    }
}