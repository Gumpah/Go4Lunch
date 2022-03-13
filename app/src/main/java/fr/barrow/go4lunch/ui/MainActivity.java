package fr.barrow.go4lunch.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.material.snackbar.Snackbar;

import java.util.Collections;
import java.util.List;

import fr.barrow.go4lunch.R;
import fr.barrow.go4lunch.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            this::onSignInResult
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        setupListeners();
    }

    public void createSignInIntent(AuthUI.IdpConfig provider) {
        List<AuthUI.IdpConfig> providers =  Collections.singletonList(provider);

        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(R.drawable.logo)
                .build();
        signInLauncher.launch(signInIntent);
    }

    public void signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(task -> {
                    // When completed
                });
    }

    private void setupListeners(){
        // Login Button
        binding.buttonSignInFacebook.setOnClickListener(view -> createSignInIntent(new AuthUI.IdpConfig.FacebookBuilder().build()));
        binding.buttonSignInGoogle.setOnClickListener(view -> createSignInIntent(new AuthUI.IdpConfig.GoogleBuilder().build()));
    }

    private void initUI(){
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
    }

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();

        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in
            showSnackBar(getString(R.string.connection_succeed));
        } else {
            // Sign in failed
            if (response == null) {
                // User pressed back button
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

    // Show Snack Bar with a message
    private void showSnackBar(String message){
        Snackbar.make(binding.constraintLayoutMainActivity, message, Snackbar.LENGTH_SHORT).show();
    }
}