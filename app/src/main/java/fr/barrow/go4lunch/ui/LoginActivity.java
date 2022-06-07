package fr.barrow.go4lunch.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;
import java.util.List;

import fr.barrow.go4lunch.BuildConfig;
import fr.barrow.go4lunch.R;
import fr.barrow.go4lunch.databinding.ActivityLoginBinding;
import fr.barrow.go4lunch.ui.viewmodels.MyViewModel;
import fr.barrow.go4lunch.utils.MyViewModelFactory;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    private MyViewModel mMyViewModel;

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            this::onSignInResult
    );

    private final Observer<Boolean> activeNetworkStateObserver = this::onNetworkStatusChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureViewModel();
        checkIfUserConnected();
        initUI();
        setupListeners();
        initNetworkStatus();
    }

    private void configureViewModel() {
        mMyViewModel = new ViewModelProvider(this, MyViewModelFactory.getInstance(this)).get(MyViewModel.class);
    }

    private void initNetworkStatus() {
        mMyViewModel.getConnectionStatus().observe(this, activeNetworkStateObserver);
    }

    private void onNetworkStatusChange(boolean isConnected) {
        if (isConnected) {
            Toast.makeText(
                    this, R.string.internet_connected,
                    Toast.LENGTH_SHORT
            ).show();
            binding.buttonSignInFacebook.setClickable(true);
            binding.buttonSignInFacebook.setText(R.string.button_facebookSignIn);
            binding.buttonSignInFacebook.setBackgroundColor(getResources().getColor(R.color.facebook));
            binding.buttonSignInGoogle.setClickable(true);
            binding.buttonSignInGoogle.setText(R.string.button_googleSignIn);
            binding.buttonSignInGoogle.setBackgroundColor(getResources().getColor(R.color.google));
            binding.buttonSignInTwitter.setClickable(true);
            binding.buttonSignInTwitter.setText(R.string.button_twitterSignIn);
            binding.buttonSignInTwitter.setBackgroundColor(getResources().getColor(R.color.twitter));
            binding.buttonSignInEmail.setClickable(true);
            binding.buttonSignInEmail.setText(R.string.button_EmailSignIn);
            binding.buttonSignInEmail.setBackgroundColor(getResources().getColor(R.color.mail));
        } else {
            Toast.makeText(
                    this, R.string.internet_disconnected,
                    Toast.LENGTH_SHORT
            ).show();
            binding.buttonSignInFacebook.setClickable(false);
            binding.buttonSignInFacebook.setText(R.string.button_NoNetwork);
            binding.buttonSignInFacebook.setBackgroundColor(getResources().getColor(R.color.facebookNoNetwork));
            binding.buttonSignInGoogle.setClickable(false);
            binding.buttonSignInGoogle.setText(R.string.button_NoNetwork);
            binding.buttonSignInGoogle.setBackgroundColor(getResources().getColor(R.color.googleNoNetwork));
            binding.buttonSignInTwitter.setClickable(false);
            binding.buttonSignInTwitter.setText(R.string.button_NoNetwork);
            binding.buttonSignInTwitter.setBackgroundColor(getResources().getColor(R.color.twitterNoNetwork));
            binding.buttonSignInEmail.setClickable(false);
            binding.buttonSignInEmail.setText(R.string.button_NoNetwork);
            binding.buttonSignInEmail.setBackgroundColor(getResources().getColor(R.color.mailNoNetwork));
        }
    }

    public void createSignInIntent(AuthUI.IdpConfig provider) {
        //List<AuthUI.IdpConfig> providers =  Collections.singletonList(provider);
        List<AuthUI.IdpConfig> providers =  Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build(),
                new AuthUI.IdpConfig.TwitterBuilder().build(),
                new AuthUI.IdpConfig.EmailBuilder().build());


        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setDefaultProvider(provider)
                .setIsSmartLockEnabled(!BuildConfig.DEBUG /* credentials */, true /* hints */)
                .setLogo(R.drawable.logo)
                .build();
        signInLauncher.launch(signInIntent);
    }

    private void checkIfUserConnected() {
        if (mMyViewModel.isCurrentUserLogged()) {
            //finish();
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    private void setupListeners(){
        binding.buttonSignInFacebook.setOnClickListener(view -> createSignInIntent(new AuthUI.IdpConfig.FacebookBuilder().build()));
        binding.buttonSignInGoogle.setOnClickListener(view -> createSignInIntent(new AuthUI.IdpConfig.GoogleBuilder().build()));
        binding.buttonSignInTwitter.setOnClickListener(view -> createSignInIntent(new AuthUI.IdpConfig.TwitterBuilder().build()));
        binding.buttonSignInEmail.setOnClickListener(view -> createSignInIntent(new AuthUI.IdpConfig.EmailBuilder().build()));
    }

    private void initUI(){
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
    }

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();

        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in
            showSnackBar(getString(R.string.connection_succeed));
            mMyViewModel.createUser();
            //finish();
            startActivity(new Intent(this, MainActivity.class));
        } else {
            if (response == null) {
                showSnackBar(getString(R.string.sign_in_cancelled));
                return;
            }

            if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                showSnackBar(getString(R.string.no_internet_connection));
                return;
            }

            showSnackBar(getString(R.string.unknown_error));
            Log.e("MainActivity", "Sign-in error: ", response.getError());
        }
    }

    private void showSnackBar(String message){
        Snackbar.make(binding.constraintLayoutMainActivity, message, Snackbar.LENGTH_SHORT).show();
    }
}