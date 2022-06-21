package fr.barrow.go4lunch.data;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import fr.barrow.go4lunch.data.model.User;

public class UserRepository {

    FirebaseHelper mFirebaseHelper;

    @VisibleForTesting
    public User user;

    private final MutableLiveData<List<User>> listOfUsersPickedRestaurant;
    private final MutableLiveData<List<User>> listOfUsersPickedRestaurantFromArray;
    private final MutableLiveData<List<User>> listOfAllUsers;

    private final MutableLiveData<User> localUser;

    public UserRepository(FirebaseHelper firebaseHelper) {
        mFirebaseHelper = firebaseHelper;
        listOfUsersPickedRestaurant = new MutableLiveData<>();
        listOfUsersPickedRestaurantFromArray = new MutableLiveData<>();
        listOfAllUsers = new MutableLiveData<>();
        localUser = new MutableLiveData<>();
    }

    // Create User in Firestore
    public void createUser() {
        FirebaseUser user = mFirebaseHelper.getCurrentFirebaseUser();
        mFirebaseHelper.getFirestoreUserDocumentReference().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    updateLocalUserData();
                } else if (mFirebaseHelper.isFirebaseUserNotNull()) {
                    String urlPicture = (user.getPhotoUrl() != null) ? user.getPhotoUrl().toString() : null;
                    String username = user.getDisplayName();
                    String uid = user.getUid();
                    User userToCreate = new User(uid, username, urlPicture);
                    DocumentReference documentReference = mFirebaseHelper.getUserDocumentReference();
                    documentReference.set(userToCreate);
                }
            }
        }).addOnFailureListener(e -> {
        });
    }

    public User getUser() {
        return user;
    }

    public Task<DocumentSnapshot> getUserData() {
        if (mFirebaseHelper.isFirebaseUserNotNull()) {
            return mFirebaseHelper.getFirestoreUserDocumentReference();
        } else {
            return null;
        }
    }

    public void sendUserDataToFirestore() {
        mFirebaseHelper.getUserDocumentReference().set(user);
        getUpdatedLocalUserData();
    }

    public void updateLocalUserData() {
        getUserData().addOnSuccessListener(documentSnapshot -> user = documentSnapshot.toObject(User.class));
    }

    public void setPickedRestaurant(String restaurantId, String restaurantName) {
        getUser().setPickedRestaurant(restaurantId, restaurantName);
        sendUserDataToFirestore();
    }

    public void removePickedRestaurant() {
        getUser().removePickedRestaurant();
        sendUserDataToFirestore();
    }

    public void addLikedRestaurant(String restaurantId) {
        getUser().addLikedRestaurant(restaurantId);
        sendUserDataToFirestore();
    }

    public void removeLikedRestaurant(String restaurantId) {
        getUser().removeLikedRestaurant(restaurantId);
        sendUserDataToFirestore();
    }

    public MutableLiveData<List<User>> getEveryFirestoreUserWhoPickedThisRestaurant(String restaurantId) {
        mFirebaseHelper.getEveryFirestoreUserWhoPickedThisRestaurant(restaurantId).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<User> users = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    User user = document.toObject(User.class);
                    if (!mFirebaseHelper.getCurrentFirebaseUserUID().equals(user.getUid())) {
                        users.add(user);
                    }
                }
                listOfUsersPickedRestaurant.postValue(users);
            } else {
                Log.d("Error", "Error getting documents: ", task.getException());
            }
        }).addOnFailureListener(e -> {
            listOfUsersPickedRestaurant.postValue(null);
        });
        return listOfUsersPickedRestaurant;
    }

    public MutableLiveData<List<User>> getEveryUserWhoPickedARestaurant() {
        mFirebaseHelper.getEveryUserWhoPickedARestaurant().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<User> users = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    User user = document.toObject(User.class);
                    if (!mFirebaseHelper.getCurrentFirebaseUserUID().equals(user.getUid())) users.add(user);
                }
                listOfUsersPickedRestaurantFromArray.postValue(users);
            } else {
                Log.d("Error", "Error getting documents: ", task.getException());
            }
        }).addOnFailureListener(e -> {
            listOfUsersPickedRestaurantFromArray.postValue(null);
        });
        return listOfUsersPickedRestaurantFromArray;
    }

    public MutableLiveData<List<User>> getEveryFirestoreUser() {
        mFirebaseHelper.getEveryFirestoreUser().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<User> users = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    User user = document.toObject(User.class);
                    if (!mFirebaseHelper.getCurrentFirebaseUserUID().equals(user.getUid())) users.add(user);
                }
                listOfAllUsers.postValue(users);
            } else {
                Log.d("Error", "Error getting documents: ", task.getException());
            }
        }).addOnFailureListener(e -> {
            listOfAllUsers.postValue(null);
        });
        return listOfAllUsers;
    }

    public MutableLiveData<User> getUpdatedLocalUserData() {
        mFirebaseHelper.getFirestoreUserDocumentReference().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                User user;
                user = task.getResult().toObject(User.class);
                localUser.postValue(user);
            } else {
                Log.d("Error", "Error getting documents: ", task.getException());
            }
        }).addOnFailureListener(e -> {
            localUser.postValue(null);
        });
        return localUser;
    }

    @Nullable
    public FirebaseUser getCurrentFirebaseUser() {
        return mFirebaseHelper.getCurrentFirebaseUser();
    }

    public Task<Void> signOut(Context context){
        return mFirebaseHelper.getAuthUI().signOut(context);
    }
}
