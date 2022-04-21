package fr.barrow.go4lunch.utils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import fr.barrow.go4lunch.model.placedetails.PlaceDetailsList;
import fr.barrow.go4lunch.model.placesnearby.PlacesList;
import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class PlacesStreams {

    public static Observable<PlacesList> streamFetchNearbyPlaces(String mapApiKey) {
        PlacesService placesService = PlacesService.retrofit.create(PlacesService.class);
        return placesService.getNearbyPlaces(mapApiKey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    public static Observable<PlaceDetailsList> streamFetchPlaceDetails(String mapApiKey, String placeId) {
        PlacesService placesService = PlacesService.retrofit.create(PlacesService.class);
        return placesService.getPlaceDetails(mapApiKey, placeId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    public static Observable<String> streamFetchPhotoUrl(String mapApiKey, String photoReference) {
        PlacesService placesService = PlacesService.retrofit.create(PlacesService.class);
        return placesService.getPhotoUrl(mapApiKey, photoReference)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    public static Observable<PlaceDetailsList> streamFetchNearbyPlacesAndFetchFirstPlaceDetails(String mapApiKey){
        return streamFetchNearbyPlaces(mapApiKey)
                .map(new Function<PlacesList, String>() {
                    @Override
                    public String apply(PlacesList placesList) throws Exception {
                        return placesList.getResults().get(0).getPlaceId();
                    }
                })
                .flatMap(new Function<String, Observable<PlaceDetailsList>>() {
                    @Override
                    public Observable<PlaceDetailsList> apply(String placeId) throws Exception {
                        return streamFetchPlaceDetails(mapApiKey, placeId);
                    }
                });
    }
}
