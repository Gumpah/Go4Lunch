package fr.barrow.go4lunch.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import fr.barrow.go4lunch.databinding.ActivitySettingsBinding;
import fr.barrow.go4lunch.utils.MyViewModelFactory;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;
    private MyViewModel mMyViewModel;
    private Context mContext;
    private Resources mResources;
    private final String preferencesName = "MyPref";
    private final String preferenceNotifications = "receiving_notifications";
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        configureViewModel();
        initSharedPreferences();
        initNotificationsSwitch();
    }

    private void initUI() {
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
    }

    private void configureViewModel() {
        mMyViewModel = new ViewModelProvider(this, MyViewModelFactory.getInstance(this)).get(MyViewModel.class);
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