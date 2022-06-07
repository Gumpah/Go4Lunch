package fr.barrow.go4lunch.utils;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import org.jetbrains.annotations.NotNull;

import fr.barrow.go4lunch.data.Repository;
import fr.barrow.go4lunch.data.RestaurantRepository;
import fr.barrow.go4lunch.data.UserRepository;
import fr.barrow.go4lunch.ui.viewmodels.MyViewModel;

public class MyViewModelFactory implements ViewModelProvider.Factory {

    private static MyViewModelFactory factory;
    private final Repository mRepository;
    private final RestaurantRepository mRestaurantRepository;
    private final UserRepository mUserRepository;
    private final NetworkMonitoring mNetworkMonitoring;

    public static MyViewModelFactory getInstance(Context context) {
        if (factory == null) {
            synchronized (MyViewModelFactory.class) {
                if (factory == null) {
                    factory = new MyViewModelFactory(context.getApplicationContext());
                }
            }
        }
        return factory;
    }

    private MyViewModelFactory(Context context) {
        mRepository = new Repository();
        mRestaurantRepository = new RestaurantRepository();
        mUserRepository = new UserRepository(context);
        mNetworkMonitoring = new NetworkMonitoring(context);
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull @NotNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MyViewModel.class)) {
            return (T) new MyViewModel(mRepository, mRestaurantRepository, mUserRepository, mNetworkMonitoring);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
