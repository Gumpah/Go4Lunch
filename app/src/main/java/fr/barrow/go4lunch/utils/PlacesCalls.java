package fr.barrow.go4lunch.utils;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import fr.barrow.go4lunch.model.placesnearby.PlacesList;
import fr.barrow.go4lunch.model.placesnearby.Place;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlacesCalls {
/*
    public interface Callbacks {
        void onSuccess(@Nullable List<Place> placesList);
        void onFailure();
    }

    public static void fetchNearbyPlaces(Callbacks callbacks, String mapApiKey) {
        final WeakReference<Callbacks> callbacksWeakReference = new WeakReference<Callbacks>(callbacks);
        PlacesService placesService = PlacesService.retrofit.create(PlacesService.class);

        Call<PlacesList> call = placesService.getNearbyPlaces(mapApiKey);
        call.enqueue(new Callback<PlacesList>() {
            @Override
            public void onResponse(@NonNull Call<PlacesList> call, @NonNull Response<PlacesList> response) {
                if (callbacksWeakReference.get() != null) {
                    if (response.body() != null) {
                        List<Place> places = response.body().getResults();
                        if (places == null || places.isEmpty()) {
                            callbacksWeakReference.get().onSuccess(new ArrayList<>());
                        } else {
                            callbacksWeakReference.get().onSuccess(places);
                        }
                    } else {
                        callbacksWeakReference.get().onSuccess(new ArrayList<>());
                    }

                }
            }

            @Override
            public void onFailure(Call<PlacesList> call, Throwable t) {
                t.printStackTrace();
                if (callbacksWeakReference.get() != null) callbacksWeakReference.get().onFailure();
            }
        });
    }

 */
}
