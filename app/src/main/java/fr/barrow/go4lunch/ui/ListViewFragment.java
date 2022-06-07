package fr.barrow.go4lunch.ui;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import fr.barrow.go4lunch.BuildConfig;
import fr.barrow.go4lunch.R;
import fr.barrow.go4lunch.databinding.FragmentListViewBinding;
import fr.barrow.go4lunch.model.Restaurant;
import fr.barrow.go4lunch.ui.viewmodels.MyViewModel;
import fr.barrow.go4lunch.utils.ClickCallback;
import fr.barrow.go4lunch.utils.MyViewModelFactory;

public class ListViewFragment extends Fragment implements SearchView.OnQueryTextListener, ClickCallback {

    private FragmentListViewBinding binding;
    private MyViewModel mMyViewModel;
    private RecyclerView mRecyclerView;
    private String apiKey;

    private AutocompleteSearchAdapter mAutocompleteSearchAdapter;
    private ListViewAdapter mListViewAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentListViewBinding.inflate(inflater, container, false);
        apiKey = BuildConfig.MAPS_API_KEY;
        configureViewModel();
        initRecyclerView();
        initRestaurantsData();
        initUsersData();
        setHasOptionsMenu(true);
        initRestaurantsAutocompleteObserver();
        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        SearchManager searchManager =
                (SearchManager) requireActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(requireActivity().getComponentName()));
        searchView.setOnQueryTextListener(this);
        searchView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                changeRecyclerView(false);
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void initRestaurantsAutocompleteObserver() {
        mMyViewModel.getRestaurantsAutocompleteMutableLiveData().observe(requireActivity(), list -> {
            mAutocompleteSearchAdapter.setData(list);
        });
    }

    private void filter(String text) {
        changeRecyclerView(!text.isEmpty());
        mMyViewModel.autocompleteRequest(text, requireContext(), apiKey);
    }

    private void initRecyclerView() {
        mRecyclerView = binding.recyclerview;
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requireContext(),
                DividerItemDecoration.VERTICAL);
        Drawable drawable = ContextCompat.getDrawable(requireContext(), R.drawable.divider_decoration);
        if (drawable != null) {
            dividerItemDecoration.setDrawable(drawable);
        } //TODO fournir image si null par défaut
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mListViewAdapter = new ListViewAdapter(new ArrayList<>(), this, this, mMyViewModel.getLocation());
        mAutocompleteSearchAdapter = new AutocompleteSearchAdapter(new ArrayList<>(), this, this);
        mRecyclerView.setAdapter(mListViewAdapter);
    }

    private void changeRecyclerView(boolean isInSearchMode) {
        if (isInSearchMode) {
            mRecyclerView.setAdapter(mAutocompleteSearchAdapter);
        } else {
            mRecyclerView.setAdapter(mListViewAdapter);
        }
    }

    public void configureViewModel() {
        mMyViewModel = new ViewModelProvider(requireActivity(), MyViewModelFactory.getInstance(requireContext())).get(MyViewModel.class);
    }

    public void initRestaurantsData() {
        mMyViewModel.getRestaurantsMutableLiveData().observe(getViewLifecycleOwner(), list -> {
            if (list != null && !list.isEmpty() && binding.recyclerview.getAdapter() != null) {
                mListViewAdapter.setData(list);
            }
        });
    }

    public void initUsersData() {
        mMyViewModel.getUsersWhoPickedARestaurant().observe(getViewLifecycleOwner(), users -> {
            if (users != null && !users.isEmpty() && binding.recyclerview.getAdapter() != null)
                mListViewAdapter.setDataUsers(users);
        });
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

    @Override
    public void myClickCallback(String restaurantId) {
        if (mMyViewModel.getRestaurantFromId(restaurantId) == null) {
            mMyViewModel.fetchRestaurantDetailsAndAddRestaurant(apiKey, restaurantId, requireContext());
            mMyViewModel.getRestaurantsMutableLiveData().observe(this, list -> {
                if (mMyViewModel.getRestaurantFromId(restaurantId) != null) {
                    Restaurant mRestaurant = mMyViewModel.getRestaurantFromId(restaurantId);
                    startDetailsActivity(mRestaurant);
                }
            });
        } else {
            Restaurant mRestaurant = mMyViewModel.getRestaurantFromId(restaurantId);
            startDetailsActivity(mRestaurant);
        }
    }

    private void startDetailsActivity(Restaurant restaurant) {
        Intent intent = new Intent(requireContext(), RestaurantDetailsActivity.class);
        intent.putExtra("RESTAURANT", restaurant);
        requireContext().startActivity(intent);
    }
}