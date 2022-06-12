package fr.barrow.go4lunch.ui.viewmodels;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import fr.barrow.go4lunch.data.UserRepository;
import fr.barrow.go4lunch.model.User;
import fr.barrow.go4lunch.model.UserStateItem;
import fr.barrow.go4lunch.utils.NetworkMonitoring;
import fr.barrow.go4lunch.utils.NetworkStateManager;

public class UserViewModel extends ViewModel {

    UserRepository mUserRepository;
    MutableLiveData<User> mUser;
    public NetworkMonitoring mNetworkMonitoring;

    public UserViewModel(UserRepository userRepository, NetworkMonitoring networkMonitoring) {
        mUserRepository = userRepository;
        mUser = new MutableLiveData<>();
        mNetworkMonitoring = networkMonitoring;
        mNetworkMonitoring.checkNetworkState();
        mNetworkMonitoring.registerNetworkCallbackEvents();
    }

    public LiveData<Boolean> getConnectionStatus() {
        return NetworkStateManager.getInstance().getNetworkConnectivityStatus();
    }

    public LiveData<List<UserStateItem>> mapListDataToViewState(LiveData<List<User>> users) {
        return Transformations.map(users, user -> {
            List<UserStateItem> userViewStateItems = new ArrayList<>();
            for (User u : user) {
                userViewStateItems.add(
                        new UserStateItem(u)
                );
            }
            return userViewStateItems;
        });
    }

    public LiveData<UserStateItem> mapDataToViewState(LiveData<User> user) {
        return Transformations.map(user, UserStateItem::new);
    }

    public LiveData<List<UserStateItem>> getEveryFirestoreUserWhoPickedThisRestaurant(String restaurantId) {
        return mapListDataToViewState(mUserRepository.getEveryFirestoreUserWhoPickedThisRestaurant(restaurantId));
    }

    public LiveData<List<UserStateItem>> getEveryUserWhoPickedARestaurant() {
        return mapListDataToViewState(mUserRepository.getEveryUserWhoPickedARestaurant());
    }

    public LiveData<List<UserStateItem>> getEveryFirestoreUser() {
        return mapListDataToViewState(mUserRepository.getEveryFirestoreUser());
    }

    public LiveData<UserStateItem> getUpdatedLocalUserData() {
        return mapDataToViewState(mUserRepository.getUpdatedLocalUserData());
    }

    public void createUser() {
        mUserRepository.createUser();
    }

    public void updateLocalUserData() {
        mUserRepository.updateLocalUserData();
        mUser.setValue(mUserRepository.getUser());
    }

    public Boolean isCurrentUserLogged() {
        return (mUserRepository.getCurrentFirebaseUser() != null);
    }

    public void setPickedRestaurant(String restaurantId, String restaurantName) {
        mUserRepository.setPickedRestaurant(restaurantId, restaurantName);
        mUser.setValue(mUserRepository.getUser());
    }

    public void removePickedRestaurant() {
        mUserRepository.removePickedRestaurant();
        mUser.setValue(mUserRepository.getUser());
    }

    public void addLikedRestaurant(String restaurantId) {
        mUserRepository.addLikedRestaurant(restaurantId);
        mUser.setValue(mUserRepository.getUser());
    }

    public void removeLikedRestaurant(String restaurantId) {
        mUserRepository.removeLikedRestaurant(restaurantId);
        mUser.setValue(mUserRepository.getUser());
    }

    @Nullable
    public FirebaseUser getCurrentUser(){
        return mUserRepository.getCurrentFirebaseUser();
    }

    public Task<Void> signOut(Context context){
        return mUserRepository.signOut(context);
    }
    
    /*
    public Task<Void> deleteUserFromFirebase(Context context){
        return mUserRepository.deleteUserFromFirebase(context);
    }
     */
}
