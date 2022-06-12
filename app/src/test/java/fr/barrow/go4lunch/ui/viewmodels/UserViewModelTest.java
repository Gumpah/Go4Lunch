package fr.barrow.go4lunch.ui.viewmodels;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import fr.barrow.go4lunch.LiveDataTestUtils;
import fr.barrow.go4lunch.data.UserRepository;
import fr.barrow.go4lunch.model.User;
import fr.barrow.go4lunch.model.UserStateItem;
import fr.barrow.go4lunch.utils.NetworkMonitoring;
import fr.barrow.go4lunch.utils.NetworkStateManager;

@RunWith(MockitoJUnitRunner.class)
public class UserViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private UserViewModel mUserViewModel;

    @Mock
    UserRepository mUserRepository;

    @Mock
    NetworkMonitoring mNetworkMonitoring;

    @Mock
    NetworkStateManager mNetworkStateManager;

    @Mock
    UserViewModel spyUserViewModel;

    @Before
    public void setUp() throws Exception {
        mUserViewModel = new UserViewModel(mUserRepository, mNetworkMonitoring);
        spyUserViewModel = spy(mUserViewModel);
    }

    @Test
    public void getConnectionStatus() {
        MockedStatic<NetworkStateManager> utilities = mockStatic(NetworkStateManager.class);
        MutableLiveData<Boolean> expected_liveData = new MutableLiveData<>(true);
        when(mNetworkStateManager.getNetworkConnectivityStatus()).thenReturn(expected_liveData);
        utilities.when(NetworkStateManager::getInstance).thenReturn(mNetworkStateManager);

        LiveData<Boolean> actual_liveData = mUserViewModel.getConnectionStatus();
        assertEquals(expected_liveData.getValue(), actual_liveData.getValue());
    }

    @Test
    public void mapListDataToViewState() {
        String expected_uid = "1";
        String expected_username = "username";
        String expected_urlPicture = "url.com";
        User user = new User(expected_uid, expected_username, expected_urlPicture);

        List<User> userList = Collections.singletonList(user);
        MutableLiveData<List<User>> liveDataUserList = new MutableLiveData<>(userList);
        LiveData<List<UserStateItem>> liveDataUserStateItemList = mUserViewModel.mapListDataToViewState(liveDataUserList);
        LiveDataTestUtils.observeForTesting(liveDataUserStateItemList, liveData -> {
            List<UserStateItem> userStateItemList = liveData.getValue();
            UserStateItem userStateItem = new UserStateItem();
            if (userStateItemList != null) {
                userStateItem = userStateItemList.get(0);
            }
            String actual_uid = userStateItem.getUid();
            String actual_username = userStateItem.getUsername();
            String actual_urlPicture = userStateItem.getUrlPicture();

            assertEquals(expected_uid, actual_uid);
            assertEquals(expected_username, actual_username);
            assertEquals(expected_urlPicture, actual_urlPicture);
        });

    }

    @Test
    public void mapDataToViewState() {
        String expected_uid = "12";
        String expected_username = "username";
        String expected_urlPicture = "url.com";
        User user = new User(expected_uid, expected_username, expected_urlPicture);
        MutableLiveData<User> liveDataUser = new MutableLiveData<>(user);
        LiveData<UserStateItem> liveDataUserStateItem = mUserViewModel.mapDataToViewState(liveDataUser);
        LiveDataTestUtils.observeForTesting(liveDataUserStateItem, liveData -> {
            UserStateItem userStateItem = new UserStateItem();
            if (liveData.getValue() != null) {
                userStateItem = liveData.getValue();
            }
            String actual_uid = userStateItem.getUid();
            String actual_username = userStateItem.getUsername();
            String actual_urlPicture = userStateItem.getUrlPicture();

            assertEquals(expected_uid, actual_uid);
            assertEquals(expected_username, actual_username);
            assertEquals(expected_urlPicture, actual_urlPicture);
        });
    }

    @Mock
    MutableLiveData<List<User>> mListUserStateItemMutableLiveData;

    @Test
    public void getEveryFirestoreUserWhoPickedThisRestaurant() {
        String restaurantId = "123";
        when(mUserRepository.getEveryFirestoreUserWhoPickedThisRestaurant(restaurantId)).thenReturn(mListUserStateItemMutableLiveData);

        spyUserViewModel.getEveryFirestoreUserWhoPickedThisRestaurant(restaurantId);

        verify(spyUserViewModel, times(1)).mapListDataToViewState(mListUserStateItemMutableLiveData);
    }

    @Test
    public void getEveryUserWhoPickedARestaurant() {
        when(mUserRepository.getEveryUserWhoPickedARestaurant()).thenReturn(mListUserStateItemMutableLiveData);

        spyUserViewModel.getEveryUserWhoPickedARestaurant();

        verify(spyUserViewModel, times(1)).mapListDataToViewState(mListUserStateItemMutableLiveData);
    }

    @Test
    public void getEveryFirestoreUser() {
        when(mUserRepository.getEveryFirestoreUser()).thenReturn(mListUserStateItemMutableLiveData);

        spyUserViewModel.getEveryFirestoreUser();

        verify(spyUserViewModel, times(1)).mapListDataToViewState(mListUserStateItemMutableLiveData);
    }

    @Mock
    MutableLiveData<User> mUserMutableLiveData;

    @Test
    public void getUpdatedLocalUserData() {
        when(mUserRepository.getUpdatedLocalUserData()).thenReturn(mUserMutableLiveData);

        spyUserViewModel.getUpdatedLocalUserData();

        verify(spyUserViewModel, times(1)).mapDataToViewState(mUserMutableLiveData);
    }

    @Test
    public void createUser() {
        mUserViewModel.createUser();

        verify(mUserRepository, times(1)).createUser();
    }

    @Test
    public void updateLocalUserData() {
        String expected_uid = "1234";
        String expected_username = "username";
        String expected_urlPicture = "url.com";
        User expected_user = new User(expected_uid, expected_username, expected_urlPicture);

        when(mUserRepository.getUser()).thenReturn(expected_user);

        mUserViewModel.updateLocalUserData();
        MutableLiveData<User> userLiveData = mUserViewModel.mUser;

        verify(mUserRepository, times(1)).updateLocalUserData();

        LiveDataTestUtils.observeForTesting(userLiveData, liveData -> {
            User actual_user = new User();
            if (liveData.getValue() != null) {
                actual_user = liveData.getValue();
            }
            String actual_uid = actual_user.getUid();
            String actual_username = actual_user.getUsername();
            String actual_urlPicture = actual_user.getUrlPicture();

            assertEquals(expected_uid, actual_uid);
            assertEquals(expected_username, actual_username);
            assertEquals(expected_urlPicture, actual_urlPicture);
        });
    }

    @Test
    public void isCurrentUserLogged() {
        Boolean expected_boolean = false;
        when(mUserRepository.getCurrentFirebaseUser()).thenReturn(null);

        Boolean actual_boolean = mUserViewModel.isCurrentUserLogged();

        assertEquals(expected_boolean, actual_boolean);
    }

    @Test
    public void setPickedRestaurant() {
    }

    @Test
    public void removePickedRestaurant() {
    }

    @Test
    public void addLikedRestaurant() {
    }

    @Test
    public void removeLikedRestaurant() {
    }

    @Test
    public void getCurrentUser() {
    }

    @Test
    public void signOut() {
    }
}