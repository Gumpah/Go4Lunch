package fr.barrow.go4lunch.data;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static fr.barrow.go4lunch.ListenersTestUtil.setupTask;
import static fr.barrow.go4lunch.ListenersTestUtil.testOnCompleteListener;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.jraska.livedata.TestObserver;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

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

    private UserRepository mUserRepository;

    @Before
    public void setUp() {
        mUserRepository = new UserRepository(mFirebaseHelper);
        setupTask(documentSnapshotTask);
    }

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void getUserDataToLocalUser() throws InterruptedException {
        String uid = "123";
        User user = new User(uid, "username", "url.com");
        User user2 = new User("2", "username", "url.com");
        when(mFirebaseHelper.getUser()).thenReturn(documentSnapshotTask);

        when(documentSnapshotTask.addOnCompleteListener(testOnCompleteListener.capture())).thenReturn(documentSnapshotTask);
        when(documentSnapshotTask.getResult()).thenReturn(documentSnapshot);
        when(documentSnapshot.toObject(User.class)).thenReturn(user);
        when(documentSnapshotTask.isSuccessful()).thenReturn(true);

        MutableLiveData<User> result = mUserRepository.getUserDataToLocalUser();

        testOnCompleteListener.getValue().onComplete(documentSnapshotTask);


        TestObserver.test(result)
                .awaitValue()
                .assertValue(user);
    }
}