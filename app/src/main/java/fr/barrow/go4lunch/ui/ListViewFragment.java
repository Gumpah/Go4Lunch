package fr.barrow.go4lunch.ui;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import com.bumptech.glide.load.engine.Resource;

import java.util.ArrayList;

import fr.barrow.go4lunch.R;
import fr.barrow.go4lunch.databinding.FragmentListViewBinding;
import fr.barrow.go4lunch.model.Restaurant;
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
    private ArrayList<Restaurant> mRestaurantsCopy = new ArrayList<>();
    private ArrayList<UserStateItem> mUsers = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentListViewBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        configureViewModel();
        initRecyclerView(view);
        initRestaurantsData();
        //setupDataRequest();
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
        if (mRestaurants != null && mRecyclerView.getAdapter() != null) {
            mRestaurants.clear();
            if(text.isEmpty()){
                mRestaurants.addAll(mRestaurantsCopy);
            } else {
                text = text.toLowerCase();
                for(Restaurant item: mRestaurantsCopy){
                    if(item.getName().toLowerCase().contains(text)){
                        mRestaurants.add(item);
                    }
                }
            }
            mRecyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    private void initRecyclerView(View view) {
        mRecyclerView = binding.recyclerview;
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(view.getContext(),
                DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.divider_decoration, null));
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        ListViewAdapter mAdapter = new ListViewAdapter(mRestaurants, this, mUsers);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void configureViewModel() {
        mMyViewModel = new ViewModelProvider(this, MyViewModelFactory.getInstance(requireActivity())).get(MyViewModel.class);
    }

    private void setupDataRequest() {
        if (mMyViewModel.getLocation() != null) {
            mMyViewModel.fetchAndUpdateRestaurants(mMyViewModel.getLocationString(), disposable, apiKey);
        }
    }

    public void initRestaurantsData() {
        mMyViewModel.getRestaurantsMutableLiveData().observe(requireActivity(), list -> {
            if (list != null && !list.isEmpty() && mRecyclerView.getAdapter() != null) {
                mRestaurants.clear();
                mRestaurants.addAll(list);
                mRestaurantsCopy.clear();
                mRestaurantsCopy.addAll(list);
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