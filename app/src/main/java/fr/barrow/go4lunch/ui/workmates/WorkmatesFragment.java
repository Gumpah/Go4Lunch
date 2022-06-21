package fr.barrow.go4lunch.ui.workmates;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;

import fr.barrow.go4lunch.BuildConfig;
import fr.barrow.go4lunch.R;
import fr.barrow.go4lunch.databinding.FragmentWorkmatesBinding;
import fr.barrow.go4lunch.data.model.Restaurant;
import fr.barrow.go4lunch.data.model.UserStateItem;
import fr.barrow.go4lunch.ui.ClickCallback;
import fr.barrow.go4lunch.ui.restaurantdetails.RestaurantDetailsActivity;
import fr.barrow.go4lunch.ui.viewmodels.RestaurantViewModel;
import fr.barrow.go4lunch.ui.viewmodels.UserViewModel;
import fr.barrow.go4lunch.ui.viewmodels.RestaurantViewModelFactory;
import fr.barrow.go4lunch.ui.viewmodels.UserViewModelFactory;
import fr.barrow.go4lunch.ui.workmates.WorkmatesAdapter;

public class WorkmatesFragment extends Fragment implements SearchView.OnQueryTextListener, ClickCallback {

    private FragmentWorkmatesBinding binding;
    private RestaurantViewModel mRestaurantViewModel;
    private UserViewModel mUserViewModel;

    private ArrayList<UserStateItem> mUsers = new ArrayList<>();
    private String apiKey;
    private WorkmatesAdapter mAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWorkmatesBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        apiKey = BuildConfig.MAPS_API_KEY;
        configureViewModel();
        initRecyclerView(view);
        initUsersData();
        setHasOptionsMenu(true);
        return view;
    }

    private void initRecyclerView(View view) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        binding.recyclerview.setLayoutManager(layoutManager);
        mAdapter = new WorkmatesAdapter(new ArrayList<>(), this);
        binding.recyclerview.setAdapter(mAdapter);
    }

    public void initUsersData() {
        mUserViewModel.getEveryFirestoreUser().observe(getViewLifecycleOwner(), list -> {
            if (list != null && !list.isEmpty() && binding.recyclerview.getAdapter() != null) {
                mUsers.clear();
                mUsers.addAll(list);
                mAdapter.setData(list);
            }
        });
    }

    public void filter(String text) {
        ArrayList<UserStateItem> users = new ArrayList<>();
        if (mUsers != null && binding.recyclerview.getAdapter() != null) {
            if(text.isEmpty()){
                users.addAll(mUsers);
            } else {
                text = text.toLowerCase();
                for(UserStateItem item: mUsers){
                    if(item.getUsername().toLowerCase().contains(text)){
                        users.add(item);
                    }
                }
            }
            mAdapter.setData(users);
        }
    }

    public void configureViewModel() {
        mRestaurantViewModel = new ViewModelProvider(requireActivity(), RestaurantViewModelFactory.getInstance()).get(RestaurantViewModel.class);
        mUserViewModel = new ViewModelProvider(requireActivity(), UserViewModelFactory.getInstance(requireContext())).get(UserViewModel.class);
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
        searchView.setQueryHint(requireActivity().getString(R.string.search_workmates));
        searchView.setOnQueryTextListener(this);
        searchView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                mAdapter.setData(mUsers);
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
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
        if (mRestaurantViewModel.getRestaurantFromId(restaurantId) == null) {
            mRestaurantViewModel.fetchRestaurantDetailsAndAddRestaurant(apiKey, restaurantId);
            mRestaurantViewModel.getRestaurantsMutableLiveData().observe(getViewLifecycleOwner(), list -> {
                if (mRestaurantViewModel.getRestaurantFromId(restaurantId) != null) {
                    Restaurant mRestaurant = mRestaurantViewModel.getRestaurantFromId(restaurantId);
                    startDetailsActivity(mRestaurant);
                }
            });
        } else {
            Restaurant mRestaurant = mRestaurantViewModel.getRestaurantFromId(restaurantId);
            startDetailsActivity(mRestaurant);
        }
    }

    private void startDetailsActivity(Restaurant restaurant) {
        Intent intent = new Intent(requireContext(), RestaurantDetailsActivity.class);
        intent.putExtra("RESTAURANT", restaurant);
        requireContext().startActivity(intent);
    }
}