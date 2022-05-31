package fr.barrow.go4lunch.ui;

import android.app.SearchManager;
import android.content.Context;
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

import fr.barrow.go4lunch.R;
import fr.barrow.go4lunch.databinding.FragmentListViewBinding;
import fr.barrow.go4lunch.model.Restaurant;
import fr.barrow.go4lunch.model.RestaurantAutocomplete;
import fr.barrow.go4lunch.utils.MyViewModelFactory;
import io.reactivex.disposables.Disposable;

public class ListViewFragment extends Fragment implements SearchView.OnQueryTextListener {

    private FragmentListViewBinding binding;
    private MyViewModel mMyViewModel;
    private Disposable disposable;
    private RecyclerView mRecyclerView;
    private ArrayList<Restaurant> mRestaurants = new ArrayList<>();
    private ArrayList<RestaurantAutocomplete> mRestaurantAutocomplete = new ArrayList<>();
    private View view;
    private String apiKey;

    private AutocompleteSearchAdapter mAutocompleteSearchAdapter;
    private ListViewAdapter mListViewAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentListViewBinding.inflate(inflater, container, false);
        view = binding.getRoot();
        apiKey = getString(R.string.MAPS_API_KEY);
        configureViewModel();
        initRecyclerView();
        initRestaurantsData();
        setHasOptionsMenu(true);
        initRestaurantsAutocompleteObserver();
        return view;
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
            mRestaurantAutocomplete.clear();
            mRestaurantAutocomplete.addAll(list);
            mRecyclerView.getAdapter().notifyDataSetChanged(); //TODO déplacer dans adapteur
        });
    }

    private void filter(String text) {
        changeRecyclerView(!text.isEmpty());
        mMyViewModel.autocompleteRequest(text, requireContext(), apiKey);
    }

    private void initRecyclerView() {
        mRecyclerView = binding.recyclerview;
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(view.getContext(),
                DividerItemDecoration.VERTICAL);
        Drawable drawable = ContextCompat.getDrawable(requireContext(), R.drawable.divider_decoration);
        if (drawable != null) {
            dividerItemDecoration.setDrawable(drawable);
        } //TODO fournir image si null par défaut
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mListViewAdapter = new ListViewAdapter(mRestaurants, this);
        mAutocompleteSearchAdapter = new AutocompleteSearchAdapter(mRestaurantAutocomplete, this);
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
        mMyViewModel = new ViewModelProvider(this, MyViewModelFactory.getInstance(requireActivity())).get(MyViewModel.class);
    }

    public void initRestaurantsData() {
        System.out.println("AAA / 0");
        mMyViewModel.getRestaurantsMutableLiveData().observe(requireActivity(), list -> {
            System.out.println("AAA / 1");
            if (list != null && !list.isEmpty() && binding.recyclerview.getAdapter() != null) {
                System.out.println("AAA / 2");
                mRestaurants.clear();
                mRestaurants.addAll(list);
                binding.recyclerview.getAdapter().notifyDataSetChanged(); //TODO déplacer dans adapteur
            }
        });
    }

    private void disposeWhenDestroy() {
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