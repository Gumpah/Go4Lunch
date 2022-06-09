package fr.barrow.go4lunch.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;

import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.LiveData;

import java.util.List;

import fr.barrow.go4lunch.R;
import fr.barrow.go4lunch.data.FirebaseHelper;
import fr.barrow.go4lunch.data.UserRepository;
import fr.barrow.go4lunch.model.User;

public class NotificationMaker extends LifecycleService {

    private final int NOTIFICATION_ID = 007;
    private final UserRepository mUserRepository;

    public NotificationMaker() {
        mUserRepository = new UserRepository(new FirebaseHelper());
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
                        sendNotification((getString(R.string.notification_alone_firstPart) + user.getPickedRestaurantName()), notificationManager, pendingIntent, notification);
                    } else {
                        sendNotification((getString(R.string.notification_notAlone_firstPart) + user.getPickedRestaurantName() + getString(R.string.notification_notAlone_with) + getWorkmatesJoiningString(list)), notificationManager, pendingIntent, notification);
                    }
                    observer.removeObservers(this);
                }
            });

        }
    }

    private void sendNotification(String notificationMessage, NotificationManager notificationManager, PendingIntent pendingIntent, NotificationCompat.Builder notification) {
        notification
               .setContentTitle(getString(R.string.app_name))
               .setContentText(notificationMessage)
               .setSmallIcon(R.drawable.logo)
               .setAutoCancel(true)
               .setContentIntent(pendingIntent);

        notificationManager.notify(NOTIFICATION_ID, notification.build());
    }

    private LiveData<List<User>> getUsersWhoPickedRestaurant(String restaurantId) {
        return mUserRepository.getAllUsersWhoPickedARestaurant(restaurantId);
    }

    private LiveData<User> getUser() {
        return mUserRepository.getUserDataToLocalUser();
    }

    private String getWorkmatesJoiningString (List<User> users) {
        if (users.size() == 1) {
            return users.get(0).getUsername();
        } else {
            StringBuilder workmatesNamesList = new StringBuilder();
            for (int i = 0; i < users.size(); i++) {
                if (i == (users.size() - 1)) {
                    workmatesNamesList.append((getString(R.string.notification_notAlone_and))).append(users.get(i).getUsername());
                } else {
                    workmatesNamesList.append(users.get(i).getUsername()).append(", ");
                }
            }
            return workmatesNamesList.toString();
        }
    }
}
