package fr.barrow.go4lunch.utils;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import org.jetbrains.annotations.NotNull;

import fr.barrow.go4lunch.data.Repository;
import fr.barrow.go4lunch.data.RestaurantRepository;
import fr.barrow.go4lunch.ui.MyViewModel;

public class MyViewModelFactory implements ViewModelProvider.Factory {

    private static MyViewModelFactory factory;
    private final Repository mRepository;
    private final RestaurantRepository mRestaurantRepository;

    public static MyViewModelFactory getInstance(Context context) {
        if (factory == null) {
            synchronized (MyViewModelFactory.class) {
                if (factory == null) {
                    factory = new MyViewModelFactory(context);
                }
            }
        }
        return factory;
    }

    private MyViewModelFactory(Context context) {
        mRepository = new Repository(context.getApplicationContext());
        mRestaurantRepository = new RestaurantRepository();
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull @NotNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MyViewModel.class)) {
            return (T) new MyViewModel(mRepository, mRestaurantRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
