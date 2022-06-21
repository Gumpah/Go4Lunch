package fr.barrow.go4lunch.ui.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import org.jetbrains.annotations.NotNull;

import fr.barrow.go4lunch.data.PlacesStreams;
import fr.barrow.go4lunch.data.RestaurantRepository;

public class RestaurantViewModelFactory implements ViewModelProvider.Factory {

    private static RestaurantViewModelFactory factory;
    private final RestaurantRepository mRestaurantRepository;
    private final PlacesStreams mPlacesStreams;

    public static RestaurantViewModelFactory getInstance() {
        if (factory == null) {
            synchronized (RestaurantViewModelFactory.class) {
                if (factory == null) {
                    factory = new RestaurantViewModelFactory();
                }
            }
        }
        return factory;
    }

    private RestaurantViewModelFactory() {
        mRestaurantRepository = new RestaurantRepository();
        mPlacesStreams = new PlacesStreams();
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull @NotNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RestaurantViewModel.class)) {
            return (T) new RestaurantViewModel(mRestaurantRepository, mPlacesStreams);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}