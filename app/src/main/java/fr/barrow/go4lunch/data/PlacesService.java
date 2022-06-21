package fr.barrow.go4lunch.data;

import fr.barrow.go4lunch.data.model.placedetails.PlaceDetailsResult;
import fr.barrow.go4lunch.data.model.placesnearby.PlacesNearbyResult;
import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PlacesService {

    @GET("maps/api/place/nearbysearch/json?type=restaurant&radius=60&opennow=true") //re-add opennow=true & put radius back to 500
    Observable<PlacesNearbyResult> getNearbyPlaces(@Query("key") String apiKey, @Query("location") String location);

    @GET("maps/api/place/details/json?")
    Observable<PlaceDetailsResult> getPlaceDetails(@Query("key") String apiKey, @Query("place_id") String placeId);

    @GET("maps/api/place/photo?maxwidth=800")
    Observable<Response<String>> getPhotoUrl(@Query("key") String apiKey, @Query("photo_reference") String photoReference);

    Retrofit retrofitGsonConverter = new Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();

    Retrofit retrofitScalarsConverter = new Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();
}
