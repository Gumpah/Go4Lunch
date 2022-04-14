package fr.barrow.go4lunch.utils;

import fr.barrow.go4lunch.model.places.PlacesList;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PlacesService {
    @GET("maps/api/place/nearbysearch/json?type=restaurant&location=48.8379049,2.2397836&radius=500")
    Call<PlacesList> getNearbyPlaces(@Query("key") String mapApiKey);

    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
