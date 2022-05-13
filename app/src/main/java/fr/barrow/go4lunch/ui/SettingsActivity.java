package fr.barrow.go4lunch.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;

import java.util.Locale;

import fr.barrow.go4lunch.R;
import fr.barrow.go4lunch.databinding.ActivityMainBinding;
import fr.barrow.go4lunch.databinding.ActivitySettingsBinding;
import fr.barrow.go4lunch.utils.LocaleHelper;
import fr.barrow.go4lunch.utils.MyViewModelFactory;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;
    private MyViewModel mMyViewModel;
    private LocaleHelper mLocaleHelper;
    private Context mContext;
    private Resources mResources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        configureViewModel();
        setLocaleHelper();
        setClickListeners();
    }

    private void initUI() {
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
    }

    private void configureViewModel() {
        mMyViewModel = new ViewModelProvider(this, MyViewModelFactory.getInstance(this)).get(MyViewModel.class);
    }

    private void setLocaleHelper() {
        mLocaleHelper = new LocaleHelper();
    }

    private void setClickListeners() {
        binding.buttonSetEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Configuration config = getBaseContext().getResources().getConfiguration();
                Locale locale = new Locale("en");
                Locale.setDefault(locale);
                config.locale = locale;
                getBaseContext().getResources().updateConfiguration(config,
                        getBaseContext().getResources().getDisplayMetrics());
                binding.buttonSetEnglish.setText(R.string.star);

            }
        });

        binding.buttonSetFrench.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Configuration config = getBaseContext().getResources().getConfiguration();
                Locale locale = new Locale("fr");
                Locale.setDefault(locale);
                config.locale = locale;
                getBaseContext().getResources().updateConfiguration(config,
                        getBaseContext().getResources().getDisplayMetrics());

                binding.buttonSetEnglish.setText(R.string.star);
            }
        });
    }
}