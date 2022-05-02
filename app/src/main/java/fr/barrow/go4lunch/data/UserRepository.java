package fr.barrow.go4lunch.data;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import fr.barrow.go4lunch.model.User;

public class UserRepository {

    private final String COLLECTION_NAME = "users";
    private final String USERNAME_FIELD = "username";
    private final String LIKED_RESTAURANTS_FIELD = "likedRestaurants";
    private final String PICKED_RESTAURANT_FIELD = "pickedRestaurant";
    private final FirebaseHelper mFirebaseHelper;


    private User user;
    private MutableLiveData<ArrayList<User>> usersWhoPickedRestaurant;

    private final MutableLiveData<List<User>> listOfUsersPickedRestaurant = new MutableLiveData<>();
    private final MutableLiveData<List<User>> listOfUsersPickedRestaurantFromArray = new MutableLiveData<>();
    private final MutableLiveData<User> userNew = new MutableLiveData<>();

    public UserRepository() {
        mFirebaseHelper = new FirebaseHelper();
        usersWhoPickedRestaurant = new MutableLiveData<>();
    }

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
            // If the user already exist in Firestore, we get his data
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
            System.out.println("Should be first");
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
        if(getCurrentUserUid() != null) {
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

    public MutableLiveData<List<User>> getAllUsersWhoPickedARestaurant(String restaurantId) {
        mFirebaseHelper.getAllUsersWhoPickedARestaurant(restaurantId).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<User> users = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    users.add(document.toObject(User.class));
                }
                listOfUsersPickedRestaurant.postValue(users);
                System.out.println("Should be second");
            } else {
                Log.d("Error", "Error getting documents: ", task.getException());
            }
        }).addOnFailureListener(e -> {
            //handle error
            listOfUsersPickedRestaurant.postValue(null);
        });
        return listOfUsersPickedRestaurant;
    }

    public MutableLiveData<List<User>> getUsersWhoPickedARestaurant() {
        mFirebaseHelper.getUsersWhoPickedARestaurant().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<User> users = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    users.add(document.toObject(User.class));
                }
                listOfUsersPickedRestaurantFromArray.postValue(users);
                System.out.println("Should be second");
            } else {
                Log.d("Error", "Error getting documents: ", task.getException());
            }
        }).addOnFailureListener(e -> {
            //handle error
            listOfUsersPickedRestaurantFromArray.postValue(null);
        });
        return listOfUsersPickedRestaurantFromArray;
    }

    public MutableLiveData<User> getUserNew() {
        mFirebaseHelper.getUser().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                User user;
                user = task.getResult().toObject(User.class);
                System.out.println("Test" + user.getUsername());
                userNew.postValue(user);
            } else {
                Log.d("Error", "Error getting documents: ", task.getException());
            }
        }).addOnFailureListener(e -> {
            //handle error
            userNew.postValue(null);
        });
        return userNew;
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
