package com.labibkamran.nustfruta;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.labibkamran.nustfruta.Model.UserModel;
import com.labibkamran.nustfruta.databinding.ActivityRegisterBinding;

public class ActivityRegister extends AppCompatActivity {

    ActivityRegisterBinding binding;

    private String name;
    private String password;
    private String email;
    FirebaseAuth auth;
    DatabaseReference database;
    GoogleSignInClient googleSignInClient;

    // Create an instance of the ActivityResultLauncher
    private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    try {
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        if (account != null) {
                            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                            auth.signInWithCredential(credential)
                                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(ActivityRegister.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                finish();
                                            } else {
                                                Toast.makeText(ActivityRegister.this, "Sign in failed", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    } catch (ApiException e) {
                        Toast.makeText(ActivityRegister.this, "Sign in failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        name = binding.Username.getText().toString();
        email = binding.email.getText().toString();
        password = binding.Password.getText().toString();
        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = binding.Username.getText().toString();
                email = binding.email.getText().toString();
                password = binding.Password.getText().toString();

                if (checkCredentials()) {
                    createAccount(email, password);
                }
            }
        });
        binding.google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signIntent = googleSignInClient.getSignInIntent();
                launcher.launch(signIntent);

            }
        });
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivityLogin.class);
                startActivity(intent);
            }
        });
    }

    private void createAccount(String email, String password) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                saveUserData();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            } else {
                Toast.makeText(this, "Account creation failed", Toast.LENGTH_SHORT).show();
                Log.d("Account", "Create Account:    Failure", task.getException());
            }
        });
    }

    private void saveUserData() {
        name = binding.Username.getText().toString();
        email = binding.email.getText().toString();
        password = binding.Password.getText().toString();

        UserModel user = new UserModel(name, email, password);
        FirebaseUser userid = FirebaseAuth.getInstance().getCurrentUser();
        if (userid != null) {
            String userId = userid.getUid();
            // save data to real time data base
            database.child("user").child(userId).setValue(user);
        } else {
            // Handle the case where there's no signed-in user
        }
    }

    private boolean checkCredentials() {

        if (name.isEmpty())
            showError(binding.Username, "please fill this field");
        else if (email.isEmpty())
            showError(binding.email, "please fill this field");
        else if (password.isEmpty())
            showError(binding.Password, "please fill this field");

        else {
            return true;
        }
        return false;
    }

    private void showError(EditText input, String error) {
        input.setError(error);
        input.requestFocus();
    }
}
