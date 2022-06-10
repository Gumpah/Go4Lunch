package fr.barrow.go4lunch.data;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import fr.barrow.go4lunch.model.User;

public class UserRepository {

    private final FirebaseHelper mFirebaseHelper;

    private User user;

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

    public void test() {
        testMethodToCall();
    }

    public void testMethodToCall() {
    }

    // Create User in Firestore
    public void createUser() {
        FirebaseUser user = mFirebaseHelper.getCurrentUser();
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

                    mFirebaseHelper.getUserDocumentReference().set(userToCreate);
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
        if (mFirebaseHelper.getCurrentUser() != null) {
            return mFirebaseHelper.getUser();
        } else {
            return null;
        }
    }

    public void sendUserDataToFirestore() {
        mFirebaseHelper.getUserDocumentReference().set(user);
        getUpdatedLocalUserData();
    }

    public void updateUserData() {
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

    public MutableLiveData<List<User>> getAllUsersWhoPickedARestaurant(String restaurantId) {
        mFirebaseHelper.getAllUsersWhoPickedARestaurant(restaurantId).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<User> users = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    User user = document.toObject(User.class);
                    if (!mFirebaseHelper.getCurrentUserUid().equals(user.getUid())) users.add(user);
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
                    if (!mFirebaseHelper.getCurrentUserUid().equals(user.getUid())) users.add(user);
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
                    if (!mFirebaseHelper.getCurrentUserUid().equals(user.getUid())) users.add(user);
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

    public MutableLiveData<User> getUpdatedLocalUserData() {
        System.out.println("1");
        mFirebaseHelper.getUser().addOnCompleteListener(task -> {
            System.out.println("2");
            if (task.isSuccessful()) {
                User user;
                user = task.getResult().toObject(User.class);
                localUser.postValue(user);
            } else {
                Log.d("Error", "Error getting documents: ", task.getException());
            }
        }).addOnFailureListener(e -> {
            System.out.println("3");
            //handle error
            localUser.postValue(null);
        });
        return localUser;
    }

    @Nullable
    public FirebaseUser getCurrentUser() {
        return mFirebaseHelper.getCurrentUser();
    }

    public Task<Void> signOut(Context context){
        return mFirebaseHelper.getAuthUI().signOut(context);
    }

    /*
    public Task<Void> deleteUserFromFirebase(Context context){
        return AuthUI.getInstance().delete(context);
    }

    public void deleteUserFromFirestore() {
        if (getCurrentUser() != null) {
            String uid = getCurrentUser().getUid();
            mFirebaseHelper.getUserDocumentReference().delete();
        }
    }
     */
}
