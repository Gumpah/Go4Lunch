package fr.barrow.go4lunch.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;

import fr.barrow.go4lunch.R;
import fr.barrow.go4lunch.databinding.FragmentWorkmatesBinding;
import fr.barrow.go4lunch.model.Restaurant;
import fr.barrow.go4lunch.model.placedetails.CombinedPlaceAndString;
import fr.barrow.go4lunch.model.placedetails.PlaceDetailsResult;
import fr.barrow.go4lunch.utils.MyViewModelFactory;
import fr.barrow.go4lunch.data.PlacesStreams;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class WorkmatesFragment extends Fragment {

    private FragmentWorkmatesBinding binding;
    private Disposable disposable;
    private String apiKey;
    private MyViewModel mMyViewModel;

    private MutableLiveData<ArrayList<Restaurant>> mRestaurantList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWorkmatesBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        apiKey = getString(R.string.MAPS_API_KEY);
        configureViewModel();
        return view;
    }

    public void configureViewModel() {
        mMyViewModel = new ViewModelProvider(this, MyViewModelFactory.getInstance(requireActivity())).get(MyViewModel.class);
    }

    private void disposeWhenDestroy(){
        if (this.disposable != null && !this.disposable.isDisposed()) this.disposable.dispose();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.disposeWhenDestroy();
    }
}