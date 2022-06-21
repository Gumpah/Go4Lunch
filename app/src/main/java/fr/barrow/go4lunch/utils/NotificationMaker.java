package fr.barrow.go4lunch.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;

import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.LiveData;

import java.util.List;

import fr.barrow.go4lunch.R;
import fr.barrow.go4lunch.data.FirebaseHelper;
import fr.barrow.go4lunch.data.UserRepository;
import fr.barrow.go4lunch.data.model.User;

public class NotificationMaker extends LifecycleService {

    private final int NOTIFICATION_ID = 007;
    private final UserRepository mUserRepository;
    private Context context;
    private String notification_alone_firstPart;
    private String notification_notAlone_firstPart;
    private String notification_notAlone_with;
    private String notification_notAlone_and;
    private String app_name;

    public NotificationMaker(String notification_alone_firstPart, String notification_notAlone_firstPart, String notification_notAlone_with, String notification_notAlone_and, String app_name) {
        mUserRepository = new UserRepository(new FirebaseHelper());

        this.notification_alone_firstPart = notification_alone_firstPart;
        this.notification_notAlone_firstPart = notification_notAlone_firstPart;
        this.notification_notAlone_with = notification_notAlone_with;
        this.notification_notAlone_and = notification_notAlone_and;
        this.app_name = app_name;
    }

    @Override
    public void onCreate() {
        notification_alone_firstPart = context.getString(R.string.notification_alone_firstPart);
        notification_notAlone_firstPart = context.getString(R.string.notification_notAlone_firstPart);
        notification_notAlone_with = context.getString(R.string.notification_notAlone_with);
        notification_notAlone_and = context.getString(R.string.notification_notAlone_and);
        app_name = context.getString(R.string.app_name);
        super.onCreate();
    }

    public void fetchingAndReturningString(NotificationManager notificationManager, PendingIntent pendingIntent, NotificationCompat.Builder notification) {
        LiveData<User> observer = getUser();
        observer.observeForever(user -> {
            if (user != null) {
                fetchWorkmatesJoining(user, notificationManager, pendingIntent, notification);
                observer.removeObservers(this);
            }
        });
    }

    private void fetchWorkmatesJoining(User user, NotificationManager notificationManager, PendingIntent pendingIntent, NotificationCompat.Builder notification) {
        if (user == null) {
            return;
        }
        if (user.getPickedRestaurant() != null) {
            LiveData<List<User>> observer = getUsersWhoPickedRestaurant(user.getPickedRestaurant());
            observer.observeForever(list -> {
                if (list != null) {
                    if (list.isEmpty()) {
                        sendNotification((notification_alone_firstPart + user.getPickedRestaurantName()), notificationManager, pendingIntent, notification);
                    } else {
                        sendNotification((notification_notAlone_firstPart + user.getPickedRestaurantName() + notification_notAlone_with + getWorkmatesJoiningString(list)), notificationManager, pendingIntent, notification);
                    }
                    observer.removeObservers(this);
                }
            });

        }
    }

    private void sendNotification(String notificationMessage, NotificationManager notificationManager, PendingIntent pendingIntent, NotificationCompat.Builder notification) {
        notification
               .setContentTitle(app_name)
               .setContentText(notificationMessage)
               .setSmallIcon(R.drawable.logo)
               .setAutoCancel(true)
               .setContentIntent(pendingIntent);

        notificationManager.notify(NOTIFICATION_ID, notification.build());
    }

    private LiveData<List<User>> getUsersWhoPickedRestaurant(String restaurantId) {
        return mUserRepository.getEveryFirestoreUserWhoPickedThisRestaurant(restaurantId);
    }

    private LiveData<User> getUser() {
        return mUserRepository.getUpdatedLocalUserData();
    }

    private String getWorkmatesJoiningString (List<User> users) {
        if (users.size() == 1) {
            return users.get(0).getUsername();
        } else {
            StringBuilder workmatesNamesList = new StringBuilder();
            for (int i = 0; i < users.size(); i++) {
                if (i == (users.size() - 1)) {
                    workmatesNamesList.append(notification_notAlone_and).append(users.get(i).getUsername());
                } else {
                    workmatesNamesList.append(users.get(i).getUsername()).append(", ");
                }
            }
            return workmatesNamesList.toString();
        }
    }
}
