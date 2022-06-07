package fr.barrow.go4lunch.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import fr.barrow.go4lunch.R;
import fr.barrow.go4lunch.ui.MainActivity;

public class NotificationService extends FirebaseMessagingService {

    private final int NOTIFICATION_ID = 007;
    private final String NOTIFICATION_TAG = "FIREBASEOC";
    private final String preferencesName = "MyPref";
    private final String preferenceNotifications = "receiving_notifications";
    private String token;
    private String CHANNEL_ID;
    private PendingIntent pendingIntent;
    private NotificationManager notificationManager;


    @Override
    public void onNewToken(@NonNull String newToken) {
        super.onNewToken(newToken);
        token = newToken;
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        SharedPreferences pref = getApplicationContext().getSharedPreferences(preferencesName, 0);

        boolean areNotificationsAllowed = pref.getBoolean(preferenceNotifications, true);
        if (remoteMessage.getNotification() != null && areNotificationsAllowed) {

            Intent intent = new Intent(this, MainActivity.class);
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            CHANNEL_ID = getString(R.string.default_notification_channel_id);

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel(notificationManager);
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT + PendingIntent.FLAG_IMMUTABLE);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT + PendingIntent.FLAG_IMMUTABLE);
            } else {
                pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            }

            NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CHANNEL_ID);

            new Handler(Looper.getMainLooper()).post(() -> {
                NotificationMaker notificationMaker = new NotificationMaker(this);
                notificationMaker.fetchingAndReturningString(notificationManager, pendingIntent, notification);
            });
        }
    }



    @RequiresApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(NotificationManager notificationManager) {
        CharSequence channelName = "Firebase Messages";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        String CHANNEL_ID = getString(R.string.default_notification_channel_id);
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, importance);
        notificationManager.createNotificationChannel(channel);
    }
}