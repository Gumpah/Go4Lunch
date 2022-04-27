package fr.barrow.go4lunch.data;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import fr.barrow.go4lunch.model.User;

public class UserRepository {

    private static final String COLLECTION_NAME = "users";
    private static final String USERNAME_FIELD = "username";
    private static final String LIKED_RESTAURANTS_FIELD = "likedRestaurants";
    private static final String PICKED_RESTAURANT_FIELD = "pickedRestaurant";

    private static volatile UserRepository instance;

    private User user;

    public UserRepository() { }

    // Get the Collection Reference
    public CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // Create User in Firestore
    public void createUser() {
        FirebaseUser user = getCurrentUser();
        if(user == null){
            String urlPicture = (user.getPhotoUrl() != null) ? user.getPhotoUrl().toString() : null;
            String username = user.getDisplayName();
            String uid = user.getUid();

            User userToCreate = new User(uid, username, urlPicture);

            Task<DocumentSnapshot> userData = getUserData();
            // If the user already exist in Firestore, we get his data (isMentor)
            userData.addOnSuccessListener(documentSnapshot -> {
                this.getUsersCollection().document(uid).set(userToCreate);
            });
        } else {
            updateUserData();
        }
    }

    public User getUser() {
        return user;
    }

    // Get User Data from Firestore
    public Task<DocumentSnapshot> getUserData() {
        String uid = this.getCurrentUser().getUid();
        if(uid != null) {
            DocumentReference docRef = FirebaseFirestore.getInstance().collection(COLLECTION_NAME).document(uid);
            return docRef.get();
        } else {
            return null;
        }
    }

    public void sendUserDataToFirestore() {
        String uid = this.getCurrentUser().getUid();
        getUserData().addOnSuccessListener(documentSnapshot -> {
            this.getUsersCollection().document(uid).set(user);
        });
    }

    public void updateUserData() {
        getUserData().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                user = documentSnapshot.toObject(User.class);
            }
        });
    }

    public String getCurrentUserUid() {
        return this.getCurrentUser().getUid();
    }

    public void setPickedRestaurant(String restaurantId) {
        user.setPickedRestaurant(restaurantId);
        if(getCurrentUserUid() != null){
            sendUserDataToFirestore();
        }
    }

    public void removePickedRestaurant() {
        user.removePickedRestaurant();
        if(getCurrentUserUid() != null){
            sendUserDataToFirestore();
        }
    }

    public void addLikedRestaurant(String restaurantId) {
        user.addLikedRestaurant(restaurantId);
        if(getCurrentUserUid() != null){
            sendUserDataToFirestore();
        }
    }

    public void removeLikedRestaurant(String restaurantId) {
        user.removeLikedRestaurant(restaurantId);
        if(getCurrentUserUid() != null){
            sendUserDataToFirestore();
        }
    }

    public void deleteUserFromFirestore() {
        String uid = this.getCurrentUser().getUid();
        if(uid != null){
            this.getUsersCollection().document(uid).delete();
        }
    }

    @Nullable
    public FirebaseUser getCurrentUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public Task<Void> signOut(Context context){
        return AuthUI.getInstance().signOut(context);
    }

    public Task<Void> deleteUser(Context context){
        return AuthUI.getInstance().delete(context);
    }

}
