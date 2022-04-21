package fr.barrow.go4lunch.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import fr.barrow.go4lunch.R;
import fr.barrow.go4lunch.databinding.FragmentWorkmatesBinding;
import fr.barrow.go4lunch.model.placedetails.PlaceDetailsList;
import fr.barrow.go4lunch.utils.PlacesStreams;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class WorkmatesFragment extends Fragment {

    private FragmentWorkmatesBinding binding;
    private Disposable disposable;
    private String apiKey;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWorkmatesBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        apiKey = getString(R.string.MAPS_API_KEY);
        executeHttpRequestWithRetrofit();
        return view;
    }

    private void executeHttpRequestWithRetrofit(){
        this.disposable = PlacesStreams.streamFetchNearbyPlacesAndFetchFirstPlaceDetails(apiKey).subscribeWith(new DisposableObserver<PlaceDetailsList>() {
            @Override
            public void onNext(PlaceDetailsList placeDetailsList) {
                System.out.println(placeDetailsList.getResult().getFormattedPhoneNumber());
                Log.e("TAG","On Next");
            }

            @Override
            public void onError(Throwable e) {
                Log.e("TAG","On Error"+Log.getStackTraceString(e));
            }

            @Override
            public void onComplete() {
                Log.e("TAG","On Complete !!");
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