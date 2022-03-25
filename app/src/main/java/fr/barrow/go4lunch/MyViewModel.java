package fr.barrow.go4lunch;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import fr.barrow.go4lunch.model.Restaurant;

public class MyViewModel extends ViewModel {
    Repository mRepository;
    MutableLiveData<ArrayList<Restaurant>> mMutableLiveData;

    public MyViewModel(Repository repository) {
        mRepository = repository;
        mMutableLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<ArrayList<Restaurant>> getMutableLiveData() {
        return mMutableLiveData;
    }

    public void fetchRestaurants() {
        //ArrayList<Restaurant> restaurants = mRepository.getRestaurants;
        //mMutableLiveData.setValue(restaurants);
    }
}
