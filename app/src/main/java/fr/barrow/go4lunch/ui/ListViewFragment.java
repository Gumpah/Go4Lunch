package fr.barrow.go4lunch.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import fr.barrow.go4lunch.R;
import fr.barrow.go4lunch.databinding.FragmentListViewBinding;
import fr.barrow.go4lunch.model.Restaurant;
import fr.barrow.go4lunch.utils.MyViewModelFactory;
import io.reactivex.disposables.Disposable;

public class ListViewFragment extends Fragment {

    private FragmentListViewBinding binding;
    private String apiKey;
    private MyViewModel mMyViewModel;
    private Disposable disposable;
    private RecyclerView mRecyclerView;
    private ArrayList<Restaurant> mRestaurants = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentListViewBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        apiKey = getString(R.string.MAPS_API_KEY);
        configureViewModel();
        initRecyclerView(view);
        initDataChangeObserve();
        setupDataRequest();
        return view;
    }

    private void initRecyclerView(View view) {
        mRecyclerView = binding.recyclerview;
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        ListViewAdapter mAdapter = new ListViewAdapter(mRestaurants);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(view.getContext(),
                layoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void configureViewModel() {
        mMyViewModel = new ViewModelProvider(this, MyViewModelFactory.getInstance(requireActivity())).get(MyViewModel.class);
    }

    private void setupDataRequest() {
        if (mMyViewModel.getLocationString() != null) {
            mMyViewModel.fetchAndUpdateRestaurants(mMyViewModel.getLocationString(), disposable, apiKey);
        }
    }

    public void initDataChangeObserve() {
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
}