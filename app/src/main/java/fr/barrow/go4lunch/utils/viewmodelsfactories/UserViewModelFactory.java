package fr.barrow.go4lunch.utils.viewmodelsfactories;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import org.jetbrains.annotations.NotNull;

import fr.barrow.go4lunch.data.FirebaseHelper;
import fr.barrow.go4lunch.data.UserRepository;
import fr.barrow.go4lunch.ui.viewmodels.UserViewModel;
import fr.barrow.go4lunch.utils.NetworkMonitoring;

public class UserViewModelFactory implements ViewModelProvider.Factory {

    private static UserViewModelFactory factory;
    private final UserRepository mUserRepository;
    private final NetworkMonitoring mNetworkMonitoring;

    public static UserViewModelFactory getInstance(Context context) {
        if (factory == null) {
            synchronized (UserViewModelFactory.class) {
                if (factory == null) {
                    factory = new UserViewModelFactory(context.getApplicationContext());
                }
            }
        }
        return factory;
    }

    private UserViewModelFactory(Context context) {
        mUserRepository = new UserRepository(new FirebaseHelper());
        mNetworkMonitoring = new NetworkMonitoring(context);
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull @NotNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(UserViewModel.class)) {
            return (T) new UserViewModel(mUserRepository, mNetworkMonitoring);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}