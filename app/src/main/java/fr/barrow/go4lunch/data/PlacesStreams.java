package fr.barrow.go4lunch.data;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import fr.barrow.go4lunch.model.placedetails.CombinedPlaceAndString;
import fr.barrow.go4lunch.model.placedetails.PlaceDetailsResult;
import fr.barrow.go4lunch.model.placesnearby.Place;
import fr.barrow.go4lunch.model.placesnearby.PlacesNearbyResult;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class PlacesStreams {

    public Observable<PlacesNearbyResult> streamFetchNearbyPlaces(String mapApiKey, String location) {
        PlacesService placesService = PlacesService.retrofitGsonConverter.create(PlacesService.class);
        return placesService.getNearbyPlaces(mapApiKey, location)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    public Observable<PlaceDetailsResult> streamFetchPlaceDetails(String mapApiKey, String placeId) {
        PlacesService placesService = PlacesService.retrofitGsonConverter.create(PlacesService.class);
        return placesService.getPlaceDetails(mapApiKey, placeId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    public Observable<CombinedPlaceAndString> streamFetchPhotoUrlAndAddToCombinedObject(String mapApiKey, String photoReference, CombinedPlaceAndString combinedPlaceAndString) {
        PlacesService placesService = PlacesService.retrofitScalarsConverter.create(PlacesService.class);
        if (photoReference == null) {
            return Observable.just(combinedPlaceAndString)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .timeout(10, TimeUnit.SECONDS);
        }
        return placesService.getPhotoUrlTest(mapApiKey, photoReference)
                .map(body -> {
                    combinedPlaceAndString.setPhotoUrl(body.raw().request().url().toString());
                    return combinedPlaceAndString;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    public Observable<CombinedPlaceAndString> streamCombinePlaceDetailsAndPhotoUrl(String mapApiKey, String placeId) {
        return streamFetchPlaceDetails(mapApiKey, placeId)
                .map(placeDetailsResult -> {
                    CombinedPlaceAndString combinedPlaceAndString = new CombinedPlaceAndString(placeDetailsResult, null);
                    if (combinedPlaceAndString.getPlaceDetailsResult().getResult().getPhotos() != null) {
                        combinedPlaceAndString.setPhotoReference(combinedPlaceAndString.getPlaceDetailsResult().getResult().getPhotos().get(0).getPhotoReference());
                    } else {
                        combinedPlaceAndString.setPhotoReference(null);
                    }
                    return combinedPlaceAndString;
                })
                .flatMap(combinedPlaceAndString -> streamFetchPhotoUrlAndAddToCombinedObject(mapApiKey,
                        combinedPlaceAndString.getPhotoReference(),
                        combinedPlaceAndString));
    }

    public Observable<CombinedPlaceAndString> streamFetchNearbyPlacesAndFetchTheirDetails(String mapApiKey, String location){
        return streamFetchNearbyPlaces(mapApiKey, location)
                .map(placesNearbyResult -> {
                    ArrayList<String> placeIdList = new ArrayList<>();
                    for (Place place : placesNearbyResult.getResults()) {
                        placeIdList.add(place.getPlaceId());
                    }
                    return placeIdList;
                })
                .flatMap(Observable::fromIterable)
                .flatMap(placeId -> streamCombinePlaceDetailsAndPhotoUrl(mapApiKey, placeId));
    }
}
