package fr.barrow.go4lunch.data;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import fr.barrow.go4lunch.model.Restaurant;
import fr.barrow.go4lunch.model.places.Place;

public class RestaurantRepository {

    private static final String COLLECTION_NAME = "restaurants";
    private static final String USERNAME_FIELD = "username";

    private static volatile RestaurantRepository instance;

    private RestaurantRepository() { }

    public static RestaurantRepository getInstance() {
        RestaurantRepository result = instance;
        if (result != null) {
            return result;
        }
        synchronized(RestaurantRepository.class) {
            if (instance == null) {
                instance = new RestaurantRepository();
            }
            return instance;
        }
    }

    // Get the Collection Reference
    private CollectionReference getRestaurantsCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // Create User in Firestore
    public void createRestaurant(Place place) {
    }

    /*
    // Get User Data from Firestore
    public Task<DocumentSnapshot> getRestaurantData(){
    }

    // Delete the User from Firestore
    public void deleteRestaurantFromFirestore() {
        String uid = this.getCurrentUser().getUid();
        if(uid != null){
            this.getRestaurantsCollection().document(uid).delete();
        }
    }
    */
}

