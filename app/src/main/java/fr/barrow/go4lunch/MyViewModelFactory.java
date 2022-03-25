package fr.barrow.go4lunch;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import org.jetbrains.annotations.NotNull;

public class MyViewModelFactory implements ViewModelProvider.Factory {

    private static MyViewModelFactory factory;

    private MyViewModelFactory(Context context) {
    }

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

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull @NotNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MyViewModel.class)) {
            //return (T) new MyViewModel(Repository, mExecutor);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
