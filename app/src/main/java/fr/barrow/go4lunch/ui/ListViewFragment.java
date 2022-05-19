package fr.barrow.go4lunch.ui;

import android.app.SearchManager;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;

import fr.barrow.go4lunch.R;
import fr.barrow.go4lunch.databinding.FragmentListViewBinding;
import fr.barrow.go4lunch.model.Restaurant;
import fr.barrow.go4lunch.model.RestaurantAutocomplete;
import fr.barrow.go4lunch.model.UserStateItem;
import fr.barrow.go4lunch.utils.MyViewModelFactory;
import io.reactivex.disposables.Disposable;

public class ListViewFragment extends Fragment implements SearchView.OnQueryTextListener {

    private FragmentListViewBinding binding;
    private String apiKey;
    private MyViewModel mMyViewModel;
    private Disposable disposable;
    private RecyclerView mRecyclerView;
    private ArrayList<Restaurant> mRestaurants = new ArrayList<>();
    private ArrayList<UserStateItem> mUsers = new ArrayList<>();
    private ArrayList<RestaurantAutocomplete> mRestaurantAutocomplete = new ArrayList<>();
    private AutocompleteSessionToken mToken;
    private PlacesClient placesClient;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentListViewBinding.inflate(inflater, container, false);
        view = binding.getRoot();
        apiKey = getString(R.string.MAPS_API_KEY);
        configureViewModel();
        initRecyclerView(false);
        initRestaurantsData();
        if (!Places.isInitialized()) {
            Places.initialize(requireActivity(), apiKey);
        }
        placesClient = Places.createClient(requireActivity());
        mToken = AutocompleteSessionToken.newInstance();
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        SearchManager searchManager =
                (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setOnQueryTextListener(this);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void filter(String text) {
        changeRecyclerView(!text.isEmpty());
        if (mMyViewModel.getLocation() != null) {

            Location myLocation = mMyViewModel.getLocation();
            RectangularBounds bounds = RectangularBounds.newInstance(
                    new LatLng(myLocation.getLatitude() - 0.1, myLocation.getLongitude() - 0.1),
                    new LatLng(myLocation.getLatitude() + 0.1, myLocation.getLongitude() + 0.1));

            FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                    .setLocationBias(bounds)
                    .setOrigin(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()))
                    .setCountries("FR")
                    .setTypeFilter(TypeFilter.ESTABLISHMENT)
                    .setSessionToken(mToken)
                    .setQuery(text)
                    .build();

            placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
                mRestaurantAutocomplete.clear();
                for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                    if (prediction.getPlaceTypes().toString().toLowerCase().contains("restaurant") && prediction.getPrimaryText(null).toString().toLowerCase().contains(text.toLowerCase())) {
                        mRestaurantAutocomplete.add(new RestaurantAutocomplete(prediction.getPlaceId(), prediction.getPrimaryText(null).toString(), prediction.getDistanceMeters().toString()));
                    }
                    System.out.println(prediction.getPlaceTypes().toString().toLowerCase().contains("restaurant") + " " + prediction.getPrimaryText(null).toString().toLowerCase().contains(text.toLowerCase()));
                    System.out.println(prediction.getFullText(null) + " // " + prediction.getPrimaryText(null) + " // " + prediction.getSecondaryText(null));
                }
                mRecyclerView.getAdapter().notifyDataSetChanged();
            }).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    Log.e("Test", "Place not found: " + apiException.getStatusCode());
                }
            });

        }
    }

    private void initRecyclerView(boolean isInSearchMode) {
        mRecyclerView = binding.recyclerview;
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(view.getContext(),
                DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.divider_decoration, null));
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        if (isInSearchMode) {
            AutocompleteSearchAdapter mAdapter = new AutocompleteSearchAdapter(mRestaurantAutocomplete, this);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            ListViewAdapter mAdapter = new ListViewAdapter(mRestaurants, this, mUsers);
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    private void changeRecyclerView(boolean isInSearchMode) {
        mRecyclerView = binding.recyclerview;
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        if (isInSearchMode) {
            AutocompleteSearchAdapter mAdapter = new AutocompleteSearchAdapter(mRestaurantAutocomplete, this);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            ListViewAdapter mAdapter = new ListViewAdapter(mRestaurants, this, mUsers);
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    public void configureViewModel() {
        mMyViewModel = new ViewModelProvider(this, MyViewModelFactory.getInstance(requireActivity())).get(MyViewModel.class);
    }

    public void initRestaurantsData() {
        mMyViewModel.getRestaurantsMutableLiveData().observe(requireActivity(), list -> {
            if (list != null && !list.isEmpty() && mRecyclerView.getAdapter() != null) {
                mRestaurants.clear();
                mRestaurants.addAll(list);
                mRecyclerView.getAdapter().notifyDataSetChanged();
            }
        });
    }

    private void disposeWhenDestroy(){
        if (this.disposable != null && !this.disposable.isDisposed()) this.disposable.dispose();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.disposeWhenDestroy();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        filter(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        filter(newText);
        return true;
    }
}