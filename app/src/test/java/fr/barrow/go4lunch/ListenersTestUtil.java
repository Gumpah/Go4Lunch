package fr.barrow.go4lunch;

import static org.mockito.Mockito.when;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.ListenerRegistration;

import org.mockito.ArgumentCaptor;

public class ListenersTestUtil {

    static final String ANY_EMAIL = "email@email.com";
    static final String RESULT_CODE = "ABC";
    static final String ANY_CODE = "ABCDE";
    static final String ANY_PASSWORD = "ANY_PASSWORD";
    static final String ANY_TOKEN = "ANY_KEY";
    static final String ANY_KEY = "_token_";
    static final String PREVIOUS_CHILD_NAME = "NONE";
    static final Exception EXCEPTION = new Exception("Something bad happen");
    static final long ANY_TIME = 12000;

    public static ArgumentCaptor<OnCompleteListener> testOnCompleteListener = ArgumentCaptor.forClass(OnCompleteListener.class);
    public static ArgumentCaptor<OnSuccessListener> testOnSuccessListener = ArgumentCaptor.forClass(OnSuccessListener.class);
    public static ArgumentCaptor<OnFailureListener> testOnFailureListener = ArgumentCaptor.forClass(OnFailureListener.class);
    public static ArgumentCaptor<EventListener<DocumentSnapshot>> eventSnapshotListener = ArgumentCaptor.forClass(EventListener.class);

    public static <T> void setupTask(Task<T> task) {
        when(task.addOnCompleteListener(testOnCompleteListener.capture())).thenReturn(task);
        when(task.addOnSuccessListener(testOnSuccessListener.capture())).thenReturn(task);
        when(task.addOnFailureListener(testOnFailureListener.capture())).thenReturn(task);
    }

    public static void setupOfflineTask(DocumentReference documentReference, ListenerRegistration registration) {
        when(documentReference.addSnapshotListener(eventSnapshotListener.capture())).thenReturn(registration);
    }
}
