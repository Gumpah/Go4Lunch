package fr.barrow.go4lunch.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import fr.barrow.go4lunch.R;
import fr.barrow.go4lunch.databinding.FragmentListViewBinding;
import fr.barrow.go4lunch.model.Restaurant;
import fr.barrow.go4lunch.model.placedetails.CombinedPlaceAndString;
import fr.barrow.go4lunch.model.placedetails.PlaceDetailsResult;
import fr.barrow.go4lunch.utils.MyViewModelFactory;
import fr.barrow.go4lunch.data.PlacesStreams;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class ListViewFragment extends Fragment {

    private FragmentListViewBinding binding;
    private String apiKey;
    private MyViewModel mMyViewModel;
    private Disposable disposable;
    private RecyclerView mRecyclerView;
    private ArrayList<Restaurant> mRestaurants = new ArrayList<>();

    private MutableLiveData<ArrayList<Restaurant>> mRestaurantList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentListViewBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        apiKey = getString(R.string.MAPS_API_KEY);
        configureViewModel();
        initRecyclerView();
        initDataChangeObserve();
        setupDataRequest();
        return view;
    }

    private void initRecyclerView() {
        mRecyclerView = binding.recyclerview;
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        ListViewAdapter mAdapter = new ListViewAdapter(mRestaurants);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                layoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void configureViewModel() {
        mMyViewModel = new ViewModelProvider(this, MyViewModelFactory.getInstance(requireActivity())).get(MyViewModel.class);
    }

    private void setupDataRequest() {
        if (mMyViewModel.getLocation() != null) {
            executeHttpRequestWithRetrofit(mMyViewModel.getLocation());
        }
    }

    public void initDataChangeObserve() {
        mRestaurantList = mMyViewModel.getRestaurants();
        mRestaurantList.observe(requireActivity(), list -> {
            mRestaurants.clear();
            mRestaurants.addAll(list);
            mRecyclerView.getAdapter().notifyDataSetChanged();
        });
    }

    private void executeHttpRequestWithRetrofit(String location) {
        mMyViewModel.clearRestaurants();
        this.disposable = PlacesStreams.streamFetchNearbyPlacesAndFetchTheirDetails(apiKey, location).subscribeWith(new DisposableObserver<CombinedPlaceAndString>() {
            @Override
            public void onNext(CombinedPlaceAndString combinedPlaceAndString) {
                PlaceDetailsResult placeDetailsResult = combinedPlaceAndString.getPlaceDetailsResult();
                String photoUrl = combinedPlaceAndString.getPhotoUrl();
                mMyViewModel.addRestaurant(mMyViewModel.placeDetailsToRestaurantObject(placeDetailsResult, photoUrl));
                Log.e("TAG","On Next");
            }

            @Override
            public void onError(Throwable e) {
                Log.e("TAG","On Error"+Log.getStackTraceString(e));
            }

            @Override
            public void onComplete() {
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