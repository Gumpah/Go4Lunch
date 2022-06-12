package fr.barrow.go4lunch.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static fr.barrow.go4lunch.ListenersTestUtil.testOnCompleteListener;
import static fr.barrow.go4lunch.ListenersTestUtil.testOnSuccessListener;

import android.content.Context;
import android.net.Uri;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jraska.livedata.TestObserver;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.barrow.go4lunch.LiveDataTestUtils;
import fr.barrow.go4lunch.model.User;

@RunWith(MockitoJUnitRunner.class)
public class UserRepositoryTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private FirebaseHelper mFirebaseHelper;

    @Mock
    private DocumentSnapshot documentSnapshot;

    @Mock
    private Task<DocumentSnapshot> documentSnapshotTask;

    @Mock
    private Task<QuerySnapshot> querySnapshotTask;

    @Mock
    private QuerySnapshot querySnapshot;

    @Mock
    private QueryDocumentSnapshot queryDocumentSnapshot;

    private UserRepository mUserRepository;

    private UserRepository spyUserRepository;

    private int idIncrementing;

    @Before
    public void setUp() {
        mUserRepository = new UserRepository(mFirebaseHelper);
        spyUserRepository = spy(mUserRepository);
        idIncrementing = 1;
    }

    private String getNextID() {
        idIncrementing++;
        return String.valueOf(idIncrementing);
    }

    @Mock
    FirebaseUser mFirebaseUser;

    @Mock
    DocumentReference mDocumentReference;

    @Mock
    Uri urlPictureUri;

    @Test
    public void createUser() {
        String urlPicture = "url.com";
        String username = "username";
        String uid = getNextID();
        User expected_user = new User(uid, username, urlPicture);
        when(mFirebaseHelper.getCurrentFirebaseUser()).thenReturn(mFirebaseUser);
        when(mFirebaseHelper.getFirestoreUserDocumentReference()).thenReturn(documentSnapshotTask);
        when(documentSnapshotTask.addOnCompleteListener(testOnCompleteListener.capture())).thenReturn(documentSnapshotTask);
        when(documentSnapshotTask.isSuccessful()).thenReturn(true);
        when(documentSnapshotTask.getResult()).thenReturn(documentSnapshot);
        when(documentSnapshot.exists()).thenReturn(false);
        when(mFirebaseHelper.isFirebaseUserNotNull()).thenReturn(true);

        when(mFirebaseHelper.getCurrentFirebaseUser().getPhotoUrl()).thenReturn(urlPictureUri);
        when(urlPictureUri.toString()).thenReturn(urlPicture);
        when(mFirebaseHelper.getCurrentFirebaseUser().getDisplayName()).thenReturn(username);
        when(mFirebaseHelper.getCurrentFirebaseUser().getUid()).thenReturn(uid);

        when(mFirebaseHelper.getUserDocumentReference()).thenReturn(mDocumentReference);

        mUserRepository.createUser();

        testOnCompleteListener.getValue().onComplete(documentSnapshotTask);
        verify(mDocumentReference, times(1)).set(expected_user);
    }

    @Test
    public void getUserData() {
        when(mFirebaseHelper.isFirebaseUserNotNull()).thenReturn(true);

        mUserRepository.getUserData();

        verify(mFirebaseHelper, times(1)).getFirestoreUserDocumentReference();
    }

    @Test
    public void sendUserDataToFirestore() {
        when(mFirebaseHelper.getUserDocumentReference()).thenReturn(mDocumentReference);
        doReturn(null).when(spyUserRepository).getUpdatedLocalUserData();

        spyUserRepository.sendUserDataToFirestore();

        verify(mDocumentReference, times(1)).set(spyUserRepository.user);
        verify(spyUserRepository, times(1)).getUpdatedLocalUserData();
    }

    @Test
    public void updateUserData() {
        String urlPicture = "url.com";
        String username = "username";
        String uid = getNextID();
        User expected_user = new User(uid, username, urlPicture);
        doReturn(documentSnapshotTask).when(spyUserRepository).getUserData();
        when(documentSnapshotTask.addOnSuccessListener(testOnSuccessListener.capture())).thenReturn(documentSnapshotTask);
        when(documentSnapshot.toObject(User.class)).thenReturn(expected_user);

        spyUserRepository.updateLocalUserData();

        testOnSuccessListener.getValue().onSuccess(documentSnapshot);

        User actual_user = spyUserRepository.getUser();

        assertEquals(expected_user, actual_user);
    }

    @Test
    public void setPickedRestaurant() {
        String urlPicture = "url.com";
        String username = "username";
        String uid = getNextID();
        User expected_user = new User(uid, username, urlPicture);

        doReturn(documentSnapshotTask).when(spyUserRepository).getUserData();
        when(documentSnapshotTask.addOnSuccessListener(testOnSuccessListener.capture())).thenReturn(documentSnapshotTask);
        when(documentSnapshot.toObject(User.class)).thenReturn(expected_user);

        spyUserRepository.updateLocalUserData();

        testOnSuccessListener.getValue().onSuccess(documentSnapshot);

        doNothing().when(spyUserRepository).sendUserDataToFirestore();

        String expected_restaurantId = getNextID();
        String expected_restaurantName = "Restaurant";
        spyUserRepository.setPickedRestaurant(expected_restaurantId, expected_restaurantName);

        User resultUser = spyUserRepository.getUser();
        String actual_restaurantId = resultUser.getPickedRestaurant();
        String actual_restaurantName = resultUser.getPickedRestaurantName();

        System.out.println(expected_restaurantId + " " + actual_restaurantId);
        assertEquals(expected_restaurantId, actual_restaurantId);
        assertEquals(expected_restaurantName, actual_restaurantName);
        verify(spyUserRepository, times(1)).sendUserDataToFirestore();
    }

    @Test
    public void removePickedRestaurant() {
        String urlPicture = "url.com";
        String username = "username";
        String uid = getNextID();
        User testUser = new User(uid, username, urlPicture);
        String restaurantId = getNextID();
        String restaurantName = "Restaurant";
        testUser.setPickedRestaurant(restaurantId, restaurantName);

        doReturn(documentSnapshotTask).when(spyUserRepository).getUserData();
        when(documentSnapshotTask.addOnSuccessListener(testOnSuccessListener.capture())).thenReturn(documentSnapshotTask);
        when(documentSnapshot.toObject(User.class)).thenReturn(testUser);

        spyUserRepository.updateLocalUserData();

        testOnSuccessListener.getValue().onSuccess(documentSnapshot);

        doNothing().when(spyUserRepository).sendUserDataToFirestore();

        spyUserRepository.removePickedRestaurant();
        User resultUser = spyUserRepository.getUser();
        String actual_restaurantId = resultUser.getPickedRestaurant();
        String actual_restaurantName = resultUser.getPickedRestaurantName();

        assertNull(actual_restaurantName);
        assertNull(actual_restaurantId);
        verify(spyUserRepository, times(1)).sendUserDataToFirestore();
    }

    @Test
    public void addLikedRestaurant() {
        String urlPicture = "url.com";
        String username = "username";
        String uid = getNextID();
        User testUser = new User(uid, username, urlPicture);
        String restaurantId = getNextID();

        doReturn(documentSnapshotTask).when(spyUserRepository).getUserData();
        when(documentSnapshotTask.addOnSuccessListener(testOnSuccessListener.capture())).thenReturn(documentSnapshotTask);
        when(documentSnapshot.toObject(User.class)).thenReturn(testUser);

        spyUserRepository.updateLocalUserData();

        testOnSuccessListener.getValue().onSuccess(documentSnapshot);

        doNothing().when(spyUserRepository).sendUserDataToFirestore();

        spyUserRepository.addLikedRestaurant(restaurantId);
        User resultUser = spyUserRepository.getUser();
        ArrayList<String> actual_restaurantId = resultUser.getLikedRestaurants();

        assertTrue(actual_restaurantId.contains(restaurantId));
        verify(spyUserRepository, times(1)).sendUserDataToFirestore();
    }

    @Test
    public void removeLikedRestaurant() {
        String urlPicture = "url.com";
        String username = "username";
        String uid = getNextID();
        User testUser = new User(uid, username, urlPicture);
        String restaurantId = getNextID();
        testUser.addLikedRestaurant(restaurantId);

        doReturn(documentSnapshotTask).when(spyUserRepository).getUserData();
        when(documentSnapshotTask.addOnSuccessListener(testOnSuccessListener.capture())).thenReturn(documentSnapshotTask);
        when(documentSnapshot.toObject(User.class)).thenReturn(testUser);

        spyUserRepository.updateLocalUserData();

        testOnSuccessListener.getValue().onSuccess(documentSnapshot);

        doNothing().when(spyUserRepository).sendUserDataToFirestore();

        spyUserRepository.removeLikedRestaurant(restaurantId);
        User resultUser = spyUserRepository.getUser();

        assertFalse(resultUser.getLikedRestaurants().contains(restaurantId));
        verify(spyUserRepository, times(1)).sendUserDataToFirestore();
    }

    @Test
    public void getAllUsersWhoPickedARestaurant() throws InterruptedException {
        String urlPicture1 = "url.com";
        String username1 = "username1";
        String uid1 = getNextID();
        User testUser1 = new User(uid1, username1, urlPicture1);
        String urlPicture2 = "url.com";
        String username2 = "username2";
        String uid2 = getNextID();
        User testUser2 = new User(uid2, username2, urlPicture2);
        String restaurantId = getNextID();
        when(mFirebaseHelper.getEveryFirestoreUserWhoPickedThisRestaurant(restaurantId)).thenReturn(querySnapshotTask);
        when(querySnapshotTask.addOnCompleteListener(testOnCompleteListener.capture())).thenReturn(querySnapshotTask);
        when(querySnapshotTask.isSuccessful()).thenReturn(true);
        when(querySnapshotTask.getResult()).thenReturn(querySnapshot);
        when(querySnapshot.iterator()).thenReturn(Arrays.asList(queryDocumentSnapshot, queryDocumentSnapshot).iterator());
        when(queryDocumentSnapshot.toObject(User.class)).thenReturn(testUser1, testUser2);
        when(mFirebaseHelper.getCurrentFirebaseUserUID()).thenReturn(uid2);

        MutableLiveData<List<User>> result = mUserRepository.getEveryFirestoreUserWhoPickedThisRestaurant(restaurantId);

        testOnCompleteListener.getValue().onComplete(querySnapshotTask);

        LiveDataTestUtils.observeForTesting(result, liveData -> {
            assertEquals(liveData.getValue().size(), 1);
            assertEquals(liveData.getValue().get(0), testUser1);
        });
    }

    @Test
    public void getUsersWhoPickedARestaurant() {
        String urlPicture1 = "url.com";
        String username1 = "username1";
        String uid1 = getNextID();
        User testUser1 = new User(uid1, username1, urlPicture1);
        String urlPicture2 = "url.com";
        String username2 = "username2";
        String uid2 = getNextID();
        User testUser2 = new User(uid2, username2, urlPicture2);
        when(mFirebaseHelper.getEveryUserWhoPickedARestaurant()).thenReturn(querySnapshotTask);
        when(querySnapshotTask.addOnCompleteListener(testOnCompleteListener.capture())).thenReturn(querySnapshotTask);
        when(querySnapshotTask.isSuccessful()).thenReturn(true);
        when(querySnapshotTask.getResult()).thenReturn(querySnapshot);
        when(querySnapshot.iterator()).thenReturn(Arrays.asList(queryDocumentSnapshot, queryDocumentSnapshot).iterator());
        when(queryDocumentSnapshot.toObject(User.class)).thenReturn(testUser1, testUser2);
        when(mFirebaseHelper.getCurrentFirebaseUserUID()).thenReturn(uid2);

        MutableLiveData<List<User>> result = mUserRepository.getEveryUserWhoPickedARestaurant();

        testOnCompleteListener.getValue().onComplete(querySnapshotTask);

        LiveDataTestUtils.observeForTesting(result, liveData -> {
            assertEquals(liveData.getValue().size(), 1);
            assertEquals(liveData.getValue().get(0), testUser1);
        });
    }

    @Test
    public void getAllUsers() {
        String urlPicture1 = "url.com";
        String username1 = "username1";
        String uid1 = getNextID();
        User testUser1 = new User(uid1, username1, urlPicture1);
        String urlPicture2 = "url.com";
        String username2 = "username2";
        String uid2 = getNextID();
        User testUser2 = new User(uid2, username2, urlPicture2);
        when(mFirebaseHelper.getEveryFirestoreUser()).thenReturn(querySnapshotTask);
        when(querySnapshotTask.addOnCompleteListener(testOnCompleteListener.capture())).thenReturn(querySnapshotTask);
        when(querySnapshotTask.isSuccessful()).thenReturn(true);
        when(querySnapshotTask.getResult()).thenReturn(querySnapshot);
        when(querySnapshot.iterator()).thenReturn(Arrays.asList(queryDocumentSnapshot, queryDocumentSnapshot).iterator());
        when(queryDocumentSnapshot.toObject(User.class)).thenReturn(testUser1, testUser2);
        when(mFirebaseHelper.getCurrentFirebaseUserUID()).thenReturn(uid2);

        MutableLiveData<List<User>> result = mUserRepository.getEveryFirestoreUser();

        testOnCompleteListener.getValue().onComplete(querySnapshotTask);

        LiveDataTestUtils.observeForTesting(result, liveData -> {
            assertEquals(liveData.getValue().size(), 1);
            assertEquals(liveData.getValue().get(0), testUser1);
        });
    }

    @Test
    public void getUserDataToLocalUser() throws InterruptedException {
        String uid = getNextID();
        User user = new User(uid, "username", "url.com");
        when(mFirebaseHelper.getFirestoreUserDocumentReference()).thenReturn(documentSnapshotTask);

        when(documentSnapshotTask.addOnCompleteListener(testOnCompleteListener.capture())).thenReturn(documentSnapshotTask);
        when(documentSnapshotTask.getResult()).thenReturn(documentSnapshot);
        when(documentSnapshot.toObject(User.class)).thenReturn(user);
        when(documentSnapshotTask.isSuccessful()).thenReturn(true);

        MutableLiveData<User> result = mUserRepository.getUpdatedLocalUserData();

        testOnCompleteListener.getValue().onComplete(documentSnapshotTask);


        TestObserver.test(result)
                .awaitValue()
                .assertValue(user);
    }

    @Test
    public void getCurrentUser() {
        mUserRepository.getCurrentFirebaseUser();

        verify(mFirebaseHelper, times(1)).getCurrentFirebaseUser();
    }

    @Mock
    AuthUI mAuthUI;

    @Mock
    Context mContext;

    @Test
    public void signOut() {
        when(mFirebaseHelper.getAuthUI()).thenReturn(mAuthUI);

        mUserRepository.signOut(mContext);

        verify(mAuthUI, times(1)).signOut(any());
    }
}