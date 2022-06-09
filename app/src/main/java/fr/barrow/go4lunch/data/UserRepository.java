package fr.barrow.go4lunch.data;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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

    private final MutableLiveData<List<User>> listOfUsersPickedRestaurant;
    private final MutableLiveData<List<User>> listOfUsersPickedRestaurantFromArray;
    private final MutableLiveData<List<User>> listOfAllUsers;
    private final MutableLiveData<User> userNew;
    private Context mContext;

    public UserRepository(Context context) {
        mContext = context;
        mFirebaseHelper = new FirebaseHelper();
        listOfUsersPickedRestaurant = new MutableLiveData<>();
        listOfUsersPickedRestaurantFromArray = new MutableLiveData<>();
        listOfAllUsers = new MutableLiveData<>();
        userNew = new MutableLiveData<>();
    }

    // Get the Collection Reference
    public CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // Create User in Firestore
    public void createUser() {
        FirebaseUser user = getCurrentUser();
        mFirebaseHelper.getUser().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    updateUserData();
                } else if (user != null) {
                    String urlPicture = (user.getPhotoUrl() != null) ? user.getPhotoUrl().toString() : null;
                    String username = user.getDisplayName();
                    String uid = user.getUid();

                    User userToCreate = new User(uid, username, urlPicture);

                    getUsersCollection().document(uid).set(userToCreate);
                }
            }
        }).addOnFailureListener(e -> {
            //handle error
        });
    }

    public User getUser() {
        return user;
    }

    public Task<DocumentSnapshot> getUserData() {
        String uid = getCurrentUser().getUid();
        if(uid != null) {
            DocumentReference docRef = FirebaseFirestore.getInstance().collection(COLLECTION_NAME).document(uid);
            return docRef.get();
        } else {
            return null;
        }
    }

    public void sendUserDataToFirestore() {
        String uid = getCurrentUser().getUid();
        getUserData().addOnSuccessListener(documentSnapshot -> {
            getUsersCollection().document(uid).set(user);
            getUserNew();
        });
    }

    public void updateUserData() {
        getUserData().addOnSuccessListener(documentSnapshot -> user = documentSnapshot.toObject(User.class));
    }

    public String getCurrentUserUid() {
        return getCurrentUser().getUid();
    }

    public void setPickedRestaurant(String restaurantId, String restaurantName) {
        user.setPickedRestaurant(restaurantId, restaurantName);
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
        if (getCurrentUser() != null) {
            String uid = getCurrentUser().getUid();
            if(uid != null){
                getUsersCollection().document(uid).delete();
            }
        }

    }

    public MutableLiveData<List<User>> getAllUsersWhoPickedARestaurant(String restaurantId) {
        mFirebaseHelper.getAllUsersWhoPickedARestaurant(restaurantId).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<User> users = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    User user = document.toObject(User.class);
                    if (getCurrentUser() != null) {
                        if (!getCurrentUser().getUid().equals(user.getUid())) users.add(user);
                    } else {
                        users.add(user);
                    }
                }
                listOfUsersPickedRestaurant.postValue(users);
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
                    User user = document.toObject(User.class);
                    if (getCurrentUser() != null) {
                        if (!getCurrentUser().getUid().equals(user.getUid())) users.add(user);
                    } else {
                        users.add(user);
                    }
                }
                listOfUsersPickedRestaurantFromArray.postValue(users);
            } else {
                Log.d("Error", "Error getting documents: ", task.getException());
            }
        }).addOnFailureListener(e -> {
            //handle error
            listOfUsersPickedRestaurantFromArray.postValue(null);
        });
        return listOfUsersPickedRestaurantFromArray;
    }

    public MutableLiveData<List<User>> getAllUsers() {
        mFirebaseHelper.getAllUsers().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<User> users = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    User user = document.toObject(User.class);
                    if (!user.getUid().equals(getCurrentUserUid())) users.add(user);
                }
                listOfAllUsers.postValue(users);
            } else {
                Log.d("Error", "Error getting documents: ", task.getException());
            }
        }).addOnFailureListener(e -> {
            //handle error
            listOfAllUsers.postValue(null);
        });
        return listOfAllUsers;
    }

    public MutableLiveData<User> getUserNew() {
        mFirebaseHelper.getUser().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                User user;
                user = task.getResult().toObject(User.class);
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
    public FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public Task<Void> signOut(Context context){
        return AuthUI.getInstance().signOut(mContext);
    }

    public Task<Void> deleteUser(Context context){
        return AuthUI.getInstance().delete(context);
    }

}
