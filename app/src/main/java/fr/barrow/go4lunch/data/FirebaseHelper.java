package fr.barrow.go4lunch.data;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;

public class FirebaseHelper {

    private final String COLLECTION_NAME = "users";
    private final String PICKED_RESTAURANT_FIELD = "pickedRestaurant";

    public final FirebaseFirestore db = FirebaseFirestore.getInstance();
    public final FirebaseAuth auth = FirebaseAuth.getInstance();

    public final CollectionReference usersRef = db.collection(COLLECTION_NAME);


    public FirebaseHelper() {
    }

    public String getCurrentUserUid() {
        return auth.getCurrentUser().getUid();
    }

    public Task<DocumentSnapshot> getUser(){
        return usersRef.document(getCurrentUserUid()).get();
    }

    public Task<QuerySnapshot> getAllUsersWhoPickedARestaurant(String restaurantId) {
        return usersRef.whereEqualTo(PICKED_RESTAURANT_FIELD, restaurantId).get();
    }

    public Task<QuerySnapshot> getUsersWhoPickedARestaurant() {
        return usersRef.whereNotEqualTo(PICKED_RESTAURANT_FIELD, null).get();
    }

    public Task<QuerySnapshot> getAllUsers() {
        return usersRef.orderBy(PICKED_RESTAURANT_FIELD, Query.Direction.DESCENDING).get();
    }
}
