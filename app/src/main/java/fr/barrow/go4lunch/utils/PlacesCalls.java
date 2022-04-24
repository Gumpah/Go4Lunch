package fr.barrow.go4lunch.utils;

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
