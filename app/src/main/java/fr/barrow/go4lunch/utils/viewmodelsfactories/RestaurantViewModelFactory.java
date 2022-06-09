package fr.barrow.go4lunch.utils.viewmodelsfactories;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import org.jetbrains.annotations.NotNull;

import fr.barrow.go4lunch.data.RestaurantRepository;
import fr.barrow.go4lunch.ui.viewmodels.RestaurantViewModel;

public class RestaurantViewModelFactory implements ViewModelProvider.Factory {

    private static RestaurantViewModelFactory factory;
    private final RestaurantRepository mRestaurantRepository;

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
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull @NotNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RestaurantViewModel.class)) {
            return (T) new RestaurantViewModel(mRestaurantRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}