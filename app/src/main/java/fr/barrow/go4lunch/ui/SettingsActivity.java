package fr.barrow.go4lunch.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import fr.barrow.go4lunch.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;
    private final String preferencesName = "MyPref";
    private final String preferenceNotifications = "receiving_notifications";
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        initSharedPreferences();
        initNotificationsSwitch();
    }

    private void initUI() {
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
    }

    private void initSharedPreferences() {
        pref = getApplicationContext().getSharedPreferences(preferencesName, 0);
        editor = pref.edit();
    }

    private void initNotificationsSwitch() {
        binding.switchNotifications.setChecked(getNotificationStatus());
        binding.switchNotifications.setOnCheckedChangeListener(((buttonView, isChecked) -> setNotificationStatus(isChecked)));
    }

    private boolean getNotificationStatus() {
        return pref.getBoolean(preferenceNotifications, true);
    }

    private void setNotificationStatus(boolean status) {
        editor.putBoolean(preferenceNotifications, status);
        editor.apply();
    }

}