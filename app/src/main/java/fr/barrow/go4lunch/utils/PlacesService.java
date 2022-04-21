package fr.barrow.go4lunch.utils;

import fr.barrow.go4lunch.model.placedetails.PlaceDetailsList;
import fr.barrow.go4lunch.model.placesnearby.PlacesList;
import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PlacesService {

    @GET("maps/api/place/nearbysearch/json?type=restaurant&location=48.8379049,2.2397836&radius=500&opennow=true")
    Observable<PlacesList> getNearbyPlaces(@Query("key") String apiKey);

    @GET("maps/api/place/details/json?")
    Observable<PlaceDetailsList> getPlaceDetails(@Query("key") String apiKey, @Query("place_id") String placeId);

    @GET("maps/api/place/photo?maxwidth=800")
    Observable<String> getPhotoUrl(@Query("key") String apiKey, @Query("photo_reference") String photoReference);


    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();
}
