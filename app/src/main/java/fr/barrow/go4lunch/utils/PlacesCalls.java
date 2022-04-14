package fr.barrow.go4lunch.utils;

import java.lang.ref.WeakReference;
import java.util.List;

import javax.annotation.Nullable;

import fr.barrow.go4lunch.model.places.PlacesList;
import fr.barrow.go4lunch.model.places.Place;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlacesCalls {

    public interface Callbacks {
        void onResponse(@Nullable List<Place> placesList);
        void onFailure();
    }

    public static void fetchNearbyPlaces(Callbacks callbacks, String mapApiKey) {
        final WeakReference<Callbacks> callbacksWeakReference = new WeakReference<Callbacks>(callbacks);
        PlacesService placesService = PlacesService.retrofit.create(PlacesService.class);

        Call<PlacesList> call = placesService.getNearbyPlaces(mapApiKey);
        call.enqueue(new Callback<PlacesList>() {
            @Override
            public void onResponse(Call<PlacesList> call, Response<PlacesList> response) {
                if (callbacksWeakReference.get() != null) callbacksWeakReference.get().onResponse(response.body().getResults());
            }

            @Override
            public void onFailure(Call<PlacesList> call, Throwable t) {
                if (callbacksWeakReference.get() != null) callbacksWeakReference.get().onFailure();
            }
        });
    }

}
