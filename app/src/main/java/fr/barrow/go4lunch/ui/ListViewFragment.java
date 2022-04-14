package fr.barrow.go4lunch.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.List;

import fr.barrow.go4lunch.R;
import fr.barrow.go4lunch.databinding.FragmentListViewBinding;
import fr.barrow.go4lunch.model.places.Place;
import fr.barrow.go4lunch.utils.PlacesCalls;

public class ListViewFragment extends Fragment implements PlacesCalls.Callbacks{

    private static final String TAG = ListViewFragment.class.getSimpleName();
    private FragmentListViewBinding binding;
    private PlacesClient placesClient;
    private String apiKey;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentListViewBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        apiKey = getString(R.string.MAPS_API_KEY);
        //Places.initialize(requireContext(), apiKey);
        //placesClient = Places.createClient(requireActivity());
        executeHttpRequest();
        return view;
    }

    private void executeHttpRequest() {
        PlacesCalls.fetchNearbyPlaces(this, apiKey);
    }

    @Override
    public void onResponse(@Nullable List<Place> placesList) {
        if(placesList != null) {
            for (int i = 0;i < placesList.size(); i++) {
                System.out.println(placesList.get(i).getName());
            }
        }
    }

    @Override
    public void onFailure() {

    }
}