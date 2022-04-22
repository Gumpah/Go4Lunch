package fr.barrow.go4lunch.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import fr.barrow.go4lunch.model.placedetails.PlaceDetailsList;
import fr.barrow.go4lunch.model.placesnearby.Place;
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
                .map(new Function<PlacesList, ArrayList<String>>() {
                    @Override
                    public ArrayList<String> apply(PlacesList placesList) throws Exception {
                        ArrayList<String> placeIdList = new ArrayList<>();
                        for (Place place : placesList.getResults()) {
                            placeIdList.add(place.getPlaceId());
                        }
                        return placeIdList;
                    }
                })
                .flatMap(Observable::fromIterable)
                .flatMap(placeId -> streamFetchPlaceDetails(mapApiKey, placeId));
    }
}
